-- Add is_admin column for role-based access (Admin vs Customer)
-- Run this if you get "Unknown column 'is_admin'" errors.

ALTER TABLE Customer ADD COLUMN is_admin TINYINT(1) DEFAULT 0;
