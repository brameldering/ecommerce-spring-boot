-- This query updates each address and sets its user_id
-- by finding one corresponding user in the old join table.
UPDATE ecomm.address a
SET user_id = (
    SELECT ua.user_id
    FROM ecomm.user_address ua
    WHERE ua.address_id = a.id
    LIMIT 1
);