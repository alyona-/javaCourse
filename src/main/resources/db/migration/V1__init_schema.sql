-- Удаляю старые версии таблиц, если есть (с учётом зарезервированного "order")
--DROP TABLE IF EXISTS public."order" CASCADE;
--DROP TABLE IF EXISTS public.order_status CASCADE;
--DROP TABLE IF EXISTS public.customer CASCADE;
--DROP TABLE IF EXISTS public.product CASCADE;

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