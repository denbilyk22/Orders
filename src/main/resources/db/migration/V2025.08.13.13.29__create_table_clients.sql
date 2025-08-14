CREATE TABLE IF NOT EXISTS clients
(
    id                UUID                 DEFAULT gen_random_uuid() PRIMARY KEY,
    name              VARCHAR,
    email             VARCHAR UNIQUE,
    address           VARCHAR,
    active            BOOLEAN              DEFAULT true,
    deactivation_date TIMESTAMPTZ,
    created_date      TIMESTAMPTZ NOT NULL DEFAULT now()
);