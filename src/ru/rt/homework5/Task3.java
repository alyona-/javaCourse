package ru.rt.homework5;

import java.util.Arrays;
import java.util.Scanner;

public class Task3 {

    public static void main(String[] args) {
        new Task3().startScanner();
    }

    public void startScanner() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Введите два английских слова через пробел (или 'exit' для выхода):");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Выход из программы.");
                break;
            }

            String result = sortWords(input);
            System.out.println("Отсортированная строка: " + result);
        }
    }

    public String sortWords(String input) {
        String[] words = input.toLowerCase().split(" ");

        StringBuilder result = new StringBuilder();
        for (String word : words) {
            char[] letters = word.toCharArray();
            Arrays.sort(letters);
            result.append(new String(letters)).append(" ");
        }

        return result.toString().trim();
    }
}
