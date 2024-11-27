package model;

public class Client {
    private final String name;
    private final int age;

    // Construtor, getters e setters
    public Client(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}