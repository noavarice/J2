package com.company.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseController extends AbstractController {
    private Connection connection;

    private Properties getConnectionProperties(String filePath) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(new File(filePath)));
        return props;
    }

    public DatabaseController(String filePath) throws ClassNotFoundException,
                                                      InstantiationException,
                                                      IllegalAccessException,
                                                      IOException,
                                                      SQLException
    {
        Properties props = getConnectionProperties(filePath);
        Class.forName(props.getProperty("driver")).newInstance();
        StringBuilder builder = new StringBuilder();
        builder.append(props.getProperty("url"))
               .append("user=")
               .append(props.getProperty("username"))
               .append("&password=")
               .append(props.getProperty("password"));
        connection = DriverManager.getConnection(builder.toString());
    }

    public void insert(Properties props) {
    }

    public void delete(int id) {
    }

    public void update(int id, Properties props) {
    }

    public void show() {
    }
}
