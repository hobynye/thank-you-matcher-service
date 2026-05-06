-- Align supporter table with 2026 income spreadsheet upload format

-- Rename columns
ALTER TABLE supporter RENAME COLUMN organization   TO name;
ALTER TABLE supporter RENAME COLUMN donor_type     TO category;
ALTER TABLE supporter RENAME COLUMN sponsor_county TO sponsored_county;

-- Drop columns replaced by the new format
ALTER TABLE supporter DROP COLUMN full_name;
ALTER TABLE supporter DROP COLUMN first_name;
ALTER TABLE supporter DROP COLUMN last_name;
ALTER TABLE supporter DROP COLUMN donation_info;
ALTER TABLE supporter DROP COLUMN beneficiary_first;
ALTER TABLE supporter DROP COLUMN beneficiary_last;

-- Add columns from the new format
ALTER TABLE supporter ADD COLUMN contact_name         VARCHAR(500);
ALTER TABLE supporter ADD COLUMN club                 VARCHAR(255);
ALTER TABLE supporter ADD COLUMN street2              VARCHAR(500);
ALTER TABLE supporter ADD COLUMN email                VARCHAR(255);
ALTER TABLE supporter ADD COLUMN phone                VARCHAR(50);
-- Comma-separated name(s) of J Staff member(s) directly sponsored by this donor
ALTER TABLE supporter ADD COLUMN sponsored_j_staff    TEXT;
-- Comma/or-separated first+last name(s); if multiple, pick the one with fewest letters at match time
ALTER TABLE supporter ADD COLUMN sponsored_ambassador TEXT;