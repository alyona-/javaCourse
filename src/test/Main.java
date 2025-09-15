package test;
import model.Car;
import repository.CarsRepository;
import repository.CarsRepositoryImpl;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        CarsRepository repository = new CarsRepositoryImpl();

        // Чтение данных из файла
        List<Car> allCars = repository.findAll();

        // Вывод всех автомобилей
        System.out.println("Автомобили в базе:");
        System.out.println("Number Model Color Mileage Cost");
        for (Car car : allCars) {
            System.out.println(car);
        }
        System.out.println();

        // 1. Номера автомобилей по цвету или пробегу
        List<Car> filteredCars = repository.findByColorOrMileage("Black", 0L);
        System.out.print("Номера автомобилей по цвету или пробегу: ");
        for (int i = 0; i < filteredCars.size(); i++) {
            if (i == 4) System.out.println();
            System.out.print(filteredCars.get(i).getNumber() +
                    (i < filteredCars.size() - 1 ? " " : ""));
        }
        System.out.println();

        // 2. Количество уникальных моделей в ценовом диапазоне
        long uniqueCount = repository.countUniqueModelsInPriceRange(700_000L, 800_000L);
        System.out.println("Уникальные автомобили: " + uniqueCount + " шт.");

        // 3. Цвет автомобиля с минимальной стоимостью
        Car cheapestCar = repository.findCheapestCar();
        if (cheapestCar != null) {
            System.out.println("Цвет автомобиля с минимальной стоимостью: " + cheapestCar.getColor());
        } else {
            System.out.println("Цвет автомобиля с минимальной стоимостью: Не найден");
        }

        // 4. Средняя стоимость моделей
        double avgToyota = repository.calculateAverageCostByModel("Toyota");
        double avgVolvo = repository.calculateAverageCostByModel("Volvo");
        System.out.printf("Средняя стоимость модели Toyota: %.2f%n", avgToyota);
        System.out.printf("Средняя стоимость модели Volvo: %.2f%n", avgVolvo);
    }
}