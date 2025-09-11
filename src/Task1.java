import java.util.*;
import java.util.stream.Collectors;

/** Task1 — уникальные элементы ArrayList<T> */
public class Task1 {

    /** Вариант 1 (классический): LinkedHashSet — удаляет дубли и сохраняет порядок */
    public static <T> Set<T> uniqueClassic(ArrayList<T> list) {
        return new LinkedHashSet<>(list);
    }

    /** Вариант 2 (Stream): distinct() + collect в LinkedHashSet для сохранения порядка */
    public static <T> Set<T> uniqueStream(ArrayList<T> list) {
        return list.stream()
                .distinct()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
