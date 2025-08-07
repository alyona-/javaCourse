package ru.rt.homework5;

import java.util.Scanner;

public class Task2 {

    public static void main(String[] args) {
        new Task2().startScanner(); // Точка входа
    }

    public void startScanner() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Введите строку (или 'exit' для выхода):");
            String input = scanner.next();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Выход из программы.");
                break;
            }

            int count = countArrows(input);
            System.out.println("Количество стрел: " + count);
        }
    }

    public int countArrows(String input) {
        int count = 0;
        //Ищем подстроки в строке, увеличиваем значение на 1 если нашли
        for (int i = 0; i <= input.length() - 5; i++) {
            String sub = input.substring(i, i + 5);
            if (sub.equals(">>-->") || sub.equals("<--<<")) {
                count++;
            }
        }

        return count;
    }
}
