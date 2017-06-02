package com.company.controllers;

import com.company.loader.FileLoader;
import com.company.models.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.zip.DataFormatException;

public abstract class AbstractController {
    protected String filePath;

    protected int maxId;

    protected Hashtable<Integer, Product> productMap;

    protected final String[][] allowedPropertySets = new String[][] {
            new String[] {"price", "fattiness", "brand"},
            new String[] {"price", "flour_type"},
            new String[] {"price", "meat_type"}
    };

    protected static Comparator<Product> ascendingPriceComparator = (x, y) ->
    {
        if (x.getPrice() < y.getPrice()) {
            return -1;
        }
        if (x.getPrice() > y.getPrice()) {
            return 1;
        }
        return 0;
    };

    protected static final Comparator<Product> descendingPriceComparator = (x, y) ->
    {
        if (x.getPrice() < y.getPrice()) {
            return 1;
        }
        if (x.getPrice() > y.getPrice()) {
            return -1;
        }
        return 0;
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

    protected boolean updateProduct(int id, Properties props)
    {
        Integer integerId = new Integer(id);
        Product updatingProduct = productMap.get(integerId).getCopy();
        for (String key : props.stringPropertyNames()) {
            BiConsumer<Product, String> updater = propertyToUpdater.get(key);
            try {
                updater.accept(updatingProduct, props.getProperty(key));
            } catch (ClassCastException e) {
                return false;
            }
        }
        productMap.put(integerId, updatingProduct);
        return true;
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

    public AbstractController(String filePath)
    {
        this.filePath = filePath;
    }

    public abstract boolean insert(Properties props);

    public abstract boolean delete(int id);

    public abstract boolean update(int id, Properties props);

    public abstract void show(OutputStream out) throws IOException;

    public void showSorted(OutputStream out, boolean ascending) throws
            IOException
    {
        List<Product> products = new LinkedList<>(productMap.values());
        Collections.sort(products, ascending ? ascendingPriceComparator : descendingPriceComparator);
        for (Product p : products) {
            out.write(p.toString().getBytes());
            out.write('\n');
        }
    }

    public void filter(OutputStream out, BiPredicate<Product, Double> pred, double maxPrice) throws
            IOException
    {
        List<Product> products = new LinkedList<>(productMap.values());
        for (Product p : products) {
            if (pred.test(p, maxPrice)) {
                out.write(p.toString().getBytes());
                out.write('\n');
            }
        }
    }


    public abstract boolean save();

    public boolean saveToFile(String filePath)
    {
        return FileLoader.save(filePath, productMap);
    }
}
