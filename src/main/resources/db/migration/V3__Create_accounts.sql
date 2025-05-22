CREATE TABLE IF NOT EXISTS accounts (
  id                  SERIAL PRIMARY KEY,
  balance             NUMERIC(38,2),
  creation_date       TIMESTAMP(6),
  iban                VARCHAR(255) NOT NULL,
  user_account_number INTEGER NOT NULL,
  owner_id            INTEGER NOT NULL,
  CONSTRAINT uk_accounts_iban UNIQUE (iban),
  CONSTRAINT fk_accounts_owner FOREIGN KEY (owner_id)
    REFERENCES users (id) ON DELETE CASCADE
);
