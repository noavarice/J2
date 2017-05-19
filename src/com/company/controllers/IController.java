package com.company.controllers;

import java.sql.SQLException;
import java.util.Properties;

public interface IController {
    boolean insert(Properties props);

    boolean delete(int id);

    boolean update(int id, Properties props);

    void show();
}
