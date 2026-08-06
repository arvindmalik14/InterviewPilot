import { useEffect, useState } from 'react'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Tabs from '@mui/material/Tabs'
import Tab from '@mui/material/Tab'
import Paper from '@mui/material/Paper'
import Table from '@mui/material/Table'
import TableBody from '@mui/material/TableBody'
import TableCell from '@mui/material/TableCell'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import Button from '@mui/material/Button'
import IconButton from '@mui/material/IconButton'
import Dialog from '@mui/material/Dialog'
import DialogTitle from '@mui/material/DialogTitle'
import DialogContent from '@mui/material/DialogContent'
import DialogActions from '@mui/material/DialogActions'
import TextField from '@mui/material/TextField'
import MenuItem from '@mui/material/MenuItem'
import Alert from '@mui/material/Alert'
import Snackbar from '@mui/material/Snackbar'
import AddIcon from '@mui/icons-material/Add'
import EditIcon from '@mui/icons-material/Edit'
import DeleteIcon from '@mui/icons-material/Delete'
import { listExams } from '../api/exams.js'
import * as adminApi from '../api/admin.js'

const emptyExam = { name: '', category: '', description: '' }
const emptyQuestion = {
  examId: '',
  question: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  answer: 'A',
  explanation: '',
  difficulty: 'MEDIUM',
}

function ExamDialog({ open, initial, onClose, onSave }) {
  const [form, setForm] = useState(emptyExam)

  useEffect(() => {
    setForm(initial || emptyExam)
  }, [initial, open])

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>{initial ? 'Edit exam' : 'New exam'}</DialogTitle>
      <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
        <TextField label="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <TextField
          label="Category"
          value={form.category}
          onChange={(e) => setForm({ ...form, category: e.target.value })}
        />
        <TextField
          label="Description"
          multiline
          minRows={2}
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button variant="contained" onClick={() => onSave(form)}>
          Save
        </Button>
      </DialogActions>
    </Dialog>
  )
}

