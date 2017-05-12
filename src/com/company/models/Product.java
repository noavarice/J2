package com.company.models;

import java.util.Properties;

public class Product {
    private double price;

    private Properties props;

    public Product(double price, Properties properties)
    {
        this.price = price;
        props = properties;
    }

    public double getPrice()
    {
        return price;
    }

    public Properties getProperties()
    {
        return props;
    }
}
