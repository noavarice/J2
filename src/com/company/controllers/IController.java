package com.company.controllers;

import java.util.Properties;

public interface IController {
    void insert(Properties props);

    void delete(int id);

    void update(int id, Properties props);

    void show();
}
