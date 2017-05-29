package com.company.controllers;

import com.company.loader.ProductLoader;
import com.company.models.Product;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.*;
import java.sql.*;
import java.util.*;

public class DatabaseController implements IController {
    private Hashtable<Integer, Product> productMap;

    private Connection connection;

    private PreparedStatement deleteStmt;

    private PreparedStatement selectSingleItemStmt;

    private PreparedStatement showStmt;

    public DatabaseController(String filePath) throws
            SQLException,
            IOException
    {
        Properties props = new Properties();
        props.load(new FileInputStream(new File(filePath)));
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("shop");
        ds.setUser(props.getProperty("username"));
        ds.setPassword(props.getProperty("password"));
        connection = ds.getConnection();
        connection.setAutoCommit(false);
        deleteStmt = connection.prepareStatement("DELETE FROM products WHERE id = ?;");
        selectSingleItemStmt = connection.prepareStatement("SELECT * FROM products WHERE id = ?;");
        showStmt = connection.prepareStatement("SELECT * FROM products;");
        productMap = ProductLoader.loadFromDatabase(connection);
    }

    public boolean insert(Properties props) throws
            SQLException
    {
        StringBuilder columnNames = new StringBuilder();
        StringBuilder columnValues = new StringBuilder();
        for (String columnName : props.stringPropertyNames()) {
            columnNames.append(columnName).append(",");
            columnValues.append(props.getProperty(columnName)).append(",");
        }
        columnNames.deleteCharAt(columnNames.length() - 1);
        columnValues.deleteCharAt(columnValues.length() - 1);
        String query = "INSERT INTO products (" + columnNames.toString() + ") VALUES (" + columnValues.toString() +");";
        Statement s = connection.createStatement();
        boolean result = s.executeUpdate(query, Statement.RETURN_GENERATED_KEYS) != 0;
        if (result) {
            ResultSet rs = s.getGeneratedKeys();
            rs.next();
            productMap.put(rs.getInt(1), ProductLoader.getProductFromProperties(props));
            connection.commit();
        }
        return result;
    }

    public boolean delete(int id) throws SQLException {
        deleteStmt.setInt(1, id);
        boolean result = deleteStmt.executeUpdate() != 0;
        if (result) {
            connection.commit();
        }
        return result;
    }

    public boolean update(int id, Properties props) throws SQLException {
        selectSingleItemStmt.setInt(1, id);
        ResultSet set = selectSingleItemStmt.executeQuery();
        if (!set.next()) {
            return false;
        }
        for (String columnName : props.stringPropertyNames()) {
            if (set.getBytes(columnName) == null) {
                connection.rollback();
                return false;
            }
            String value = props.getProperty(columnName);
            Statement s = connection.createStatement();
            String query = "UPDATE products SET " + columnName + " = " + value + " WHERE id = " + String.valueOf(id);
            s.executeUpdate(query);
        }
        connection.commit();
        return true;
    }

    public void show(OutputStream out) throws
            SQLException,
            IOException
    {
        for (Product p : productMap.values()) {
            out.write(p.toString().getBytes());
            out.write('\n');
        }
    }
}
