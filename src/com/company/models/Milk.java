package com.company.models;

import java.util.zip.DataFormatException;

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

    public void setFattiness(double newFattiness) throws
            DataFormatException
    {
        if (newFattiness <= 0) {
            throw new DataFormatException();
        }
        fattiness = newFattiness;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String newBrand) throws
            NullPointerException
    {
        if (newBrand == null) {
            throw new NullPointerException();
        }
        brand = newBrand;
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("Milk, price: ").append(price).append(", fattiness: ").append(fattiness).append(", brand: \"")
                .append(brand).append("\"");
        return b.toString();
    }

    @Override
    public Product getCopy()
    {
        return new Milk(price, fattiness, brand);
    }
}
