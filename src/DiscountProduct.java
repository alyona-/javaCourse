
import java.time.LocalDate;

public class DiscountProduct extends Product {
    private final double discountPercent; // (0;100)
    private final LocalDate validUntil;   // включительно

    public DiscountProduct(String name, double basePrice, double discountPercent, LocalDate validUntil) {
        super(name, basePrice); // super(...)
        if (validUntil == null)
            throw new IllegalArgumentException("Для скидочного продукта должна быть указана дата окончания скидки");
        if (discountPercent <= 0 || discountPercent >= 100)
            throw new IllegalArgumentException("Скидка должна быть в диапазоне (0; 100)");
        // Проверяем, что цена со скидкой > 0
        double effective = basePrice * (1.0 - discountPercent / 100.0);
        if (effective <= 0)
            throw new IllegalArgumentException("Итоговая цена со скидкой должна быть больше 0");
        this.discountPercent = discountPercent;
        this.validUntil = validUntil;
    }

    public double getDiscountPercent() { return discountPercent; }
    public LocalDate getValidUntil() { return validUntil; }

    @Override
    public double getPrice() {
        double base = super.getPrice(); // опора на родителя
        if (!LocalDate.now().isAfter(validUntil)) {
            double effective = base * (1.0 - discountPercent / 100.0);
            if (effective <= 0)
                throw new IllegalStateException("Итоговая цена скидочного продукта стала некорректной");
            return effective;
        }
        // Срок скидки прошёл — цена меняется на базовую
        return base;
    }

    @Override
    public String toString() {
        return getName() + " (база: " + super.getPrice() + ", скидка " + discountPercent + "% до " + validUntil + ", сейчас: " + getPrice() + ")";
    }
}
