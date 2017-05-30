package com.company.controllers;

import com.company.loader.FileLoader;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class FileController extends AbstractController
{
    public FileController(String filePath) throws
            IOException
    {
        super(filePath);
        productMap = FileLoader.load(filePath);
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
        List<Integer> keys = Collections.list(productMap.keys());
        Collections.sort(keys);
        for (Integer key : keys) {
            out.write(String.valueOf(key).getBytes());
            out.write(": ".getBytes());
            out.write(productMap.get(key).toString().getBytes());
            out.write('\n');
        }
    }

    @Override
    public void save() throws
            IOException,
            SQLException
    {
    }
}
