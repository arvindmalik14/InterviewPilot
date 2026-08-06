import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import Box from '@mui/material/Box'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Alert from '@mui/material/Alert'
import Link from '@mui/material/Link'
import { useAuth } from '../context/AuthContext.jsx'
import * as authApi from '../api/auth.js'

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,20}$/
const PASSWORD_HELP = '8-20 characters, with uppercase, lowercase, a digit, and a special character'

const validatePassword = (value) => {
  if (!value) return 'New password is required'
  if (!PASSWORD_PATTERN.test(value)) return PASSWORD_HELP
  return ''
}

export function ChangePasswordPage() {
  const { completePasswordChange, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const [email, setEmail] = useState(location.state?.email || '')
  const [emailError, setEmailError] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [passwordError, setPasswordError] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [confirmError, setConfirmError] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleEmailChange = (e) => {
    const value = e.target.value
    setEmail(value)
    if (emailError) setEmailError(EMAIL_PATTERN.test(value) ? '' : 'Enter a valid email address')
  }

  const handlePasswordChange = (e) => {
    const value = e.target.value
    setNewPassword(value)
    if (passwordError) setPasswordError(validatePassword(value))
    if (confirmError && confirmPassword) {
      setConfirmError(value === confirmPassword ? '' : 'Passwords do not match')
    }
  }

  const handleConfirmChange = (e) => {
    const value = e.target.value
    setConfirmPassword(value)
    if (confirmError) setConfirmError(value === newPassword ? '' : 'Passwords do not match')
  }

  const handleBackToLogin = () => {
    logout()
    navigate('/login')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    const emailValidationError = !email.trim()
      ? 'Email is required'
      : !EMAIL_PATTERN.test(email)
        ? 'Enter a valid email address'
        : ''
    const passwordValidationError = validatePassword(newPassword)
    const confirmValidationError = confirmPassword !== newPassword ? 'Passwords do not match' : ''

    setEmailError(emailValidationError)
    setPasswordError(passwordValidationError)
    setConfirmError(confirmValidationError)
    if (emailValidationError || passwordValidationError || confirmValidationError) return

    setSubmitting(true)
    try {
      const data = await authApi.changePassword(email, newPassword, confirmPassword)
      await completePasswordChange(data)
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
          Set a new password
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          You logged in with a temporary password. Choose a new password to continue.
        </Typography>

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            label="Email"
            type="email"
            value={email}
            onChange={handleEmailChange}
            error={Boolean(emailError)}
            helperText={emailError}
            required
            fullWidth
          />
          <TextField
            label="New password"
            type="password"
            value={newPassword}
            onChange={handlePasswordChange}
            error={Boolean(passwordError)}
            helperText={passwordError || PASSWORD_HELP}
            required
            fullWidth
          />
          <TextField
            label="Confirm new password"
            type="password"
            value={confirmPassword}
            onChange={handleConfirmChange}
            error={Boolean(confirmError)}
            helperText={confirmError}
            required
            fullWidth
          />
          <Button type="submit" variant="contained" size="large" disabled={submitting}>
            {submitting ? 'Updating…' : 'Update password'}
          </Button>
        </Box>

        <Typography variant="body2" sx={{ mt: 3, textAlign: 'center' }}>
          <Link component="button" type="button" onClick={handleBackToLogin}>
            Not you? Back to log in
          </Link>
        </Typography>
      </Paper>
    </Box>
  )
}
