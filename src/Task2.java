import java.text.Normalizer;
import java.util.Arrays;

/** Task2 — проверка анаграмм (кириллица/латиница; игнор регистра, пробелов, пунктуации) */
public class Task2 {

    /** Вариант 1 (сортировка): нормализуем, сортируем массивы кодпоинтов и сравниваем */
    public static boolean isAnagramSort(String s, String t) {
        int[] a = normalizedLetters(s);
        int[] b = normalizedLetters(t);
        Arrays.sort(a);
        Arrays.sort(b);
        return Arrays.equals(a, b);
    }

    /** Вариант 2 (Map-счётчик): считаем частоты символов и сравниваем (O(n)) */
    public static boolean isAnagramCount(String s, String t) {
        java.util.Map<Integer, Integer> freq = new java.util.HashMap<>();
        for (int cp : normalizedLetters(s)) freq.merge(cp, 1, Integer::sum);
        for (int cp : normalizedLetters(t)) freq.merge(cp, -1, Integer::sum);
        for (int v : freq.values()) if (v != 0) return false;
        return true;
    }

    /** Нормализация: NFKD, удаление диакритики, берём только буквы, приводим к lowerCase (по Unicode) */
    private static int[] normalizedLetters(String s) {
        String nfkd = Normalizer.normalize(s, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "");
        return nfkd.codePoints()
                .filter(Character::isLetter)
                .map(Character::toLowerCase)
                .toArray();
    }
}
