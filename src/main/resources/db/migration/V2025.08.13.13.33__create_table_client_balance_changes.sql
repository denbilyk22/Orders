CREATE TYPE client_balance_change_type AS ENUM ('ORDER_CREATION', 'ADJUSTMENT');

CREATE TABLE IF NOT EXISTS client_balance_changes
(
    id           UUID                                DEFAULT gen_random_uuid() PRIMARY KEY,
    amount       NUMERIC                    NOT NULL,
    change_type  CLIENT_BALANCE_CHANGE_TYPE NOT NULL,
    created_date TIMESTAMPTZ                NOT NULL DEFAULT now(),

    client_id    UUID REFERENCES clients,
    order_id     UUID REFERENCES orders
);