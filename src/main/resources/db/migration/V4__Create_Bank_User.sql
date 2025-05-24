DO $$
BEGIN
  -- Check if user already exists
  IF NOT EXISTS (
    SELECT 1 FROM users WHERE id = -1
  ) THEN
    INSERT INTO users (
      id,
      credentials_expired,
      enabled,
      expired,
      locked,
      password,
      username,
      account_counter,
      date_of_birth,
      email,
      firstname,
      lastname,
      pesel,
      phone_number
    ) OVERRIDING SYSTEM VALUE
    VALUES (
      -1,
      FALSE,  -- not credentials_expired
      TRUE,   -- enabled
      FALSE,  -- not expired
      FALSE,  -- not locked
      '${bank_password}',  -- ← fill in .env
      'bankapp',
      0,
      '2025-02-08',
      'bankapp@mackiewicz.info',
      'Bank',
      'App',
      '00000000000',
      '000000000'
    );
  END IF;
END $$;
