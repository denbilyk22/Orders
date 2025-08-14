CREATE TABLE IF NOT EXISTS orders
(
    id                    UUID                 DEFAULT gen_random_uuid() PRIMARY KEY,
    name                  VARCHAR,
    price                 NUMERIC     NOT NULL,
    start_processing_date TIMESTAMPTZ,
    end_processing_date   TIMESTAMPTZ,
    created_date          TIMESTAMPTZ NOT NULL DEFAULT now(),

    supplier_id           UUID        NOT NULL REFERENCES clients,
    consumer_id           UUID        NOT NULL REFERENCES clients,

    UNIQUE (name, supplier_id, consumer_id)
);