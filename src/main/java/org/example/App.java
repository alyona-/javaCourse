package org.example;

import org.flywaydb.core.Flyway;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Properties;

public class App {
    public static void main(String[] args) {
        // 1) Загружаем application.properties
        Properties prop = new Properties();
        try (InputStream in = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) throw new IOException("application.properties не найден");
            prop.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки application.properties", e);
        }

        // 2) Читаем параметры подключения и флаги
        String url  = prop.getProperty("db.url");
        String user = prop.getProperty("db.user");
        String pass = prop.getProperty("db.password");

        boolean flywayEnabled = Boolean.parseBoolean(prop.getProperty("flyway.enabled", "true"));
        String locations      = prop.getProperty("flyway.locations", "classpath:db/migration");
        String schemas        = prop.getProperty("flyway.schemas", "public");
        boolean cleanOnStart  = Boolean.parseBoolean(prop.getProperty("flyway.cleanOnStart", "true"));
        boolean deleteDemo    = Boolean.parseBoolean(prop.getProperty("demo.deleteAfter", "false")); // удалять демо-данные в конце?

        // 3) Flyway миграции (опционально)
        if (flywayEnabled) {
            Flyway flyway = Flyway.configure()
                    .loggers("slf4j")
                    .dataSource(url, user, pass)
                    .locations(locations)
                    .schemas(schemas)
                    .cleanDisabled(!cleanOnStart)
                    .load();

            if (cleanOnStart) {
                flyway.clean();
            }
            var result = flyway.migrate();
            System.out.printf("Выполнено миграций: %d%n", result.migrationsExecuted);
        }

        // 4) Транзакция:  CRUD
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            conn.setAutoCommit(false);
            try {
                // --- 4.1 INSERT product ---
                Long newProductId;
                String insertProductSql = """
                        INSERT INTO public.product(description, price, quantity, category, created_at)
                        VALUES (?, ?, ?, ?, NOW())
                        RETURNING id
                        """;
                try (PreparedStatement ps = conn.prepareStatement(insertProductSql)) {
                    ps.setString(1, "Тестовый товар (demo)");
                    ps.setBigDecimal(2, new BigDecimal("12345.67"));
                    ps.setInt(3, 5);
                    ps.setString(4, "demo");
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        newProductId = rs.getLong(1);
                        System.out.println("Добавлен товар id=" + newProductId);
                    }
                }

                // --- 4.2 INSERT customer ---
                Long newCustomerId;
                String uniqueEmail = "demo_buyer_" + System.currentTimeMillis() + "@example.com";
                String insertCustomerSql = """
                        INSERT INTO public.customer(first_name, last_name, phone, email, created_at)
                        VALUES (?, ?, ?, ?, NOW())
                        RETURNING id
                        """;
                try (PreparedStatement ps = conn.prepareStatement(insertCustomerSql)) {
                    ps.setString(1, "Демо");
                    ps.setString(2, "Покупатель");
                    ps.setString(3, "+7 (900) 000-00-00");
                    ps.setString(4, uniqueEmail);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        newCustomerId = rs.getLong(1);
                        System.out.println("Добавлен покупатель id=" + newCustomerId + ", email=" + uniqueEmail);
                    }
                }

                // --- 4.3 INSERT order (status NEW) ---
                int orderQty = 2;
                long statusNewId;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT id FROM public.order_status WHERE name = ?")) {
                    ps.setString(1, "NEW");
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new IllegalStateException("Статус NEW не найден");
                        statusNewId = rs.getLong(1);
                    }
                }

                long newOrderId;
                String insertOrderSql = """
                        INSERT INTO public."order"(product_id, customer_id, order_date, quantity, status_id)
                        VALUES (?, ?, NOW(), ?, ?)
                        RETURNING id
                        """;
                try (PreparedStatement ps = conn.prepareStatement(insertOrderSql)) {
                    ps.setLong(1, newProductId);
                    ps.setLong(2, newCustomerId);
                    ps.setInt(3, orderQty);
                    ps.setLong(4, statusNewId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        newOrderId = rs.getLong(1);
                        System.out.println("Создан заказ id=" + newOrderId);
                    }
                }

                // --- 4.4 SELECT: последние 5 заказов с JOIN ---
                String last5Sql = """
                        SELECT o.id,
                               o.order_date,
                               c.first_name || ' ' || c.last_name AS customer_name,
                               p.description AS product_desc,
                               os.name AS status_name,
                               o.quantity,
                               (o.quantity * p.price) AS total_amount_now
                        FROM public."order" o
                        JOIN public.customer c      ON c.id = o.customer_id
                        JOIN public.product  p      ON p.id = o.product_id
                        JOIN public.order_status os ON os.id = o.status_id
                        ORDER BY o.order_date DESC, o.id DESC
                        LIMIT 5
                        """;
                try (PreparedStatement ps = conn.prepareStatement(last5Sql);
                     ResultSet rs = ps.executeQuery()) {
                    System.out.println("Последние 5 заказов:");
                    while (rs.next()) {
                        System.out.printf(" - #%d | %s | %s | %s | %s | qty=%d | total=%.2f%n",
                                rs.getLong("id"),
                                rs.getTimestamp("order_date"),
                                rs.getString("customer_name"),
                                rs.getString("product_desc"),
                                rs.getString("status_name"),
                                rs.getInt("quantity"),
                                rs.getBigDecimal("total_amount_now"));
                    }
                }

                // --- 4.5 UPDATE: поднять цену + уменьшить остаток по заказу ---
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE public.product " +
                                "SET price = price + ?, quantity = quantity - ? " +
                                "WHERE id = ? " +
                                "RETURNING price, quantity")) {
                    ps.setBigDecimal(1, new BigDecimal("1000"));
                    ps.setInt(2, orderQty);
                    ps.setLong(3, newProductId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            System.out.printf("Обновили товар id=%d: новая цена=%s, остаток=%d%n",
                                    newProductId, rs.getBigDecimal("price"), rs.getInt("quantity"));
                        }
                    }
                }

                // --- 4.6 (опционально) DELETE демо-данных ---
                if (deleteDemo) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM public.\"order\" WHERE id = ?")) {
                        ps.setLong(1, newOrderId);
                        int r = ps.executeUpdate();
                        System.out.println("Удалено заказов: " + r);
                    }
                    try (PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM public.product WHERE id = ?")) {
                        ps.setLong(1, newProductId);
                        int r = ps.executeUpdate();
                        System.out.println("Удалено товаров: " + r);
                    }
                    try (PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM public.customer WHERE id = ?")) {
                        ps.setLong(1, newCustomerId);
                        int r = ps.executeUpdate();
                        System.out.println("Удалено покупателей: " + r);
                    }
                }

                conn.commit();
                System.out.println("Транзакция зафиксирована (commit).");
            } catch (Exception e) {
                conn.rollback();
                System.out.println("Ошибка. Транзакция отменена (rollback).");
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
