-- Razorpay subscription module schema (MySQL 8.0).
--
-- Reference/production DDL matching the JPA entities in
-- com.malik.InterviewPilot.razorpay.entity. In dev, Hibernate's ddl-auto:update
-- creates these tables automatically from the entities; run this script directly
-- against a MySQL instance when you don't want Hibernate managing DDL (e.g. prod).

CREATE TABLE IF NOT EXISTS subscription_plan (
    plan_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_name           VARCHAR(100)    NOT NULL,
    price               DECIMAL(10, 2)  NOT NULL,
    duration_in_months  INT             NOT NULL,
    question_limit      INT             NOT NULL,
    status              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS payment_order (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT          NOT NULL,
    plan_id             BIGINT          NOT NULL,
    razorpay_order_id   VARCHAR(100)    NOT NULL,
    -- Smallest currency unit (paise for INR) — matches what's sent to/received from Razorpay.
    amount              BIGINT          NOT NULL,
    currency            VARCHAR(10)     NOT NULL DEFAULT 'INR',
    status              VARCHAR(20)     NOT NULL DEFAULT 'CREATED',
    receipt             VARCHAR(100)    NOT NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_payment_order_razorpay_order_id UNIQUE (razorpay_order_id),
    CONSTRAINT fk_payment_order_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_payment_order_plan FOREIGN KEY (plan_id) REFERENCES subscription_plan (plan_id),
    INDEX idx_payment_order_user_plan_status_created (user_id, plan_id, status, created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS payment_transaction (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT        NOT NULL,
    order_id              BIGINT        NOT NULL,
    razorpay_payment_id   VARCHAR(100)  NOT NULL,
    razorpay_signature    VARCHAR(255)  NOT NULL,
    status                VARCHAR(20)   NOT NULL,
    payment_method        VARCHAR(50),
    created_at            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- Enforces "one processed row per Razorpay payment" — the DB-level backstop
    -- against double-processing under concurrent/duplicate verify calls.
    CONSTRAINT uk_payment_transaction_razorpay_payment_id UNIQUE (razorpay_payment_id),
    CONSTRAINT fk_payment_transaction_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_payment_transaction_order FOREIGN KEY (order_id) REFERENCES payment_order (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS user_subscription (
    id                          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                     BIGINT        NOT NULL,
    plan_id                     BIGINT        NOT NULL,
    start_date                  DATE          NOT NULL,
    end_date                    DATE          NOT NULL,
    subscription_status         VARCHAR(20)   NOT NULL,
    remaining_question_count    INT           NOT NULL,
    -- Optimistic-lock column (JPA @Version) guarding concurrent renew/extend writes.
    version                     BIGINT        NOT NULL DEFAULT 0,
    created_at                  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_subscription_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_subscription_plan FOREIGN KEY (plan_id) REFERENCES subscription_plan (plan_id),
    INDEX idx_user_subscription_user_plan_status (user_id, plan_id, subscription_status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Seed data — adjust prices/limits to the real business terms before going live.
-- All four tiers carry 12-month validity. Free is auto-assigned at registration
-- (no payment); Basic/Premium/Enterprise are purchased and require payment.
INSERT INTO subscription_plan (plan_name, price, duration_in_months, question_limit, status)
SELECT * FROM (SELECT 'Free', 0.00, 12, 50, 'ACTIVE') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM subscription_plan WHERE plan_name = 'Free');

INSERT INTO subscription_plan (plan_name, price, duration_in_months, question_limit, status)
SELECT * FROM (SELECT 'Basic', 99.00, 12, 500, 'ACTIVE') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM subscription_plan WHERE plan_name = 'Basic');

INSERT INTO subscription_plan (plan_name, price, duration_in_months, question_limit, status)
SELECT * FROM (SELECT 'Premium', 299.00, 12, 2000, 'ACTIVE') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM subscription_plan WHERE plan_name = 'Premium');

INSERT INTO subscription_plan (plan_name, price, duration_in_months, question_limit, status)
SELECT * FROM (SELECT 'Enterprise', 999.00, 12, 10000, 'ACTIVE') AS tmp
WHERE NOT EXISTS (SELECT 1 FROM subscription_plan WHERE plan_name = 'Enterprise');
