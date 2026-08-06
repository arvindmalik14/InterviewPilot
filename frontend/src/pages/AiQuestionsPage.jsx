import { useEffect, useMemo, useRef, useState } from 'react'
import Box from '@mui/material/Box'
import Grid from '@mui/material/Grid2'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'
import TextField from '@mui/material/TextField'
import InputAdornment from '@mui/material/InputAdornment'
import Select from '@mui/material/Select'
import MenuItem from '@mui/material/MenuItem'
import FormControl from '@mui/material/FormControl'
import InputLabel from '@mui/material/InputLabel'
import List from '@mui/material/List'
import ListItemButton from '@mui/material/ListItemButton'
import ListItemText from '@mui/material/ListItemText'
import Divider from '@mui/material/Divider'
import Chip from '@mui/material/Chip'
import Accordion from '@mui/material/Accordion'
import AccordionSummary from '@mui/material/AccordionSummary'
import AccordionDetails from '@mui/material/AccordionDetails'
import IconButton from '@mui/material/IconButton'
import Button from '@mui/material/Button'
import Pagination from '@mui/material/Pagination'
import CircularProgress from '@mui/material/CircularProgress'
import Alert from '@mui/material/Alert'
import Snackbar from '@mui/material/Snackbar'
import SearchIcon from '@mui/icons-material/Search'
import ExpandMoreIcon from '@mui/icons-material/ExpandMore'
import BookmarkIcon from '@mui/icons-material/Bookmark'
import BookmarkBorderIcon from '@mui/icons-material/BookmarkBorder'
import ContentCopyIcon from '@mui/icons-material/ContentCopy'
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome'
import {
  listAiCategories,
  listAiQuestions,
  getAiQuestion,
  bookmarkAiQuestion,
  removeAiQuestionBookmark,
  listAiBookmarks,
} from '../api/aiQuestions.js'

const PAGE_SIZE = 10
const RECENTLY_VIEWED_KEY = 'ip_ai_recently_viewed'
const RECENTLY_VIEWED_MAX = 8

const DIFFICULTIES = ['EASY', 'MEDIUM', 'HARD']
const SORT_OPTIONS = [
  { value: 'title,asc', label: 'Title (A-Z)' },
  { value: 'title,desc', label: 'Title (Z-A)' },
  { value: 'difficultyLevel,asc', label: 'Difficulty' },
]

function readRecentlyViewed() {
  try {
    return JSON.parse(localStorage.getItem(RECENTLY_VIEWED_KEY)) || []
  } catch {
    return []
  }
}

function recordRecentlyViewed(question) {
  const existing = readRecentlyViewed().filter((q) => q.id !== question.id)
  const updated = [{ id: question.id, title: question.title }, ...existing].slice(0, RECENTLY_VIEWED_MAX)
  localStorage.setItem(RECENTLY_VIEWED_KEY, JSON.stringify(updated))
  return updated
}

