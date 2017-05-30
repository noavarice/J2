package com.company.controllers;

import com.company.models.*;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.zip.DataFormatException;

public abstract class AbstractController {
    protected int maxId;

    protected LinkedList<String> transactions;

    protected Hashtable<Integer, Product> productMap;

    protected final String[][] allowedPropertySets = new String[][] {
            new String[] {"price", "fattiness", "brand"},
            new String[] {"price", "flour_type"},
            new String[] {"price", "meat_type"}
    };

    private static final Hashtable<String, ProductType> nameToType = new Hashtable<String, ProductType>() {
        {
            put("bread", ProductType.Bread);
            put("meat", ProductType.Meat);
            put("milk", ProductType.Milk);
        }
    };

    private static final Hashtable<String, BiConsumer<Product, String>> propertyToUpdater =
            new Hashtable<String, BiConsumer<Product, String>>() {
        {
            put("price", (updatingProduct, newPrice) -> {
                try {
                    updatingProduct.setPrice(Double.valueOf(newPrice));
                } catch (DataFormatException e) {
                    e.printStackTrace();
                }
            });
            put("fattiness", (updatingProduct, newFattiness) -> {
                try {
                    ((Milk) updatingProduct).setFattiness(Double.valueOf(newFattiness));
                } catch (DataFormatException e) {
                    e.printStackTrace();
                }
            });
            put("brand", (updatingProduct, newBrand) -> ((Milk)updatingProduct).setBrand(newBrand));
            put("flour_type", (updatingProduct, newFlourType) -> ((Bread)updatingProduct).setFlourType(newFlourType));
            put("meat_type", (updatingProduct, newMeatType) -> ((Meat)updatingProduct).setMeatType(newMeatType));
        }
    };

    protected static void updateProduct(Product updatingProduct, Properties props)
    {
        for (String key : props.stringPropertyNames()) {
            BiConsumer<Product, String> updater = propertyToUpdater.get(key);
            updater.accept(updatingProduct, props.getProperty(key));
        }
    }

    protected static Product getProductFromProperties(Properties props) {
        Product result = null;
        double price = Double.parseDouble(props.getProperty("price"));
        StringBuilder productName = new StringBuilder(props.getProperty("product_name"));
        productName.deleteCharAt(0);
        productName.deleteCharAt(productName.length() - 1);
        switch (nameToType.get(productName.toString())) {
            case Bread: {
                result = new Bread(price, props.getProperty("flour_type"));
            }
            break;

            case Meat: {
                result = new Meat(price, props.getProperty("meat_type"));
            }
            break;

            case Milk: {
                double fattiness = Double.parseDouble(props.getProperty("fattiness"));
                result = new Milk(price, fattiness, props.getProperty("brand"));
            }
            break;
        }
        return result;
    }

    public abstract void insert(Properties props) throws SQLException;

    public abstract boolean delete(int id) throws SQLException;

    public abstract boolean update(int id, Properties props) throws SQLException;

    public abstract void show(OutputStream out) throws SQLException, IOException;
}
