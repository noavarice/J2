package com.company.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Properties;

public interface IController {
    boolean insert(Properties props) throws SQLException;

    boolean delete(int id) throws SQLException;

    boolean update(int id, Properties props) throws SQLException;

    void show(OutputStream out) throws SQLException, IOException;
}
