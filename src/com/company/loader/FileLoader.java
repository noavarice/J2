package com.company.loader;

import com.company.models.*;

import java.io.*;
import java.util.*;

public class FileLoader
{
    private static final Hashtable<String, ProductType> nameToType = new Hashtable<String, ProductType>() {
        {
            put("bread", ProductType.Bread);
            put("meat", ProductType.Meat);
            put("milk", ProductType.Milk);
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

    public static boolean save(String filePath, Hashtable<Integer, Product> products)
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
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
