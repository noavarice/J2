package com.company.interaction;

import com.company.controllers.AbstractController;

import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Pattern.compile("\\s*(" + INSERT_COMMAND + ")\\s+milk\\s+(" + SET_PRICE + "\\s*,\\s*" + SET_FATTINESS
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

    private static TerminalCommand getCommandFromInput(String userInput) {
        // прогоняем строку по регуляркам, если нашли совпадение - создаем из строки команду
        for (int i = 0; i < COMMAND_PATTERNS.length; ++i) {
            Matcher matcher = COMMAND_PATTERNS[i].matcher(userInput);
            if (matcher.matches()) {
                String tableName = matcher.group(CommandPart.TABLE_NAME.ordinal());
                CommandType type = nameToCommandType.get(matcher.group(CommandPart.COMMAND_NAME.ordinal()));
                String idString = matcher.group(CommandPart.PRIMARY_KEY.ordinal());
                int id = idString.isEmpty() ? -1 : Integer.parseInt(idString);
                String columnValuesPairs = matcher.group(CommandPart.SET_VALUES_STATEMENTS.ordinal());
                return new TerminalCommand(tableName, type, id, columnValuesPairs);
            }
        }
        return new TerminalCommand();
    }

    private static final String PAIRS_PATTERN = "\\s*(\\w+)" + ASSIGNMENT_PATTERN + "(([^\\s]+)|(" +
                                                NOT_NULL_STRING_PATTERN + ")|(" + NULL_STRING_PATTERN + "))\\s*";

    public static boolean execute(AbstractController controller, String userInput)
    {
        return controller.tryExecute(getCommandFromInput(userInput));
    }
}
