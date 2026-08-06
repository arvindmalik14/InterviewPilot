# H2 → MySQL migration

Status: **done and verified**. The backend boots cleanly against MySQL with `ddl-auto: update`,
all 10 entities are present with their migrated data intact, and every listed feature was
smoke-tested end to end against the real MySQL-backed app.

## Deliverables

| Deliverable | Where |
|---|---|
| Updated `application.yml` | `src/main/resources/application.yaml` — already pointed at MySQL (`jdbc:mysql://localhost:3306/interviewpilot`, `devops`/`devops`, `ddl-auto: update`) before this work started; left as-is. |
| Converted MySQL schema script | `src/main/resources/db/InterviewPilot_schema.sql` (fixed — see below) |
| Converted MySQL data script | `src/main/resources/db/backup_migrated_mysql.sql` (new) |
| Modified entity classes | `src/main/java/com/malik/InterviewPilot/entity/Question.java` — pinned `optiona`/`optionb`/`optionc`/`optiond` as explicit `@Column(name=...)` (see below). No other entity changed; no business logic touched anywhere. |
| Migration steps | This document |
| Error fixes | This document |

## Migration steps

1. **Create the database.**
   ```sql
   CREATE DATABASE interviewpilot;
   ```
   (Also happens automatically via the datasource URL's `createDatabaseIfNotExist=true`, but
   run explicitly first if loading the SQL scripts by hand before ever starting the app.)

