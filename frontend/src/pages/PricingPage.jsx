import { useEffect, useState } from 'react'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'
import Grid from '@mui/material/Grid2'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import CardActions from '@mui/material/CardActions'
import Button from '@mui/material/Button'
import Chip from '@mui/material/Chip'
import Snackbar from '@mui/material/Snackbar'
import Alert from '@mui/material/Alert'
import CircularProgress from '@mui/material/CircularProgress'
import { useAuth } from '../context/AuthContext.jsx'
import { listPlans, getMySubscriptions, createOrder, verifyPayment } from '../api/razorpay.js'

function formatDate(isoDate) {
  return new Date(isoDate).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' })
}

export function PricingPage() {
  const { user, refreshActivePlan } = useAuth()
  const [plans, setPlans] = useState([])
  const [subscriptions, setSubscriptions] = useState([])
  const [loading, setLoading] = useState(true)
  const [payingPlanId, setPayingPlanId] = useState(null)
  const [toast, setToast] = useState(null)

  const loadData = () =>
    Promise.all([listPlans(), getMySubscriptions()]).then(([planData, subData]) => {
      setPlans(planData)
      setSubscriptions(subData)
    })

  useEffect(() => {
    loadData().finally(() => setLoading(false))
  }, [])

  // A user holds exactly one active plan at a time, so at most one card is ever "current".
  const activePlanId = subscriptions.find((s) => s.subscriptionStatus === 'ACTIVE')?.planId
  const activeSubscriptionFor = (planId) =>
    subscriptions.find((s) => s.planId === planId && s.subscriptionStatus === 'ACTIVE')

  const handlePurchase = async (plan) => {
    setPayingPlanId(plan.planId)
    try {
      const order = await createOrder(user.id, plan.planId)

      if (!order.paymentRequired) {
        // Free tier (or any zero-price plan) — already activated server-side, no checkout needed.
        await Promise.all([loadData(), refreshActivePlan()])
        setToast({ severity: 'success', message: `${order.planName} plan activated.` })
        setPayingPlanId(null)
        return
      }

      if (!window.Razorpay) {
        setToast({ severity: 'error', message: 'Payment widget failed to load. Please refresh and try again.' })
        setPayingPlanId(null)
        return
      }

      const razorpay = new window.Razorpay({
        key: order.razorpayKeyId,
        amount: order.amount,
        currency: order.currency,
        order_id: order.razorpayOrderId,
        name: 'InterviewPilot',
        description: `${order.planName} subscription`,
        prefill: { name: user.name, email: user.email },
        theme: { color: '#4f46e5' },
        handler: async (response) => {
          try {
            await verifyPayment(
              response.razorpay_order_id,
              response.razorpay_payment_id,
              response.razorpay_signature,
            )
            await Promise.all([loadData(), refreshActivePlan()])
            setToast({ severity: 'success', message: `${order.planName} plan activated.` })
          } catch (err) {
            setToast({ severity: 'error', message: err.message })
          } finally {
            setPayingPlanId(null)
          }
        },
        modal: {
          ondismiss: () => setPayingPlanId(null),
        },
      })

      razorpay.on('payment.failed', (response) => {
        setToast({ severity: 'error', message: response.error?.description || 'Payment failed' })
        setPayingPlanId(null)
      })

      razorpay.open()
    } catch (err) {
      setToast({ severity: 'error', message: err.message })
      setPayingPlanId(null)
    }
  }

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
        <CircularProgress />
      </Box>
    )
  }

  return (
    <Box>
      <Typography variant="h4" sx={{ fontWeight: 700, mb: 1 }}>
        Plans & pricing
      </Typography>
      <Box sx={{ mb: 4, display: 'flex', alignItems: 'center', gap: 1, flexWrap: 'wrap' }}>
        <Typography color="text.secondary">Upgrade for unlimited mock tests and a bigger question quota.</Typography>
        <Chip size="small" label="Secured by Razorpay" variant="outlined" />
      </Box>

      <Grid container spacing={3}>
        {plans.map((plan) => {
          const activeSub = activeSubscriptionFor(plan.planId)
          const isCurrentPlan = plan.planId === activePlanId
          const isPaying = payingPlanId === plan.planId
          return (
            <Grid key={plan.planId} size={{ xs: 12, sm: 6, md: 3 }}>
              <Card
                variant={isCurrentPlan ? 'elevation' : 'outlined'}
                elevation={isCurrentPlan ? 4 : 0}
                sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}
              >
                <CardContent sx={{ flexGrow: 1 }}>
                  <Typography variant="h6" sx={{ fontWeight: 700 }}>
                    {plan.planName}
                  </Typography>
                  <Typography variant="h4" sx={{ my: 1 }}>
                    ₹{plan.price}
                    <Typography component="span" variant="body2" color="text.secondary">
                      {' '}
                      / {plan.durationInMonths === 12 ? 'year' : `${plan.durationInMonths} months`}
                    </Typography>
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
                    {plan.questionLimit.toLocaleString()} questions included
                  </Typography>
                  {activeSub && (
                    <Chip
                      size="small"
                      color="success"
                      variant="outlined"
                      label={`Active until ${formatDate(activeSub.endDate)}`}
                    />
                  )}
                </CardContent>
                <CardActions sx={{ p: 2 }}>
                  <Button
                    fullWidth
                    variant={isCurrentPlan ? 'outlined' : 'contained'}
                    disabled={isCurrentPlan || isPaying}
                    onClick={() => handlePurchase(plan)}
                  >
                    {isPaying ? 'Processing…' : isCurrentPlan ? 'Current plan' : 'Choose plan'}
                  </Button>
                </CardActions>
              </Card>
            </Grid>
          )
        })}
      </Grid>

      <Snackbar open={Boolean(toast)} autoHideDuration={5000} onClose={() => setToast(null)}>
        {toast && <Alert severity={toast.severity}>{toast.message}</Alert>}
      </Snackbar>
    </Box>
  )
}
