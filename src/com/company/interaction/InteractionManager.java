package com.company.interaction;

import java.lang.String;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class InteractionManager {
    private static final String COLUMN_NAMES = "";

    private static final String SET_VALUE_STMT = COLUMN_NAMES + "\\s*=\\s*.+";

    private static final String SET_VALUES_STMT = SET_VALUE_STMT + "\\s*(,\\s*" + SET_VALUE_STMT + "\\s*)*";

    private static final String PRIMARY_KEY = "id\\s*=\\s*[1-9]\\d*";

    private static final ArrayList<Pattern> COMMAND_PATTERNS = new ArrayList<Pattern>() {
        {
            Pattern.compile("\\s*insert\\s+" + SET_VALUES_STMT);
            Pattern.compile("\\s*delete\\s+(" + PRIMARY_KEY + "\\s*(,\\s*" + PRIMARY_KEY + "\\s*)*)|\\*");
            Pattern.compile("\\s*update\\s+" + PRIMARY_KEY + "\\s+" + SET_VALUES_STMT);
        }
    };
}
