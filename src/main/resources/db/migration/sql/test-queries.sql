----------------------------------------------------------------------------------------------------------
-- 1) SELECT: Заказы за 7 дней с ФИО, товаром и статусом
SELECT
    o.id                 AS order_id,
    o.order_date,
    c.first_name || ' ' || c.last_name AS customer_name,
    p.description        AS product_description,
    os.name              AS status_name,
    o.quantity,
    (o.quantity * p.price) AS total_amount_now
FROM public."order" o
JOIN public.customer c      ON c.id = o.customer_id
JOIN public.product  p      ON p.id = o.product_id
JOIN public.order_status os ON os.id = o.status_id
WHERE o.order_date >= NOW() - INTERVAL '7 days'
ORDER BY o.order_date DESC, o.id DESC;

----------------------------------------------------------------------------------------------------------
-- 2) SELECT: Топ-3 самых популярных товаров по количеству заказанных единиц
SELECT
    p.id,
    p.description,
    SUM(o.quantity) AS total_qty
FROM public.product p
JOIN public."order" o ON o.product_id = p.id
GROUP BY p.id, p.description
ORDER BY total_qty DESC
LIMIT 3;

----------------------------------------------------------------------------------------------------------
-- 3) SELECT: Выручка по дням за последние 30 дней (текущая цена)
SELECT
    date_trunc('day', o.order_date)::date AS day,
    SUM(o.quantity * p.price)              AS revenue
FROM public."order" o
JOIN public.product p ON p.id = o.product_id
WHERE o.order_date >= NOW() - INTERVAL '30 days'
GROUP BY day
ORDER BY day DESC;

----------------------------------------------------------------------------------------------------------
-- 4) SELECT: ТОП клиентов по сумме заказов (топ-5)
SELECT
    c.id,
    c.first_name,
    c.last_name,
    COUNT(*)                  AS orders_cnt,
    SUM(o.quantity * p.price) AS total_spent
FROM public.customer c
JOIN public."order" o ON o.customer_id = c.id
JOIN public.product  p ON p.id = o.product_id
GROUP BY c.id, c.first_name, c.last_name
ORDER BY total_spent DESC
LIMIT 5;

----------------------------------------------------------------------------------------------------------
-- 5) SELECT: Товары с низким остатком и количеством открытых заказов по ним
-- (низкий остаток: < 10; открытые статусы: NEW, PROCESSING, ON_HOLD)
SELECT
    p.id,
    p.description,
    p.quantity AS stock_qty,
    COALESCE(SUM(CASE WHEN os.name IN ('NEW','PROCESSING','ON_HOLD') THEN o.quantity END), 0) AS open_orders_qty
FROM public.product p
LEFT JOIN public."order" o      ON o.product_id = p.id
LEFT JOIN public.order_status os ON os.id = o.status_id
GROUP BY p.id, p.description, p.quantity
HAVING p.quantity < 10
ORDER BY stock_qty ASC, open_orders_qty DESC;
----------------------------------------------------------------------------------------------------------
-- 6) UPDATE: пометить оплаченные заказы как SHIPPED, если они старше 1 дня
UPDATE public."order" o
SET status_id = (SELECT id FROM public.order_status WHERE name = 'SHIPPED')
WHERE o.status_id = (SELECT id FROM public.order_status WHERE name = 'PAID')
  AND o.order_date < NOW() - INTERVAL '1 day'
RETURNING o.id, o.order_date;

----------------------------------------------------------------------------------------------------------
-- 7) UPDATE: нормализация телефона клиента (убрать пробелы/дефисы/скобки)
-- (пример простой очистки; при необходимости добавьте более строгий шаблон)
UPDATE public.customer
SET phone = REGEXP_REPLACE(phone, '[\s\-\(\)]', '', 'g')
WHERE phone ~ '[\s\-\(\)]'
RETURNING id, phone;

----------------------------------------------------------------------------------------------------------
-- 8) UPDATE: уменьшить остаток товара по одному конкретному заказу
-- (пример: применить отгрузку для заказа с заданным id)
WITH v AS (
    --SELECT :order_id::bigint AS order_id   --Можно через переменную, но через IDE не работает
    SELECT 1::bigint AS order_id  -- пример заказа 1
)
UPDATE public.product p
SET quantity = p.quantity - o.quantity
FROM public."order" o, v
WHERE o.id = v.order_id
  AND o.product_id = p.id
  AND p.quantity >= o.quantity
RETURNING p.id AS product_id, p.quantity AS new_stock;

----------------------------------------------------------------------------------------------------------
-- 9) DELETE: удалить клиентов без единого заказа
DELETE FROM public.customer c
WHERE NOT EXISTS (
    SELECT 1 FROM public."order" o WHERE o.customer_id = c.id
)
RETURNING c.id, c.first_name, c.last_name;

----------------------------------------------------------------------------------------------------------
-- 10) DELETE: удалить отменённые заказы старше 90 дней
DELETE FROM public."order" o
WHERE o.status_id = (SELECT id FROM public.order_status WHERE name = 'CANCELLED')
  AND o.order_date < NOW() - INTERVAL '90 days'
RETURNING o.id, o.order_date;
