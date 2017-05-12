package com.company.controllers;

import com.company.interaction.TerminalCommand;

import java.util.Properties;

public abstract class AbstractController {
    public abstract boolean tryExecute(TerminalCommand command);

    protected abstract void insert(Properties props);

    protected abstract void delete(int id);

    protected abstract void update(int id, Properties props);

    protected abstract void show();
}
