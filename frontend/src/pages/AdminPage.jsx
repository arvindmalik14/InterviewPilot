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
import Chip from '@mui/material/Chip'
import Checkbox from '@mui/material/Checkbox'
import ListItemText from '@mui/material/ListItemText'
import AddIcon from '@mui/icons-material/Add'
import EditIcon from '@mui/icons-material/Edit'
import DeleteIcon from '@mui/icons-material/Delete'
import { listExams } from '../api/exams.js'
import { listPlans } from '../api/razorpay.js'
import * as adminApi from '../api/admin.js'

const emptyAiCategory = { name: '', description: '', status: 'ACTIVE' }
const emptyAiQuestion = {
  title: '',
  detailedAnswer: '',
  realWorldExample: '',
  difficultyLevel: 'MEDIUM',
  status: 'ACTIVE',
  categoryIds: [],
}

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

function AiCategoryDialog({ open, initial, onClose, onSave }) {
  const [form, setForm] = useState(emptyAiCategory)

  useEffect(() => {
    setForm(initial || emptyAiCategory)
  }, [initial, open])

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>{initial ? 'Edit AI category' : 'New AI category'}</DialogTitle>
      <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
        <TextField label="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <TextField
          label="Description"
          multiline
          minRows={2}
          value={form.description || ''}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
        />
        <TextField
          select
          label="Status"
          value={form.status}
          onChange={(e) => setForm({ ...form, status: e.target.value })}
        >
          {['ACTIVE', 'INACTIVE'].map((opt) => (
            <MenuItem key={opt} value={opt}>
              {opt}
            </MenuItem>
          ))}
        </TextField>
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

function AiQuestionDialog({ open, initial, categories, onClose, onSave }) {
  const [form, setForm] = useState(emptyAiQuestion)

  useEffect(() => {
    setForm(initial ? { ...initial, categoryIds: initial.categories?.map((c) => c.id) || [] } : emptyAiQuestion)
  }, [initial, open])

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <DialogTitle>{initial ? 'Edit AI question' : 'New AI question'}</DialogTitle>
      <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
        <TextField label="Title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
        <TextField
          label="Detailed answer"
          multiline
          minRows={3}
          value={form.detailedAnswer}
          onChange={(e) => setForm({ ...form, detailedAnswer: e.target.value })}
        />
        <TextField
          label="Real-world example"
          multiline
          minRows={2}
          value={form.realWorldExample || ''}
          onChange={(e) => setForm({ ...form, realWorldExample: e.target.value })}
        />
        <Box sx={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 2 }}>
          <TextField
            select
            label="Difficulty"
            value={form.difficultyLevel}
            onChange={(e) => setForm({ ...form, difficultyLevel: e.target.value })}
          >
            {['EASY', 'MEDIUM', 'HARD'].map((opt) => (
              <MenuItem key={opt} value={opt}>
                {opt}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            select
            label="Status"
            value={form.status}
            onChange={(e) => setForm({ ...form, status: e.target.value })}
          >
            {['ACTIVE', 'INACTIVE'].map((opt) => (
              <MenuItem key={opt} value={opt}>
                {opt}
              </MenuItem>
            ))}
          </TextField>
        </Box>
        <TextField
          select
          label="Categories"
          value={form.categoryIds}
          onChange={(e) => setForm({ ...form, categoryIds: e.target.value })}
          slotProps={{ select: { multiple: true, renderValue: (selected) => categories.filter((c) => selected.includes(c.id)).map((c) => c.name).join(', ') } }}
        >
          {categories.map((category) => (
            <MenuItem key={category.id} value={category.id}>
              <Checkbox checked={form.categoryIds.includes(category.id)} size="small" />
              <ListItemText primary={category.name} />
            </MenuItem>
          ))}
        </TextField>
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

  const [aiCategories, setAiCategories] = useState([])
  const [aiQuestions, setAiQuestions] = useState([])
  const [aiCategoryDialog, setAiCategoryDialog] = useState(null)
  const [aiQuestionDialog, setAiQuestionDialog] = useState(null)
  const [plans, setPlans] = useState([])
  const [planAssignments, setPlanAssignments] = useState({}) // { [questionId]: Set<planId> }

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

  const reloadAiCategories = () => adminApi.listAdminAiCategories().then(setAiCategories)

  const reloadAiQuestions = () =>
    adminApi.listAdminAiQuestions(0, 200).then((page) => setAiQuestions(page.content))

  const reloadPlanAssignments = (planList) =>
    Promise.all(
      planList.map((plan) =>
        adminApi.listAiQuestionsForPlan(plan.planId).then((page) => ({ planId: plan.planId, questionIds: page.content.map((q) => q.id) })),
      ),
    ).then((results) => {
      const map = {}
      results.forEach(({ planId, questionIds }) => {
        questionIds.forEach((qId) => {
          if (!map[qId]) map[qId] = new Set()
          map[qId].add(planId)
        })
      })
      setPlanAssignments(map)
    })

  useEffect(() => {
    if (tab !== 2 && tab !== 3) return
    reloadAiCategories()
    reloadAiQuestions()
    listPlans().then((data) => {
      setPlans(data)
      reloadPlanAssignments(data)
    })
  }, [tab])

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

  const handleSaveAiCategory = async (form) => {
    try {
      if (form.id) {
        await adminApi.updateAiCategory(form.id, form)
      } else {
        await adminApi.createAiCategory(form)
      }
      setAiCategoryDialog(null)
      await reloadAiCategories()
      notify('Category saved')
    } catch (err) {
      notify(err.message, 'error')
    }
  }

  const handleDeleteAiCategory = async (category) => {
    if (!window.confirm(`Delete category "${category.name}"?`)) return
    try {
      await adminApi.deleteAiCategory(category.id)
      await reloadAiCategories()
      notify('Category deleted')
    } catch (err) {
      notify(err.message, 'error')
    }
  }

  const handleSaveAiQuestion = async (form) => {
    try {
      const payload = {
        title: form.title,
        detailedAnswer: form.detailedAnswer,
        realWorldExample: form.realWorldExample,
        difficultyLevel: form.difficultyLevel,
        status: form.status,
        categoryIds: form.categoryIds,
      }
      if (form.id) {
        await adminApi.updateAiQuestion(form.id, payload)
      } else {
        await adminApi.createAiQuestion(payload)
      }
      setAiQuestionDialog(null)
      await reloadAiQuestions()
      notify('AI question saved')
    } catch (err) {
      notify(err.message, 'error')
    }
  }

  const handleDeleteAiQuestion = async (question) => {
    if (!window.confirm('Delete this AI question?')) return
    try {
      await adminApi.deleteAiQuestion(question.id)
      await reloadAiQuestions()
      notify('AI question deleted')
    } catch (err) {
      notify(err.message, 'error')
    }
  }

  const handleToggleAiQuestionStatus = async (question) => {
    try {
      if (question.status === 'ACTIVE') {
        await adminApi.deactivateAiQuestion(question.id)
      } else {
        await adminApi.activateAiQuestion(question.id)
      }
      await reloadAiQuestions()
      notify(question.status === 'ACTIVE' ? 'Question deactivated' : 'Question activated')
    } catch (err) {
      notify(err.message, 'error')
    }
  }

  const handleTogglePlanAssignment = async (question, plan) => {
    const isAssigned = planAssignments[question.id]?.has(plan.planId)
    try {
      if (isAssigned) {
        await adminApi.removeAiQuestionFromPlan(plan.planId, question.id)
      } else {
        await adminApi.assignAiQuestionToPlan(plan.planId, question.id)
      }
      setPlanAssignments((prev) => {
        const next = { ...prev }
        const set = new Set(next[question.id] || [])
        if (isAssigned) set.delete(plan.planId)
        else set.add(plan.planId)
        next[question.id] = set
        return next
      })
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
        <Tab label="AI Categories" />
        <Tab label="AI Questions" />
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

      {tab === 2 && (
        <>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
            <Button startIcon={<AddIcon />} variant="contained" onClick={() => setAiCategoryDialog('new')}>
              New category
            </Button>
          </Box>
          <Paper>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Name</TableCell>
                  <TableCell>Description</TableCell>
                  <TableCell align="right">Questions</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {aiCategories.map((category) => (
                  <TableRow key={category.id}>
                    <TableCell>{category.name}</TableCell>
                    <TableCell sx={{ maxWidth: 360 }}>{category.description}</TableCell>
                    <TableCell align="right">{category.questionCount}</TableCell>
                    <TableCell>
                      <Chip
                        size="small"
                        label={category.status}
                        color={category.status === 'ACTIVE' ? 'success' : 'default'}
                        variant="outlined"
                      />
                    </TableCell>
                    <TableCell align="right">
                      <IconButton size="small" onClick={() => setAiCategoryDialog(category)}>
                        <EditIcon fontSize="small" />
                      </IconButton>
                      <IconButton size="small" onClick={() => handleDeleteAiCategory(category)}>
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

      {tab === 3 && (
        <>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
            <Button
              startIcon={<AddIcon />}
              variant="contained"
              disabled={aiCategories.length === 0}
              onClick={() => setAiQuestionDialog('new')}
            >
              New AI question
            </Button>
          </Box>
          <Paper>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Title</TableCell>
                  <TableCell>Categories</TableCell>
                  <TableCell>Difficulty</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Plans</TableCell>
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {aiQuestions.map((question) => (
                  <TableRow key={question.id}>
                    <TableCell sx={{ maxWidth: 320 }}>{question.title}</TableCell>
                    <TableCell>
                      {question.categories.map((c) => (
                        <Chip key={c.id} size="small" label={c.name} variant="outlined" sx={{ mr: 0.5, mb: 0.5 }} />
                      ))}
                    </TableCell>
                    <TableCell>{question.difficultyLevel}</TableCell>
                    <TableCell>
                      <Chip
                        size="small"
                        label={question.status}
                        color={question.status === 'ACTIVE' ? 'success' : 'default'}
                        variant="outlined"
                        onClick={() => handleToggleAiQuestionStatus(question)}
                      />
                    </TableCell>
                    <TableCell>
                      {plans.map((plan) => (
                        <Chip
                          key={plan.planId}
                          size="small"
                          label={plan.planName}
                          color={planAssignments[question.id]?.has(plan.planId) ? 'primary' : 'default'}
                          variant={planAssignments[question.id]?.has(plan.planId) ? 'filled' : 'outlined'}
                          onClick={() => handleTogglePlanAssignment(question, plan)}
                          sx={{ mr: 0.5, mb: 0.5 }}
                        />
                      ))}
                    </TableCell>
                    <TableCell align="right">
                      <IconButton size="small" onClick={() => setAiQuestionDialog(question)}>
                        <EditIcon fontSize="small" />
                      </IconButton>
                      <IconButton size="small" onClick={() => handleDeleteAiQuestion(question)}>
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

      <AiCategoryDialog
        open={Boolean(aiCategoryDialog)}
        initial={aiCategoryDialog === 'new' ? null : aiCategoryDialog}
        onClose={() => setAiCategoryDialog(null)}
        onSave={handleSaveAiCategory}
      />

      <AiQuestionDialog
        open={Boolean(aiQuestionDialog)}
        initial={aiQuestionDialog === 'new' ? null : aiQuestionDialog}
        categories={aiCategories}
        onClose={() => setAiQuestionDialog(null)}
        onSave={handleSaveAiQuestion}
      />

      <Snackbar open={Boolean(toast)} autoHideDuration={3500} onClose={() => setToast(null)}>
        {toast && <Alert severity={toast.severity}>{toast.message}</Alert>}
      </Snackbar>
    </Box>
  )
}
