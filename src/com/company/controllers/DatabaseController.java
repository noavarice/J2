package com.company.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseController implements IController {
    private static final String INSERT_STMT = "INSERT INTO %1 (%2) VALUES (%3)";

    private static final String DELETE_STMT = "DELETE FROM %1 WHERE id = %2";

    private static final String UPDATE_STMT = "UPDATE %1 SET %2 = %3";

    private String tableName = "";

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
        /*Properties props = getConnectionProperties(filePath);
        Class.forName(props.getProperty("driver")).newInstance();
        StringBuilder builder = new StringBuilder();
        builder.append(props.getProperty("url"))
               .append("user=")
               .append(props.getProperty("username"))
               .append("&password=")
               .append(props.getProperty("password"));
        connection = DriverManager.getConnection(builder.toString());*/
    }

    public void insert(Properties props)
    {
        StringBuilder columnNamesBuilder = new StringBuilder();
        StringBuilder columnValuesBuilder = new StringBuilder();
        for (String columnName : props.stringPropertyNames()) {
            columnNamesBuilder.append(columnName);
            columnValuesBuilder.append(props.get(columnName));
        }
    }

    public void delete(int id)
    {
    }

    public void update(int id, Properties props)
    {
    }

    public void show()
    {
    }
}
