package com.company.interaction;

import com.company.controllers.AbstractController;
import com.company.controllers.DatabaseController;
import com.company.controllers.FileController;
import com.company.models.Product;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum CommandType {
    LOAD,
    INSERT,
    DELETE,
    UPDATE,
    SHOW,
    FILTER,
    SAVE,
    EXIT
}

enum CommandResult {
    SUCCEEDED,
    FAILED,
    LOADING_FAILED,
    SAVING_FAILED,
    CONTROLLER_IS_NOT_CHOSEN,
    FINISHED,
}

public class InteractionManager {
    private static final String ASSIGNMENT_PATTERN = "\\s*=\\s*";

    private static final String NOT_NULL_STRING_PATTERN = "\"[^\"]{1,50}\"";

    private static final String FILE_NAME_PATTERN = "[^\"]+";

    private static final String PRICE_PATTERN = "(\\d{1,3}(\\.\\d{1,2})?)";

    private static final String SET_PRICE = "(price" + ASSIGNMENT_PATTERN + PRICE_PATTERN + ")";

    //Milk props
    private static final String SET_FATTINESS = "(fattiness" + ASSIGNMENT_PATTERN + "\\d(\\.\\d)?)";

    private static final String SET_BRAND = "brand" + ASSIGNMENT_PATTERN + NOT_NULL_STRING_PATTERN;

    //Meat & Bread props
    private static final String SET_TYPE = "type" + ASSIGNMENT_PATTERN + NOT_NULL_STRING_PATTERN;

    private static final String SET_FLOUR_TYPE = "flour_type" + ASSIGNMENT_PATTERN + NOT_NULL_STRING_PATTERN;

    private static final String SET_MEAT_TYPE = "meat_type" + ASSIGNMENT_PATTERN + NOT_NULL_STRING_PATTERN;

    //PK
    private static final String PRIMARY_KEY = "\\d+";

    private static final String PRIMARY_KEYS = PRIMARY_KEY + "\\s*(,\\s*" + PRIMARY_KEY + "\\s*)*";

    private static final String SET_COLUMN = "((" + SET_PRICE + ")|(" + SET_BRAND + ")|(" + SET_FATTINESS + ")|(" +
            SET_TYPE + ")|(" + SET_FLOUR_TYPE + ")|(" + SET_MEAT_TYPE + "))";

    private static final Hashtable<String, BiPredicate<Product, Double>> signToPredicate = new Hashtable<String, BiPredicate<Product, Double>>() {
        {
            put("<", (product, maxPrice) -> product.getPrice() < maxPrice);
            put(">", (product, maxPrice) -> product.getPrice() > maxPrice);
            put("<=", (product, maxPrice) -> product.getPrice() <= maxPrice);
            put(">=", (product, maxPrice) -> product.getPrice() >= maxPrice);
            put("=", (product, maxPrice) -> product.getPrice() == maxPrice);
        }
    };

    private static final Pattern[] COMMAND_PATTERNS = {
            Pattern.compile("\\s*(load)\\s+(file)" + ASSIGNMENT_PATTERN + "\"(" + FILE_NAME_PATTERN + ")\"\\s*"),
            Pattern.compile("\\s*(load)\\s+(db)\\s*"),
            Pattern.compile("\\s*(insert)\\s+(milk)\\s+(" + SET_PRICE + "\\s*,\\s*" + SET_FATTINESS + "\\s*,\\s*" +
                    SET_BRAND + ")\\s*"),
            Pattern.compile("\\s*(insert)\\s+(bread)\\s+(" + SET_PRICE + "\\s*,\\s*" + SET_FLOUR_TYPE + ")\\s*"),
            Pattern.compile("\\s*(insert)\\s+(meat)\\s+(" + SET_PRICE + "\\s*,\\s*" + SET_MEAT_TYPE + ")\\s*"),
            Pattern.compile("\\s*(delete)\\s+()(" + PRIMARY_KEYS + ")\\s*"),
            Pattern.compile("\\s*(delete)\\s+(-i)\\s+(" + PRIMARY_KEY + "\\s*-\\s*" + PRIMARY_KEY + ")\\s*"),
            Pattern.compile("\\s*(update)\\s+id" + ASSIGNMENT_PATTERN + "(" + PRIMARY_KEY + ")\\s+(" + SET_COLUMN +
                    "(\\s*,\\s*" + SET_COLUMN + ")*)\\s*"),
            Pattern.compile("\\s*(show)(\\s+sort\\s+\\b(asc|desc)\\b)?\\s*"),
            Pattern.compile("\\s*(filter)\\s+price\\s+(([<>]=?)|(=))\\s*(" + PRICE_PATTERN + ")\\s*"),
            Pattern.compile(("\\s*(save)(\\s+file" + ASSIGNMENT_PATTERN + "\"" + FILE_NAME_PATTERN + "\")?\\s*")),
            Pattern.compile(("\\s*(exit)\\s*")),
    };

