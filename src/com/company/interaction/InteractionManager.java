package com.company.interaction;

import java.util.regex.Pattern;

public class InteractionManager {
    private enum CommandPart {
        COMMAND_NAME,
        PRIMARY_KEY,
        SET_VALUES_STATEMENT
    };

    private static final String COLUMN_NAME_IDENTIFIER = "[\\w$]+";

    private static final String COLUMN_VALUE_IDENTIFIER = "[\\w]*";

    private static final String SET_VALUE_STMT = COLUMN_NAME_IDENTIFIER + "\\s*=\\s*" + COLUMN_VALUE_IDENTIFIER;

    private static final String SET_VALUES_STMT = SET_VALUE_STMT + "\\s*(,\\s*" + SET_VALUE_STMT + "\\s*)*";

    private static final String PRIMARY_KEY = "id\\s*=\\s*[1-9]\\d*";

    private static final Pattern[] COMMAND_PATTERNS = {
        Pattern.compile("\\s*(insert)\\s+()(" + SET_VALUES_STMT + ")"),
        Pattern.compile("\\s*(delete)\\s+((" + PRIMARY_KEY + "\\s*(,\\s*" + PRIMARY_KEY + "\\s*)*)|\\*)"),
        Pattern.compile("\\s*(update)\\s+(" + PRIMARY_KEY + ")\\s+(" + SET_VALUES_STMT + ")")
    };
}
