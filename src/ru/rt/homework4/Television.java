package ru.rt.homework4;

public class Television {
    private int brightness; //Яркость от 0 до 100
    private int volume; //Звук от 0 до 100
    private int currentChannel; //Каналы от 1 до 100
    private boolean isOn;//включен ли телевизор

    //Конструктор при включении телевизора
    public Television() {
        this.brightness = 50;
        this.volume = 30;
        this.currentChannel = 1;
        this.isOn = false;//Изначально выкл телевизор, только после вкл кнопки питания включится
    }

    //Включение/Выключение телевизора. На пульте обычно одна кнопка, поэтому и тут также
    public void togglePower() {
        isOn = !isOn;
        if (isOn) {
            System.out.println("Телевизор включен. ");
        } else {
            System.out.println("Телевизор выключен. ");
        }
    }


    //Проверка включен ли телевизор
    private boolean checkIfOn() {
        if (!isOn) {
            System.out.println("Ошибка: телевизор выключен. Выполнить действие нельзя.");
            return false;
        }
        return true;
    }

    //Метод для установки яркости
    public void setBrightness(int newBrightness) {
        //Перед выполнением действия проверяем, включён ли телевизор
        if (!checkIfOn()) return;
        if (newBrightness < 0 || newBrightness > 100) {
            System.out.println("Ошибка. Яркость должна быть от 0 до 100");
        } else {
            this.brightness = newBrightness;
            System.out.println("Яркость установлена на " + newBrightness);
        }
    }


    //Метод получения яркости
    public String getBrightnessInfo() {
        return isOn ? "Яркость: " + brightness : "Яркость недоступна — телевизор выключен.";
    }

    //Метод для установки громкости в телевизоре
    public void setVolume(int newVolume) {
        //Перед выполнением действия проверяем, включён ли телевизор
        if (!checkIfOn()) return;
        if (newVolume < 0 || newVolume > 100) {
            System.out.println("Ошибка: громкость должна быть от 0 до 100");
        } else {
            this.volume = newVolume;
            System.out.println("Громкость установлена на " + newVolume);
        }
    }

    //Метод для получения текущей громкости
    public String getVolumeInfo() {
        return isOn ? "Громкость: " + volume : "Громкость недоступна — телевизор выключен.";
    }

    public void increaseVolume() {
        //Перед выполнением действия проверяем, включён ли телевизор
        if (!checkIfOn()) return;
        setVolume(Math.min(volume + 10, 100)); // делаем защиту , чтобы не выходить за диапозон
    }

    public void decreaseVolume() {
        //Перед выполнением действия проверяем, включён ли телевизор
        if (!checkIfOn()) return;
        setVolume(Math.max(volume - 10, 0));// делаем защиту , чтобы не выходить за диапозон
    }

    //Каналы
    public void setChannel(int newChannel) {
        //Перед выполнением действия проверяем, включён ли телевизор
        if (!checkIfOn()) return;
        if (newChannel < 1 || newChannel > 100) {
            System.out.println("Ошибка: канал должен быть от 1 до 100");
        } else {
            this.currentChannel = newChannel;
            System.out.println("Переключено на канал #" + newChannel);
        }
    }

    public void nextChannel() {
        //Перед выполнением действия проверяем, включён ли телевизор
        if (!checkIfOn()) return;
        if (currentChannel < 100) {
            setChannel(currentChannel + 1);
        } else {
            setChannel(1); //Циклично
        }
    }

    public void previousChannel() {
        //Перед выполнением действия проверяем, включён ли телевизор
        if (!checkIfOn()) return;
        if (currentChannel > 1) {
            setChannel(currentChannel - 1);
        } else {
            setChannel(100); //Циклично , если пользователь дойдет до 1 канала и опять переключит канал кнопкой листания каналов, тогда опять попадает на 100 канал
        }
    }

    public String getCurrentChannelInfo() {
        return isOn ? "Канал: #" + currentChannel : "Канал недоступен — телевизор выключен.";
    }

    //Статус
    public void printStatus() {
        if (isOn) {
            System.out.printf("Телевизор включён. Канал: #%d, Яркость: %d, Громкость: %d%n", currentChannel, brightness, volume);
        } else {
            System.out.println("Телевизор выключен.");
        }
    }

}
