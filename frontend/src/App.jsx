import { Routes, Route } from 'react-router-dom'
import Box from '@mui/material/Box'
import { Navbar } from './components/Navbar.jsx'
import { Footer } from './components/Footer.jsx'
import { ProtectedRoute } from './components/ProtectedRoute.jsx'
import { LandingPage } from './pages/LandingPage.jsx'
import { LoginPage } from './pages/LoginPage.jsx'
import { RegisterPage } from './pages/RegisterPage.jsx'
import { DashboardPage } from './pages/DashboardPage.jsx'
import { ExamsPage } from './pages/ExamsPage.jsx'
import { TestPage } from './pages/TestPage.jsx'
import { ResultPage } from './pages/ResultPage.jsx'
import { PricingPage } from './pages/PricingPage.jsx'
import { LeaderboardPage } from './pages/LeaderboardPage.jsx'
import { AdminPage } from './pages/AdminPage.jsx'
import { NotFoundPage } from './pages/NotFoundPage.jsx'

export default function App() {
  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default', display: 'flex', flexDirection: 'column' }}>
      <Navbar />
      <Box component="main" sx={{ maxWidth: 1200, mx: 'auto', px: { xs: 2, md: 3 }, py: 4, flexGrow: 1, width: '100%' }}>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route element={<ProtectedRoute />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/exams" element={<ExamsPage />} />
            <Route path="/exams/:examId/test" element={<TestPage />} />
            <Route path="/results/:testId" element={<ResultPage />} />
            <Route path="/pricing" element={<PricingPage />} />
            <Route path="/leaderboard" element={<LeaderboardPage />} />
          </Route>

          <Route element={<ProtectedRoute adminOnly />}>
            <Route path="/admin" element={<AdminPage />} />
          </Route>

          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Box>
      <Footer />
    </Box>
  )
}
