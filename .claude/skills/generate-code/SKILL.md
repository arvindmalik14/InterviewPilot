---
name: interview-prep-platform-builder
description: >
  Roadmap and reference architecture for building an AI-powered technical
  interview/certification prep platform (Java, Spring Boot, AWS/Azure exam
  questions, mock tests, AI explanations). Use this skill whenever the user is
  planning, scoping, architecting, or building a product like this, e.g. mentions
  "interview prep platform", "exam/certification practice app", "mock test engine",
  "question bank app", or references product names like SkillForge AI,
  InterviewPilot, PrepMaster AI, TechAce AI, or InterviewHub AI. Also trigger for
  sub-tasks that belong to this roadmap, such as designing the DB schema for
  users/exams/questions/tests/payments, picking a tech stack for this kind of app,
  laying out microservices (auth/exam/payment), designing the API routes, planning
  AI features (question generation, resume analysis, code review), or sequencing a
  week-by-week MVP build.
---

# Interview Prep Platform Builder

A reference playbook for building an AI-powered exam/interview-prep SaaS product
(question bank + mock tests + AI explanations, targeted at Java/Spring Boot/AWS/Azure
certification candidates). Use this as a starting point and adapt scope to what the
user actually asks for — don't dump the whole roadmap if they only asked about one piece
(e.g. just the DB schema, or just the API design).

## How to use this skill

1. Figure out which stage the user is at (idea validation, MVP scoping, schema design,
   architecture, API design, AI feature design, infra setup, or week-by-week execution)
   and jump to that section.
2. Treat everything below as a *starting default*, not a mandate — confirm scope,
   audience, and budget constraints before generating code or files, since these
   choices (e.g. Razorpay vs Stripe, MySQL) are regional/business decisions.
3. When asked to actually generate artifacts (SQL migrations, Spring Boot services,
   API clients, Docker/Nginx config), use the relevant document-creation or code
   tools — this file is planning reference, not the deliverable itself.

## Core Product Definition

- **Category**: Certification / technical interview prep platform
- **Target audience (niche, not everyone)**: Java developers, Spring Boot developers,
  AWS certification candidates, Azure certification candidates, freshers, and
  experienced engineers. Resist scope creep to "everyone learning tech."
- **Candidate names**: SkillForge AI, InterviewPilot, PrepMaster AI, TechAce AI,
  InterviewHub AI
- **One core problem to solve first**: a focused practice-test + AI-explanation loop,
  not a sprawling LMS.

## Feature Priority (v1 scope discipline)

| Feature | Priority |
|---|---|
| Login | High |
| Dashboard | High |
| Question bank | High |
| Mock tests | High |
| AI explanation | High |
| Subscription/payment | High |
| Leaderboard | Medium |
| Certificate generation | Low |

**MVP v1** = Registration, Login, Dashboard, AWS/Java/Spring Boot question sets, Mock
tests, AI explanations, Payment gateway, Admin panel. Everything else is post-MVP.

## Recommended Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React + Material UI |
| Backend | Spring Boot |
| Security | Spring Security + JWT |
| Database | MySQL |
| Cache | Redis |
| File storage | Cloudflare R2 |
| Search | Elasticsearch |
| Payments | Razorpay (swap for Stripe/PayPal outside India) |
| Email | Resend |
| AI | OpenAI API |
| Container / proxy | Docker + Nginx |

Low-budget infra option: Vercel (frontend, free tier), Railway (backend, free tier),
Neon (Postgres, free tier), Cloudflare R2 (storage, near-free), Grafana (monitoring,
free), Namecheap (domain, ~₹1,000/yr). Good for validating before committing to
paid infra.

## Database Schema (starting point)

Five core tables — extend, don't reinvent, unless the user's domain differs:

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50),
    created_at TIMESTAMP
);

CREATE TABLE exams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    category VARCHAR(255)
);

CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    exam_id BIGINT REFERENCES exams(id),
    question TEXT,
    option_a TEXT,
    option_b TEXT,
    option_c TEXT,
    option_d TEXT,
    answer VARCHAR(10),
    explanation TEXT
);

CREATE TABLE tests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    score INTEGER,
    duration INTEGER
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    amount DECIMAL(10,2),
    status VARCHAR(50)
);
```

> Note: the source roadmap omits FKs on `exam_id`/`user_id` — add them (as above) unless
> there's a specific reason for looser coupling (e.g. separate databases per service).

## Microservice Architecture

```
Internet → Cloudflare → React Frontend
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
   Auth Service         Exam Service        Payment Service
          │                   │                   │
          └─────────┬─────────┴───────────────────┘
                     ▼
                MySQL → Redis → OpenAI API
```

Three services to start: **auth-service**, **exam-service**, **payment-service**.
If services need to call each other synchronously or asynchronously (e.g. exam-service
notifying payment-service of a completed test tier upgrade), pull in the companion
sync/async-communication skill for RestTemplate/WebClient/Feign/Kafka/Resilience4j
patterns rather than duplicating that here.

## Project Structure

```
interview-platform/
├── frontend/
│   ├── src/
│   ├── components/
│   ├── services/
│   └── pages/
├── auth-service/
├── exam-service/
├── payment-service/
├── docker/
└── docs/
```

## API Design

```
Auth
  POST /api/auth/register
  POST /api/auth/login
  POST /api/auth/logout

Questions
  GET    /api/questions
  GET    /api/questions/{id}
  POST   /api/questions
  DELETE /api/questions/{id}

Tests
  POST /api/tests/start
  POST /api/tests/submit
  GET  /api/tests/result

Payments
  POST /api/payment/create
  POST /api/payment/webhook
```

## AI Feature Set

1. **AI interview/question generator** — input: tech + years of experience (e.g.
   "Java 21, 5 years") → output: N questions with difficulty level, answers, explanations.
2. **AI résumé analysis** — input: uploaded PDF résumé → output: improvement suggestions.
3. **AI code review** — input: pasted source code → output: optimization suggestions.

All three are OpenAI API calls with a structured-output prompt (JSON schema for
questions/answers, plain-text for suggestions). Keep prompts scoped per feature rather
than one mega-prompt.

## Suggested 6-Week Build Sequence

| Week | Focus |
|---|---|
| 1 | Login, registration, JWT auth |
| 2 | Question module, category module |
| 3 | Mock test engine |
| 4 | Payment integration |
| 5 | AI integration |
| 6 | Deployment |

## Revenue Model (starting point)

| Plan | Price |
|---|---|
| Free | ₹0 |
| Basic | ₹99 |
| Premium | ₹299 |
| Enterprise | ₹999 |

Adjust currency/pricing to the target market before treating these as final numbers.
