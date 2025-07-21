package ru.rt.homework3;

import java.util.Scanner;

public class First {

    public void HelloUser () {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя: ");
        String name =scanner.nextLine();
        System.out.println("Привет, "+name);
    }
}
