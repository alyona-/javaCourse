import java.util.*;
import java.nio.charset.StandardCharsets;

/** Main — Решаю задачи 2 способами. Обычным и через стримы
 *  Соответствует условию: для Task2 строки читаются с консоли. */
public class Main {
    public static void main(String[] args) {
        // Задача 1
        System.out.println("---------------Задача 1---------------");
        ArrayList<String> list = new ArrayList<>(List.of("a", "b", "a", "в", "б", "a", "в"));
        System.out.println("Исходный список: " + list);
        System.out.println("Уникальные (классика): " + Task1.uniqueClassic(list));
        System.out.println("Уникальные (Stream):   " + Task1.uniqueStream(list));
        System.out.println();

        // Задача 2
        System.out.println("---------------Задача 2---------------");
        try (Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8)) {
            System.out.print("Введите строку s: ");
            String s = sc.nextLine();
            System.out.print("Введите строку t: ");
            String t = sc.nextLine();

            boolean sortAns = Task2.isAnagramSort(s, t);
            boolean countAns = Task2.isAnagramCount(s, t);
            System.out.println("isAnagram (сортировка) = " + sortAns);
            System.out.println("isAnagram (Map-счётчик) = " + countAns);
        }
        System.out.println();

        //Задача 3
        System.out.println("---------------Задача 3---------------");
        Set<Integer> set1 = new HashSet<>(List.of(1, 2, 3));
        Set<Integer> set2 = new HashSet<>(List.of(0, 1, 2, 4));
        Task3 ps = new Task3();
        System.out.println("set1 = " + set1);
        System.out.println("set2 = " + set2);
        System.out.println("Пересечение (Stream):  " + ps.intersection(set1, set2));
        System.out.println("Пересечение (классика): " + ps.intersectionClassic(set1, set2));
        System.out.println("Объединение (Stream):  " + ps.union(set1, set2));
        System.out.println("Объединение (классика): " + ps.unionClassic(set1, set2));
        System.out.println("Отн. дополнение (Stream):  " + ps.relativeComplement(set1, set2));
        System.out.println("Отн. дополнение (классика): " + ps.relativeComplementClassic(set1, set2));
    }
}