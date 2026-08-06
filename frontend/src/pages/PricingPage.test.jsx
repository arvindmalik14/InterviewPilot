import { describe, expect, it, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { PricingPage } from './PricingPage.jsx'
import * as razorpayApi from '../api/razorpay.js'
import { useAuth } from '../context/AuthContext.jsx'

vi.mock('../api/razorpay.js', () => ({
  listPlans: vi.fn(),
  getMySubscriptions: vi.fn(),
  createOrder: vi.fn(),
  verifyPayment: vi.fn(),
}))

vi.mock('../context/AuthContext.jsx', () => ({
  useAuth: vi.fn(),
}))

const PLANS = [
  { planId: 1, planName: 'Free', price: 0, durationInMonths: 12, questionLimit: 50 },
  { planId: 2, planName: 'Basic', price: 99, durationInMonths: 12, questionLimit: 500 },
  { planId: 3, planName: 'Premium', price: 299, durationInMonths: 12, questionLimit: 2000 },
  { planId: 4, planName: 'Enterprise', price: 999, durationInMonths: 12, questionLimit: 10000 },
]

function activeSubscriptionFor(planId) {
  return [{ planId, subscriptionStatus: 'ACTIVE', endDate: '2027-01-01' }]
}

function setCurrentPlan(planId) {
  razorpayApi.listPlans.mockResolvedValue(PLANS)
  razorpayApi.getMySubscriptions.mockResolvedValue(activeSubscriptionFor(planId))
}

beforeEach(() => {
  vi.clearAllMocks()
  useAuth.mockReturnValue({
    user: { id: 1, name: 'Test User', email: 'test@example.com' },
    refreshActivePlan: vi.fn().mockResolvedValue(undefined),
  })
  razorpayApi.createOrder.mockResolvedValue({ paymentRequired: false, planName: 'Chosen' })
})

async function chooseCardButton(planName) {
  const card = screen.getByText(planName).closest('.MuiCard-root')
  const button = card.querySelector('button')
  await userEvent.click(button)
}

describe('PricingPage plan-switch confirmation', () => {
  it('scenario 1: Free -> paid plan proceeds without a confirmation dialog', async () => {
    setCurrentPlan(1) // Free is active
    render(<PricingPage />)
    await waitFor(() => expect(screen.getByText('Basic')).toBeInTheDocument())

    await chooseCardButton('Basic')

    expect(screen.queryByText(/Do you want to continue\?/)).not.toBeInTheDocument()
    await waitFor(() => expect(razorpayApi.createOrder).toHaveBeenCalledWith(1, 2))
  })

  it('scenario 2: paid plan -> Free shows the downgrade confirmation and proceeds on Yes', async () => {
    setCurrentPlan(2) // Basic is active
    render(<PricingPage />)
    await waitFor(() => expect(screen.getByText('Free')).toBeInTheDocument())

    await chooseCardButton('Free')

    expect(await screen.findByText(/You are about to switch to the Free plan/)).toBeInTheDocument()
    expect(razorpayApi.createOrder).not.toHaveBeenCalled()

    await userEvent.click(screen.getByRole('button', { name: 'Yes' }))

    await waitFor(() => expect(razorpayApi.createOrder).toHaveBeenCalledWith(1, 1))
    expect(screen.queryByText(/You are about to switch to the Free plan/)).not.toBeInTheDocument()
  })

  it('scenario 2: clicking No cancels the downgrade and makes no request', async () => {
    setCurrentPlan(2) // Basic is active
    render(<PricingPage />)
    await waitFor(() => expect(screen.getByText('Free')).toBeInTheDocument())

    await chooseCardButton('Free')
    expect(await screen.findByText(/You are about to switch to the Free plan/)).toBeInTheDocument()

    await userEvent.click(screen.getByRole('button', { name: 'No' }))

    expect(screen.queryByText(/You are about to switch to the Free plan/)).not.toBeInTheDocument()
    expect(razorpayApi.createOrder).not.toHaveBeenCalled()
  })

  it('scenario 3: paid plan -> a different paid plan shows the plan-switch confirmation and proceeds on Yes', async () => {
    setCurrentPlan(2) // Basic is active
    render(<PricingPage />)
    await waitFor(() => expect(screen.getByText('Premium')).toBeInTheDocument())

    await chooseCardButton('Premium')

    expect(
      await screen.findByText(/Changing your current subscription plan will remove the benefits/),
    ).toBeInTheDocument()
    expect(razorpayApi.createOrder).not.toHaveBeenCalled()

    await userEvent.click(screen.getByRole('button', { name: 'Yes' }))

    await waitFor(() => expect(razorpayApi.createOrder).toHaveBeenCalledWith(1, 3))
  })

  it('scenario 3: clicking No cancels the plan-to-plan switch and makes no request', async () => {
    setCurrentPlan(2) // Basic is active
    render(<PricingPage />)
    await waitFor(() => expect(screen.getByText('Premium')).toBeInTheDocument())

    await chooseCardButton('Premium')
    expect(
      await screen.findByText(/Changing your current subscription plan will remove the benefits/),
    ).toBeInTheDocument()

    await userEvent.click(screen.getByRole('button', { name: 'No' }))

    expect(razorpayApi.createOrder).not.toHaveBeenCalled()
  })

  it('disables every plan button while a request is in flight, preventing duplicate requests', async () => {
    setCurrentPlan(1) // Free is active — Basic purchase proceeds directly
    let resolveOrder
    razorpayApi.createOrder.mockReturnValue(
      new Promise((resolve) => {
        resolveOrder = resolve
      }),
    )
    render(<PricingPage />)
    await waitFor(() => expect(screen.getByText('Basic')).toBeInTheDocument())

    await chooseCardButton('Basic')

    const premiumCard = screen.getByText('Premium').closest('.MuiCard-root')
    expect(premiumCard.querySelector('button')).toBeDisabled()

    resolveOrder({ paymentRequired: false, planName: 'Basic' })
  })
})
