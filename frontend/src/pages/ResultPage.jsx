import { useState } from 'react'
import { Link as RouterLink, useLocation, useParams, Navigate } from 'react-router-dom'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Paper from '@mui/material/Paper'
import Chip from '@mui/material/Chip'
import Grid from '@mui/material/Grid2'
import Button from '@mui/material/Button'
import Divider from '@mui/material/Divider'
import Alert from '@mui/material/Alert'
import CircularProgress from '@mui/material/CircularProgress'
import CheckCircleIcon from '@mui/icons-material/CheckCircle'
import CancelIcon from '@mui/icons-material/Cancel'
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome'
import { explainQuestion } from '../api/ai.js'

function ScoreRing({ score }) {
  const color = score >= 70 ? 'success.main' : score >= 40 ? 'warning.main' : 'error.main'
  return (
    <Box
      sx={{
        width: 120,
        height: 120,
        borderRadius: '50%',
        border: '8px solid',
        borderColor: color,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        mx: 'auto',
      }}
    >
      <Typography variant="h4" sx={{ fontWeight: 800 }}>
        {score}%
      </Typography>
    </Box>
  )
}

function AnswerRow({ answer, index }) {
  const [aiExplanation, setAiExplanation] = useState(null)
  const [loadingAi, setLoadingAi] = useState(false)

  const handleAiExplain = async () => {
    setLoadingAi(true)
    try {
      const data = await explainQuestion(answer.questionId)
      setAiExplanation(data.explanation)
    } catch {
      setAiExplanation('Could not load AI explanation right now.')
    } finally {
      setLoadingAi(false)
    }
  }

  return (
    <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
      <Box sx={{ display: 'flex', alignItems: 'flex-start', gap: 1 }}>
        {answer.correct ? (
          <CheckCircleIcon color="success" sx={{ mt: 0.3 }} />
        ) : (
          <CancelIcon color="error" sx={{ mt: 0.3 }} />
        )}
        <Box sx={{ flexGrow: 1 }}>
          <Typography sx={{ fontWeight: 600 }}>
            {index + 1}. {answer.question}
          </Typography>
          <Typography variant="body2" sx={{ mt: 0.5 }}>
            Your answer: <b>{answer.selectedOption || '—'}</b>
            {!answer.correct && (
              <>
                {' '}
                &nbsp;·&nbsp; Correct answer: <b>{answer.correctOption}</b>
              </>
            )}
          </Typography>

          {answer.explanation && (
            <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
              {answer.explanation}
            </Typography>
          )}

          {!answer.correct && (
            <Box sx={{ mt: 1 }}>
              {aiExplanation ? (
                <Alert severity="info" icon={<AutoAwesomeIcon fontSize="small" />}>
                  {aiExplanation}
                </Alert>
              ) : (
                <Button
                  size="small"
                  startIcon={<AutoAwesomeIcon />}
                  onClick={handleAiExplain}
                  disabled={loadingAi}
                >
                  {loadingAi ? 'Asking AI…' : 'Get AI explanation'}
                </Button>
              )}
            </Box>
          )}
        </Box>
      </Box>
    </Paper>
  )
}

export function ResultPage() {
  const location = useLocation()
  const { testId } = useParams()
  const result = location.state?.result

  if (!result) {
    // Direct navigation/refresh without state — send back rather than show a broken page.
    return <Navigate to="/dashboard" replace />
  }

  return (
    <Box>
      <Paper sx={{ p: 4, mb: 4, textAlign: 'center' }}>
        <Typography variant="overline" color="text.secondary">
          Result — {result.examName}
        </Typography>
        <ScoreRing score={result.score} />
        <Grid container spacing={2} sx={{ mt: 2, maxWidth: 420, mx: 'auto' }}>
          <Grid size={4}>
            <Typography variant="h6">{result.correctCount}</Typography>
            <Typography variant="caption" color="text.secondary">
              Correct
            </Typography>
          </Grid>
          <Grid size={4}>
            <Typography variant="h6">{result.totalQuestions - result.correctCount}</Typography>
            <Typography variant="caption" color="text.secondary">
              Incorrect
            </Typography>
          </Grid>
          <Grid size={4}>
            <Typography variant="h6">{result.durationSeconds}s</Typography>
            <Typography variant="caption" color="text.secondary">
              Time taken
            </Typography>
          </Grid>
        </Grid>
        <Box sx={{ mt: 3, display: 'flex', gap: 2, justifyContent: 'center' }}>
          <Button component={RouterLink} to="/dashboard" variant="outlined">
            Back to dashboard
          </Button>
        </Box>
      </Paper>

      <Divider sx={{ mb: 3 }}>
        <Chip label="Review answers" />
      </Divider>

      {result.answers.map((answer, idx) => (
        <AnswerRow key={answer.questionId} answer={answer} index={idx} />
      ))}
    </Box>
  )
}
