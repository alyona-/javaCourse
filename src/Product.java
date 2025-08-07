import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Objects;
class Product {
    private String name;
    private double price;


    public Product(String name, double price) {
        setName(name);
        setPrice(price);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название продукта не может быть пустым");
        }
        this.name = name.trim();
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Стоимость продукта не может быть отрицательной");
        }
        this.price = price;
    }

    public static Map<String, Product> readProducts(Scanner scanner) {
        Map<String, Product> products = new LinkedHashMap<>();
        System.out.println("\nВведите продукты (например: Хлеб = 40; Торт = 1000). Пустая строка — переход к покупкам:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) break;
            String[] entries = input.split(";");
            for (String entry : entries) {
                try {
                    Product p = parse(entry);
                    products.put(p.getName(), p);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return products;
    }

    public static Product parse(String input) {
        if (!input.contains("=")) {
            throw new IllegalArgumentException("Неверный формат продукта. Пример: Хлеб = 40");
        }
        String[] parts = input.split("=");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Неверный формат продукта. Пример: Хлеб = 40");
        }
        String name = parts[0].trim();
        double price;
        try {
            price = Double.parseDouble(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат стоимости для " + name);
        }
        return new Product(name, price);
    }

    @Override
    public String toString() {
        return name + " (" + price + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Double.compare(product.price, price) == 0 && name.equals(product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }


}