-- Sample data for the demo. These are made-up demo accounts, not real people or passwords.
INSERT INTO users (username, password, full_name, role, phone, position) VALUES
    ('admin', 'admin123', 'Nguyen Thi Mai', 'ADMIN',    NULL,         'Manager'),
    ('hung',  'staff123', 'Le Van Hung',    'STAFF',    NULL,         'Cashier'),
    ('linh',  'staff123', 'Pham Thu Linh',  'STAFF',    NULL,         'Waiter'),
    ('lan',   'lan123',   'Tran Thi Lan',   'CUSTOMER', '0901234567', NULL),
    ('minh',  'minh123',  'Vo Quang Minh',  'CUSTOMER', '0912345678', NULL);

INSERT INTO menu_items (name, category, price, available) VALUES
    ('Pho bo',          'MAIN',    45000, 1),
    ('Bun cha',         'MAIN',    40000, 1),
    ('Com tam',         'MAIN',    38000, 1),
    ('Banh mi',         'MAIN',    25000, 1),
    ('Mi xao hai san',  'MAIN',    55000, 1),
    ('Iced tea',        'DRINK',   10000, 1),
    ('Ca phe sua da',   'DRINK',   20000, 1),
    ('Orange juice',    'DRINK',   30000, 1),
    ('Coconut water',   'DRINK',   25000, 1),
    ('Che ba mau',      'DESSERT', 20000, 1),
    ('Flan',            'DESSERT', 15000, 1),
    ('Mango sticky rice', 'DESSERT', 35000, 1),
    ('Spring rolls',    'SIDE',    30000, 1),
    ('French fries',    'SIDE',    25000, 1),
    ('Green salad',     'SIDE',    20000, 1);

INSERT INTO dining_tables (number, seats) VALUES
    (1, 2), (2, 2), (3, 4), (4, 4), (5, 6), (6, 8);
