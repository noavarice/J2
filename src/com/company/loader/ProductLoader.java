package com.company.loader;

import com.company.models.Bread;
import com.company.models.Meat;
import com.company.models.Milk;
import com.company.models.Product;

import java.sql.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Properties;

enum ProductType {
    Bread,
    Meat,
    Milk
}

public class ProductLoader {
    private static Hashtable<String, ProductType> nameToType = new Hashtable<String, ProductType>() {
        {
            put("bread", ProductType.Bread);
            put("meat", ProductType.Meat);
            put("milk", ProductType.Milk);
        }
    };

    public static LinkedList<Product> loadFromDatabase(Connection databaseConnection) throws
            SQLException
    {
        LinkedList<Product> result = new LinkedList<>();
        Statement s = databaseConnection.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM products");
        while (rs.next()) {
            Product temp = null;
            double price = rs.getDouble("price");
            switch (nameToType.get(rs.getString("product_name"))) {
                case Bread: {
                    temp = new Bread(price, rs.getString("flour_type"));
                }
                break;

                case Meat: {
                    temp = new Meat(price, rs.getString("meat_type"));
                }
                break;

                case Milk: {
                    temp = new Milk(price, rs.getDouble("fattiness"), rs.getString("brand"));
                }
                break;
            }
            result.add(temp);
        }
        return result;
    }

    public static Product getProductFromProperties(Properties props)
    {
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

    public static LinkedList<Product> loadFromFile(String filePath)
    {
        return null;
    }
}
