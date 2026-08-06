export const PLAN_SWITCH_SCENARIO = {
  DOWNGRADE_TO_FREE: 'DOWNGRADE_TO_FREE',
  PLAN_TO_PLAN: 'PLAN_TO_PLAN',
}

export const PLAN_SWITCH_MESSAGES = {
  [PLAN_SWITCH_SCENARIO.DOWNGRADE_TO_FREE]:
    'You are about to switch to the Free plan.\n\n' +
    'By continuing, you will lose access to the features and benefits available in your current subscription.\n\n' +
    'Do you want to continue?',
  [PLAN_SWITCH_SCENARIO.PLAN_TO_PLAN]:
    'Changing your current subscription plan will remove the benefits associated with your existing plan ' +
    'and activate the newly selected plan.\n\n' +
    'Do you want to continue?',
}

/**
 * Decides whether switching from currentPlanName to selectedPlanName needs a confirmation dialog.
 * Returns null when the switch should proceed directly (default Free -> any paid plan, or no-op).
 */
export function getPlanSwitchScenario(currentPlanName, selectedPlanName) {
  const current = (currentPlanName || '').toUpperCase()
  const selected = (selectedPlanName || '').toUpperCase()

  if (!current || current === 'FREE') return null
  if (selected === 'FREE') return PLAN_SWITCH_SCENARIO.DOWNGRADE_TO_FREE
  if (current !== selected) return PLAN_SWITCH_SCENARIO.PLAN_TO_PLAN
  return null
}
