import { useState } from 'react'
import { Link as RouterLink, useNavigate } from 'react-router-dom'
import AppBar from '@mui/material/AppBar'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'
import Box from '@mui/material/Box'
import Chip from '@mui/material/Chip'
import IconButton from '@mui/material/IconButton'
import Menu from '@mui/material/Menu'
import MenuItem from '@mui/material/MenuItem'
import AccountCircleIcon from '@mui/icons-material/AccountCircle'
import { useAuth } from '../context/AuthContext.jsx'

export function Navbar() {
  const { user, activePlan, logout } = useAuth()
  const navigate = useNavigate()
  const [anchorEl, setAnchorEl] = useState(null)

  const handleLogout = () => {
    setAnchorEl(null)
    logout()
    navigate('/login')
  }

  const navLinks = [
    { to: '/dashboard', label: 'Dashboard' },
    { to: '/exams', label: 'Question Bank' },
    { to: '/leaderboard', label: 'Leaderboard' },
    { to: '/pricing', label: 'Pricing' },
  ]

  return (
    <AppBar position="static" elevation={0} color="inherit" sx={{ borderBottom: '1px solid #e5e7eb' }}>
      <Toolbar sx={{ gap: 1 }}>
        <Typography
          variant="h6"
          component={RouterLink}
          to={user ? '/dashboard' : '/'}
          sx={{ fontWeight: 700, color: 'primary.main', textDecoration: 'none', mr: 3 }}
        >
          InterviewPilot
        </Typography>

        <Box sx={{ display: 'flex', gap: 1, flexGrow: 1 }}>
          {user &&
            navLinks.map((link) => (
              <Button key={link.to} component={RouterLink} to={link.to} color="inherit">
                {link.label}
              </Button>
            ))}
          {user?.role === 'ADMIN' && (
            <Button component={RouterLink} to="/admin" color="inherit">
              Admin
            </Button>
          )}
        </Box>

        {user ? (
          <>
            {activePlan && (
              <Chip label={activePlan.planName} size="small" color="secondary" variant="outlined" sx={{ mr: 1 }} />
            )}
            <IconButton onClick={(e) => setAnchorEl(e.currentTarget)}>
              <AccountCircleIcon />
            </IconButton>
            <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={() => setAnchorEl(null)}>
              <MenuItem disabled>{user.name}</MenuItem>
              <MenuItem onClick={handleLogout}>Log out</MenuItem>
            </Menu>
          </>
        ) : (
          <Box sx={{ display: 'flex', gap: 1 }}>
            <Button component={RouterLink} to="/login">
              Log in
            </Button>
            <Button component={RouterLink} to="/register" variant="contained">
              Sign up
            </Button>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  )
}
