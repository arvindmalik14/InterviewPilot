import { describe, expect, it } from 'vitest'
import { getPlanSwitchScenario, PLAN_SWITCH_SCENARIO } from './planSwitch.js'

describe('getPlanSwitchScenario', () => {
  it('proceeds directly when the current plan is Free, regardless of the selected plan', () => {
    expect(getPlanSwitchScenario('Free', 'Basic')).toBeNull()
    expect(getPlanSwitchScenario('Free', 'Premium')).toBeNull()
    expect(getPlanSwitchScenario('Free', 'Enterprise')).toBeNull()
  })

  it('proceeds directly when there is no current plan at all', () => {
    expect(getPlanSwitchScenario(undefined, 'Basic')).toBeNull()
    expect(getPlanSwitchScenario(null, 'Basic')).toBeNull()
  })

  it('requires confirmation when switching from any paid plan down to Free', () => {
    expect(getPlanSwitchScenario('Basic', 'Free')).toBe(PLAN_SWITCH_SCENARIO.DOWNGRADE_TO_FREE)
    expect(getPlanSwitchScenario('Premium', 'Free')).toBe(PLAN_SWITCH_SCENARIO.DOWNGRADE_TO_FREE)
    expect(getPlanSwitchScenario('Enterprise', 'Free')).toBe(PLAN_SWITCH_SCENARIO.DOWNGRADE_TO_FREE)
  })

  it('requires confirmation when switching between two different paid plans', () => {
    expect(getPlanSwitchScenario('Basic', 'Premium')).toBe(PLAN_SWITCH_SCENARIO.PLAN_TO_PLAN)
    expect(getPlanSwitchScenario('Premium', 'Enterprise')).toBe(PLAN_SWITCH_SCENARIO.PLAN_TO_PLAN)
    expect(getPlanSwitchScenario('Enterprise', 'Basic')).toBe(PLAN_SWITCH_SCENARIO.PLAN_TO_PLAN)
  })

  it('is case-insensitive', () => {
    expect(getPlanSwitchScenario('BASIC', 'free')).toBe(PLAN_SWITCH_SCENARIO.DOWNGRADE_TO_FREE)
    expect(getPlanSwitchScenario('basic', 'PREMIUM')).toBe(PLAN_SWITCH_SCENARIO.PLAN_TO_PLAN)
  })

  it('proceeds directly when the selected plan is the same as the current plan', () => {
    expect(getPlanSwitchScenario('Premium', 'Premium')).toBeNull()
  })
})
