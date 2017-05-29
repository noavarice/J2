package com.company;

import com.company.interaction.InteractionManager;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws
            IOException,
            SQLException
    {
        InteractionManager.interact();
    }
}
