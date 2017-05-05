package com.company.controllers;

import java.util.Properties;

public abstract class AbstractController {
    public abstract void insert(Properties props);

    public abstract void delete(int id);

    public abstract void update(int id, Properties props);
}
