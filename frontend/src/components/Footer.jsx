import Box from '@mui/material/Box'
import Container from '@mui/material/Container'
import Typography from '@mui/material/Typography'
import logo from '../assets/logo2.jpg'

export function Footer() {
  const year = new Date().getFullYear()

  return (
    <Box component="footer" sx={{ mt: 'auto', borderTop: '1px solid #e5e7eb', bgcolor: 'background.paper' }}>
      <Container
        maxWidth="lg"
        sx={{
          py: 3,
          display: 'flex',
          flexDirection: { xs: 'column', sm: 'row' },
          alignItems: 'center',
          justifyContent: 'space-between',
          gap: 2,
          textAlign: { xs: 'center', sm: 'left' },
        }}
      >
        <Box component="img" src={logo} alt="InterviewPilot" sx={{ height: 72, width: 'auto', flexShrink: 0 }} />

        <Box>
          <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 600 }}>
            © {year} InterviewPilot. All rights reserved.
          </Typography>
          <Typography variant="caption" component="p" color="text.secondary" sx={{ mt: 0.5, maxWidth: 560 }}>
            InterviewPilot™ is a trademark of InterviewPilot. Unauthorized reproduction, distribution, or use of any
            content is strictly prohibited.
          </Typography>
        </Box>
      </Container>
    </Box>
  )
}
