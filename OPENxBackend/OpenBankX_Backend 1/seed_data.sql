-- OpenBankX Comprehensive Seed Data Script
-- --------------------------------------------------------
-- This script contains all the INSERT statements required to populate the
-- database with realistic data spanning every domain: Identity, Products, 
-- TPP Apps, Consents, Accounts, Payments, and Auditing.
-- --------------------------------------------------------

-- 1. Insert Users (All 4 core roles)
INSERT INTO users (email, name, password, phone, role, status) VALUES 
('admin@openbankx.com', 'System Admin', 'Test@1234', '+1234567890', 'ADMIN', 'ACTIVE'),
('ops@openbankx.com', 'Operations User', 'Test@1234', '+1234567891', 'OPERATIONS', 'ACTIVE'),
('tpp@fintech.com', 'Fintech TPP User', 'Test@1234', '+1234567892', 'TPP', 'ACTIVE'),
('akash@openbankx.com', 'Akash Kumar', 'Test@1234', '+1234567893', 'CUSTOMER', 'ACTIVE'),
('jane@example.com', 'Jane Doe', 'Test@1234', '+1234567894', 'CUSTOMER', 'ACTIVE');

-- 2. Insert TPP Profile
INSERT INTO tpp (legal_name, registration_number, status, user_id) VALUES 
('Fintech Innovations Ltd', 'REG-123456', 'ACTIVE', 3);

-- 3. Insert API Products
INSERT INTO api_product (description, name, status, version) VALUES 
('Provides access to customer account details and balances.', 'AISP API (Accounts)', 'PUBLISHED', 'v3.1'),
('Enables TPPs to initiate payments on behalf of customers.', 'PISP API (Payments)', 'PUBLISHED', 'v3.1'),
('Allows verifying if funds are available before a transaction.', 'CBPII API (Funds Check)', 'PUBLISHED', 'v3.1');

-- 4. Insert API Plans
INSERT INTO api_plan (description, name, price, rate_limit_per_minute, product_id) VALUES 
('Free tier for AISP', 'Basic AISP', 0.00, 100, 1),
('Premium tier for AISP', 'Premium AISP', 50.00, 1000, 1),
('Standard tier for PISP', 'Standard PISP', 20.00, 500, 2),
('Standard CBPII', 'Basic CBPII', 0.00, 200, 3);

-- 5. Insert TPP Apps
INSERT INTO tpp_app (app_name, callback_url, description, status, tpp_id) VALUES 
('Budgeting Tracker App', 'https://fintech.com/callback', 'Personal finance and budgeting tracker', 'ACTIVE', 1),
('QuickPay Wallet', 'https://fintech.com/pay-callback', 'Fast payment gateway', 'ACTIVE', 1);

-- 6. Insert Auth Clients (OAuth info for apps)
INSERT INTO auth_client (client_secret, created_date, grant_types, redirect_uris, tpp_app_id) VALUES 
('secret-1234', NOW(), '["authorization_code","client_credentials"]', '["https://fintech.com/callback"]', 1),
('secret-5678', NOW(), '["authorization_code","client_credentials"]', '["https://fintech.com/pay-callback"]', 2);

-- 7. Insert TPP Subscriptions
INSERT INTO tpp_subscription (start_date, status, plan_id, tpp_app_id) VALUES 
(NOW(), 'ACTIVE', 2, 1), 
(NOW(), 'ACTIVE', 3, 2); 

-- 8. Insert Customer Accounts
INSERT INTO account_ref (account_number_masked, currency, status, type, user_id) VALUES 
('****5678', 'GBP', 'ACTIVE', 'SAVINGS', 4),
('****9012', 'GBP', 'ACTIVE', 'CHECKING', 4),
('****1122', 'EUR', 'ACTIVE', 'SAVINGS', 5);

-- 9. Insert Transactions for Account 1 (Akash's Savings)
INSERT INTO transaction_ref (amount, currency, narrative, txn_date, txn_type, account_id) VALUES 
(150.50, 'GBP', 'Grocery Store', NOW() - INTERVAL 1 DAY, 'DEBIT', 1),
(2000.00, 'GBP', 'Salary Deposit', NOW() - INTERVAL 5 DAY, 'CREDIT', 1),
(45.00, 'GBP', 'Electricity Bill', NOW() - INTERVAL 10 DAY, 'DEBIT', 1);

-- 10. Insert Consents
INSERT INTO consent (created_date, expiry_date, resource_filterjson, scopejson, status, tpp_app_tpp_app_id, user_user_id) VALUES 
(NOW(), NOW() + INTERVAL 90 DAY, '["account_id:1","account_id:2"]', '["accounts","balances","transactions"]', 'ACTIVE', 1, 4),
(NOW() - INTERVAL 100 DAY, NOW() - INTERVAL 10 DAY, '["account_id:1"]', '["accounts"]', 'EXPIRED', 1, 4);

-- 11. Insert Consent Events
INSERT INTO consent_event (action, timestamp, consent_consent_id) VALUES 
('CREATED', NOW(), 1),
('AUTHORIZED', NOW(), 1);

-- 12. Insert Payment Initiations
INSERT INTO payment_initiation (amount, created_date, creditor_account_ref, currency, debtor_account_ref, status, tpp_app_id) VALUES 
(25.00, NOW() - INTERVAL 2 HOUR, 'GB12ABCD34567890123456', 'GBP', 'GB98XYZW12345678901234', 'COMPLETED', 2),
(100.00, NOW() - INTERVAL 5 MINUTE, 'GB12ABCD34567890123456', 'GBP', 'GB98XYZW12345678901234', 'PENDING', 2);

-- 13. Insert Funds Checks
INSERT INTO funds_check (amount, checked_date, currency, result, account_id, tpp_app_id) VALUES 
(50.00, NOW(), 'GBP', 'AVAILABLE', 1, 1),
(50000.00, NOW(), 'GBP', 'UNAVAILABLE', 1, 1);

-- 14. Insert System Notifications
INSERT INTO notification (category, created_date, message, recipient_id, recipient_type, status) VALUES 
('SECURITY', NOW(), 'New device login detected.', 4, 'CUSTOMER', 'UNREAD'),
('SYSTEM', NOW(), 'Scheduled maintenance on Sunday.', 4, 'CUSTOMER', 'READ'),
('COMPLIANCE', NOW(), 'Action required: Renew TPP certificate.', 3, 'TPP', 'UNREAD');

-- 15. Insert Incidents
INSERT INTO incident (description, reported_date, resolution_date, severity, status, title) VALUES 
('API latency spike in PISP gateway', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY, 'HIGH', 'RESOLVED', 'PISP Latency Issue'),
('Customer reported 403 error on consent page', NOW(), NULL, 'MEDIUM', 'OPEN', 'Consent Page Error');

-- 16. Insert SCA Events
INSERT INTO sca_event (action, method, reference_id, status, timestamp, user_user_id) VALUES 
('LOGIN', 'SMS_OTP', 'REF-98123', 'SUCCESS', NOW(), 4),
('PAYMENT', 'BIOMETRIC', 'REF-98124', 'FAILED', NOW() - INTERVAL 1 DAY, 4);

-- 17. Insert Audit Trail
INSERT INTO audit_trail (action, actor, details, resource, timestamp) VALUES 
('CREATE', 'Admin User', 'Created new API plan', 'API_PLAN', NOW()),
('UPDATE', 'Akash Kumar', 'Revoked consent', 'CONSENT', NOW()),
('DELETE', 'System Process', 'Archived old logs', 'SYSTEM', NOW());
