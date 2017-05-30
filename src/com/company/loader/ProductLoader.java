package com.company.loader;

import com.company.models.Bread;
import com.company.models.Meat;
import com.company.models.Milk;
import com.company.models.Product;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Hashtable;
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

    public static Hashtable<Integer, Product> loadFromDatabase(String connectionPropsFilePath) throws
            SQLException,
            IOException
    {
        Properties props = new Properties();
        props.load(new FileInputStream(new File(connectionPropsFilePath)));
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("shop");
        ds.setUser(props.getProperty("username"));
        ds.setPassword(props.getProperty("password"));
        Connection connection = ds.getConnection();
        Statement s = connection.createStatement();
        Hashtable<Integer, Product> result = new Hashtable<>();
        ResultSet rs = s.executeQuery("SELECT * FROM products");
        while (rs.next()) {
            Product temp = null;
            int id = rs.getInt("id");
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
            result.put(id, temp);
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

    public static Hashtable<Integer, Product> loadFromFile(String filePath)
    {
        return null;
    }
}
