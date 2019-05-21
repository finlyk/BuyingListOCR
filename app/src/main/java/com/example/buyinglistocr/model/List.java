package com.example.buyinglistocr.model;

/**
 * Represent the List object
 */
public class List {

    // Different attributes of List
    private long id;
    private String name;
    private float spent;

    /**
     * The constructor of the class
     * @param id - The id of the list
     * @param name - The name of the list
     * @param spent - The spent for the list
     */
    public List(long id, String name, float spent) {

        this.id = id;
        this.name = name;
        this.spent = spent;

    }

    /**
     * Getter of the id attribute
     * @return - The id
     */
    public long getId() {

        return this.id;

    }

    /**
     * Setter of the id attribute
     * @param id - The id
     */
    public void setId(long id) {

        this.id = id;

    }

    /**
     * Getter of the name attribute
     * @return - The name
     */
    public String getName() {

        return this.name;

    }

    /**
     * Setter of the name attribute
     * @param name - The name
     */
    public void setName(String name) {

        this.name = name;

    }

    /**
     * Getter of the spent attribute
     * @return - The spent
     */
    public float getSpent() {

        return this.spent;

    }

    /**
     * Setter of the spent attribute
     * @param spent - The spent
     */
    public void setSpent(float spent) {

        this.spent = spent;

    }

}



