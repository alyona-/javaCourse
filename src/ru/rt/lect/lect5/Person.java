package ru.rt.lect.lect5;

import java.util.Objects;

public class Person {
    private String name;
    private int age;


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Person person)) return false;

        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + age;
        return result;
    }
}
