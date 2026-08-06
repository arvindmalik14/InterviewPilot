import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Grid from '@mui/material/Grid2'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import Chip from '@mui/material/Chip'
import Pagination from '@mui/material/Pagination'
import CircularProgress from '@mui/material/CircularProgress'
import ToggleButton from '@mui/material/ToggleButton'
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup'
import Button from '@mui/material/Button'
import { listExams, listQuestions } from '../api/exams.js'

export function ExamsPage() {
  const navigate = useNavigate()
  const [exams, setExams] = useState([])
  const [selectedExamId, setSelectedExamId] = useState(null)
  const [questionsPage, setQuestionsPage] = useState(null)
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(true)
  const [loadingQuestions, setLoadingQuestions] = useState(false)

  useEffect(() => {
    listExams()
      .then((data) => {
        setExams(data)
        if (data.length > 0) setSelectedExamId(data[0].id)
      })
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    if (selectedExamId == null) return
    setLoadingQuestions(true)
    listQuestions(selectedExamId, page, 10)
      .then(setQuestionsPage)
      .finally(() => setLoadingQuestions(false))
  }, [selectedExamId, page])

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress />
      </Box>
    )
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 700 }}>
          Question bank
        </Typography>
        {selectedExamId && (
          <Button variant="contained" onClick={() => navigate(`/exams/${selectedExamId}/test`)}>
            Start mock test
          </Button>
        )}
      </Box>

      <ToggleButtonGroup
        value={selectedExamId}
        exclusive
        onChange={(_, value) => {
          if (value !== null) {
            setSelectedExamId(value)
            setPage(0)
          }
        }}
        sx={{ mb: 3, flexWrap: 'wrap' }}
      >
        {exams.map((exam) => (
          <ToggleButton key={exam.id} value={exam.id} sx={{ textTransform: 'none' }}>
            {exam.name}
          </ToggleButton>
        ))}
      </ToggleButtonGroup>

      {loadingQuestions ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <CircularProgress size={28} />
        </Box>
      ) : (
        <>
          <Grid container spacing={2}>
            {questionsPage?.content?.map((question, idx) => (
              <Grid key={question.id} size={12}>
                <Card variant="outlined">
                  <CardContent>
                    <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 1 }}>
                      {page * 10 + idx + 1}. {question.question}
                    </Typography>
                    <Grid container spacing={1}>
                      {['optionA', 'optionB', 'optionC', 'optionD'].map((key, i) => (
                        <Grid key={key} size={{ xs: 12, sm: 6 }}>
                          <Chip
                            label={`${String.fromCharCode(65 + i)}. ${question[key]}`}
                            variant="outlined"
                            sx={{ width: '100%', justifyContent: 'flex-start', height: 'auto', py: 1 }}
                          />
                        </Grid>
                      ))}
                    </Grid>
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>

          {questionsPage && questionsPage.totalPages > 1 && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
              <Pagination
                count={questionsPage.totalPages}
                page={page + 1}
                onChange={(_, value) => setPage(value - 1)}
              />
            </Box>
          )}
        </>
      )}
    </Box>
  )
}
