package com.company.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Properties;

public class FileController extends AbstractController
{
    public FileController(String filePath)
    {
        super(filePath);
    }

    @Override
    public void insert(Properties props) throws SQLException
    {
    }

    @Override
    public boolean delete(int id) throws
            SQLException
    {
        return false;
    }

    @Override
    public boolean update(int id, Properties props) throws
            SQLException
    {
        return false;
    }

    @Override
    public void show(OutputStream out) throws
            SQLException,
            IOException
    {
    }

    @Override
    public void save() throws
            IOException,
            SQLException
    {
    }
}
