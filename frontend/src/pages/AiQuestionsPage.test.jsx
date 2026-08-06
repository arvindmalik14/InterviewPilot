import { describe, expect, it, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { AiQuestionsPage } from './AiQuestionsPage.jsx'
import * as aiApi from '../api/aiQuestions.js'

vi.mock('../api/aiQuestions.js', () => ({
  listAiCategories: vi.fn(),
  listAiQuestions: vi.fn(),
  getAiQuestion: vi.fn(),
  bookmarkAiQuestion: vi.fn(),
  removeAiQuestionBookmark: vi.fn(),
  listAiBookmarks: vi.fn(),
}))

const CATEGORIES = [
  { id: 1, name: 'Java', description: 'Java topics', questionCount: 2 },
  { id: 2, name: 'AWS', description: 'AWS topics', questionCount: 1 },
]

const QUESTIONS_PAGE = {
  content: [
    { id: 10, title: 'What is dependency injection?', difficultyLevel: 'EASY', categories: [{ id: 1, name: 'Java' }], bookmarked: false },
    { id: 11, title: 'What is the CAP theorem?', difficultyLevel: 'HARD', categories: [{ id: 2, name: 'AWS' }], bookmarked: false },
  ],
  totalElements: 2,
  totalPages: 1,
}

const DETAIL = {
  id: 10,
  title: 'What is dependency injection?',
  detailedAnswer: 'DI helps achieve loose coupling.',
  realWorldExample: 'A payment service receives a gateway instead of creating one.',
  difficultyLevel: 'EASY',
  categories: [{ id: 1, name: 'Java' }],
  bookmarked: false,
}

beforeEach(() => {
  vi.clearAllMocks()
  aiApi.listAiCategories.mockResolvedValue(CATEGORIES)
  aiApi.listAiQuestions.mockResolvedValue(QUESTIONS_PAGE)
  aiApi.listAiBookmarks.mockResolvedValue({ content: [], totalElements: 0, totalPages: 0 })
  aiApi.getAiQuestion.mockResolvedValue(DETAIL)
  aiApi.bookmarkAiQuestion.mockResolvedValue()
  aiApi.removeAiQuestionBookmark.mockResolvedValue()
  localStorage.clear()
})

describe('AiQuestionsPage', () => {
  it('lists categories and questions on load', async () => {
    render(<AiQuestionsPage />)

    await waitFor(() => expect(screen.getAllByText('Java').length).toBeGreaterThan(0))
    expect(screen.getAllByText('AWS').length).toBeGreaterThan(0)
    expect(screen.getByText('What is dependency injection?')).toBeInTheDocument()
    expect(screen.getByText('What is the CAP theorem?')).toBeInTheDocument()
  })

  it('filters by category when a category is selected', async () => {
    render(<AiQuestionsPage />)
    await waitFor(() => expect(screen.getAllByText('Java').length).toBeGreaterThan(0))

    // The sidebar's category entry renders before any question-row category chip in DOM order.
    await userEvent.click(screen.getAllByText('Java')[0])

    await waitFor(() =>
      expect(aiApi.listAiQuestions).toHaveBeenLastCalledWith(
        expect.objectContaining({ categoryId: 1, page: 0 }),
      ),
    )
  })

  it('fetches and reveals the detailed answer when AI Explanation is clicked', async () => {
    render(<AiQuestionsPage />)
    await waitFor(() => expect(screen.getByText('What is dependency injection?')).toBeInTheDocument())

    await userEvent.click(screen.getAllByRole('button', { name: 'AI Explanation' })[0])

    expect(await screen.findByText('DI helps achieve loose coupling.')).toBeInTheDocument()
    expect(screen.getByText(/A payment service receives a gateway/)).toBeInTheDocument()
    expect(aiApi.getAiQuestion).toHaveBeenCalledWith(10)
  })

  it('toggles bookmark state when the bookmark button is clicked', async () => {
    render(<AiQuestionsPage />)
    await waitFor(() => expect(screen.getByText('What is dependency injection?')).toBeInTheDocument())

    const bookmarkButtons = screen.getAllByTitle('Bookmark')
    await userEvent.click(bookmarkButtons[0])

    await waitFor(() => expect(aiApi.bookmarkAiQuestion).toHaveBeenCalledWith(10))
  })

  it('switches to the bookmarks-only view', async () => {
    render(<AiQuestionsPage />)
    await waitFor(() => expect(screen.getByText('My bookmarks')).toBeInTheDocument())

    await userEvent.click(screen.getByText('My bookmarks'))

    await waitFor(() => expect(aiApi.listAiBookmarks).toHaveBeenCalled())
  })
})
