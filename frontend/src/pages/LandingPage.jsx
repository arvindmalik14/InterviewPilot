import { Link as RouterLink } from 'react-router-dom'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'
import Grid from '@mui/material/Grid2'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import CodeIcon from '@mui/icons-material/Code'
import CloudIcon from '@mui/icons-material/Cloud'
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome'
import TimerIcon from '@mui/icons-material/Timer'
import { useAuth } from '../context/AuthContext.jsx'
import { Navigate } from 'react-router-dom'

const features = [
  {
    icon: <CodeIcon color="primary" fontSize="large" />,
    title: 'Java & Spring Boot',
    body: 'Deep question banks covering core Java, collections, concurrency, and Spring Boot fundamentals.',
  },
  {
    icon: <CloudIcon color="primary" fontSize="large" />,
    title: 'AWS & Azure certs',
    body: 'Practice sets aligned with real certification objectives for AWS and Azure fundamentals.',
  },
  {
    icon: <TimerIcon color="primary" fontSize="large" />,
    title: 'Timed mock tests',
    body: 'Simulate real exam pressure with timed, randomized mock tests and instant scoring.',
  },
  {
    icon: <AutoAwesomeIcon color="primary" fontSize="large" />,
    title: 'AI explanations',
    body: 'Get an explanation for every question you get wrong, so you learn while you practice.',
  },
]

export function LandingPage() {
  const { user } = useAuth()
  if (user) {
    return <Navigate to="/dashboard" replace />
  }

  return (
    <Box>
      <Box sx={{ textAlign: 'center', pt: { xs: 1, sm: 2 }, pb: { xs: 4, md: 8 } }}>
        <Typography variant="h3" sx={{ fontWeight: 800, mb: 2 }}>
          Ace your next technical interview
        </Typography>
        <Typography variant="h6" color="text.secondary" sx={{ mb: 4, maxWidth: 640, mx: 'auto' }}>
          Focused practice tests and AI-powered explanations for Java, Spring Boot, AWS, and Azure
          candidates — freshers and experienced engineers alike.
        </Typography>
        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
          <Button component={RouterLink} to="/register" variant="contained" size="large">
            Start practicing free
          </Button>
          <Button component={RouterLink} to="/login" variant="outlined" size="large">
            Log in
          </Button>
        </Box>
      </Box>

      <Grid container spacing={3} sx={{ mb: 6 }}>
        {features.map((feature) => (
          <Grid key={feature.title} size={{ xs: 12, sm: 6, md: 3 }}>
            <Card sx={{ height: '100%' }}>
              <CardContent>
                {feature.icon}
                <Typography variant="h6" sx={{ mt: 2, mb: 1, fontWeight: 700 }}>
                  {feature.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {feature.body}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  )
}
