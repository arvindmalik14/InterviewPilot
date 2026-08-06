-- InterviewPilot — complete database schema (MySQL 8.0).
--
-- This is the single, authoritative reference DDL for every table in the app, matching the
-- JPA entities under com.malik.InterviewPilot.entity and com.malik.InterviewPilot.razorpay.entity
-- as of this writing. In dev, Hibernate's ddl-auto:update creates/updates all of this
-- automatically from the entities — nothing here needs to be run by hand. Run this script
-- directly against a MySQL instance when you don't want Hibernate managing DDL (e.g. prod,
-- where application.yaml's MySQL block sets ddl-auto: validate instead).
--
-- This file supersedes and consolidates the three narrower reference scripts that used to
-- live here (razorpay_schema.sql, users_password_reset_schema.sql, users_registration_schema.sql) —
-- those covered incremental ALTERs layered on top of each other as features were added; this
-- one is the fresh-install, all-at-once version. Tables are created in FK-dependency order.
--
-- No column below has a DB-level DEFAULT, even though several entities carry an @Builder.Default
-- Java-side initializer (e.g. User.active = true, SubscriptionPlan.status = ACTIVE,
-- TestAttempt.startedAt = Instant.now()). Verified directly against Hibernate's own generated
-- schema (H2 console, INFORMATION_SCHEMA.COLUMNS): @Builder.Default is a Lombok/Java construct
-- only — Hibernate's ddl-auto:update does not translate it into a DB DEFAULT clause. Since every
-- write to these tables goes through the entities (never raw SQL), that gap never matters in
-- practice; adding DEFAULTs here anyway would just make this file a less faithful mirror of what
-- ddl-auto:update actually produces. If you're tempted to "fix" that by re-adding DEFAULT TRUE /
-- DEFAULT CURRENT_TIMESTAMP / etc. based on reading the entities, don't — that was tried and
-- confirmed wrong against the real generated DDL.
--
-- Every identifier is backtick-quoted so a handful of columns/tables that happen to collide with
-- MySQL reserved or context-sensitive keywords (e.g. `role`) don't need to be tracked by hand —
-- Hibernate's own MySQLDialect already quotes such identifiers automatically in its generated
-- DDL, so this just mirrors that rather than relying on memorizing MySQL's keyword list.
--
-- Run once against an empty schema:
--   mysql -u <user> -p <database> < InterviewPilot_schema.sql

