package com.company.loader;

import com.company.models.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class FileLoader
{
    private static final Hashtable<String, ProductType> nameToType = new Hashtable<String, ProductType>() {
        {
            put("bread", ProductType.Bread);
            put("meat", ProductType.Meat);
            put("milk", ProductType.Milk);
        }
    };

    private static final Hashtable<String, Function<List<String>, Product>> nameToProduct =
            new Hashtable<String, Function<List<String>, Product>>() {
        {
            put("bread", list -> {
                double price = Double.parseDouble(list.get(0));
                return new Bread(price, list.get(1));
            });
            put("meat", list -> {
                double price = Double.parseDouble(list.get(0));
                return new Meat(price, list.get(1));
            });
            put("milk", list -> {
                double price = Double.parseDouble(list.get(0));
                double fattiness = Double.parseDouble(list.get(1));
                return new Milk(price, fattiness, list.get(2));
            });
        }
    };


    public static Hashtable<Integer, Product> load(String filePath) throws
            IOException
    {
        Hashtable<Integer, Product> result = new Hashtable<>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(line -> {
                ArrayList<String> parts = new ArrayList<String>(Arrays.asList(line.split(",")));
                int id = Integer.parseInt(parts.get(0));
                parts.remove(0);
                String productType = parts.get(0);
                parts.remove(0);
                result.put(new Integer(id), nameToProduct.get(productType).apply(parts));
            });
        }
        return result;
    }

    public static void save(String filePath, Hashtable<Integer, Product> products)
    {
    }
}
