CREATE TABLE public.customer_account_profile (
 id SERIAL PRIMARY KEY,
 customer_id TEXT NOT NULL,
 account_id TEXT NOT NULL,
 name TEXT NOT NULL,
 minor_flag CHAR(1) NOT NULL CHECK (minor_flag IN ('Y','N')),
 employee_flag CHAR(1) NOT NULL CHECK (employee_flag IN ('Y','N')),
 ingest_ts TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
