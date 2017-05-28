package com.company.interaction;

import com.company.controllers.IController;

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

public class InteractionManager {
    private static final String ASSIGNMENT_PATTERN = "\\s*=\\s*";

    private static final String NOT_NULL_STRING_PATTERN = "\"[^\"]{1,50}?\"";

    private static final String PRICE_PATTERN = "(\\d{1,3}(\\.\\d{1,2})?)";

    private static final String SET_PRICE = "(price" + ASSIGNMENT_PATTERN + PRICE_PATTERN + ")";

    //Milk props
    private static final String SET_FATTINESS = "(fattiness" + ASSIGNMENT_PATTERN + "\\d(\\.\\d)?)";

    private static final String SET_BRAND = "brand" + ASSIGNMENT_PATTERN + NOT_NULL_STRING_PATTERN;

    //Meat & Bread props
    private static final String SET_TYPE = "type" + ASSIGNMENT_PATTERN + NOT_NULL_STRING_PATTERN;

    //PK
    private static final String PRIMARY_KEY = "\\d+";

    private static final String PRIMARY_KEYS = PRIMARY_KEY + "\\s*(,\\s*" + PRIMARY_KEY + "\\s*)*";

    private static final String SET_COLUMN = "(" + SET_PRICE + ")|(" + SET_BRAND + ")|(" + SET_FATTINESS + ")|(" +
            SET_TYPE + ")";

    private static final Pattern[] COMMAND_PATTERNS = {
        Pattern.compile("\\s*(insert)\\s+(milk)\\s+(" + SET_PRICE + "\\s*,\\s*" + SET_FATTINESS + "\\s*,\\s*"
                + SET_BRAND + ")\\s*"),
        Pattern.compile("\\s*(insert)\\s+\\b(meat|bread)\\b\\s+(" + SET_PRICE + "\\s*,\\s*" + SET_TYPE + ")\\s*"),
        Pattern.compile("\\s*(delete)\\s+(" + PRIMARY_KEYS + ")\\s*"),
        Pattern.compile("\\s*(update)\\s+id" + ASSIGNMENT_PATTERN + "(" + PRIMARY_KEY + ")\\s+((" + SET_COLUMN +
                ")(\\s*,\\s*(" + SET_COLUMN + "))*)\\s*"),
        Pattern.compile("\\s*(show)\\s*"),
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

    public static boolean execute(IController controller, String userInput) throws SQLException {
        Matcher matcher = getMatcherFromInput(userInput);
        if (matcher == null) {
            return false;
        }
        switch (nameToCommandType.get(matcher.group(1))) {
            case INSERT: {
                Properties props = new Properties();
                props.setProperty("product_name", "\"" + matcher.group(2) + "\"");
                String[] setValues = matcher.group(3).split(",");
                for (int i = 0; i < setValues.length; ++i) {
                    String[] pair = setValues[i].split("=");
                    props.setProperty(pair[0].trim(), pair[1].trim());
                }
                return controller.insert(props);
            }

            case UPDATE: {
                Properties props = new Properties();
                int id = Integer.parseInt(matcher.group(2));
                String[] setStmts = matcher.group(3).split(",");
                for (String stmt : setStmts) {
                    String[] pair = stmt.split("=");
                    props.setProperty(pair[0].trim(), pair[1].trim());
                }
                return controller.update(id , props);
            }

            case DELETE: {
                String[] pks = matcher.group(2).split(",");
                boolean result = false;
                for (int i = 0; i < pks.length; ++i) {
                    result = result || controller.delete(Integer.parseInt(pks[i].trim()));
                }
                return result;
            }

            case SHOW: {
                controller.show();
            }
            break;
        }
        return true;
    }
}
