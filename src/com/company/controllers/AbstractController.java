package com.company.controllers;

import com.company.models.Bread;
import com.company.models.Meat;
import com.company.models.Milk;
import com.company.models.Product;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.zip.DataFormatException;

public abstract class AbstractController {
    protected Hashtable<Integer, Product> productMap;

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

    public abstract boolean insert(Properties props) throws SQLException;

    public abstract boolean delete(int id) throws SQLException;

    public abstract boolean update(int id, Properties props) throws SQLException;

    public abstract void show(OutputStream out) throws SQLException, IOException;
}