function QuestionDialog({ open, initial, examId, onClose, onSave }) {
  const [form, setForm] = useState(emptyQuestion)

  useEffect(() => {
    setForm(initial ? { ...initial } : { ...emptyQuestion, examId })
  }, [initial, examId, open])

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <DialogTitle>{initial ? 'Edit question' : 'New question'}</DialogTitle>
      <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
        <TextField
          label="Question"
          multiline
          minRows={2}
          value={form.question}
          onChange={(e) => setForm({ ...form, question: e.target.value })}
        />
        <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
          <TextField label="Option A" value={form.optionA} onChange={(e) => setForm({ ...form, optionA: e.target.value })} />
          <TextField label="Option B" value={form.optionB} onChange={(e) => setForm({ ...form, optionB: e.target.value })} />
          <TextField label="Option C" value={form.optionC} onChange={(e) => setForm({ ...form, optionC: e.target.value })} />
          <TextField label="Option D" value={form.optionD} onChange={(e) => setForm({ ...form, optionD: e.target.value })} />
        </Box>
        <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
          <TextField
            select
            label="Correct answer"
            value={form.answer}
            onChange={(e) => setForm({ ...form, answer: e.target.value })}
          >
            {['A', 'B', 'C', 'D'].map((opt) => (
              <MenuItem key={opt} value={opt}>
                {opt}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            select
            label="Difficulty"
            value={form.difficulty}
            onChange={(e) => setForm({ ...form, difficulty: e.target.value })}
          >
            {['EASY', 'MEDIUM', 'HARD'].map((opt) => (
              <MenuItem key={opt} value={opt}>
                {opt}
              </MenuItem>
            ))}
          </TextField>
        </Box>
        <TextField
          label="Explanation"
          multiline
          minRows={2}
          value={form.explanation}
          onChange={(e) => setForm({ ...form, explanation: e.target.value })}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button variant="contained" onClick={() => onSave(form)}>
          Save
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export function AdminPage() {
  const [tab, setTab] = useState(0)
  const [exams, setExams] = useState([])
  const [selectedExamId, setSelectedExamId] = useState(null)
  const [questions, setQuestions] = useState([])
  const [examDialog, setExamDialog] = useState(null) // null | 'new' | exam object
  const [questionDialog, setQuestionDialog] = useState(null)
  const [toast, setToast] = useState(null)

  const reloadExams = () =>
    listExams().then((data) => {
      setExams(data)
      if (!selectedExamId && data.length > 0) setSelectedExamId(data[0].id)
    })

  const reloadQuestions = (examId) => {
    if (!examId) return
    adminApi.listAdminQuestions(examId, 0, 100).then((page) => setQuestions(page.content))
  }

  useEffect(() => {
    reloadExams()
  }, [])

  useEffect(() => {
    reloadQuestions(selectedExamId)
  }, [selectedExamId])

  const notify = (message, severity = 'success') => setToast({ message, severity })

  const handleSaveExam = async (form) => {
    try {
      if (form.id) {
        await adminApi.updateExam(form.id, form)
      } else {
        await adminApi.createExam(form)
      }
      setExamDialog(null)
      await reloadExams()
      notify('Exam saved')
    } catch (err) {
      notify(err.message, 'error')
    }
  }

  const handleDeleteExam = async (exam) => {
    if (!window.confirm(`Delete exam "${exam.name}" and all its questions?`)) return
    try {
      await adminApi.deleteExam(exam.id)
      if (selectedExamId === exam.id) setSelectedExamId(null)
      await reloadExams()
      notify('Exam deleted')
    } catch (err) {
      notify(err.message, 'error')
    }
  }

  const handleSaveQuestion = async (form) => {
    try {
      const payload = { ...form, examId: Number(form.examId) }
      if (form.id) {
        await adminApi.updateQuestion(form.id, payload)
      } else {
        await adminApi.createQuestion(payload)
      }
      setQuestionDialog(null)
      reloadQuestions(selectedExamId)
      notify('Question saved')
    } catch (err) {
      notify(err.message, 'error')
    }
  }

  const handleDeleteQuestion = async (question) => {
    if (!window.confirm('Delete this question?')) return
    try {
      await adminApi.deleteQuestion(question.id)
      reloadQuestions(selectedExamId)
      notify('Question deleted')
    } catch (err) {
      notify(err.message, 'error')
    }
  }

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 3 }}>
        Admin panel
      </Typography>

      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 3 }}>
        <Tab label="Exams" />
        <Tab label="Questions" />
      </Tabs>

      {tab === 0 && (
        <>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
            <Button startIcon={<AddIcon />} variant="contained" onClick={() => setExamDialog('new')}>
              New exam
            </Button>
          </Box>
          <Paper>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Name</TableCell>
                  <TableCell>Category</TableCell>
                  <TableCell align="right">Questions</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {exams.map((exam) => (
                  <TableRow key={exam.id}>
                    <TableCell>{exam.name}</TableCell>
                    <TableCell>{exam.category}</TableCell>
                    <TableCell align="right">{exam.questionCount}</TableCell>
                    <TableCell align="right">
                      <IconButton size="small" onClick={() => setExamDialog(exam)}>
                        <EditIcon fontSize="small" />
                      </IconButton>
                      <IconButton size="small" onClick={() => handleDeleteExam(exam)}>
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Paper>
        </>
      )}

      {tab === 1 && (
        <>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2, gap: 2 }}>
            <TextField
              select
              label="Exam"
              value={selectedExamId || ''}
              onChange={(e) => setSelectedExamId(Number(e.target.value))}
              sx={{ minWidth: 240 }}
            >
              {exams.map((exam) => (
                <MenuItem key={exam.id} value={exam.id}>
                  {exam.name}
                </MenuItem>
              ))}
            </TextField>
            <Button
              startIcon={<AddIcon />}
              variant="contained"
              disabled={!selectedExamId}
              onClick={() => setQuestionDialog('new')}
            >
              New question
            </Button>
          </Box>

          <Paper>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Question</TableCell>
                  <TableCell>Answer</TableCell>
                  <TableCell>Difficulty</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {questions.map((q) => (
                  <TableRow key={q.id}>
                    <TableCell sx={{ maxWidth: 480 }}>{q.question}</TableCell>
                    <TableCell>{q.answer}</TableCell>
                    <TableCell>{q.difficulty}</TableCell>
                    <TableCell align="right">
                      <IconButton size="small" onClick={() => setQuestionDialog(q)}>
                        <EditIcon fontSize="small" />
                      </IconButton>
                      <IconButton size="small" onClick={() => handleDeleteQuestion(q)}>
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Paper>
        </>
      )}

      <ExamDialog
        open={Boolean(examDialog)}
        initial={examDialog === 'new' ? null : examDialog}
        onClose={() => setExamDialog(null)}
        onSave={handleSaveExam}
      />

      <QuestionDialog
        open={Boolean(questionDialog)}
        initial={questionDialog === 'new' ? null : questionDialog}
        examId={selectedExamId}
        onClose={() => setQuestionDialog(null)}
        onSave={handleSaveQuestion}
      />

      <Snackbar open={Boolean(toast)} autoHideDuration={3500} onClose={() => setToast(null)}>
        {toast && <Alert severity={toast.severity}>{toast.message}</Alert>}
      </Snackbar>
    </Box>
  )
}
