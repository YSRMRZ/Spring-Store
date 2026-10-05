-- 1. Insert Categories (TINYINT IDs)
INSERT INTO categories (name)
VALUES ('Electronics'),
       ('Apparel & Fashion'),
       ('Home & Kitchen'),
       ('Fitness & Outdoors'),
       ('Books & Media');

-- 2. Insert Products (10 Real-World Examples linked to Categories)
INSERT INTO products (name, price, description, category_id)
VALUES ('Sony WH-1000XM5 Wireless Headphones', 398.00,
        'Industry-leading noise-canceling headphones with 30-hour battery life and crystal-clear hands-free calling.',
        1),
       ('Apple iPad Air (11-inch, M2)', 599.00,
        'Powered by the M2 chip with a Liquid Retina display, Wi-Fi 6E, and support for Apple Pencil Pro.', 1),
       ('Logitech MX Master 3S Wireless Mouse', 99.99,
        'Ergonomic performance mouse with 8K DPI tracking and quiet clicks.', 1),
       ('Men Classic Fit Denim Jacket', 69.50,
        'Timeless trucker jacket made from 100% durable cotton denim with button closure.', 2),
       ('Women Ultra-Lightweight Down Jacket', 89.90, 'Water-resistant, packable winter coat with premium insulation.',
        2),
       ('Instant Pot Duo 7-in-1 Pressure Cooker', 89.95,
        'Multi-functional cooker: pressure cooker, slow cooker, rice cooker, steamer, and yogurt maker.', 3),
       ('Nespresso VertuoPlus Coffee Machine', 169.00,
        'Single-serve coffee and espresso maker utilizing Centrifusion technology for rich crema.', 3),
       ('Manduka PRO Yoga Mat (6mm)', 138.00,
        'High-density, non-slip cushioning yoga mat designed for longevity and joint support.', 4),
       ('Hydro Flask 32 oz Wide Mouth Bottle', 44.95,
        'Vacuum-insulated stainless steel water bottle that keeps drinks cold for up to 24 hours.', 4),
       ('Designing Data-Intensive Applications', 42.50,
        'Comprehensive guide to data system architectures by Martin Kleppmann.', 5);