-- Dodaj użytkownika API do tabeli "admins", jeśli nie istnieje
INSERT INTO admins (credentials_expired, enabled, expired, locked, password, username)
VALUES (FALSE, TRUE, FALSE, FALSE, '$2a$12$aNaLg9djXhl.lndvoCRT9OktCz1.ED6BH6plSS/u0I01L//hyYAX6', 'api')
    ON CONFLICT (username) DO NOTHING;

-- Dodaj rolę "ROLE_SWAGGER" dla użytkownika 'api', jeśli jeszcze nie istnieje
INSERT INTO admin_roles (admin_id, role)
SELECT id, 'ROLE_SWAGGER'
FROM admins
WHERE username = 'api'
  AND NOT EXISTS (
    SELECT 1 FROM admin_roles
    WHERE admin_id = admins.id AND role = 'ROLE_SWAGGER'
);
