package com.company.models;

public class Bread extends Product {
    private String flourType;

    public Bread(double price, String flourType)
    {
        super(price);
        this.flourType = flourType;
    }

    public String getFlourType()
    {
        return flourType;
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("Bread, price: ").append(price).append(", flour type: \"").append(flourType).append("\"");
        return b.toString();
    }
}
