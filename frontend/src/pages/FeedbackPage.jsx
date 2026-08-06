import { useState } from 'react'
import Box from '@mui/material/Box'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'
import TextField from '@mui/material/TextField'
import Select from '@mui/material/Select'
import MenuItem from '@mui/material/MenuItem'
import FormControl from '@mui/material/FormControl'
import InputLabel from '@mui/material/InputLabel'
import Rating from '@mui/material/Rating'
import Button from '@mui/material/Button'
import Alert from '@mui/material/Alert'
import RateReviewIcon from '@mui/icons-material/RateReview'
import { useAuth } from '../context/AuthContext.jsx'
import { submitFeedback } from '../api/feedback.js'

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const CATEGORIES = ['Bug Report', 'Feature Request', 'General Feedback', 'Compliment']

const emptyErrors = { name: '', email: '', category: '', rating: '', message: '' }

export function FeedbackPage() {
  const { user } = useAuth()
  const [form, setForm] = useState({
    name: user?.name || '',
    email: user?.email || '',
    category: '',
    rating: 0,
    message: '',
  })
  const [errors, setErrors] = useState(emptyErrors)
  const [submitting, setSubmitting] = useState(false)
  const [result, setResult] = useState(null) // { severity, message }

  const setField = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  const validate = () => {
    const next = { ...emptyErrors }
    if (!form.name.trim()) next.name = 'Name is required'
    if (!form.email.trim()) next.email = 'Email is required'
    else if (!EMAIL_PATTERN.test(form.email)) next.email = 'Enter a valid email address'
    if (!form.category) next.category = 'Please select a category'
    if (!form.rating) next.rating = 'Please rate your experience'
    if (!form.message.trim()) next.message = 'Message is required'
    setErrors(next)
    return Object.values(next).every((v) => !v)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setResult(null)
    if (!validate()) return

    setSubmitting(true)
    try {
      await submitFeedback(form)
      setResult({ severity: 'success', message: 'Thank you for your feedback! Our team will take a look shortly.' })
      setForm({ name: user?.name || '', email: user?.email || '', category: '', rating: 0, message: '' })
      setErrors(emptyErrors)
    } catch (err) {
      setResult({ severity: 'error', message: err.message })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Box sx={{ display: 'flex', justifyContent: 'center', mt: { xs: 2, md: 6 } }}>
      <Paper sx={{ width: '100%', maxWidth: 520, overflow: 'hidden' }} elevation={3}>
        <Box
          sx={{
            p: 4,
            pb: 3,
            background: 'linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%)',
            color: 'common.white',
            textAlign: 'center',
          }}
        >
          <RateReviewIcon sx={{ fontSize: 40, mb: 1 }} />
          <Typography variant="h5" sx={{ fontWeight: 700 }}>
            We'd love your feedback
          </Typography>
          <Typography variant="body2" sx={{ opacity: 0.9, mt: 0.5 }}>
            Tell us what's working, what isn't, and what you'd like to see next.
          </Typography>
        </Box>

        <Box sx={{ p: 4 }}>
          {result && (
            <Alert severity={result.severity} sx={{ mb: 2 }}>
              {result.message}
            </Alert>
          )}

          <Box component="form" noValidate onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField
              label="Your name"
              value={form.name}
              onChange={setField('name')}
              error={Boolean(errors.name)}
              helperText={errors.name}
              fullWidth
            />
            <TextField
              label="Email"
              type="email"
              value={form.email}
              onChange={setField('email')}
              error={Boolean(errors.email)}
              helperText={errors.email}
              fullWidth
            />
            <FormControl fullWidth error={Boolean(errors.category)}>
              <InputLabel id="feedback-category-label">Category</InputLabel>
              <Select
                labelId="feedback-category-label"
                label="Category"
                value={form.category}
                onChange={setField('category')}
              >
                {CATEGORIES.map((c) => (
                  <MenuItem key={c} value={c}>
                    {c}
                  </MenuItem>
                ))}
              </Select>
              {errors.category && (
                <Typography variant="caption" color="error" sx={{ mt: 0.5, ml: 1.75 }}>
                  {errors.category}
                </Typography>
              )}
            </FormControl>

            <Box>
              <Typography component="legend" variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                How would you rate your experience?
              </Typography>
              <Rating
                value={form.rating}
                onChange={(_, value) => setForm({ ...form, rating: value })}
                size="large"
              />
              {errors.rating && (
                <Typography variant="caption" color="error" sx={{ display: 'block', mt: 0.5 }}>
                  {errors.rating}
                </Typography>
              )}
            </Box>

            <TextField
              label="Your feedback"
              multiline
              minRows={4}
              value={form.message}
              onChange={setField('message')}
              error={Boolean(errors.message)}
              helperText={errors.message}
              fullWidth
            />

            <Button type="submit" variant="contained" size="large" disabled={submitting}>
              {submitting ? 'Sending…' : 'Send feedback'}
            </Button>
          </Box>
        </Box>
      </Paper>
    </Box>
  )
}
