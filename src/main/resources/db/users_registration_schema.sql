-- Signup-flow columns on `users` (MySQL 8.0).
--
-- Reference/production DDL matching the fields added to com.malik.InterviewPilot.entity.User
-- for the temp-password signup flow. In dev, Hibernate's ddl-auto:update adds these columns
-- automatically; run this script directly against a MySQL instance when you don't want
-- Hibernate managing DDL (e.g. prod).
--
-- Note: the originally requested first_login / password_changed / account_locked columns are
-- NOT added here — they would duplicate concepts already introduced by the forgot-password
-- feature (see users_password_reset_schema.sql): password_reset_required already means "this
-- account has a pending forced password change" (true for both a fresh signup and a forgot-
-- password request), and account_locked_until is a self-expiring lock that supersedes a plain
-- boolean flag. Running two parallel "must change password" / "is locked" mechanisms would be
-- confusing and error-prone, so the signup flow reuses those same columns.

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS mobile_number VARCHAR(20),
    ADD COLUMN IF NOT EXISTS active         BOOLEAN NOT NULL DEFAULT TRUE;
