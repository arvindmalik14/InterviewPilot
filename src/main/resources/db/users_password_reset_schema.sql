-- Forgot-password / account-lockout columns on `users` (MySQL 8.0).
--
-- Reference/production DDL matching the fields added to com.malik.InterviewPilot.entity.User.
-- In dev, Hibernate's ddl-auto:update adds these columns automatically; run this script
-- directly against a MySQL instance when you don't want Hibernate managing DDL (e.g. prod).

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS temporary_password         VARCHAR(255),
    ADD COLUMN IF NOT EXISTS temporary_password_expiry  TIMESTAMP NULL,
    ADD COLUMN IF NOT EXISTS password_reset_required     BOOLEAN NOT NULL DEFAULT FALSE,
    -- Brute-force / account-locking support (beyond the originally specified columns) —
    -- required by the "prevent brute-force attacks" / "account-locking" security requirements.
    ADD COLUMN IF NOT EXISTS failed_login_attempts       INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS account_locked_until         TIMESTAMP NULL,
    -- Bumped on every password change/reset; JWTs issued before this instant are rejected —
    -- this is how "invalidate all active sessions" is enforced without a server-side session store.
    ADD COLUMN IF NOT EXISTS password_changed_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_users_temporary_password_expiry ON users (temporary_password_expiry);
