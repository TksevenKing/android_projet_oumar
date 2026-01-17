package com.waritrack.data.models;

public class Category {
    private long id;
    private String name;
    private String colorHex;

    public Category(long id, String name, String colorHex) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColorHex() {
        return colorHex;
    }
}
