package ru.rt.homework5;

import java.util.Scanner;

public class Task1 {

    private final char[] keys = {
            'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
            'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
            'z', 'x', 'c', 'v', 'b', 'n', 'm'
    };

    public static void main(String[] args) {
        new Task1().startScanner(); // Точка входа
    }

    public void startScanner() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Введите англ. букву (или 'exit' для выхода):");
            String input = scanner.next();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Выход из программы.");
                break;
            }

            if (input.length() != 1) {
                System.out.println("Ошибка: введите только 1 символ.");
                continue;
            }

            char ch = input.charAt(0);

            if (!isEngLetter(ch)) {
                System.out.println("Ошибка: символ не является английской буквой.");
                continue;
            }

            ch = Character.toLowerCase(ch);
            char left = getLeftSymbol(ch);

            System.out.println("Слева стоящая буква: " + left);
        }
    }

    private boolean isEngLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

    public char getLeftSymbol(char currentSymbol) {
        // Особые случаи по условию
        if (currentSymbol == 'a') return 'p';
        if (currentSymbol == 'z') return 'l';
        if (currentSymbol == 'q') return 'm';

        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == currentSymbol) {
                int leftIndex = (i - 1 + keys.length) % keys.length;
                return keys[leftIndex];
            }
        }

        return '?'; // Если символ не найден
    }
}
