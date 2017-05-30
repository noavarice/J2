package com.company.loader;

import com.company.models.*;

import java.io.*;
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
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                try {
                    int size = objectInputStream.readInt();
                    for (int i = 1; i <= size; ++i) {
                        result.put(new Integer(i), (Product)(objectInputStream.readObject()));
                    }
                } catch (EOFException e) {
                    return result;
                } finally {
                    objectInputStream.close();
                    fileInputStream.close();
                }
            } catch (ClassNotFoundException e) {
                return result;
            } catch (StreamCorruptedException e) {
                return result;
            }
        }
        return result;
    }

    public static void save(String filePath, Hashtable<Integer, Product> products) throws
            IOException
    {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeInt(products.size());
                List<Integer> keys = Collections.list(products.keys());
                Collections.sort(keys);
                for (Integer key : keys) {
                    objectOutputStream.writeObject(products.get(key));
                }
            }
        }
    }
}
