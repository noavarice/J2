package com.company.controllers;

import com.company.loader.DatabaseLoader;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;

public class DatabaseController extends AbstractController {
    private LinkedList<String> transactions;

    public DatabaseController(String filePath) throws
            IOException,
            SQLException
    {
        super(filePath);
        productMap = DatabaseLoader.load(filePath);
        if (productMap == null) {
            throw new SQLException();
        }
        List<Integer> keys = Collections.list(productMap.keys());
        maxId = keys.isEmpty() ? 0 : Collections.max(keys);
        transactions = new LinkedList<>();
    }

    @Override
    public boolean insert(Properties props)
    {
        Set<String> keys = props.stringPropertyNames();
        for (String[] allowedSet : allowedPropertySets) {
            Set<String> temp = new HashSet<>(Arrays.asList(allowedSet));
            temp.add("product_name");
            if (temp.containsAll(keys)) {
                StringBuilder columnNames = new StringBuilder();
                StringBuilder columnValues = new StringBuilder();
                columnNames.append("id,");
                columnValues.append(++maxId).append(",");
                for (String columnName : props.stringPropertyNames()) {
                    columnNames.append(columnName).append(",");
                    columnValues.append(props.getProperty(columnName)).append(",");
                }
                columnNames.deleteCharAt(columnNames.length() - 1);
                columnValues.deleteCharAt(columnValues.length() - 1);
                String query = "INSERT INTO products (" + columnNames.toString() + ") VALUES (" + columnValues.toString() +");";
                transactions.add(query);
                productMap.put(maxId, getProductFromProperties(props));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id)
    {
        transactions.add("DELETE FROM products WHERE id = " + String.valueOf(id));
        boolean result = productMap.keySet().contains(new Integer(id));
        if (result) {
            productMap.remove(new Integer(id));
        }
        return result;
    }

    @Override
    public boolean update(int id, Properties props)
    {
        if (!productMap.keySet().contains(new Integer(id))) {
            return false;
        }
        Set<String> keys = props.stringPropertyNames();
        for (String[] allowedSet : allowedPropertySets) {
            Set<String> temp = new HashSet<>(Arrays.asList(allowedSet));
            if (!temp.containsAll(keys)) {
                continue;
            }
            boolean result = updateProduct(id, props);
            if (result) {
                StringBuilder query = new StringBuilder("UPDATE products SET ");
                for (String propName : props.stringPropertyNames()) {
                    query.append(propName).append("=").append(props.getProperty(propName)).append(",");
                }
                query.deleteCharAt(query.length() - 1);
                query.append(" WHERE id = ").append(String.valueOf(id));
                transactions.add(query.toString());
            }
            return result;
        }
        return false;
    }

    @Override
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

    @Override
    public boolean save()
    {
        boolean result = true;
        if (!transactions.isEmpty()) {
            result = DatabaseLoader.save(filePath, transactions);
            transactions.clear();
        }
        return result;
    }
}
