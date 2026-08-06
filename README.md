# InterviewPilot

A full-stack MVP for an AI-assisted technical interview/certification prep platform:
question bank, timed mock tests, AI explanations, subscriptions, leaderboard, and an
admin panel — covering Java, Spring Boot, AWS, and Azure practice exams.

This build follows the "Full-stack MVP, single backend" scope: one Spring Boot backend
(no microservices split), a React frontend, and an embedded H2 database that requires
zero setup to run locally. Subscription payments are real (Razorpay); AI features are
**stubbed** — see [What's stubbed](#whats-stubbed-vs-real) below.

## Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 4.1 (Java 21, Gradle) |
| Auth | Spring Security + JWT (stateless) |
| Database | H2 in-memory (dev) — swappable to MySQL, connector already included |
| Frontend | React 18 + Vite + Material UI 6 |
| Routing | react-router-dom |

## Prerequisites

- JDK 21
- Node.js 18+ and npm

## Running the backend

```bash
# from the repo root
./gradlew bootRun        # Linux/macOS
gradlew.bat bootRun       # Windows
```

Starts on **http://localhost:8080**. On first boot it seeds:

- An **admin** account: `admin@interviewpilot.dev` / `Admin@123` (assigned the Enterprise plan)
- A **demo** account: `demo@interviewpilot.dev` / `Demo@123` (assigned the Free plan, same as any new signup)
- 4 exams (Core Java, Spring Boot, AWS Cloud Practitioner, Azure Fundamentals), 10
  questions each
- 4 subscription plans (Free/Basic/Premium/Enterprise) — see
  [Subscription plans & Razorpay module](#subscription-plans--razorpay-module) below
- Every seeded question assigned to one or more plans, cascading by tier (1st question →
  all four plans, 2nd → Basic and up, 3rd → Premium and up, 4th → Enterprise only, then
  repeats) — see [Plan-scoped question access](#plan-scoped-question-access) below

The H2 database is in-memory — all data resets on every restart. The H2 console is
available at `http://localhost:8082` — its own standalone server, not part of the app's port
8080 (Spring Boot 4 dropped H2's built-in servlet auto-configuration; see `H2ConsoleConfig`).
Log in with JDBC URL `jdbc:h2:mem:interviewpilot`, user `sa`, empty password.

## Running the frontend

```bash
cd frontend
npm install
npm run dev
```

Starts on **http://localhost:5173** and proxies `/api/*` calls to the backend on port
8080 (see `frontend/vite.config.js`). Log in with either seeded account above.

## Switching to MySQL

`src/main/resources/application.yaml` has a commented-out MySQL datasource block.
Uncomment it (and comment out the H2 block above it), point it at a running MySQL
instance, and set `DB_USERNAME`/`DB_PASSWORD` env vars as needed. The `mysql-connector-j`
dependency is already in `build.gradle.kts`. A standalone reference DDL script — the complete
schema for every table, in FK-dependency order, for when you don't want Hibernate's
`ddl-auto: update` managing schema (e.g. prod) — is at `src/main/resources/db/InterviewPilot_schema.sql`:

```bash
mysql -u <user> -p <database> < src/main/resources/db/InterviewPilot_schema.sql
```

## Subscription plans & Razorpay module

Subscription tiers live entirely in the database (`subscription_plan` table) — package
`com.malik.InterviewPilot.razorpay` — so prices/limits can change without a redeploy.
Four tiers are seeded by default, all with 12-month validity:

| Plan | Price | Questions included |
|---|---|---|
| Free | ₹0 | 50 |
| Basic | ₹99 | 500 |
| Premium | ₹299 | 2,000 |
| Enterprise | ₹999 | 10,000 |

**Business rules:**
- Every newly registered user is assigned the Free plan immediately (`AuthService.register` → `SubscriptionService.assignFreePlan`) — no payment involved.
- Basic/Premium/Enterprise require a completed, signature-verified Razorpay payment before the plan activates. Free plans (price = 0) skip Razorpay entirely — `RazorpayService.createOrder` detects the zero price and activates immediately, returning `paymentRequired: false` so the frontend knows not to open the Checkout widget.
- **A user holds exactly one active plan at a time.** Purchasing a different plan deactivates the current one (`SubscriptionStatus.CANCELLED` — superseded, not naturally expired) and starts a fresh 12-month term for the new plan. Re-purchasing the *same* plan before it expires extends from its existing end date instead of restarting the clock.
- Feature access (question quota) is driven by whichever `UserSubscription` row is currently `ACTIVE` — see `remainingQuestionCount`. Deeper per-feature gating (e.g. blocking specific exam categories or AI endpoints by tier) isn't implemented — the app doesn't yet define which concrete features map to which tier beyond the question quota.

**Endpoints:**

```
GET  /api/plans                   public — active plan catalog
GET  /api/subscriptions/me        logged-in user's subscription history
POST /api/payments/order          { "userId": 1, "planId": 2 } -> razorpayOrderId + razorpayKeyId for the Checkout widget
                                   (or paymentRequired: false + immediate activation for a zero-price plan)
POST /api/payments/verify         { "razorpayOrderId", "razorpayPaymentId", "razorpaySignature" } -> activated subscription
```

`POST /order` enforces that `userId` in the request matches the caller (or that the
caller is an admin).

**Frontend**: `frontend/src/pages/PricingPage.jsx` fetches the real plan catalog and the
user's subscriptions, then on "Choose plan" creates an order. If payment is required it
opens the real Razorpay Checkout widget (loaded via `index.html`'s
`checkout.razorpay.com` script); otherwise (Free) it activates immediately. Either way it
refreshes `AuthContext`'s `activePlan` afterward so the Navbar/Dashboard "current plan"
chip updates everywhere without a page reload.

**Configuration** — `razorpay.key-id` / `razorpay.key-secret` in `application.yaml` read
from `RAZORPAY_KEY_ID` / `RAZORPAY_KEY_SECRET` env vars (no default, so a misconfigured
deploy fails fast rather than silently). For local development, put your test keys in
`src/main/resources/application-local.yaml` (gitignored — never commit real keys) and run
with the `local` profile active:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

**Idempotency & concurrency** (see `PaymentVerificationService` for the full reasoning):
a still-fresh pending order for the same user+plan is reused instead of creating a
duplicate Razorpay order; the order row is pessimistically locked during verification so
two concurrent verify calls for the same order can't double-process; a unique DB
constraint on `razorpay_payment_id` is the final backstop against a race slipping through,
with a graceful fallback to the winning request's result instead of a 500.

## Plan-scoped question access

Questions are managed independently of plans (`Question` ↔ `Exam` only); which questions a
plan grants access to is a separate many-to-many mapping — `plan_question`
(`com.malik.InterviewPilot.razorpay.entity.PlanQuestion`), joining `Question` and
`SubscriptionPlan`.

**Admin endpoints** (`ROLE_ADMIN`, see [API overview](#api-overview) for the full list):
create/update a plan (also how a plan is activated/deactivated — `isActive` is part of the
same update payload rather than a separate endpoint), assign a question to a plan, remove a
question from a plan, and list all questions assigned to a plan (paginated).

**User endpoint:** `GET /api/users/{userId}/questions` — resolves the caller's *currently
active* subscription, then returns only the questions assigned to that plan, paginated, with
the total capped at the plan's `question_limit` regardless of how many questions are actually
assigned to it. Self-or-admin only (`#userId == principal.id or hasRole('ADMIN')`) — a user
can never fetch another user's question list. A user with no active subscription gets a 404,
since every account is expected to hold exactly one (Free is auto-assigned at registration).

**Note:** `SubscriptionPlan` still uses `duration_in_months` (not `duration_in_days`) and an
internal `PlanStatus` enum (not a raw `is_active` column) — see `SubscriptionPlan.java`. The
admin-facing `PlanAdminResponse` DTO exposes a computed `isActive` boolean for API
compatibility without touching the underlying (tested, working) renewal logic.

## Account security: signup, login, and password recovery

Two distinct onboarding paths exist side by side:

- **`POST /api/auth/register`** — the original self-service flow: name + email + a
  user-chosen password, immediate login (used by `frontend/src/pages/RegisterPage.jsx`).
- **`POST /api/auth/signup`** — admin-style onboarding: first name + last name + email +
  mobile number, **no password field**. The system generates a temporary password, emails it
  (see below), and returns just the created user + a message — no token, since the account
  has no usable password yet.

**First login with a temporary password** (from either `/signup` or `/forgot-password`) is
handled by the *same* `POST /api/auth/login` — it tries the real password first, and falls
back to a still-valid temporary password on failure. The response's
`requiresPasswordReset: true` tells the frontend to show a forced password-change screen; the
user then calls **`POST /api/auth/reset-password`** (or the identical
**`POST /api/auth/change-password`** — same request shape, same handler, added because the
signup flow's spec named it differently) with their real Bearer token to set a permanent
password. That call requires the temporary-password-issued JWT (not just the account's
email) — a request with only `{email, newPassword, confirmPassword}` and no proof of temp-password
possession would let anyone reset any account's password knowing just their email, so this
deviates from a literal reading of the original spec on purpose.

**`POST /api/auth/forgot-password`** and **`POST /api/auth/resend-password`** both
(re)issue a temporary password for an existing account and always return the same generic
response whether or not the email is registered, to avoid leaking which emails have accounts.
Re-issuance is cooldown-throttled (`TEMP_PASSWORD_RESEND_COOLDOWN_SECONDS`, default 60s) so
repeatedly calling either can't be used to spam a victim's inbox.

**Account lockout & brute-force handling:** failed login attempts are counted per account;
after `MAX_FAILED_LOGIN_ATTEMPTS` (default 5) the account self-locks for
`ACCOUNT_LOCKOUT_MINUTES` (default 15) — enforced by Spring Security itself
(`UserPrincipal.isAccountNonLocked()`/`isEnabled()`), so a locked or deactivated account is
rejected (`423`/`403`) before any password is even checked. There is **no IP-level rate
limiting** — that needs shared infra (Redis, a gateway) this project doesn't have; a
bespoke in-process limiter wouldn't survive multiple instances, so it was deliberately left
out rather than faked.

**Session invalidation:** every password change/reset bumps `passwordChangedAt`; any JWT
issued before that instant is rejected on the next request (`JwtService.isTokenValid`) — this
is how "invalidate all active sessions" works for a stateless-JWT app with no server-side
session store.

**Email delivery** (`EmailService`/`SmtpEmailService`) needs real SMTP credentials
(`MAIL_USERNAME`/`MAIL_PASSWORD` env vars, `MAIL_HOST`/`MAIL_PORT` if not Gmail) to actually
send. Without them, every temp-password issuance still works (hashed, stored, usable to log
in) — only the email itself fails, logged as an `ERROR` rather than thrown, so a misconfigured
mailer can't turn into a 500 for the caller.

## What's stubbed vs. real

| Feature | Status |
|---|---|
| Registration / login / JWT auth | Real |
| Signup (temp-password onboarding), forgot/reset/change/resend password, account lockout | **Real** — see [Account security](#account-security-signup-login-and-password-recovery) above. No IP-level rate limiting (see caveat above). |
| Question bank, mock tests, scoring | Real |
| Plan-scoped question access (admin assigns questions to plans, user quota enforcement) | **Real** — see [Plan-scoped question access](#plan-scoped-question-access) above |
| Leaderboard | Real |
| Admin CRUD for exams/questions/plans | Real |
| Subscription plans (Free/Basic/Premium/Enterprise), Razorpay payments | **Real** — see above. There is no other payment flow in the app; the earlier stubbed Plan/Payment system has been removed entirely. |
| Temp-password / welcome emails | **Real**, but requires SMTP credentials to actually deliver — see [Account security](#account-security-signup-login-and-password-recovery) above |
| AI explanations, question generation, resume analysis, code review | **Stubbed** — canned/derived responses in `AiExplanationService`, no external API key required. Swap in a real OpenAI call there when ready. |

## Project structure

```
src/main/java/com/malik/InterviewPilot/
  entity/        JPA entities (User, Exam, Question, TestAttempt, TestAnswer)
  repository/    Spring Data JPA repositories
  security/      JWT service, auth filter, UserDetails implementation (lockout/enabled checks)
  validation/    Custom Jakarta Validation constraints (@ValidPassword)
  config/        Security config, CORS, data seeder
  dto/           Request/response records, grouped by feature (dto/common for shared shapes)
  service/       Business logic (incl. PasswordResetService, EmailService/SmtpEmailService)
  controller/    REST controllers
  exception/     Custom exceptions + global exception handler
  razorpay/      Self-contained subscription plan + Razorpay payment module, including the
                 plan_question mapping (entity/repository/dto/service/controller/exception/config) —
                 see "Subscription plans & Razorpay module" and "Plan-scoped question access" above

frontend/src/
  api/           axios clients per feature (auth, exams, tests, razorpay, ai, admin, leaderboard)
  context/       AuthContext (JWT storage, current user, active plan)
  components/    Navbar, route guards
  pages/         One component per route (dashboard, exams, test flow, results, pricing, admin, leaderboard)
```

## API overview

```
POST   /api/auth/register         self-service signup, user-chosen password, immediate login
POST   /api/auth/signup           admin-style onboarding, no password — one is emailed
POST   /api/auth/login            handles both a real password and a still-valid temp password
POST   /api/auth/logout
POST   /api/auth/forgot-password  issues a temp password for an existing account
POST   /api/auth/resend-password  re-issues one if the first email was lost (cooldown-throttled)
POST   /api/auth/reset-password   requires the temp-password login's JWT
POST   /api/auth/change-password  same operation as reset-password, different name
GET    /api/users/me
GET    /api/users/{userId}/questions   self-or-admin; capped at the caller's active plan's limit

GET    /api/exams
GET    /api/exams/{id}
GET    /api/questions?examId=

POST   /api/tests/start
POST   /api/tests/{id}/submit
GET    /api/tests/history

GET    /api/plans
GET    /api/subscriptions/me
POST   /api/payments/order
POST   /api/payments/verify

POST   /api/ai/explain
POST   /api/ai/generate-questions
POST   /api/ai/resume-analysis
POST   /api/ai/code-review

GET    /api/leaderboard

# Admin only (ROLE_ADMIN)
POST   /api/admin/exams
PUT    /api/admin/exams/{id}
DELETE /api/admin/exams/{id}
GET    /api/admin/questions?examId=
POST   /api/admin/questions
PUT    /api/admin/questions/{id}
DELETE /api/admin/questions/{id}
POST   /api/admin/plans
PUT    /api/admin/plans/{id}
POST   /api/admin/plans/{planId}/questions/{questionId}
DELETE /api/admin/plans/{planId}/questions/{questionId}
GET    /api/admin/plans/{planId}/questions
```