-- ============================================================================
-- users
-- ============================================================================
-- Columns beyond the "core" (name/email/password/role) support two features:
--   - forgot-password / first-login temporary passwords (temporary_password*,
--     password_reset_required) — never stores a plaintext password, only a BCrypt hash.
--   - brute-force / account-locking (failed_login_attempts, account_locked_until) and
--     stateless-JWT session invalidation (password_changed_at — any JWT issued before this
--     instant is rejected on the next request, since there's no server-side session store).
CREATE TABLE IF NOT EXISTS `users` (
    `id`                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`                        VARCHAR(100)  NOT NULL,
    `email`                       VARCHAR(255)  NOT NULL,
    `password`                    VARCHAR(255)  NOT NULL,
    `mobile_number`               VARCHAR(20),
    `active`                      TINYINT(1)    NOT NULL,
    `role`                        VARCHAR(50)   NOT NULL,
    `created_at`                  TIMESTAMP(6)  NOT NULL,
    `temporary_password`          VARCHAR(255),
    `temporary_password_expiry`   TIMESTAMP(6) NULL,
    `password_reset_required`     TINYINT(1)    NOT NULL,
    `failed_login_attempts`       INT           NOT NULL,
    `account_locked_until`        TIMESTAMP(6) NULL,
    `password_changed_at`         TIMESTAMP(6)  NOT NULL,
    CONSTRAINT `uk_users_email` UNIQUE (`email`),
    INDEX `idx_users_temporary_password_expiry` (`temporary_password_expiry`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ============================================================================
-- exams / questions / tests / test_answers
-- ============================================================================
CREATE TABLE IF NOT EXISTS `exams` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name`          VARCHAR(255)   NOT NULL,
    `category`      VARCHAR(255)   NOT NULL,
    `description`   VARCHAR(1000)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Questions are managed independently of subscription plans — see plan_question below for
-- which plans a question is accessible under.
--
-- optiona/optionb/optionc/optiond (no underscore) is not a typo: Hibernate's naming strategy
-- doesn't insert an underscore before a single trailing capital letter, so Question.optionA
-- maps to column `optiona`, not `option_a` — verified against the real generated schema.
CREATE TABLE IF NOT EXISTS `questions` (
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY,
    `exam_id`       BIGINT         NOT NULL,
    `question`      VARCHAR(2000)  NOT NULL,
    `optiona`       VARCHAR(1000)  NOT NULL,
    `optionb`       VARCHAR(1000)  NOT NULL,
    `optionc`       VARCHAR(1000)  NOT NULL,
    `optiond`       VARCHAR(1000)  NOT NULL,
    `answer`        VARCHAR(10)    NOT NULL,
    `explanation`   VARCHAR(2000),
    -- No NOT NULL/default: the entity's difficulty="MEDIUM" is a Java-side default only
    -- (@Builder.Default, not @Column(nullable=false)) — this column is genuinely nullable.
    `difficulty`    VARCHAR(20),
    CONSTRAINT `fk_questions_exam` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- One row per mock-test attempt. status is a free-text state ('IN_PROGRESS', 'COMPLETED', ...)
-- rather than an enum table, matching TestAttempt.status in the entity.
CREATE TABLE IF NOT EXISTS `tests` (
    `id`                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`             BIGINT         NOT NULL,
    `exam_id`             BIGINT         NOT NULL,
    `score`               INT,
    `total_questions`     INT            NOT NULL,
    `duration_seconds`    INT,
    `status`              VARCHAR(20)    NOT NULL,
    `started_at`          TIMESTAMP(6)   NOT NULL,
    `completed_at`        TIMESTAMP(6) NULL,
    CONSTRAINT `fk_tests_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_tests_exam` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`),
    -- Backs findByUserIdOrderByStartedAtDesc (test history) and findByStatusOrderByScoreDesc
    -- (leaderboard) respectively.
    INDEX `idx_tests_user_id_started_at` (`user_id`, `started_at`),
    INDEX `idx_tests_status_score` (`status`, `score`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- One row per question answered within a test attempt.
CREATE TABLE IF NOT EXISTS `test_answers` (
    `id`                BIGINT AUTO_INCREMENT PRIMARY KEY,
    `test_attempt_id`   BIGINT        NOT NULL,
    `question_id`       BIGINT        NOT NULL,
    `selected_option`   VARCHAR(10),
    `correct`           TINYINT(1)    NOT NULL,
    CONSTRAINT `fk_test_answers_test_attempt` FOREIGN KEY (`test_attempt_id`) REFERENCES `tests` (`id`),
    CONSTRAINT `fk_test_answers_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ============================================================================
-- Subscription plans, plan-scoped question access, and Razorpay payments
-- (com.malik.InterviewPilot.razorpay)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `subscription_plan` (
    `plan_id`             BIGINT AUTO_INCREMENT PRIMARY KEY,
    `plan_name`           VARCHAR(100)    NOT NULL,
    `price`               DECIMAL(10, 2)  NOT NULL,
    `duration_in_months`  INT             NOT NULL,
    `question_limit`      INT             NOT NULL,
    `status`              VARCHAR(20)     NOT NULL,
    `created_at`          TIMESTAMP(6)    NOT NULL,
    `updated_at`          TIMESTAMP(6)    NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Many-to-many: which questions a plan grants access to. A user's accessible question count
-- is still capped at the plan's question_limit even if more than that many are assigned here.
CREATE TABLE IF NOT EXISTS `plan_question` (
    `id`                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    `plan_id`             BIGINT        NOT NULL,
    `question_id`         BIGINT        NOT NULL,
    `created_at`          TIMESTAMP(6)  NOT NULL,
    CONSTRAINT `uk_plan_question_plan_id_question_id` UNIQUE (`plan_id`, `question_id`),
    CONSTRAINT `fk_plan_question_plan` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plan` (`plan_id`),
    CONSTRAINT `fk_plan_question_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`),
    INDEX `idx_plan_question_plan_id` (`plan_id`),
    INDEX `idx_plan_question_question_id` (`question_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- A user holds exactly one ACTIVE row at a time (enforced in SubscriptionService, not the DB).
CREATE TABLE IF NOT EXISTS `user_subscription` (
    `id`                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`                     BIGINT        NOT NULL,
    `plan_id`                     BIGINT        NOT NULL,
    `start_date`                  DATE          NOT NULL,
    `end_date`                    DATE          NOT NULL,
    `subscription_status`         VARCHAR(20)   NOT NULL,
    `remaining_question_count`    INT           NOT NULL,
    -- Optimistic-lock column (JPA @Version) guarding concurrent renew/extend writes. Hibernate
    -- itself supplies the value on insert (starting at 0) — no DB default needed or generated.
    `version`                     BIGINT        NOT NULL,
    `created_at`                  TIMESTAMP(6)  NOT NULL,
    `updated_at`                  TIMESTAMP(6)  NOT NULL,
    CONSTRAINT `fk_user_subscription_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_user_subscription_plan` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plan` (`plan_id`),
    INDEX `idx_user_subscription_user_plan_status` (`user_id`, `plan_id`, `subscription_status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- One row per Razorpay order created. amount is stored in the smallest currency unit (paise
-- for INR) — the same unit Razorpay's API expects — so no conversion is needed when
-- reconciling against Razorpay dashboard data.
CREATE TABLE IF NOT EXISTS `payment_order` (
    `id`                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`             BIGINT          NOT NULL,
    `plan_id`             BIGINT          NOT NULL,
    `razorpay_order_id`   VARCHAR(100)    NOT NULL,
    `amount`              BIGINT          NOT NULL,
    `currency`            VARCHAR(10)     NOT NULL,
    `status`              VARCHAR(20)     NOT NULL,
    `receipt`             VARCHAR(100)    NOT NULL,
    `created_at`          TIMESTAMP(6)    NOT NULL,
    `updated_at`          TIMESTAMP(6)    NOT NULL,
    CONSTRAINT `uk_payment_order_razorpay_order_id` UNIQUE (`razorpay_order_id`),
    CONSTRAINT `fk_payment_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_payment_order_plan` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plan` (`plan_id`),
    -- Backs the pending-order reuse-window lookup in RazorpayService.
    INDEX `idx_payment_order_user_plan_status_created` (`user_id`, `plan_id`, `status`, `created_at`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- One row per verified Razorpay payment callback. The unique constraint on
-- razorpay_payment_id is the last line of defense against double-processing a payment under
-- concurrent/duplicate verify calls — see PaymentVerificationService.
CREATE TABLE IF NOT EXISTS `payment_transaction` (
    `id`                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id`               BIGINT        NOT NULL,
    `order_id`              BIGINT        NOT NULL,
    `razorpay_payment_id`   VARCHAR(100)  NOT NULL,
    `razorpay_signature`    VARCHAR(255)  NOT NULL,
    `status`                VARCHAR(20)   NOT NULL,
    `payment_method`        VARCHAR(50),
    `created_at`            TIMESTAMP(6)  NOT NULL,
    `updated_at`            TIMESTAMP(6)  NOT NULL,
    CONSTRAINT `uk_payment_transaction_razorpay_payment_id` UNIQUE (`razorpay_payment_id`),
    CONSTRAINT `fk_payment_transaction_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_payment_transaction_order` FOREIGN KEY (`order_id`) REFERENCES `payment_order` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ============================================================================
-- Seed data
-- ============================================================================
-- Only the subscription plan catalog is seeded here — it's structural (the app hard-depends
-- on a "Free" plan existing for new registrations/signups) rather than demo content. Demo
-- accounts, sample exams, and sample questions are dev-only and stay in DataSeeder /
-- SubscriptionPlanSeeder (CommandLineRunners that only run against an empty table), not here.
--
-- All four tiers carry 12-month validity. Free is auto-assigned at registration/signup (no
-- payment); Basic/Premium/Enterprise are purchased and require payment.
INSERT INTO `subscription_plan` (`plan_name`, `price`, `duration_in_months`, `question_limit`, `status`, `created_at`, `updated_at`)
SELECT * FROM (SELECT 'Free' AS plan_name, 0.00 AS price, 12 AS duration_in_months, 50 AS question_limit,
                      'ACTIVE' AS status, NOW(6) AS created_at, NOW(6) AS updated_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `subscription_plan` WHERE `plan_name` = 'Free');

INSERT INTO `subscription_plan` (`plan_name`, `price`, `duration_in_months`, `question_limit`, `status`, `created_at`, `updated_at`)
SELECT * FROM (SELECT 'Basic' AS plan_name, 99.00 AS price, 12 AS duration_in_months, 500 AS question_limit,
                      'ACTIVE' AS status, NOW(6) AS created_at, NOW(6) AS updated_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `subscription_plan` WHERE `plan_name` = 'Basic');

INSERT INTO `subscription_plan` (`plan_name`, `price`, `duration_in_months`, `question_limit`, `status`, `created_at`, `updated_at`)
SELECT * FROM (SELECT 'Premium' AS plan_name, 299.00 AS price, 12 AS duration_in_months, 2000 AS question_limit,
                      'ACTIVE' AS status, NOW(6) AS created_at, NOW(6) AS updated_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `subscription_plan` WHERE `plan_name` = 'Premium');

INSERT INTO `subscription_plan` (`plan_name`, `price`, `duration_in_months`, `question_limit`, `status`, `created_at`, `updated_at`)
SELECT * FROM (SELECT 'Enterprise' AS plan_name, 999.00 AS price, 12 AS duration_in_months, 10000 AS question_limit,
                      'ACTIVE' AS status, NOW(6) AS created_at, NOW(6) AS updated_at) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM `subscription_plan` WHERE `plan_name` = 'Enterprise');
