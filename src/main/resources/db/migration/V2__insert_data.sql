--Если раскоментировать truncate то можно использовать как отдельный скрипт перезаписи, но мне не нужно так
--как я каждый раз пересоздаю таблицу
--truncate table public.order_status cascade;
INSERT INTO public.order_status (id, name) VALUES
(1,'NEW'),
(2,'PAID'),
(3,'SHIPPED'),
(4,'CANCELLED'),
(5,'PROCESSING'),
(6,'ON_HOLD'),
(7,'COMPLETED'),
(8,'RETURNED'),
(9,'REFUNDED'),
(10,'FAILED')
ON CONFLICT DO NOTHING;

-- Товары
--truncate table public.product cascade;
INSERT INTO public.product (description, price, quantity, category) VALUES
('Ноутбук 14"',     69990, 15,  'electronics'),
('Смартфон X',      49990, 40,  'electronics'),
('Наушники BT',      5990, 100, 'electronics'),
('Кофеварка',       12990, 20,  'home'),
('Чайник умный',     3990, 35,  'home'),
('Книга Java',       1490, 200, 'books'),
('Кресло офисное',   9990, 12,  'furniture'),
('Роутер Wi-Fi 6',   6990, 25,  'electronics'),
('SSD 1TB',          7990, 30,  'electronics'),
('Клавиатура мех.',  4990, 50,  'electronics');

-- Покупатели
--truncate table public.customer cascade;
INSERT INTO public.customer (first_name, last_name, phone, email) VALUES
('Анна',  'Соколова',  '+7-900-111-11-01', 'anna1@example.com'),
('Борис', 'Иванов',    '+7-900-111-11-02', 'boris2@example.com'),
('Вера',  'Кузнецова', '+7-900-111-11-03', 'vera3@example.com'),
('Глеб',  'Волков',    '+7-900-111-11-04', 'gleb4@example.com'),
('Дина',  'Егорова',   '+7-900-111-11-05', 'dina5@example.com'),
('Ева',   'Лебедева',  '+7-900-111-11-06', 'eva6@example.com'),
('Женя',  'Смирнов',   '+7-900-111-11-07', 'jenya7@example.com'),
('Зоя',   'Комарова',  '+7-900-111-11-08', 'zoya8@example.com'),
('Илья',  'Морозов',   '+7-900-111-11-09', 'ilya9@example.com'),
('Кирилл','Новиков',   '+7-900-111-11-10', 'kirill10@example.com')
ON CONFLICT (email) DO NOTHING;
INSERT INTO public.customer (first_name, last_name, phone, email)
VALUES
('Лена',   'Серова',    '+7-900-111-11-11', 'lena11@example.com'),
('Максим', 'Громов',    '+7-900-111-11-12', 'maxim12@example.com'),
('Нина',   'Беляева',   '+7-900-111-11-13', 'nina13@example.com')
ON CONFLICT (email) DO NOTHING;

-- Заказы
--truncate table public."order" cascade;
INSERT INTO public."order" (product_id, customer_id, order_date, quantity, status_id) VALUES
(1, 1,  NOW() - INTERVAL '1 day',    1, 1),
(2, 2,  NOW() - INTERVAL '2 days',   2, 2),
(3, 3,  NOW() - INTERVAL '3 days',   1, 3),
(4, 4,  NOW() - INTERVAL '4 days',   1, 4),
(5, 5,  NOW() - INTERVAL '5 days',   3, 5),
(6, 6,  NOW() - INTERVAL '6 days',   2, 6),
(7, 7,  NOW() - INTERVAL '7 days',   1, 7),
(8, 8,  NOW() - INTERVAL '1 day',    1, 8),
(9, 9,  NOW() - INTERVAL '2 days',   2, 9),
(10,10, NOW() - INTERVAL '10 days',  1, 10);

INSERT INTO public."order" (product_id, customer_id, order_date, quantity, status_id)
VALUES
-- создадим 3 старых отменённых заказа (пример: 100, 120, 150 дней назад)
(1, 1, NOW() - INTERVAL '100 days', 1,
(SELECT id FROM public.order_status WHERE name = 'CANCELLED')),
(2, 2, NOW() - INTERVAL '120 days', 2,
(SELECT id FROM public.order_status WHERE name = 'CANCELLED')),
(3, 3, NOW() - INTERVAL '150 days', 1,
(SELECT id FROM public.order_status WHERE name = 'CANCELLED'));
