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

    public void setMeatType(String newMeatType) throws
            NullPointerException
    {
        if (newMeatType == null) {
            throw new NullPointerException();
        }
        meatType = newMeatType;
    }

    public String toString()
    {
        StringBuilder b = new StringBuilder();
        b.append("Meat, price: ").append(price).append(", meat type: ").append(meatType);
        return b.toString();
    }

    @Override
    public Product getCopy()
    {
        return new Meat(price, meatType);
    }
}
