package ru.rt.homework3;

public class Second {
    public void playGame() {
        String turn[] = {"камень", "ножницы", "бумага"};
        int vasya =(int)(Math.random()*3);
        int petya = (int)(Math.random()*3);

        System.out.println("Вася делает ход");
        System.out.println("Вася выбрал - " + turn[vasya]);

        System.out.println("Петя делает ход");
        System.out.println("Петя выбрал - " + turn[petya]);

        if (vasya == petya) {
            System.out.println("Ничья!");
        } else if ((vasya == 0 && petya == 1) ||
                (vasya == 1 && petya == 2) ||
                (vasya == 2 && petya == 0)
        )
            System.out.println("Вася победил!");
        else
            System.out.println("Петя победил!");
    }

}
