package com.company.loader;

import com.company.models.*;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;

public class DatabaseLoader {
    private static final Hashtable<String, ProductType> nameToType = new Hashtable<String, ProductType>() {
        {
            put("bread", ProductType.Bread);
            put("meat", ProductType.Meat);
            put("milk", ProductType.Milk);
        }
    };

    private static Connection getConnection(String connectionPropsFilePath) throws
            IOException,
            SQLException
    {
        Properties props = new Properties();
        props.load(new FileInputStream(new File(connectionPropsFilePath)));
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("shop");
        ds.setUser(props.getProperty("username"));
        ds.setPassword(props.getProperty("password"));
        return ds.getConnection();
    }

    public static Hashtable<Integer, Product> load(String connectionPropsFilePath) throws
            IOException,
            SQLException
    {
        Connection connection = getConnection(connectionPropsFilePath);
        Statement s = connection.createStatement();
        Hashtable<Integer, Product> result = new Hashtable<>();
        ResultSet rs = s.executeQuery("SELECT * FROM products");
        s.close();
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
        rs.close();
        connection.close();
        return result;
    }

    public static void save(String connectionPropsFilePath, Collection<String> transactions) throws
            IOException,
            SQLException
    {
        Connection connection = getConnection(connectionPropsFilePath);
        Statement s = connection.createStatement();
        for (String query : transactions) {
            s.execute(query);
        }
        s.close();
        connection.close();
    }
}
