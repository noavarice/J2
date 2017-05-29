package com.company.controllers;

import com.company.models.Product;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;

public abstract class AbstractController {
    protected Hashtable<Integer, Product> productMap;

    public abstract boolean insert(Properties props) throws SQLException;

    public abstract boolean delete(int id) throws SQLException;

    public abstract boolean update(int id, Properties props) throws SQLException;

    public abstract void show(OutputStream out) throws SQLException, IOException;
}
