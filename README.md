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

The H2 database is in-memory — all data resets on every restart. The H2 console is
available at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:interviewpilot`,
user `sa`, empty password).

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
dependency is already in `build.gradle.kts`. For the Razorpay module specifically, a
standalone reference DDL script is at `src/main/resources/db/razorpay_schema.sql`.

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

## What's stubbed vs. real

| Feature | Status |
|---|---|
| Registration / login / JWT auth | Real |
| Question bank, mock tests, scoring | Real |
| Leaderboard | Real |
| Admin CRUD for exams/questions | Real |
| Subscription plans (Free/Basic/Premium/Enterprise), Razorpay payments | **Real** — see above. There is no other payment flow in the app; the earlier stubbed Plan/Payment system has been removed entirely. |
| AI explanations, question generation, resume analysis, code review | **Stubbed** — canned/derived responses in `AiExplanationService`, no external API key required. Swap in a real OpenAI call there when ready. |

## Project structure

```
src/main/java/com/malik/InterviewPilot/
  entity/        JPA entities (User, Exam, Question, TestAttempt, TestAnswer)
  repository/    Spring Data JPA repositories
  security/      JWT service, auth filter, UserDetails implementation
  config/        Security config, CORS, data seeder
  dto/           Request/response records, grouped by feature
  service/       Business logic
  controller/    REST controllers
  exception/     Custom exceptions + global exception handler
  razorpay/      Self-contained subscription plan + Razorpay payment module
                 (entity/repository/dto/service/controller/exception/config) —
                 see "Subscription plans & Razorpay module" above

frontend/src/
  api/           axios clients per feature (auth, exams, tests, razorpay, ai, admin, leaderboard)
  context/       AuthContext (JWT storage, current user, active plan)
  components/    Navbar, route guards
  pages/         One component per route (dashboard, exams, test flow, results, pricing, admin, leaderboard)
```

## API overview

```
POST   /api/auth/register
POST   /api/auth/login
GET    /api/users/me

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
```
