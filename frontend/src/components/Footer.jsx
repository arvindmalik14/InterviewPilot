import { Link as RouterLink } from 'react-router-dom'
import Box from '@mui/material/Box'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import Link from '@mui/material/Link'

const TRADEMARK_NOTICE =
  'InterviewPilot™ is a trademark of InterviewPilot. Unauthorized reproduction, distribution, or use of any content is strictly prohibited.'

export function Footer() {
  const year = new Date().getFullYear()

  return (
    <Box component="footer" sx={{ mt: 'auto', borderTop: '1px solid #e5e7eb', bgcolor: 'background.paper' }}>
      <Toolbar sx={{ justifyContent: 'center' }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: { xs: 1, sm: 1.5 }, minWidth: 0, overflow: 'hidden' }}>
          <Typography variant="body2" color="text.secondary" noWrap sx={{ fontWeight: 600 }}>
            © {year} InterviewPilot
          </Typography>
          <Typography
            variant="caption"
            color="text.secondary"
            title={TRADEMARK_NOTICE}
            noWrap
            sx={{ display: { xs: 'none', md: 'block' }, maxWidth: 320 }}
          >
            {TRADEMARK_NOTICE}
          </Typography>
          <Link component={RouterLink} to="/feedback" variant="body2" sx={{ flexShrink: 0, whiteSpace: 'nowrap' }}>
            Send us feedback
          </Link>
        </Box>
      </Toolbar>
    </Box>
  )
}
