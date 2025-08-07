import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Person {
    private String name;
    private double money;
    private List<Product> products = new ArrayList<>();

    public Person(String name, double money) {
        setName(name);
        setMoney(money);
    }

    public String getName() {
        return name;
    }

    public double getMoney() {
        return money;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (name.trim().length() < 3) {
            throw new IllegalArgumentException("Имя не может быть короче 3 символов");
        }
        this.name = name.trim();
    }

    public void setMoney(double money) {
        if (money < 0) {
            throw new IllegalArgumentException("Деньги не могут быть отрицательными");
        }
        this.money = money;
    }

    public void buy(Product product) {
        if (product == null) return;

        if (this.money >= product.getPrice()) {
            this.money -= product.getPrice();
            this.products.add(product);
            System.out.println(this.name + " купил " + product.getName());
        } else {
            System.out.println(this.name + " не может позволить себе " + product.getName());
        }
    }

    public static Map<String, Person> readPeople(Scanner scanner) {
        Map<String, Person> people = new LinkedHashMap<>();
        System.out.println("Введите покупателей (пример: Павел = 10000; Анна = 2000). Пустая строка — переход к продуктам:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) break;
            String[] entries = input.split(";");
            for (String entry : entries) {
                try {
                    Person p = parse(entry);
                    people.put(p.getName(), p);
                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
        return people;
    }

    public static Person parse(String input) {
        if (!input.contains("=")) {
            throw new IllegalArgumentException("Неверный формат покупателя. Пример: Иван = 100");
        }
        String[] parts = input.split("=", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Неверный формат покупателя. Пример: Иван = 100");
        }
        String name = parts[0].trim();
        double money;
        try {
            money = Double.parseDouble(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неверный формат суммы для " + name);
        }
        return new Person(name, money);
    }

    @Override
    public String toString() {
        if (products.isEmpty()) {
            return name + " - Ничего не куплено";
        }
        List<String> names = new ArrayList<>();
        for (Product p : products) {
            names.add(p.getName());
        }
        return name + " - " + String.join(", ", names);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return name.equals(person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