    private static final Hashtable<String, CommandType> nameToCommandType = new Hashtable<String, CommandType>() {
        {
            put("load", CommandType.LOAD);
            put("insert", CommandType.INSERT);
            put("delete", CommandType.DELETE);
            put("update", CommandType.UPDATE);
            put("show", CommandType.SHOW);
            put("filter", CommandType.FILTER);
            put("save", CommandType.SAVE);
            put("exit", CommandType.EXIT);
        }
    };

    static AbstractController controller = null;

    private static Matcher getMatcherFromInput(String userInput) {
        for (int i = 0; i < COMMAND_PATTERNS.length; ++i) {
            Matcher matcher = COMMAND_PATTERNS[i].matcher(userInput);
            if (matcher.matches()) {
                return matcher;
            }
        }
        return null;
    }

    private static CommandResult execute(String userInput) throws
            SQLException,
            IOException
    {
        Matcher matcher = getMatcherFromInput(userInput);
        if (matcher == null) {
            return CommandResult.FAILED;
        }
        switch (nameToCommandType.get(matcher.group(1))) {
            case LOAD: {
                String type = matcher.group(2);
                if (type.equals("db")) {
                    try {
                        DatabaseController temp = new DatabaseController("/home/alexrazinkov/Projects/Java/conn");
                        if (controller == null || !controller.getClass().isAssignableFrom(temp.getClass())) {
                            controller = temp;
                        }
                    } catch (SQLException e) {
                        return CommandResult.LOADING_FAILED;
                    } catch (IOException e) {
                        return CommandResult.LOADING_FAILED;
                    }
                } else {
                    String filePath = matcher.group(3);
                    Path path = Paths.get(filePath);
                    try {
                        FileController temp = new FileController(filePath.toString());
                        if (controller == null || !controller.getClass().isAssignableFrom(temp.getClass())) {
                            controller = temp;
                        }
                    } catch (IOException e) {
                        return CommandResult.LOADING_FAILED;
                    }
                }
                return CommandResult.SUCCEEDED;
            }

            case INSERT: {
                if (controller == null) {
                    return CommandResult.CONTROLLER_IS_NOT_CHOSEN;
                }
                Properties props = new Properties();
                props.setProperty("product_name", "\"" + matcher.group(2) + "\"");
                String[] setValues = matcher.group(3).split(",");
                for (int i = 0; i < setValues.length; ++i) {
                    String[] pair = setValues[i].split("=");
                    props.setProperty(pair[0].trim(), pair[1].trim());
                }
                if (Double.parseDouble(props.getProperty("price")) == 0) {
                    return CommandResult.FAILED;
                }
                if (props.stringPropertyNames().contains("fattiness") && Double.parseDouble(props.getProperty("fattiness")) == 0) {
                    return CommandResult.FAILED;
                }
                return controller.insert(props) ? CommandResult.SUCCEEDED : CommandResult.FAILED;
            }

            case UPDATE: {
                if (controller == null) {
                    return CommandResult.CONTROLLER_IS_NOT_CHOSEN;
                }
                Properties props = new Properties();
                int id = Integer.parseInt(matcher.group(2));
                String[] setStmts = matcher.group(3).split(",");
                for (String stmt : setStmts) {
                    String[] pair = stmt.split("=");
                    props.setProperty(pair[0].trim(), pair[1].trim());
                }
                Set<String> propNames = props.stringPropertyNames();
                if (propNames.contains("price") && Double.parseDouble(props.getProperty("price")) == 0) {
                    return CommandResult.FAILED;
                }
                if (propNames.contains("fattiness") && Double.parseDouble(props.getProperty("fattiness")) == 0) {
                    return CommandResult.FAILED;
                }
                return controller.update(id, props) ? CommandResult.SUCCEEDED : CommandResult.FAILED;
            }

            case DELETE: {
                if (controller == null) {
                    return CommandResult.CONTROLLER_IS_NOT_CHOSEN;
                }
                if (!matcher.group(2).isEmpty()) {
                    String[] interval = matcher.group(3).split("-");
                    int start = Integer.parseInt(interval[0].trim());
                    int end = Integer.parseInt(interval[1].trim());
                    if (start > end || start == 0) {
                        return CommandResult.FAILED;
                    }
                    for (int i = start; i <= end; ++i) {
                        controller.delete(i);
                    }
                } else {
                    boolean result = false;
                    String[] ids = matcher.group(3).split(",");
                    for (String id : ids) {
                        result = controller.delete(Integer.parseInt(id.trim())) || result;
                    }
                    return result ? CommandResult.SUCCEEDED : CommandResult.FAILED;
                }
            }
            break;

            case SHOW: {
                if (controller == null) {
                    return CommandResult.CONTROLLER_IS_NOT_CHOSEN;
                }
                String sortSection = matcher.group(2);
                if (sortSection != null) {
                    String[] str = sortSection.split(" ");
                    controller.showSorted(new PrintStream(System.out), str[2].equals("asc"));
                } else {
                    controller.show(new PrintStream(System.out));
                }
            }
            break;

            case FILTER: {
                if (controller == null) {
                    return CommandResult.CONTROLLER_IS_NOT_CHOSEN;
                }
                String sign = matcher.group(2);
                Double newPrice = new Double(matcher.group(5));
                controller.filter(new PrintStream(System.out), signToPredicate.get(sign), newPrice);
            }
            break;

            case SAVE: {
                if (controller == null) {
                    return CommandResult.CONTROLLER_IS_NOT_CHOSEN;
                }
                String file = matcher.group(2);
                if (file != null) {
                    String filePath = file.split("\"")[1].trim();
                    return controller.saveToFile(filePath) ? CommandResult.SUCCEEDED : CommandResult.FAILED;
                }
                return controller.save() ? CommandResult.SUCCEEDED : CommandResult.SAVING_FAILED;
            }

            case EXIT: {
                if (controller != null) {
                    controller.save();
                }
                return CommandResult.FINISHED;
            }
        }
        return CommandResult.SUCCEEDED;
    }

    public static void interact() throws
            IOException,
            SQLException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine();
        CommandResult state = CommandResult.SUCCEEDED;
        while (state != CommandResult.FINISHED) {
            state = execute(input);
            switch (state) {
                case SUCCEEDED: {
                    System.out.println("Command succeded");
                }
                break;

                case FAILED: {
                    System.out.println("Command failed");
                }
                break;

                case LOADING_FAILED: {
                    System.out.println("Loading from database or file has failed");
                }
                break;

                case SAVING_FAILED: {
                    System.out.println("Saving to database or to file has failed");
                }
                break;

                case CONTROLLER_IS_NOT_CHOSEN: {
                    System.out.println("Product management type is not chosen");
                }
                break;

                case FINISHED: {
                    System.out.println("Exiting..");
                    return;
                }
            }
            input = reader.readLine();
        }
    }
}
