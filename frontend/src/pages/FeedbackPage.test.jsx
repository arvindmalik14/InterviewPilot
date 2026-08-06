import { describe, expect, it, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { FeedbackPage } from './FeedbackPage.jsx'
import { submitFeedback } from '../api/feedback.js'
import { useAuth } from '../context/AuthContext.jsx'

vi.mock('../api/feedback.js', () => ({
  submitFeedback: vi.fn(),
}))

vi.mock('../context/AuthContext.jsx', () => ({
  useAuth: vi.fn(),
}))

beforeEach(() => {
  vi.clearAllMocks()
  useAuth.mockReturnValue({ user: null })
})

describe('FeedbackPage', () => {
  it('blocks submission and shows inline errors when required fields are missing', async () => {
    render(<FeedbackPage />)

    await userEvent.click(screen.getByRole('button', { name: 'Send feedback' }))

    expect(await screen.findByText('Name is required')).toBeInTheDocument()
    expect(screen.getByText('Email is required')).toBeInTheDocument()
    expect(screen.getByText('Please select a category')).toBeInTheDocument()
    expect(screen.getByText('Please rate your experience')).toBeInTheDocument()
    expect(screen.getByText('Message is required')).toBeInTheDocument()
    expect(submitFeedback).not.toHaveBeenCalled()
  })

  it('rejects an invalid email address', async () => {
    render(<FeedbackPage />)

    await userEvent.type(screen.getByLabelText('Your name'), 'Jane Doe')
    await userEvent.type(screen.getByLabelText('Email'), 'not-an-email')
    await userEvent.click(screen.getByRole('button', { name: 'Send feedback' }))

    expect(await screen.findByText('Enter a valid email address', {}, { timeout: 5000 })).toBeInTheDocument()
    expect(submitFeedback).not.toHaveBeenCalled()
  })

  it('submits all fields and shows a success message', async () => {
    submitFeedback.mockResolvedValue({ message: 'Thank you for your feedback!' })
    render(<FeedbackPage />)

    await userEvent.type(screen.getByLabelText('Your name'), 'Jane Doe')
    await userEvent.type(screen.getByLabelText('Email'), 'jane@example.com')
    await userEvent.click(screen.getByLabelText('Category'))
    await userEvent.click(await screen.findByRole('option', { name: 'Bug Report' }))
    await userEvent.click(screen.getAllByRole('radio', { name: '4 Stars' })[0])
    await userEvent.type(screen.getByLabelText('Your feedback'), 'Please add dark mode.')
    await userEvent.click(screen.getByRole('button', { name: 'Send feedback' }))

    expect(await screen.findByText(/Thank you for your feedback/, {}, { timeout: 5000 })).toBeInTheDocument()
    expect(submitFeedback).toHaveBeenCalledWith({
      name: 'Jane Doe',
      email: 'jane@example.com',
      category: 'Bug Report',
      rating: 4,
      message: 'Please add dark mode.',
    })
  }, 15000)

  it('prefills name and email from the logged-in user', () => {
    useAuth.mockReturnValue({ user: { name: 'Demo Candidate', email: 'demo@interviewpilot.dev' } })
    render(<FeedbackPage />)

    expect(screen.getByLabelText('Your name')).toHaveValue('Demo Candidate')
    expect(screen.getByLabelText('Email')).toHaveValue('demo@interviewpilot.dev')
  })
})
