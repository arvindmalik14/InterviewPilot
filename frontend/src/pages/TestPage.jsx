import { useEffect, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Paper from '@mui/material/Paper'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import CircularProgress from '@mui/material/CircularProgress'
import Radio from '@mui/material/Radio'
import RadioGroup from '@mui/material/RadioGroup'
import FormControlLabel from '@mui/material/FormControlLabel'
import Alert from '@mui/material/Alert'
import Dialog from '@mui/material/Dialog'
import DialogTitle from '@mui/material/DialogTitle'
import DialogContent from '@mui/material/DialogContent'
import DialogActions from '@mui/material/DialogActions'
import { startTest, submitTest, stopTest } from '../api/tests.js'

function formatDuration(totalSeconds) {
  const m = Math.floor(totalSeconds / 60)
  const s = totalSeconds % 60
  return `${m}:${String(s).padStart(2, '0')}`
}

export function TestPage() {
  const { examId } = useParams()
  const navigate = useNavigate()

  const [attempt, setAttempt] = useState(null)
  const [answers, setAnswers] = useState({})
  const [currentIndex, setCurrentIndex] = useState(0)
  const [elapsed, setElapsed] = useState(0)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [confirmOpen, setConfirmOpen] = useState(false)
  const [stopConfirmOpen, setStopConfirmOpen] = useState(false)
  const [stopping, setStopping] = useState(false)
  const timerRef = useRef(null)

  useEffect(() => {
    startTest(Number(examId), 10)
      .then((data) => setAttempt(data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [examId])

  useEffect(() => {
    if (!attempt) return
    timerRef.current = setInterval(() => setElapsed((e) => e + 1), 1000)
    return () => clearInterval(timerRef.current)
  }, [attempt])

  const handleSelect = (questionId, option) => {
    setAnswers((prev) => ({ ...prev, [questionId]: option }))
  }

  const handleSubmit = async () => {
    setConfirmOpen(false)
    setSubmitting(true)
    clearInterval(timerRef.current)
    try {
      const payload = attempt.questions.map((q) => ({ questionId: q.id, selectedOption: answers[q.id] || null }))
      const result = await submitTest(attempt.id, elapsed, payload)
      navigate(`/results/${attempt.id}`, { state: { result } })
    } catch (err) {
      setError(err.message)
      setSubmitting(false)
    }
  }

  const handleStopTest = async () => {
    setStopConfirmOpen(false)
    setStopping(true)
    clearInterval(timerRef.current)
    try {
      await stopTest(attempt.id, elapsed)
      navigate('/dashboard', { replace: true })
    } catch (err) {
      setError(err.message)
      setStopping(false)
    }
  }

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress />
      </Box>
    )
  }

  if (error && !attempt) {
    return <Alert severity="error">{error}</Alert>
  }

  const question = attempt.questions[currentIndex]
  const answeredCount = Object.keys(answers).length

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h5" sx={{ fontWeight: 700 }}>
          {attempt.examName} — Mock Test
        </Typography>
        <Chip label={`⏱ ${formatDuration(elapsed)}`} color="primary" />
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Box sx={{ display: 'flex', gap: 1, mb: 3, flexWrap: 'wrap' }}>
        {attempt.questions.map((q, idx) => (
          <Chip
            key={q.id}
            label={idx + 1}
            onClick={() => setCurrentIndex(idx)}
            color={idx === currentIndex ? 'primary' : answers[q.id] ? 'success' : 'default'}
            variant={idx === currentIndex ? 'filled' : 'outlined'}
          />
        ))}
      </Box>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Typography variant="caption" color="text.secondary">
          Question {currentIndex + 1} of {attempt.questions.length}
        </Typography>
        <Typography variant="h6" sx={{ mt: 1, mb: 2 }}>
          {question.question}
        </Typography>

        <RadioGroup value={answers[question.id] || ''} onChange={(e) => handleSelect(question.id, e.target.value)}>
          {[
            ['A', question.optionA],
            ['B', question.optionB],
            ['C', question.optionC],
            ['D', question.optionD],
          ].map(([key, text]) => (
            <FormControlLabel
              key={key}
              value={key}
              control={<Radio />}
              label={`${key}. ${text}`}
              sx={{ border: '1px solid #e5e7eb', borderRadius: 1, mb: 1, mx: 0, px: 1 }}
            />
          ))}
        </RadioGroup>
      </Paper>

      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 1 }}>
        <Button disabled={currentIndex === 0} onClick={() => setCurrentIndex((i) => i - 1)}>
          Previous
        </Button>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Typography variant="body2" color="text.secondary">
            {answeredCount}/{attempt.questions.length} answered
          </Typography>
          <Button color="error" variant="outlined" disabled={stopping} onClick={() => setStopConfirmOpen(true)}>
            {stopping ? 'Stopping…' : 'Stop Test'}
          </Button>
        </Box>

        {currentIndex < attempt.questions.length - 1 ? (
          <Button variant="contained" onClick={() => setCurrentIndex((i) => i + 1)}>
            Next
          </Button>
        ) : (
          <Button variant="contained" color="success" disabled={submitting} onClick={() => setConfirmOpen(true)}>
            {submitting ? 'Submitting…' : 'Submit test'}
          </Button>
        )}
      </Box>

      <Dialog open={confirmOpen} onClose={() => setConfirmOpen(false)}>
        <DialogTitle>Submit test?</DialogTitle>
        <DialogContent>
          <Typography>
            You've answered {answeredCount} of {attempt.questions.length} questions. You can't change answers after
            submitting.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmOpen(false)}>Keep reviewing</Button>
          <Button variant="contained" onClick={handleSubmit}>
            Submit
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={stopConfirmOpen} onClose={() => setStopConfirmOpen(false)}>
        <DialogTitle>Stop test?</DialogTitle>
        <DialogContent>
          <Typography>
            You've answered {answeredCount} of {attempt.questions.length} questions. Stopping now will end this
            attempt and mark it as incomplete in your history — you won't get a score for it.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setStopConfirmOpen(false)}>Keep going</Button>
          <Button variant="contained" color="error" onClick={handleStopTest}>
            Stop test
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}
