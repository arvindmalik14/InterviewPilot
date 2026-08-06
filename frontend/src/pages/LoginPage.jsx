import { useState } from 'react'
import { Link as RouterLink, useNavigate, useLocation } from 'react-router-dom'
import Box from '@mui/material/Box'
import Paper from '@mui/material/Paper'
import Typography from '@mui/material/Typography'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Alert from '@mui/material/Alert'
import Link from '@mui/material/Link'
import { useAuth } from '../context/AuthContext.jsx'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [email, setEmail] = useState('demo@interviewpilot.dev')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const data = await login(email, password)
      if (data.requiresPasswordReset) {
        navigate('/change-password', { state: { email } })
      } else {
        navigate(location.state?.from || '/dashboard', { replace: true })
      }
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
          Welcome back
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
          Log in to continue your prep. Demo account: demo@interviewpilot.dev / Demo@123
        </Typography>

        {!error && location.state?.registered && (
          <Alert severity="success" sx={{ mb: 2 }}>
            Account created. Please log in to continue.
          </Alert>
        )}

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
            onChange={(e) => setEmail(e.target.value)}
            required
            fullWidth
          />
          <TextField
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            fullWidth
          />
          <Typography variant="body2" sx={{ textAlign: 'right', mt: -1 }}>
            <Link component={RouterLink} to="/forgot-password">
              Forgot password?
            </Link>
          </Typography>
          <Button type="submit" variant="contained" size="large" disabled={submitting}>
            {submitting ? 'Logging in…' : 'Log in'}
          </Button>
        </Box>

        <Typography variant="body2" sx={{ mt: 3, textAlign: 'center' }}>
          Don't have an account?{' '}
          <Link component={RouterLink} to="/register">
            Sign up
          </Link>
        </Typography>
      </Paper>
    </Box>
  )
}
