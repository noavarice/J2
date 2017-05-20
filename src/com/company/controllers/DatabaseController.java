package com.company.controllers;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseController implements IController {
    private Connection connection;

    private PreparedStatement showStmt;

    private PreparedStatement deleteStmt;

    public DatabaseController(String filePath) throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            IOException,
            SQLException
    {
        Properties props = new Properties();
        props.load(new FileInputStream(new File(filePath)));
        MysqlDataSource ds = new MysqlDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("shop");
        ds.setUser(props.getProperty("username"));
        ds.setPassword(props.getProperty("password"));
        connection = ds.getConnection();
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
