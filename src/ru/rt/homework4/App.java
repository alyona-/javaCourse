package ru.rt.homework4;

public class App {

    public static void main(String[] args) {
        Television tv = new Television();

        //Попытка изменить настройки до включения
        tv.setChannel(5); //Ошибка
        tv.setVolume(40); //Ошибка
        tv.setBrightness(80);//Ошибка

        tv.togglePower(); //Включаем так было выкл


        //Установка яркости в Телевизоре
        tv.setBrightness(80);
        //Изменение громкости
        tv.setVolume(40);

        tv.increaseVolume();//Станет 50  кнопка больше меньше для громкости
        tv.decreaseVolume();//Станет 40

        tv.setChannel(5);//Канал 5
        tv.nextChannel();//Канал 6 кнопка больше для листания каналов
        tv.previousChannel();//Канал 5 кнопка меньше для листания каналов

        System.out.println("Текущий канал: " + tv.getCurrentChannelInfo());
        System.out.println("Текущая яркость: " + tv.getBrightnessInfo());
        System.out.println("Текущая громкость: " + tv.getVolumeInfo());

        tv.togglePower(); //Выключаем
        tv.nextChannel();//Ошибка
        tv.printStatus(); //Телевизор выключен
        //Можно добавить ещё 2 телевизор
        Television tv2 = new Television();
        tv2.togglePower();
        tv2.setChannel(25);
        tv2.setBrightness(70);
        tv2.printStatus();
        //Третий ТВ
        Television tv3 = new Television();
        tv3.togglePower();
        tv3.setVolume(60);
        tv3.printStatus();


    }

}
