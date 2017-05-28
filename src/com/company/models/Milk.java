package com.company.models;

public class Milk extends Product {
    private double fattiness;

    private String brand;

    public Milk(double price, double fattiness, String brand)
    {
        super(price);
        this.fattiness = fattiness;
        this.brand = brand;
    }

    public double getFattiness()
    {
        return fattiness;
    }

    public String getBrand()
    {
        return brand;
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("Milk, price: ").append(price).append("fattiness: ").append(fattiness).append("brand: \"")
                .append(brand).append("\"");
        return b.toString();
    }
}