2. **Load the schema, then the data**, in that order (data references the schema's tables):
   ```bash
   mysql -u devops -p interviewpilot < src/main/resources/db/InterviewPilot_schema.sql
   mysql -u devops -p interviewpilot < src/main/resources/db/backup_migrated_mysql.sql
   ```
   `backup_migrated_mysql.sql` is the converted form of `backup.sql` (your H2 `SCRIPT` export) —
   see the header comment in that file for the exact list of H2→MySQL syntax conversions applied
   (`CREATE MEMORY TABLE`, `IDENTITY`, `CHARACTER VARYING`, `TIMESTAMP WITH TIME ZONE`, `TRUE`/`FALSE`,
   the one `U&'...'` Unicode-escape string, etc.). It is **not idempotent** — it inserts fixed
   primary keys captured from your export, so run it once against an empty schema.

3. **Start the backend.** `ddl-auto: update` then just reconciles — since the schema script
   already matches every entity exactly, Hibernate has nothing to add and starts clean.

No separate step was needed to "let Hibernate create the schema from scratch" — the schema
script already IS what Hibernate would generate, so both requirements (run the converted
script, and let Hibernate manage the schema) are satisfied simultaneously rather than being
in tension.

## Errors found and fixed

These were found by actually executing the scripts and booting the app against a real local
MySQL server — not just eyeballing the syntax — so this list is what genuinely broke, not a
guess at what might.

1. **Duplicate derived-table column name.** `InterviewPilot_schema.sql`'s idempotent plan-seed
   INSERTs used `SELECT 'Free', 0.00, ..., NOW(6), NOW(6)` inside a derived table with no column
   aliases. MySQL names unaliased expression columns after their text, so the two `NOW(6)` calls
   collided: `SQLSyntaxErrorException: Duplicate column name 'NOW(6)'`. Fixed by aliasing every
   column in the derived table (`NOW(6) AS created_at, NOW(6) AS updated_at`, etc.).

2. **Missing seed timestamps.** Those same seed INSERTs originally omitted `created_at`/
   `updated_at` entirely, relying on a DB-level `DEFAULT CURRENT_TIMESTAMP` that (per the
   H2-console verification done earlier) Hibernate never actually generates for
   `@Builder.Default`-only fields. Once the schema stopped declaring that (incorrect) default,
   the seed INSERTs would have failed on a NOT NULL violation. Fixed by supplying `NOW(6)`
   explicitly in the INSERT itself.

3. **`Question.optionA/B/C/D` map to `optiona`/`optionb`/`optionc`/`optiond` — no underscore.**
   Confirmed against `backup.sql`'s own `CREATE MEMORY TABLE "QUESTIONS"` block, which is a
   direct dump of what Hibernate actually created: Hibernate's naming strategy doesn't insert an
   underscore before a single trailing capital letter, so `optionA` → `optiona`, not `option_a`.
   `InterviewPilot_schema.sql` had wrongly written `option_a` etc. (an assumption, not a
   verified fact) — fixed the column names there, and additionally pinned them explicitly via
   `@Column(name = "optiona")` etc. on the entity so this can't silently regress if a future
   Hibernate version's naming strategy changes.

4. **Reserved/context-sensitive keyword risk (`role`, etc.).** Rather than track MySQL's exact
   reserved-word list by hand, every identifier in both SQL scripts is now backtick-quoted —
   mirroring what Hibernate's own `MySQLDialect` already does automatically in its generated
   DDL, so the standalone scripts behave the same way a real app-generated schema would.

5. **`subscription_plan` primary-key collision between the schema seed and the real data.**
   `InterviewPilot_schema.sql`'s own idempotent seed and `backup_migrated_mysql.sql`'s real
   captured rows both target `plan_id` 1–4. Fixed with `ON DUPLICATE KEY UPDATE` on the real-data
   INSERT so it overwrites the placeholder-seeded row with the real captured values — correct
   regardless of which of the two scripts happens to run first.

6. **H2-specific DDL removed entirely** (not "converted" — these have no MySQL equivalent and
   aren't needed): `SET DB_CLOSE_DELAY -1`, the `CREATE USER "SA" ...` statement, `NOCHECK` on
   every FK (MySQL always validates FKs; the data was already consistent so this needed no
   replacement), `NULLS DISTINCT` on unique constraints (MySQL's default behavior already
   treats NULLs as distinct — same semantics, just no clause needed).

7. **`AUTO_INCREMENT` continuation.** H2's `IDENTITY(... RESTART WITH N ...)` has no direct MySQL
   equivalent when you're inserting explicit primary keys yourself. Handled with
   `ALTER TABLE ... AUTO_INCREMENT = N` statements at the end of `backup_migrated_mysql.sql`,
   matching each table's source "RESTART WITH" value — verified afterward that a fresh
   `INSERT`/registration/admin-create correctly continues from the right id with no collision
   (see Verification below).

### Investigated and ruled out (not actually bugs)

- **The em dash in question 5's explanation appeared as `�` when printed from a quick
  verification script.** Checked the raw stored bytes directly (`HEX(explanation)`) — they're
  `E2 80 94`, the correct UTF-8 encoding for U+2014. The server's `character_set_*` /
  `collation_*` variables are all `utf8mb4`/`utf8mb4_0900_ai_ci`. The garbled character was a
  Windows-console display artifact in the one-off verification tool, not a data problem — no
  fix needed, confirmed by inspecting bytes rather than trusting a terminal render.
- **`ENUM(...)` columns** (`status`, `role`, `subscription_status` in the H2 export) — MySQL
  supports `ENUM` natively, so these needed no conversion at all despite the entities mapping
  them via `@Enumerated(EnumType.STRING)`; the JDBC layer reads/writes them as plain strings
  either way, transparently.
- **`BOOLEAN`** is already a MySQL alias for `TINYINT(1)` — converting it was cosmetic
  (`SHOW CREATE TABLE` reports `tinyint(1)` either way), done anyway per the literal requirement
  and for consistency between the two script files.

## Verification performed

All of this was run against the real local MySQL server, not simulated:

- **Startup**: clean boot, `ddl-auto: update`, zero errors (one harmless deprecation notice
  about not needing to specify `hibernate.dialect` explicitly).
- **Data preservation**: row counts identical before and after the app's `DataSeeder`/
  `SubscriptionPlanSeeder` ran (2 users, 4 exams, 40 questions, 4 plans, 100 plan_question rows,
  2 user_subscriptions) — confirms the seeders correctly detected existing data and didn't
  duplicate anything.
- **All 10 entities**: `User`, `SubscriptionPlan`, `UserSubscription`, `PaymentOrder`,
  `PaymentTransaction`, `Question`, `Exam`, `TestAttempt` (`tests`), `TestAnswer`, `PlanQuestion`
  — confirmed present with correct row counts and, for the populated ones, correct data.
- **Features**, all against the live migrated data:
  - Registration — new user created as `id=3` (correctly continuing past the 2 migrated users).
  - Login — both migrated accounts (`admin@interviewpilot.dev` / `Admin@123`,
    `demo@interviewpilot.dev` / `Demo@123`) authenticate against their migrated bcrypt hashes.
  - Password reset — `forgot-password` returns the generic response correctly.
  - Subscription management — plan catalog and `GET /api/subscriptions/me` return the real
    migrated subscription (Free plan, correct dates, `remainingQuestionCount`).
  - Payment processing — order creation for a zero-price plan correctly short-circuits to
    immediate activation (`paymentRequired: false`).
  - Question retrieval — exam list, question-by-exam, and plan-scoped
    `GET /api/users/{id}/questions` all correct.
  - Test submission — start → submit → history → leaderboard, full round trip, scored correctly.
  - Admin operations — plan create (`id=5`, continuing past the 4 migrated plans), question
    create (`id=41`, continuing past the 40 migrated questions), plan-question assign/list/remove.
