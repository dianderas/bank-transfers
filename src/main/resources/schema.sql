CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL PRIMARY KEY,
    account_holder VARCHAR(100),
    currency VARCHAR(3),
    balance DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    from_account_id VARCHAR(36),
    to_account_id VARCHAR(36),
    amount DOUBLE PRECISION,
    currency VARCHAR(3),
    status VARCHAR(20)
);