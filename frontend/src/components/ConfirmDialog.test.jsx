import { describe, expect, it, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { ConfirmDialog } from './ConfirmDialog.jsx'

describe('ConfirmDialog', () => {
  it('renders nothing interactive when closed', () => {
    render(<ConfirmDialog open={false} message="Are you sure?" onConfirm={vi.fn()} onCancel={vi.fn()} />)
    expect(screen.queryByText('Are you sure?')).not.toBeInTheDocument()
  })

  it('renders the title, message, and Yes/No buttons when open', () => {
    render(
      <ConfirmDialog open title="Switch plan?" message="Do you want to continue?" onConfirm={vi.fn()} onCancel={vi.fn()} />,
    )
    expect(screen.getByText('Switch plan?')).toBeInTheDocument()
    expect(screen.getByText('Do you want to continue?')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Yes' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'No' })).toBeInTheDocument()
  })

  it('calls onConfirm when Yes is clicked and onCancel when No is clicked', async () => {
    const user = userEvent.setup()
    const onConfirm = vi.fn()
    const onCancel = vi.fn()
    render(<ConfirmDialog open message="msg" onConfirm={onConfirm} onCancel={onCancel} />)

    await user.click(screen.getByRole('button', { name: 'Yes' }))
    expect(onConfirm).toHaveBeenCalledTimes(1)

    await user.click(screen.getByRole('button', { name: 'No' }))
    expect(onCancel).toHaveBeenCalledTimes(1)
  })

  it('disables both buttons and shows a processing label while loading', () => {
    render(<ConfirmDialog open loading message="msg" onConfirm={vi.fn()} onCancel={vi.fn()} />)
    expect(screen.getByRole('button', { name: 'Processing…' })).toBeDisabled()
    expect(screen.getByRole('button', { name: 'No' })).toBeDisabled()
  })
})
