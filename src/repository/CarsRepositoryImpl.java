package repository;

import model.Car;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CarsRepositoryImpl implements CarsRepository {
    private static final String DATA_FILE = "data/cars.txt";

    @Override
    public List<Car> findAll() {
        List<Car> cars = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 5) {
                        Car car = new Car(
                                parts[0].trim(),
                                parts[1].trim(),
                                parts[2].trim(),
                                Long.parseLong(parts[3].trim()),
                                Long.parseLong(parts[4].trim())
                        );
                        cars.add(car);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
        return cars;
    }

    @Override
    public void saveAll(List<Car> cars) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Car car : cars) {
                writer.println(car.getNumber() + "|" +
                        car.getModel() + "|" +
                        car.getColor() + "|" +
                        car.getMileage() + "|" +
                        car.getCost());
            }
        } catch (IOException e) {
            System.err.println("Ошибка записи файла: " + e.getMessage());
        }
    }

    @Override
    public List<Car> findByColorOrMileage(String color, long mileage) {
        return findAll().stream()
                .filter(car -> color.equals(car.getColor()) || car.getMileage() == mileage)
                .collect(Collectors.toList());
    }

    @Override
    public long countUniqueModelsInPriceRange(long minPrice, long maxPrice) {
        return findAll().stream()
                .filter(car -> car.getCost() >= minPrice && car.getCost() <= maxPrice)
                .map(Car::getModel)
                .distinct()
                .count();
    }

    @Override
    public Car findCheapestCar() {
        return findAll().stream()
                .min(Comparator.comparingLong(Car::getCost))
                .orElse(null);
    }

    @Override
    public double calculateAverageCostByModel(String model) {
        return findAll().stream()
                .filter(car -> model.equals(car.getModel()))
                .mapToLong(Car::getCost)
                .average()
                .orElse(0.0);
    }
}