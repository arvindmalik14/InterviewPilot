import { useEffect, useState } from 'react'
import { Link as RouterLink } from 'react-router-dom'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Grid from '@mui/material/Grid2'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import CardActions from '@mui/material/CardActions'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import Paper from '@mui/material/Paper'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import CircularProgress from '@mui/material/CircularProgress'
import { useAuth } from '../context/AuthContext.jsx'
import { listExams } from '../api/exams.js'
import { getTestHistory } from '../api/tests.js'

export function DashboardPage() {
  const { user, activePlan } = useAuth()
  const [exams, setExams] = useState([])
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([listExams(), getTestHistory()])
      .then(([examData, historyData]) => {
        setExams(examData)
        setHistory(historyData)
      })
      .finally(() => setLoading(false))
  }, [])

  const completed = history.filter((h) => h.status === 'COMPLETED')
  const averageScore = completed.length
    ? Math.round(completed.reduce((sum, h) => sum + (h.score || 0), 0) / completed.length)
    : null

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress />
      </Box>
    )
  }

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 0.5 }}>
        Welcome back, {user.name.split(' ')[0]}
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 4 }}>
        Current plan: <Chip label={activePlan?.planName || '—'} size="small" />
      </Typography>

      <Grid container spacing={2} sx={{ mb: 4 }}>
        <Grid size={{ xs: 12, sm: 4 }}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="overline" color="text.secondary">
              Tests taken
            </Typography>
            <Typography variant="h4">{completed.length}</Typography>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, sm: 4 }}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="overline" color="text.secondary">
              Average score
            </Typography>
            <Typography variant="h4">{averageScore !== null ? `${averageScore}%` : '—'}</Typography>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, sm: 4 }}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="overline" color="text.secondary">
              Exams available
            </Typography>
            <Typography variant="h4">{exams.length}</Typography>
          </Paper>
        </Grid>
      </Grid>

      <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
        Practice exams
      </Typography>
      <Grid container spacing={2} sx={{ mb: 4 }}>
        {exams.map((exam) => (
          <Grid key={exam.id} size={{ xs: 12, sm: 6, md: 3 }}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              <CardContent sx={{ flexGrow: 1 }}>
                <Chip label={exam.category} size="small" color="primary" sx={{ mb: 1 }} />
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  {exam.name}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                  {exam.description}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {exam.questionCount} questions
                </Typography>
              </CardContent>
              <CardActions>
                <Button component={RouterLink} to={`/exams/${exam.id}/test`} fullWidth variant="contained">
                  Start mock test
                </Button>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
        Recent activity
      </Typography>
      <Paper>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Exam</TableCell>
              <TableCell>Status</TableCell>
              <TableCell align="right">Score</TableCell>
              <TableCell align="right">Duration</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {history.length === 0 && (
              <TableRow>
                <TableCell colSpan={4} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                  No tests yet — start one above to see your history here.
                </TableCell>
              </TableRow>
            )}
            {history.map((h) => (
              <TableRow key={h.id}>
                <TableCell>{h.examName}</TableCell>
                <TableCell>
                  <Chip
                    label={h.status}
                    size="small"
                    color={h.status === 'COMPLETED' ? 'success' : 'default'}
                    variant="outlined"
                  />
                </TableCell>
                <TableCell align="right">{h.score !== null ? `${h.score}%` : '—'}</TableCell>
                <TableCell align="right">{h.durationSeconds ? `${h.durationSeconds}s` : '—'}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    </Box>
  )
}
