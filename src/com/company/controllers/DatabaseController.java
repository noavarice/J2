package com.company.controllers;

import com.company.loader.ProductLoader;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class DatabaseController extends AbstractController {
    public DatabaseController(String filePath) throws
            IOException,
            SQLException
    {
        productMap = ProductLoader.loadFromDatabase(filePath);
        List<Integer> keys = Collections.list(productMap.keys());
        maxId = keys.isEmpty() ? 0 : Collections.max(keys);
        transactions = new LinkedList<>();
    }

    public void insert(Properties props)
    {
        StringBuilder columnNames = new StringBuilder();
        StringBuilder columnValues = new StringBuilder();
        columnNames.append("id,");
        columnValues.append(++maxId);
        for (String columnName : props.stringPropertyNames()) {
            columnNames.append(columnName).append(",");
            columnValues.append(props.getProperty(columnName)).append(",");
        }
        columnNames.deleteCharAt(columnNames.length() - 1);
        columnValues.deleteCharAt(columnValues.length() - 1);
        transactions.add("INSERT INTO products (" + columnNames.toString() + ") VALUES (" + columnValues.toString() +");");
        productMap.put(maxId, ProductLoader.getProductFromProperties(props));
    }

    public boolean delete(int id)
    {
        transactions.add("DELETE FROM products WHERE id = " + String.valueOf(id));
        return productMap.keySet().contains(new Integer(id));
    }

    public boolean update(int id, Properties props)
    {
        if (!productMap.keySet().contains(new Integer(id))) {
            return false;
        }
        Set<String> keys = props.stringPropertyNames();
        for (String[] allowedSet : allowedPropertySets) {
            if (!keys.containsAll(Arrays.asList(allowedSet))) {
                continue;
            }
            updateProduct(productMap.get(new Integer((id))), props);
            String query = "UPDATE products SET ";
            for (String propName : props.stringPropertyNames()) {
                query += propName + " = " + props.getProperty(propName);
            }
            query += " WHERE id = " + String.valueOf(id);
            transactions.add(query);
            return true;
        }
        return false;
    }

    public void show(OutputStream out) throws
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
}
