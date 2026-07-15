CREATE TABLE IF NOT EXISTS notification_templates (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    code          VARCHAR(100) NOT NULL UNIQUE,
    name          VARCHAR(200) NOT NULL,
    channel       VARCHAR(20)  NOT NULL,
    subject       VARCHAR(500),
    body_template TEXT         NOT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS notification_logs (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT,
    channel           VARCHAR(20)  NOT NULL,
    recipient_contact VARCHAR(300) NOT NULL,
    template_code     VARCHAR(100) NOT NULL,
    rendered_message  TEXT,
    status            VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    failure_reason    TEXT,
    sent_at           DATETIME(6),
    created_at        DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_notif_user    ON notification_logs(user_id);
CREATE INDEX idx_notif_status  ON notification_logs(status);
CREATE INDEX idx_notif_channel ON notification_logs(channel);

-- Seed templates
INSERT INTO notification_templates (code, name, channel, subject, body_template) VALUES
('ORDER_CONFIRMED',   'Order Confirmed',   'SMS',   NULL, 'Dear {{farmerName}}, your order #{{orderId}} has been confirmed. Total: ₹{{amount}}'),
('PAYMENT_SUCCESS',   'Payment Success',   'SMS',   NULL, 'Payment of ₹{{amount}} received for order #{{orderId}}. Thank you!'),
('SHIPMENT_CREATED',  'Shipment Created',  'SMS',   NULL, 'Your shipment #{{trackingNumber}} is on the way. Expected delivery: {{expectedDate}}'),
('FARMER_VERIFIED',   'Farmer Verified',   'SMS',   NULL, 'Congratulations {{farmerName}}! Your farm profile has been verified.'),
('OTP_LOGIN',         'OTP Login',         'SMS',   NULL, 'Your Farm Kart OTP is {{otp}}. Valid for 5 minutes. Do not share.'),
('ORDER_CONFIRMED_EMAIL', 'Order Confirmed Email', 'EMAIL', 'Order Confirmed - Farm Kart', 'Dear {{buyerName}},\n\nYour order #{{orderId}} for {{cropName}} has been confirmed.\nAmount: ₹{{amount}}\n\nRegards,\nFarm Kart Team');
