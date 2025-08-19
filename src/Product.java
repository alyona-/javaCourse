import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Product {
    private String name;
    private double price; // базовая цена (без скидки)

    public Product(String name, double price) {
        setName(name);
        setPrice(price);
    }

    public String getName() { return name; }

    /** Текущая цена. Для обычного товара — базовая. Для скидочного будет переопределено. */
    public double getPrice() { return price; }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Название продукта не может быть пустым");
        String n = name.trim();
        if (n.length() < 3)
            throw new IllegalArgumentException("Название продукта короче 3 символов");
        if (n.matches("\\d+"))
            throw new IllegalArgumentException("Название продукта не должно состоять только из цифр");
        this.name = n;
    }

    public void setPrice(double price) {
        if (price <= 0)
            throw new IllegalArgumentException("Стоимость продукта должна быть больше 0");
        this.price = price;
    }

    // ===== Ввод/парсинг продуктов =====

    public static Map<String, Product> readProducts(Scanner scanner) {
        Map<String, Product> products = new LinkedHashMap<>();
        System.out.println("\nВведите продукты. Примеры:");
        System.out.println("  Хлеб = 40");
        System.out.println("  Торт = 1000 [discount=15% until=2025-12-31]");
        System.out.println("Пустая строка — переход к покупкам:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) break;
            String[] entries = input.split(";");
            for (String entry : entries) {
                try {
                    Product p = parse(entry.trim());
                    products.put(p.getName(), p);
                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
        return products;
    }

    /** Поддерживает обычный формат и скидочный блок в квадратных скобках. */
    public static Product parse(String input) {
        if (input == null || !input.contains("="))
            throw new IllegalArgumentException("Неверный формат продукта. Пример: Хлеб = 40");

        String[] parts = input.split("=", 2);
        String name = parts[0].trim();
        String rhs = parts[1].trim();

        String pricePart = rhs;
        String params = null;
        int idx = rhs.indexOf('[');
        if (idx >= 0 && rhs.endsWith("]")) {
            pricePart = rhs.substring(0, idx).trim();
            params = rhs.substring(idx + 1, rhs.length() - 1).trim();
        }

        double price = parseDouble(pricePart);

        if (params == null || params.isEmpty()) {
            return new Product(name, price);
        }

        Map<String, String> kv = splitParams(params);
        String discStr = firstNotNull(kv.get("discount"), kv.get("скидка"));
        String untilStr = firstNotNull(kv.get("until"), kv.get("до"));

        if (discStr == null || untilStr == null) {
            // Если не хватает параметров — трактуем как обычный продукт
            return new Product(name, price);
        }

        double discountPercent = parsePercent(discStr);
        LocalDate until = parseDateFlexible(untilStr);
        return new DiscountProduct(name, price, discountPercent, until);
    }

    private static Map<String, String> splitParams(String params) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String token : params.split("\\s+")) {
            String[] kv = token.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0].trim().toLowerCase(), kv[1].trim());
            }
        }
        return map;
    }

    private static String firstNotNull(String a, String b) { return a != null ? a : b; }

    private static double parseDouble(String s) {
        try {
            return Double.parseDouble(s.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат стоимости: \"" + s + "\"");
        }
    }

    private static double parsePercent(String s) {
        String cleaned = s.replace("%", "").replace(",", ".").trim();
        double v;
        try {
            v = Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат скидки: \"" + s + "\"");
        }
        if (v <= 0 || v >= 100)
            throw new IllegalArgumentException("Скидка должна быть в диапазоне (0; 100)");
        return v;
    }

    private static LocalDate parseDateFlexible(String s) {
        s = s.trim();
        try {
            return LocalDate.parse(s); // ISO yyyy-MM-dd
        } catch (DateTimeParseException ignore) { }
        try {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты: \"" + s + "\". Используйте YYYY-MM-DD или dd.MM.yyyy");
        }
    }

    @Override
    public String toString() { return name + " (" + getPrice() + ")"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return name.equals(product.name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }
}
