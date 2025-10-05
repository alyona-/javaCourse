---Этот файл нужен для задания 2 , при запуске приложения используются только файлы V1_ , V2
-- === DDL ===
-- Удаляю старые версии таблиц, если есть (с учётом зарезервированного "order")
DROP TABLE IF EXISTS public."order" CASCADE;
DROP TABLE IF EXISTS public.order_status CASCADE;
DROP TABLE IF EXISTS public.customer CASCADE;
DROP TABLE IF EXISTS public.product CASCADE;

CREATE TABLE IF NOT EXISTS public.product (
    id              BIGSERIAL PRIMARY KEY,
    description     TEXT            NOT NULL,
    price           NUMERIC(12,2)   NOT NULL CHECK (price >= 0),
    quantity        INTEGER         NOT NULL CHECK (quantity >= 0),
    category        VARCHAR(64)     NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE public.product IS 'Товары';
COMMENT ON COLUMN public.product.id IS 'PK: идентификатор товара';
COMMENT ON COLUMN public.product.description IS 'Описание товара';
COMMENT ON COLUMN public.product.price IS 'Стоимость (≥ 0)';
COMMENT ON COLUMN public.product.quantity IS 'Количество на складе (≥ 0)';
COMMENT ON COLUMN public.product.category IS 'Категория товара';
COMMENT ON COLUMN public.product.created_at IS 'Дата/время добавления';

CREATE TABLE IF NOT EXISTS public.customer (
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(64) NOT NULL,
    last_name   VARCHAR(64) NOT NULL,
    phone       VARCHAR(32) NOT NULL,
    email       VARCHAR(128) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_customer_email UNIQUE (email),
    CONSTRAINT uq_customer_phone UNIQUE (phone)
);
COMMENT ON TABLE public.customer IS 'Покупатели';
COMMENT ON COLUMN public.customer.id IS 'PK: идентификатор покупателя';
COMMENT ON COLUMN public.customer.first_name IS 'Имя';
COMMENT ON COLUMN public.customer.last_name IS 'Фамилия';
COMMENT ON COLUMN public.customer.phone IS 'Телефон (уникальный)';
COMMENT ON COLUMN public.customer.email IS 'Email (уникальный)';
COMMENT ON COLUMN public.customer.created_at IS 'Дата/время регистрации';

CREATE TABLE IF NOT EXISTS public.order_status (
    id      SMALLSERIAL PRIMARY KEY,
    name    VARCHAR(32) NOT NULL UNIQUE
);
COMMENT ON TABLE public.order_status IS 'Справочник статусов заказов';
COMMENT ON COLUMN public.order_status.id IS 'PK: идентификатор статуса';
COMMENT ON COLUMN public.order_status.name IS 'Имя статуса (уникальное)';

-- ВАЖНО: "order" — зарезервированное слово SQL, используем кавычки
CREATE TABLE IF NOT EXISTS public."order" (
    id              BIGSERIAL PRIMARY KEY,
    product_id      BIGINT      NOT NULL REFERENCES public.product(id)   ON UPDATE CASCADE ON DELETE RESTRICT,
    customer_id     BIGINT      NOT NULL REFERENCES public.customer(id)  ON UPDATE CASCADE ON DELETE RESTRICT,
    order_date      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    quantity        INTEGER     NOT NULL CHECK (quantity > 0),
    status_id       SMALLINT    NOT NULL REFERENCES public.order_status(id)
);
COMMENT ON TABLE public."order" IS 'Заказы';
COMMENT ON COLUMN public."order".id IS 'PK: идентификатор заказа';
COMMENT ON COLUMN public."order".product_id IS 'FK → product.id';
COMMENT ON COLUMN public."order".customer_id IS 'FK → customer.id';
COMMENT ON COLUMN public."order".order_date IS 'Дата/время заказа';
COMMENT ON COLUMN public."order".quantity IS 'Количество ( > 0 )';
COMMENT ON COLUMN public."order".status_id IS 'FK → order_status.id';

-- === Индексы по внешним ключам и дате заказа ===
CREATE INDEX IF NOT EXISTS idx_order_product_id   ON public."order"(product_id);
CREATE INDEX IF NOT EXISTS idx_order_customer_id  ON public."order"(customer_id);
CREATE INDEX IF NOT EXISTS idx_order_order_date   ON public."order"(order_date);
CREATE INDEX IF NOT EXISTS idx_product_category   ON public.product(category);

COMMENT ON INDEX idx_order_product_id  IS 'Индекс по FK product_id';
COMMENT ON INDEX idx_order_customer_id IS 'Индекс по FK customer_id';
COMMENT ON INDEX idx_order_order_date  IS 'Индекс по дате заказа';
COMMENT ON INDEX idx_product_category  IS 'Индекс по категории товара';

-- === TEST DATA (>=10 rows per table) ===

truncate table public.order_status cascade;
-- 10+ статусов
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
truncate table public.product cascade;
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
truncate table public.customer cascade;
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
 ('Кирилл','Новиков',   '+7-900-111-11-10', 'kirill10@example.com');
 INSERT INTO public.customer (first_name, last_name, phone, email)
VALUES
 ('Лена',   'Серова',    '+7-900-111-11-11', 'lena11@example.com'),
 ('Максим', 'Громов',    '+7-900-111-11-12', 'maxim12@example.com'),
 ('Нина',   'Беляева',   '+7-900-111-11-13', 'nina13@example.com');

-- Заказы
truncate table public."order" cascade;
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
