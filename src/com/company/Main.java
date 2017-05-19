package com.company;

import com.company.controllers.DatabaseController;
import com.company.interaction.InteractionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws
            InstantiationException,
            IllegalAccessException,
            IOException,
            SQLException,
            ClassNotFoundException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        DatabaseController c = new DatabaseController("/home/alexrazinkov/Projects/Java/conn");
        String input = reader.readLine();
        while (!input.isEmpty()) {
            System.out.println(InteractionManager.execute(c, input) ? "True" : "False");
            input = reader.readLine();
        }
    }
}
