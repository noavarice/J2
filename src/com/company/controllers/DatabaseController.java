package com.company.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseController implements IController {
    private Connection connection;

    private PreparedStatement showStmt;

    private PreparedStatement deleteStmt;

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
        deleteStmt = connection.prepareStatement("DELETE FROM products WHERE id = ?;");
        showStmt = connection.prepareStatement("SELECT * FROM products;");
    }

    public boolean insert(Properties props) throws SQLException {
        StringBuilder columnNames = new StringBuilder();
        StringBuilder columnValues = new StringBuilder();
        for (String columnName : props.stringPropertyNames()) {
            columnNames.append(columnName).append(",");
            columnValues.append(props.getProperty(columnName)).append(",");
        }
        columnNames.deleteCharAt(columnNames.length() - 1);
        columnValues.deleteCharAt(columnValues.length() - 1);
        String query = "INSERT INTO products (" + columnNames.toString() + ") VALUES (" + columnValues.toString() + ");";
        Statement s = connection.createStatement();
        return s.executeUpdate(query) != 0;
    }

    public boolean delete(int id) throws SQLException {
        deleteStmt.setInt(1, id);
        return deleteStmt.executeUpdate() != 0;
    }

    public boolean update(int id, Properties props)
    {
        return true;
    }

    public void show() throws SQLException {
        showStmt.execute();
    }
}
