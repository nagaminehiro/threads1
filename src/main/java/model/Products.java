package model;

import java.util.UUID;

public class Products {
    private final UUID id;
    private final String name;
    private final String description;

    // Construtor, getters e setters
    public Products(String name, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}