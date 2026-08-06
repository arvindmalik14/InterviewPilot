import { useState } from 'react'
import { Link as RouterLink, useNavigate } from 'react-router-dom'
import Box from '@mui/material/Box'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Alert from '@mui/material/Alert'
import Link from '@mui/material/Link'
import { useAuth } from '../context/AuthContext.jsx'

const validateMobileNumber = (value) => {
  if (!value.trim()) return 'Mobile number is required'
  if (!/^[0-9]+$/.test(value)) return 'Digits only, no spaces or symbols'
  if (value.length < 10 || value.length > 15) return 'Must be 10-15 digits'
  return ''
}

export function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [mobileNumber, setMobileNumber] = useState('')
  const [mobileError, setMobileError] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleMobileChange = (e) => {
    const value = e.target.value
    setMobileNumber(value)
    if (mobileError) setMobileError(validateMobileNumber(value))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    const mobileValidationError = validateMobileNumber(mobileNumber)
    if (mobileValidationError) {
      setMobileError(mobileValidationError)
      return
    }

    setSubmitting(true)
    try {
      await register(name, email, mobileNumber, password)
      navigate('/dashboard', { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Box sx={{ display: 'flex', justifyContent: 'center', mt: { xs: 2, md: 6 } }}>
      <Paper sx={{ p: 4, width: '100%', maxWidth: 420 }} elevation={2}>
        <Typography variant="h5" sx={{ fontWeight: 700, mb: 1 }}>
          Create your account
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Free plan includes access to all practice exams.
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField label="Full name" value={name} onChange={(e) => setName(e.target.value)} required fullWidth />
          <TextField
            label="Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            fullWidth
          />
          <TextField
            label="Mobile number"
            type="tel"
            placeholder="9999999999"
            value={mobileNumber}
            onChange={handleMobileChange}
            error={Boolean(mobileError)}
            helperText={mobileError || '10-15 digits, numbers only'}
            required
            fullWidth
          />
          <TextField
            label="Password"
            type="password"
            helperText="8-20 characters, with uppercase, lowercase, a digit, and a special character"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            fullWidth
          />
          <Button type="submit" variant="contained" size="large" disabled={submitting}>
            {submitting ? 'Creating account…' : 'Sign up'}
          </Button>
        </Box>

        <Typography variant="body2" sx={{ mt: 3, textAlign: 'center' }}>
          Already have an account?{' '}
          <Link component={RouterLink} to="/login">
            Log in
          </Link>
        </Typography>
      </Paper>
    </Box>
  )
}
