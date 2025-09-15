package repository;

import model.Car;
import java.util.List;

public interface CarsRepository {
    List<Car> findAll();
    void saveAll(List<Car> cars);
    List<Car> findByColorOrMileage(String color, long mileage);
    long countUniqueModelsInPriceRange(long minPrice, long maxPrice);
    Car findCheapestCar();
    double calculateAverageCostByModel(String model);
}