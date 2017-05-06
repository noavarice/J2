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

    private String getConnectionProperties(String filePath) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(new File(filePath)));
        return new StringBuilder().append(props.getProperty("driver"))
                                  .append("?user=")
                                  .append(props.getProperty("username"))
                                  .append("&password=")
                                  .append(props.getProperty("password"))
                                  .toString();
    }

    public DatabaseController(String filePath) throws ClassNotFoundException,
                                                      InstantiationException,
                                                      IllegalAccessException,
                                                      IOException,
                                                      SQLException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(getConnectionProperties(filePath));
    }

    public void insert(Properties props) {
    }

    public void delete(int id) {
    }

    public void update(int id, Properties props) {
    }
}
