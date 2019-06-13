package com.example.buyinglistocr.model;

public class Shop {

    // Different attributes of Shop
    private long id;
    private String name;

    public Shop (long id, String name) {

        super();

        this.id = id;
        this.name = name;

    }

    public long getId() {

        return this.id;

    }

    public void setId( long id) {

        this.id = id;

    }

    public String getName() {

        return this.name;

    }

    public void setName( String name) {

        this.name = name;

    }

}
