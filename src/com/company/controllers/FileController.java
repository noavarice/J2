package com.company.controllers;

import com.company.loader.FileLoader;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class FileController extends AbstractController
{
    public FileController(String filePath) throws
            IOException
    {
        super(filePath);
        productMap = FileLoader.load(filePath);
        maxId = productMap.isEmpty() ? 0 : Collections.max(productMap.keySet());
    }

    @Override
    public boolean insert(Properties props)
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
    public boolean delete(int id)
    {
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
            updateProduct(id, props);
            return true;
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
        return FileLoader.save(filePath, productMap);
    }
}
