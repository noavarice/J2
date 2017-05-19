package com.company.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseController implements IController {
    private Connection connection;

    private Properties getConnectionProperties(String filePath) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(new File(filePath)));
        return props;
    }

    public DatabaseController(String filePath) throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            IOException,
            SQLException
    {
        Properties props = getConnectionProperties(filePath);
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(
                props.getProperty("url"),
                props.getProperty("username"),
                props.getProperty("password"));
    }

    public boolean insert(Properties props)
    {
        StringBuilder columnNames = new StringBuilder();
        StringBuilder columnValues = new StringBuilder();
        for (String columnName : props.stringPropertyNames()) {
            columnNames.append(columnName).append(",");
            String value = props.getProperty(columnName);
            if (columnName == "brand" || columnName == "product_name") {
                value = "\"" + value + "\"";
            }
            columnValues.append(value).append(",");
        }
        columnNames.deleteCharAt(columnNames.length() - 1);
        columnValues.deleteCharAt(columnValues.length() - 1);
        String query = "INSERT INTO products (" + columnNames.toString() + ") values (" + columnValues.toString() + ");";
        try {
            Statement s = connection.createStatement();
            return s.execute(query);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean delete(int id)
    {
        try {
            Statement s = connection.createStatement();
            return s.execute("DELETE FROM products WHERE id=" + Integer.toString(id) + ";");
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(int id, Properties props)
    {
        return true;
    }

    public void show()
    {
    }
}
