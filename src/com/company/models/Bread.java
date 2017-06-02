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

    public void setFlourType(String newFlourType) throws
            NullPointerException
    {
        if (newFlourType == null) {
            throw new NullPointerException();
        }
        flourType = newFlourType;
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("Bread, price: ").append(price).append(", flour type: ").append(flourType);
        return b.toString();
    }

    @Override
    public Product getCopy()
    {
        return new Bread(price, flourType);
    }
}
