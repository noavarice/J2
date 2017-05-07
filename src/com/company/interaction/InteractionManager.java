package com.company.interaction;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InteractionManager {
    private enum CommandPart {
        COMMAND_NAME,
        PRIMARY_KEY,
        SET_VALUES_STATEMENT
    };

    private static final String MYSQL_COLUMN_NAME_IDENTIFIER = "[\\w$]+";

    private static final String ASSIGNMENT_PATTERN = "\\s*=\\s*";

    private static final String NOT_NULL_STRING_PATTERN = "\"[^\"]+\"";

    private static final String NULL_STRING_PATTERN = "\"[^\"]*\"";

    private static final String SET_NAME = "(\\bname" + ASSIGNMENT_PATTERN + NOT_NULL_STRING_PATTERN + ")";

    private static final String SET_DESCRIPTION = "(\\bdescription" + ASSIGNMENT_PATTERN + NULL_STRING_PATTERN + ")";

    private static final String ID_PATTERN = "(([01]?\\d{1,2})" +
                                             "|(2[0-4]\\d)" +
                                             "|(25[0-5]))";

    private static final String SET_CATHEGORY_ID = "(\\bcathegory_id" + ASSIGNMENT_PATTERN + ID_PATTERN + ")";

    private static final String PRICE_PATTERN = "(\\d{1,5}(\\.\\d{1,2})?)";

    private static final String SET_PRICE = "(\\bprice" + ASSIGNMENT_PATTERN + PRICE_PATTERN + ")";

    private static final String SET_BRAND = "(\\bbrand" + ASSIGNMENT_PATTERN + NOT_NULL_STRING_PATTERN + ")";

    private static final String COLUMN_VALUE_IDENTIFIER = "[\\w]*";

    private static final String PRIMARY_KEY = "(\\bid" + ASSIGNMENT_PATTERN + ID_PATTERN + ")";

    private static final String PRIMARY_KEYS = PRIMARY_KEY + "\\s*(,\\s*" + PRIMARY_KEY + "\\*)";

    private static final String CATHEGORY_COMMAND_PREFIX = "\\bcathegory\\b";

    private static final String GOODS_COMMAND_PREFIX = "\\bgoods\\b";

    private static final String INSERT_COMMAND = "\\binsert\\b";

    private static final String DELETE_COMMAND = "\\bdelete\\b";

    private static final String UPDATE_COMMAND = "\\bupdate\\b";

    private static final Pattern[] COMMAND_PATTERNS = {
        Pattern.compile("\\s*" + CATHEGORY_COMMAND_PREFIX + "\\s+" + INSERT_COMMAND + "\\s+" + SET_NAME + "\\s+" +
                        SET_DESCRIPTION + "\\s*"),
        Pattern.compile("\\s*" + GOODS_COMMAND_PREFIX + "\\s+" + INSERT_COMMAND + "\\s+" + SET_CATHEGORY_ID + "\\s+" +
                        SET_PRICE + "\\s+" + SET_BRAND + "\\s*"),
        Pattern.compile("\\s*" + CATHEGORY_COMMAND_PREFIX + "\\s+" + DELETE_COMMAND + "\\s+" + PRIMARY_KEYS),
        Pattern.compile("\\s*" + GOODS_COMMAND_PREFIX + "\\s+" + DELETE_COMMAND + "\\s+" + PRIMARY_KEYS),
        Pattern.compile("\\s*" + CATHEGORY_COMMAND_PREFIX + "\\s+" + UPDATE_COMMAND + "\\s+" + PRIMARY_KEY + "\\s+(" +
                        SET_NAME + "|" + SET_DESCRIPTION + ")\\s*"),
        Pattern.compile("\\s*" + GOODS_COMMAND_PREFIX + "\\s+" + UPDATE_COMMAND + "\\s+" + PRIMARY_KEY + "\\s+(" +
                        SET_CATHEGORY_ID + "|" + SET_PRICE + "|" + SET_BRAND + ")\\s*")
    };

    private static final Hashtable<String, CommandType> nameToCommandType = new Hashtable<String, CommandType>() {
        {
            put("insert", CommandType.INSERT);
            put("delete", CommandType.DELETE);
            put("update", CommandType.UPDATE);
        }
    };

    public static TerminalCommand getCommandFromInput(String userInput) {
        // прогоняем строку по регуляркам, если нашли совпадение - создаем из строки команду
        for (int i = 0; i < COMMAND_PATTERNS.length; ++i) {
            Matcher matcher = COMMAND_PATTERNS[i].matcher(userInput);
            if (matcher.matches()) {
                CommandType type = nameToCommandType.get(matcher.group(CommandPart.COMMAND_NAME.ordinal()));
                int id = Integer.parseInt(matcher.group(CommandPart.PRIMARY_KEY.ordinal()));
                String columnValuesPairs = matcher.group(CommandPart.SET_VALUES_STATEMENT.ordinal());
                return new TerminalCommand(type, id, columnValuesPairs);
            }
        }
        return new TerminalCommand();
    }
}
