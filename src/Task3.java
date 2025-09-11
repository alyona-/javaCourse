import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Task3 — PowerfulSet: intersection/union/relativeComplement */
public class Task3 {

    // ===== Требуемые по условию методы (реализация через Stream) =====

    /** Пересечение set1 ∩ set2 */
    public <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        return set1.stream()
                .filter(set2::contains)
                .collect(Collectors.toSet());
    }

    /** Объединение set1 ∪ set2 */
    public <T> Set<T> union(Set<T> set1, Set<T> set2) {
        return Stream.concat(set1.stream(), set2.stream())
                .collect(Collectors.toSet());
    }

    /** Относительное дополнение set1 \ set2 */
    public <T> Set<T> relativeComplement(Set<T> set1, Set<T> set2) {
        return set1.stream()
                .filter(e -> !set2.contains(e))
                .collect(Collectors.toSet());
    }

    // Решение двумя способами для сравнения. Классика и стримы

    public <T> Set<T> intersectionClassic(Set<T> set1, Set<T> set2) {
        Set<T> res = new HashSet<>(set1);
        res.retainAll(set2);
        return res;
    }

    public <T> Set<T> unionClassic(Set<T> set1, Set<T> set2) {
        Set<T> res = new HashSet<>(set1);
        res.addAll(set2);
        return res;
    }

    public <T> Set<T> relativeComplementClassic(Set<T> set1, Set<T> set2) {
        Set<T> res = new HashSet<>(set1);
        res.removeAll(set2);
        return res;
    }
}
