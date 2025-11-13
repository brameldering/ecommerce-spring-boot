-- Make the column required
ALTER TABLE ecomm.address
    ALTER COLUMN user_id SET NOT NULL;

-- Add the foreign key constraint
ALTER TABLE ecomm.address
    ADD CONSTRAINT fk_address_user
        FOREIGN KEY(user_id)
            REFERENCES ecomm."user"(id);