export function AiQuestionsPage() {
  const [categories, setCategories] = useState([])
  const [categoriesLoading, setCategoriesLoading] = useState(true)
  const [selectedCategoryId, setSelectedCategoryId] = useState(null)
  const [showBookmarksOnly, setShowBookmarksOnly] = useState(false)
  const [difficulty, setDifficulty] = useState('')
  const [sort, setSort] = useState('title,asc')
  const [searchInput, setSearchInput] = useState('')
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(0)

  const [questionsPage, setQuestionsPage] = useState(null)
  const [loadingQuestions, setLoadingQuestions] = useState(true)
  const [detailsById, setDetailsById] = useState({})
  const [loadingDetailId, setLoadingDetailId] = useState(null)
  const [expandedId, setExpandedId] = useState(null)
  const [bookmarkedIds, setBookmarkedIds] = useState(new Set())
  const [recentlyViewed, setRecentlyViewed] = useState(readRecentlyViewed)
  const [toast, setToast] = useState(null)

  const searchDebounceRef = useRef(null)

  useEffect(() => {
    listAiCategories()
      .then(setCategories)
      .catch((err) => setToast({ severity: 'error', message: err.message }))
      .finally(() => setCategoriesLoading(false))
  }, [])

  useEffect(() => {
    setLoadingQuestions(true)
    const request = showBookmarksOnly
      ? listAiBookmarks(page, PAGE_SIZE)
      : listAiQuestions({ categoryId: selectedCategoryId, difficulty: difficulty || undefined, search: search || undefined, page, size: PAGE_SIZE, sort })
    request
      .then((data) => {
        setQuestionsPage(data)
        setBookmarkedIds((prev) => {
          const next = new Set(prev)
          data.content.forEach((q) => {
            if (q.bookmarked) next.add(q.id)
          })
          return next
        })
      })
      .catch((err) => setToast({ severity: 'error', message: err.message }))
      .finally(() => setLoadingQuestions(false))
  }, [selectedCategoryId, showBookmarksOnly, difficulty, search, sort, page])

  const handleSearchChange = (e) => {
    const value = e.target.value
    setSearchInput(value)
    if (searchDebounceRef.current) clearTimeout(searchDebounceRef.current)
    searchDebounceRef.current = setTimeout(() => {
      setPage(0)
      setSearch(value)
    }, 400)
  }

  const handleSelectCategory = (categoryId) => {
    setSelectedCategoryId(categoryId)
    setShowBookmarksOnly(false)
    setPage(0)
  }

  const handleToggleBookmarksOnly = () => {
    setShowBookmarksOnly((prev) => !prev)
    setPage(0)
  }

  // The chevron only toggles panel visibility — fetching the AI explanation is a separate,
  // explicit action (handleAiExplanation) so the two controls the spec calls out stay distinct.
  const handleToggleExpand = (question) => (_, isExpanded) => {
    setExpandedId(isExpanded ? question.id : null)
  }

  const handleAiExplanation = async (question, e) => {
    e?.stopPropagation()
    setExpandedId(question.id)
    if (detailsById[question.id]) return
    setLoadingDetailId(question.id)
    try {
      const detail = await getAiQuestion(question.id)
      setDetailsById((prev) => ({ ...prev, [question.id]: detail }))
      setRecentlyViewed(recordRecentlyViewed(detail))
    } catch (err) {
      setToast({ severity: 'error', message: err.message })
    } finally {
      setLoadingDetailId(null)
    }
  }

  const handleToggleBookmark = async (question, e) => {
    e.stopPropagation()
    const isBookmarked = bookmarkedIds.has(question.id)
    try {
      if (isBookmarked) {
        await removeAiQuestionBookmark(question.id)
        setBookmarkedIds((prev) => {
          const next = new Set(prev)
          next.delete(question.id)
          return next
        })
        if (showBookmarksOnly) {
          setQuestionsPage((prev) => ({ ...prev, content: prev.content.filter((q) => q.id !== question.id) }))
        }
      } else {
        await bookmarkAiQuestion(question.id)
        setBookmarkedIds((prev) => new Set(prev).add(question.id))
      }
    } catch (err) {
      setToast({ severity: 'error', message: err.message })
    }
  }

  const handleCopyAnswer = async (detail, e) => {
    e.stopPropagation()
    const text = `${detail.title}\n\n${detail.detailedAnswer}${detail.realWorldExample ? `\n\nReal-world example: ${detail.realWorldExample}` : ''}`
    await navigator.clipboard.writeText(text)
    setToast({ severity: 'success', message: 'Answer copied to clipboard' })
  }

  const selectedCategoryName = useMemo(
    () => categories.find((c) => c.id === selectedCategoryId)?.name,
    [categories, selectedCategoryId],
  )

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 1 }}>
        AI Technical Questions and Answers
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 3 }}>
        Study detailed, real-world explanations for common technical interview topics.
      </Typography>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 3 }}>
          <Paper variant="outlined" sx={{ p: 2 }}>
            <TextField
              fullWidth
              size="small"
              placeholder="Search questions…"
              value={searchInput}
              onChange={handleSearchChange}
              slotProps={{ input: { startAdornment: <InputAdornment position="start"><SearchIcon fontSize="small" /></InputAdornment> } }}
              sx={{ mb: 2 }}
            />

            <FormControl fullWidth size="small" sx={{ mb: 2 }}>
              <InputLabel id="difficulty-filter-label">Difficulty</InputLabel>
              <Select
                labelId="difficulty-filter-label"
                label="Difficulty"
                value={difficulty}
                onChange={(e) => {
                  setDifficulty(e.target.value)
                  setPage(0)
                }}
              >
                <MenuItem value="">All difficulties</MenuItem>
                {DIFFICULTIES.map((d) => (
                  <MenuItem key={d} value={d}>
                    {d}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <Divider sx={{ mb: 1 }} />
            <Typography variant="subtitle2" sx={{ mb: 1 }}>
              Categories
            </Typography>
            {categoriesLoading ? (
              <CircularProgress size={20} />
            ) : (
              <List dense disablePadding>
                <ListItemButton
                  selected={!showBookmarksOnly && selectedCategoryId == null}
                  onClick={() => handleSelectCategory(null)}
                  sx={{ borderRadius: 1 }}
                >
                  <ListItemText primary="All categories" />
                </ListItemButton>
                {categories.map((category) => (
                  <ListItemButton
                    key={category.id}
                    selected={!showBookmarksOnly && selectedCategoryId === category.id}
                    onClick={() => handleSelectCategory(category.id)}
                    sx={{ borderRadius: 1 }}
                  >
                    <ListItemText primary={category.name} />
                    <Chip size="small" label={category.questionCount} variant="outlined" />
                  </ListItemButton>
                ))}
                <Divider sx={{ my: 1 }} />
                <ListItemButton selected={showBookmarksOnly} onClick={handleToggleBookmarksOnly} sx={{ borderRadius: 1 }}>
                  <BookmarkIcon fontSize="small" sx={{ mr: 1 }} color={showBookmarksOnly ? 'primary' : 'action'} />
                  <ListItemText primary="My bookmarks" />
                </ListItemButton>
              </List>
            )}

            {recentlyViewed.length > 0 && (
              <>
                <Divider sx={{ my: 1.5 }} />
                <Typography variant="subtitle2" sx={{ mb: 1 }}>
                  Recently viewed
                </Typography>
                <List dense disablePadding>
                  {recentlyViewed.map((item) => (
                    <ListItemButton
                      key={item.id}
                      sx={{ borderRadius: 1 }}
                      onClick={() => {
                        setShowBookmarksOnly(false)
                        setSelectedCategoryId(null)
                        setSearch('')
                        setSearchInput('')
                        setExpandedId(item.id)
                        if (!detailsById[item.id]) {
                          getAiQuestion(item.id)
                            .then((detail) => setDetailsById((prev) => ({ ...prev, [item.id]: detail })))
                            .catch((err) => setToast({ severity: 'error', message: err.message }))
                        }
                      }}
                    >
                      <ListItemText
                        primaryTypographyProps={{ variant: 'body2', noWrap: true }}
                        primary={item.title}
                      />
                    </ListItemButton>
                  ))}
                </List>
              </>
            )}
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, md: 9 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2, flexWrap: 'wrap', gap: 1 }}>
            <Typography variant="subtitle1" color="text.secondary">
              {showBookmarksOnly ? 'My bookmarks' : selectedCategoryName || 'All categories'}
              {questionsPage ? ` — ${questionsPage.totalElements} question${questionsPage.totalElements === 1 ? '' : 's'}` : ''}
            </Typography>
            <FormControl size="small" sx={{ minWidth: 180 }}>
              <InputLabel id="sort-label">Sort by</InputLabel>
              <Select labelId="sort-label" label="Sort by" value={sort} onChange={(e) => setSort(e.target.value)}>
                {SORT_OPTIONS.map((opt) => (
                  <MenuItem key={opt.value} value={opt.value}>
                    {opt.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>

          {loadingQuestions ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
              <CircularProgress size={28} />
            </Box>
          ) : questionsPage?.content?.length === 0 ? (
            <Alert severity="info">No questions match your current filters.</Alert>
          ) : (
            <>
              {questionsPage?.content?.map((question) => {
                const detail = detailsById[question.id]
                const isBookmarked = bookmarkedIds.has(question.id)
                return (
                  <Accordion
                    key={question.id}
                    expanded={expandedId === question.id}
                    onChange={handleToggleExpand(question)}
                    sx={{ mb: 1 }}
                  >
                    <AccordionSummary expandIcon={<ExpandMoreIcon titleAccess="Expand" />}>
                      <Box sx={{ display: 'flex', alignItems: 'center', width: '100%', gap: 1, flexWrap: 'wrap' }}>
                        <Typography sx={{ flexGrow: 1, fontWeight: 600 }}>{question.title}</Typography>
                        <Chip size="small" label={question.difficultyLevel} />
                        {question.categories.map((cat) => (
                          <Chip key={cat.id} size="small" variant="outlined" label={cat.name} />
                        ))}
                        <Button
                          size="small"
                          variant="outlined"
                          startIcon={<AutoAwesomeIcon fontSize="small" />}
                          onClick={(e) => handleAiExplanation(question, e)}
                        >
                          AI Explanation
                        </Button>
                        <IconButton size="small" onClick={(e) => handleToggleBookmark(question, e)} title="Bookmark">
                          {isBookmarked ? <BookmarkIcon color="primary" fontSize="small" /> : <BookmarkBorderIcon fontSize="small" />}
                        </IconButton>
                      </Box>
                    </AccordionSummary>
                    <AccordionDetails>
                      {loadingDetailId === question.id ? (
                        <Box sx={{ display: 'flex', justifyContent: 'center', py: 2 }}>
                          <CircularProgress size={22} />
                        </Box>
                      ) : detail ? (
                        <Box>
                          <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 0.5 }}>
                            Detailed answer
                          </Typography>
                          <Typography sx={{ mb: 2, whiteSpace: 'pre-line' }}>{detail.detailedAnswer}</Typography>
                          {detail.realWorldExample && (
                            <>
                              <Typography variant="subtitle2" sx={{ fontWeight: 700, mb: 0.5 }}>
                                Real-world example
                              </Typography>
                              <Typography sx={{ whiteSpace: 'pre-line' }}>{detail.realWorldExample}</Typography>
                            </>
                          )}
                          <Button
                            size="small"
                            startIcon={<ContentCopyIcon fontSize="small" />}
                            onClick={(e) => handleCopyAnswer(detail, e)}
                            sx={{ mt: 2 }}
                          >
                            Copy answer
                          </Button>
                        </Box>
                      ) : (
                        <Button
                          startIcon={<AutoAwesomeIcon />}
                          variant="outlined"
                          onClick={(e) => handleAiExplanation(question, e)}
                        >
                          AI Explanation
                        </Button>
                      )}
                    </AccordionDetails>
                  </Accordion>
                )
              })}

              {questionsPage && questionsPage.totalPages > 1 && (
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
                  <Pagination count={questionsPage.totalPages} page={page + 1} onChange={(_, value) => setPage(value - 1)} />
                </Box>
              )}
            </>
          )}
        </Grid>
      </Grid>

      <Snackbar open={Boolean(toast)} autoHideDuration={3500} onClose={() => setToast(null)}>
        {toast && <Alert severity={toast.severity}>{toast.message}</Alert>}
      </Snackbar>
    </Box>
  )
}
