create TABLE IF NOT EXISTS ecomm."user" (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    customer_id uuid NOT NULL,
    username varchar(16),
    password varchar(72),
    role varchar(16) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(customer_id)
      REFERENCES ecomm.customer(id)
);

create TABLE IF NOT EXISTS ecomm.user_token (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    refresh_token varchar(128),
    user_id uuid NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY (user_id)
        REFERENCES ecomm."user"(id)
);