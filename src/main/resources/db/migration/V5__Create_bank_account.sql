INSERT INTO accounts (
    id,
    balance,
    creation_date,
    iban,
    user_account_number,
    owner_id
) OVERRIDING SYSTEM VALUE
VALUES (
    -1,
    100000000.00,
    NOW(),
    'PL66485112340000000000000000',
    1,
    -1
)
ON CONFLICT (id) DO NOTHING;
