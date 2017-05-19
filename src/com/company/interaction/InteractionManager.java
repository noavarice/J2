package com.company.interaction;

import com.company.controllers.IController;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum CommandType {
    INSERT,
    DELETE,
    UPDATE,
    SHOW,
}

enum CommandPart {
    COMMAND_TYPE,
}

public class InteractionManager {
    private static final String ASSIGNMENT_PATTERN = "\\s*=\\s*";

    private static final String NOT_NULL_STRING_PATTERN = "\"[^\"]+\"";

    private static final String ID_PATTERN = "(([01]?\\d{1,2})" +
                                             "|(2[0-4]\\d)" +
                                             "|(25[0-5]))";

    private static final String PRICE_PATTERN = "(\\d{1,3}(\\.\\d{1,2})?)";

    private static final String SET_PRICE = "(price" + ASSIGNMENT_PATTERN + PRICE_PATTERN + ")";

    //Milk props
    private static final String SET_FATTINESS = "(fattiness" + ASSIGNMENT_PATTERN + "\\d\\.\\d)";

    private static final String SET_BRAND = "(brand" + ASSIGNMENT_PATTERN + NOT_NULL_STRING_PATTERN + ")";

    //Meat & Bread props
    private static final String SET_TYPE = "(type" + ASSIGNMENT_PATTERN + "\\w{1,20})";

    //PK
    private static final String PRIMARY_KEY = "(id" + ASSIGNMENT_PATTERN + ID_PATTERN + ")";

    private static final String PRIMARY_KEYS = PRIMARY_KEY + "\\s*(,\\s*" + PRIMARY_KEY + "\\s*)";

    //Set exprs
    private static final String SET_MILK_PROPS = "(milk)\\s+" + PRIMARY_KEY + "\\s+(" + SET_PRICE + "|"
            + SET_FATTINESS + "|" + SET_BRAND + ")";

    private static final String SET_BREAD_OR_MEAT_PROPS = "\\b(bread|meat)\\b\\s+" + PRIMARY_KEY + "\\s+("
            + SET_PRICE + "|" + SET_TYPE + ")";

    //Commands
    private static final String INSERT_COMMAND = "insert";

    private static final String DELETE_COMMAND = "delete";

    private static final String UPDATE_COMMAND = "update";

    private static final String SHOW_COMMAND = "show";

    private static final Pattern[] COMMAND_PATTERNS = {
        Pattern.compile("\\s*(" + INSERT_COMMAND + ")\\s+(milk)\\s+(" + SET_PRICE + "\\s*,\\s*" + SET_FATTINESS
                + "\\s*,\\s*" + SET_BRAND + ")\\s*"),
        Pattern.compile("\\s*(" + INSERT_COMMAND + ")\\s+\\b(meat|bread)\\b\\s+(" + SET_PRICE + "\\s*,\\s*"
                + SET_TYPE + ")\\s*"),
        Pattern.compile("\\s*(" + DELETE_COMMAND + ")\\s+(\\b(meat|milk|bread)\\b)\\s+(" + PRIMARY_KEYS + ")\\s*"),
        Pattern.compile("\\s*(" + UPDATE_COMMAND + ")\\s+(" + SET_MILK_PROPS + "|" + SET_BREAD_OR_MEAT_PROPS + ")\\s*"),
        Pattern.compile("\\s*(" + SHOW_COMMAND + ")\\s+(\\b(meat|milk|bread)\\b)\\s*"),
    };

    private static final Hashtable<String, CommandType> nameToCommandType = new Hashtable<String, CommandType>() {
        {
            put("insert", CommandType.INSERT);
            put("delete", CommandType.DELETE);
            put("update", CommandType.UPDATE);
            put("show", CommandType.SHOW);
        }
    };

    private static Matcher getMatcherFromInput(String userInput) {
        for (int i = 0; i < COMMAND_PATTERNS.length; ++i) {
            Matcher matcher = COMMAND_PATTERNS[i].matcher(userInput);
            if (matcher.matches()) {
                return matcher;
            }
        }
        return null;
    }

    public static boolean execute(IController controller, String userInput) throws SQLException
    {
        Matcher matcher = getMatcherFromInput(userInput);
        if (matcher == null) {
            return false;
        }
        switch (nameToCommandType.get(matcher.group(1))) {
            case INSERT: {
                Properties props = new Properties();
                props.setProperty("product_name", matcher.group(2));
                String[] setValues = matcher.group(3).split(",");
                for (int i = 0; i < setValues.length; ++i) {
                    String[] pair = setValues[i].split("=");
                    props.setProperty(pair[0].trim(), pair[1].trim());
                }
                controller.insert(props);
            }
            break;

            case UPDATE: {
                Properties props = new Properties();
                Matcher m = Pattern.compile(SET_MILK_PROPS + "|" + SET_BREAD_OR_MEAT_PROPS).matcher(userInput);
                if (!m.matches()) {
                    return false;
                }
                props.setProperty("product_name", m.group(1));
                String[] pair = m.group(3).split("=");
                props.setProperty(pair[0].trim(), pair[1].trim());
                controller.update(Integer.parseInt(m.group(2)), props);
            }
            break;
        }
        return true;
    }
}
