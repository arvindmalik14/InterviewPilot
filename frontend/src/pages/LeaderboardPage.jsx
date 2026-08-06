import { useEffect, useState } from 'react'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Paper from '@mui/material/Paper'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Chip from '@mui/material/Chip'
import CircularProgress from '@mui/material/CircularProgress'
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents'
import { getLeaderboard } from '../api/leaderboard.js'

const medalColors = ['#facc15', '#cbd5e1', '#fb923c']

export function LeaderboardPage() {
  const [entries, setEntries] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getLeaderboard()
      .then(setEntries)
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress />
      </Box>
    )
  }

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 3 }}>
        Leaderboard
      </Typography>

      <Paper>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell width={80}>Rank</TableCell>
              <TableCell>Candidate</TableCell>
              <TableCell>Exam</TableCell>
              <TableCell align="right">Score</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {entries.length === 0 && (
              <TableRow>
                <TableCell colSpan={4} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                  No completed tests yet — be the first on the board.
                </TableCell>
              </TableRow>
            )}
            {entries.map((entry) => (
              <TableRow key={`${entry.rank}-${entry.userName}-${entry.examName}`}>
                <TableCell>
                  {entry.rank <= 3 ? (
                    <EmojiEventsIcon sx={{ color: medalColors[entry.rank - 1] }} />
                  ) : (
                    entry.rank
                  )}
                </TableCell>
                <TableCell>{entry.userName}</TableCell>
                <TableCell>
                  <Chip label={entry.examName} size="small" variant="outlined" />
                </TableCell>
                <TableCell align="right">{entry.score}%</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    </Box>
  )
}
