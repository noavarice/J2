package com.company.models;

import java.util.zip.DataFormatException;

public abstract class Product {
    protected double price;

    public Product(double price)
    {
        this.price = price;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double newPrice) throws
            DataFormatException
    {
        if (newPrice <= 0) {
            throw new DataFormatException();
        }
        price = newPrice;
    }

    public abstract String toString();
}
