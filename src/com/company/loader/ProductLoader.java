package com.company.loader;

import com.company.models.Bread;
import com.company.models.Meat;
import com.company.models.Milk;
import com.company.models.Product;

import java.sql.*;
import java.util.Hashtable;
import java.util.LinkedList;

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

    public static LinkedList<Product> loadFromFile(String filePath)
    {
        return null;
    }
}
