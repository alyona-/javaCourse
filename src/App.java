import java.util.*;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Person> people = Person.readPeople(scanner);
        Map<String, Product> products = Product.readProducts(scanner);
        processPurchases(scanner, people, products);
        printResults(people);
    }

    private static void processPurchases(Scanner scanner, Map<String, Person> people, Map<String, Product> products) {
        System.out.println("\nВыбирайте покупки (например: Павел - Хлеб). Завершите ввод словом END:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("END")) break;
            if (!input.contains("-")) continue;

            String[] parts = input.split("-", 2);
            if (parts.length != 2) continue;

            String personName = parts[0].trim();
            String productName = parts[1].trim();

            Person person = people.get(personName);
            Product product = products.get(productName);

            if (person == null) {
                System.out.println("Покупатель \"" + personName + "\" не найден");
                continue;
            }
            if (product == null) {
                System.out.println("Продукт \"" + productName + "\" не найден");
                continue;
            }

            person.buy(product);
        }
    }

    private static void printResults(Map<String, Person> people) {
        for (Person person : people.values()) {
            System.out.println(person);
        }
    }
}
