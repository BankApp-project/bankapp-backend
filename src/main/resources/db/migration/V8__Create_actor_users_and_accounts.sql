-- Actor users for demo account transaction history
-- Pattern follows V4__Create_Bank_User.sql / V5__Create_bank_account.sql

DO $$
BEGIN
  -- Actor -2: Acme Corp (employer)
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = -2) THEN
    INSERT INTO users (
      id, credentials_expired, enabled, expired, locked, password, username,
      account_counter, date_of_birth, email, firstname, lastname, pesel, phone_number, is_demo
    ) OVERRIDING SYSTEM VALUE
    VALUES (
      -2, FALSE, TRUE, FALSE, FALSE,
      '${bank_password}', 'acme.corp',
      0, '2000-01-01',
      'acme@demo.bankapp', 'Acme', 'Corp',
      '00000000002', '000000002', FALSE
    );
  END IF;

  -- Actor -3: Ms. Basia (landlord)
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = -3) THEN
    INSERT INTO users (
      id, credentials_expired, enabled, expired, locked, password, username,
      account_counter, date_of_birth, email, firstname, lastname, pesel, phone_number, is_demo
    ) OVERRIDING SYSTEM VALUE
    VALUES (
      -3, FALSE, TRUE, FALSE, FALSE,
      '${bank_password}', 'basia.landlord',
      0, '1975-05-15',
      'basia@demo.bankapp', 'Basia', 'Kamieniolot',
      '00000000003', '000000003', FALSE
    );
  END IF;

  -- Actor -4: Starbugs (coffee shop)
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = -4) THEN
    INSERT INTO users (
      id, credentials_expired, enabled, expired, locked, password, username,
      account_counter, date_of_birth, email, firstname, lastname, pesel, phone_number, is_demo
    ) OVERRIDING SYSTEM VALUE
    VALUES (
      -4, FALSE, TRUE, FALSE, FALSE,
      '${bank_password}', 'starbugs.coffee',
      0, '2010-06-01',
      'starbugs@demo.bankapp', 'Starbugs', 'Coffee',
      '00000000004', '000000004', FALSE
    );
  END IF;

  -- Actor -5: FrogShop (convenience store / zabka)
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = -5) THEN
    INSERT INTO users (
      id, credentials_expired, enabled, expired, locked, password, username,
      account_counter, date_of_birth, email, firstname, lastname, pesel, phone_number, is_demo
    ) OVERRIDING SYSTEM VALUE
    VALUES (
      -5, FALSE, TRUE, FALSE, FALSE,
      '${bank_password}', 'frog.shop',
      0, '2005-03-20',
      'frogshop@demo.bankapp', 'Frog', 'Shop',
      '00000000005', '000000005', FALSE
    );
  END IF;

  -- Actor -6: Kolega Marek (friend)
  IF NOT EXISTS (SELECT 1 FROM users WHERE id = -6) THEN
    INSERT INTO users (
      id, credentials_expired, enabled, expired, locked, password, username,
      account_counter, date_of_birth, email, firstname, lastname, pesel, phone_number, is_demo
    ) OVERRIDING SYSTEM VALUE
    VALUES (
      -6, FALSE, TRUE, FALSE, FALSE,
      '${bank_password}', 'kolega.marek',
      0, '1995-11-30',
      'marek@demo.bankapp', 'Marek', 'Kolega',
      '00000000006', '000000006', FALSE
    );
  END IF;
END $$;

-- Actor accounts (IBANs computed with iban4j-compatible check digits)
INSERT INTO accounts (id, balance, creation_date, iban, user_account_number, owner_id)
OVERRIDING SYSTEM VALUE
VALUES (-2, 100000000.00, NOW(), 'PL38485112340000000000020001', 1, -2)
ON CONFLICT (id) DO NOTHING;

INSERT INTO accounts (id, balance, creation_date, iban, user_account_number, owner_id)
OVERRIDING SYSTEM VALUE
VALUES (-3, 1000000.00, NOW(), 'PL86485112340000000000030001', 1, -3)
ON CONFLICT (id) DO NOTHING;

INSERT INTO accounts (id, balance, creation_date, iban, user_account_number, owner_id)
OVERRIDING SYSTEM VALUE
VALUES (-4, 1000000.00, NOW(), 'PL37485112340000000000040001', 1, -4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO accounts (id, balance, creation_date, iban, user_account_number, owner_id)
OVERRIDING SYSTEM VALUE
VALUES (-5, 1000000.00, NOW(), 'PL85485112340000000000050001', 1, -5)
ON CONFLICT (id) DO NOTHING;

INSERT INTO accounts (id, balance, creation_date, iban, user_account_number, owner_id)
OVERRIDING SYSTEM VALUE
VALUES (-6, 1000000.00, NOW(), 'PL36485112340000000000060001', 1, -6)
ON CONFLICT (id) DO NOTHING;
