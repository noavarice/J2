package com.company.controllers;

import com.company.loader.FileLoader;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;

public class FileController extends AbstractController
{
    public FileController(String filePath) throws
            IOException
    {
        super(filePath);
        productMap = FileLoader.load(filePath);
        maxId = productMap.isEmpty() ? 1 : Collections.max(productMap.keySet());
    }

    @Override
    public boolean insert(Properties props) throws SQLException
    {
        Set<String> keys = props.stringPropertyNames();
        for (String[] allowedSet : allowedPropertySets) {
            Set<String> temp = new HashSet<>(Arrays.asList(allowedSet));
            temp.add("product_name");
            if (temp.containsAll(keys)) {
                productMap.put(new Integer(String.valueOf(++maxId)), getProductFromProperties(props));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id) throws
            SQLException
    {
        boolean result = productMap.keySet().contains(new Integer(id));
        if (result) {
            productMap.remove(new Integer(id));
        }
        return result;
    }

    @Override
    public boolean update(int id, Properties props) throws
            SQLException
    {
        Set<String> keys = props.stringPropertyNames();
        for (String[] allowedSet : allowedPropertySets) {
            Set<String> temp = new HashSet<>(Arrays.asList(allowedSet));
            if (!temp.containsAll(keys)) {
                continue;
            }
            updateProduct(productMap.get(new Integer(id)), props);
            return true;
        }
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
