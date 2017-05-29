package com.company.models;

public class Meat extends Product {
    private String meatType;

    public Meat(double price, String meatType)
    {
        super(price);
        this.meatType = meatType;
    }

    public String getMeatType()
    {
        return meatType;
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("Meat, price: ").append(price).append(", meat type: ").append(meatType);
        return b.toString();
    }
}
