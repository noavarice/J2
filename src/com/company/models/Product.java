package com.company.models;

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

    public abstract String toString();
}
