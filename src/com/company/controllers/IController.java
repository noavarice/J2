package com.company.controllers;

import java.sql.SQLException;
import java.util.Properties;

public interface IController {
    void insert(Properties props) throws SQLException;

    void delete(int id);

    void update(int id, Properties props);

    void show();
}
