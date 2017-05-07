package com.company.interaction;

import com.company.controllers.AbstractController;

import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InteractionManager {
    private enum CommandPart {
        TABLE_NAME,
        COMMAND_NAME,
        PRIMARY_KEY,
        SET_VALUES_STATEMENTS,
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

    private static final String PRIMARY_KEYS = PRIMARY_KEY + "\\s*(,\\s*" + PRIMARY_KEY + "\\s*)";

    private static final String CATHEGORY_COMMAND_PREFIX = "cathegory";

    private static final String GOODS_COMMAND_PREFIX = "goods";

    private static final String INSERT_COMMAND = "insert";

    private static final String DELETE_COMMAND = "delete";

    private static final String UPDATE_COMMAND = "update";

    private static final String SHOW_COMMAND = "show";

    private static final Pattern[] COMMAND_PATTERNS = {
        Pattern.compile("\\s*(" + CATHEGORY_COMMAND_PREFIX + ")\\s+(" + INSERT_COMMAND + ")()\\s+(" + SET_NAME +
                        "\\s*,\\s*" + SET_DESCRIPTION + ")\\s*"),
        Pattern.compile("\\s*(" + GOODS_COMMAND_PREFIX + ")\\s+(" + INSERT_COMMAND + ")()\\s+(" + SET_CATHEGORY_ID +
                        "\\s*,\\s*" + SET_PRICE + "'\\s*,\\s*" + SET_BRAND + ")\\s*"),
        Pattern.compile("\\s*(" + CATHEGORY_COMMAND_PREFIX + ")\\s+(" + DELETE_COMMAND + ")\\s+(" + PRIMARY_KEYS +
                        ")()\\s*"),
        Pattern.compile("\\s*(" + GOODS_COMMAND_PREFIX + ")\\s+(" + DELETE_COMMAND + ")\\s+(" + PRIMARY_KEYS +
                        ")()\\s*"),
        Pattern.compile("\\s*(" + CATHEGORY_COMMAND_PREFIX + ")\\s+(" + UPDATE_COMMAND + ")\\s+(" + PRIMARY_KEY +
                        ")\\s+(" + SET_NAME + "|" + SET_DESCRIPTION + ")\\s*"),
        Pattern.compile("\\s*(" + GOODS_COMMAND_PREFIX + ")\\s+(" + UPDATE_COMMAND + ")\\s+(" + PRIMARY_KEY + ")\\s+(" +
                        SET_CATHEGORY_ID + "|" + SET_PRICE + "|" + SET_BRAND + ")\\s*"),
        Pattern.compile("\\s*(" + CATHEGORY_COMMAND_PREFIX + "|" + GOODS_COMMAND_PREFIX + ")\\s+(" + SHOW_COMMAND +
                        ")()()\\s*"),
        Pattern.compile("\\s*(" + GOODS_COMMAND_PREFIX + "|" + GOODS_COMMAND_PREFIX + ")\\s+(" + SHOW_COMMAND +")\\s*"),
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
        TerminalCommand command = getCommandFromInput(userInput);
        switch (command.getCommandType()) {
            case NOT_VALID: return false;

            case SHOW: controller.show();
            break;

            case INSERT: {
                Properties props = new Properties();
                String[] pairs = command.getColumnValues().split(",");
                Pattern p = Pattern.compile(PAIRS_PATTERN);
                for (int i = 0; i < pairs.length; ++i) {
                    String[] pair = pairs[i].split("=");
                    props.put(pair[0].trim(), pair[1].trim());
                }
                controller.insert(props);
            }
            break;
        }
        return true;
    }
}
