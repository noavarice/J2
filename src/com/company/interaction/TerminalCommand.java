package com.company.interaction;

enum CommandType {
    NOT_VALID,
    INSERT,
    DELETE,
    UPDATE
}

public class TerminalCommand {
    private CommandType comType = CommandType.NOT_VALID;

    private int id = -1;

    private String columnValues = "";

    public TerminalCommand() {}

    public TerminalCommand(CommandType type, int id, String columnValuePairs) {
        comType = type;
        this.id = id;
        columnValues = columnValuePairs;
    }

    public CommandType getCommandType() {
        return comType;
    }

    public int getId() {
        return id;
    }

    public String getColumnValues() {
        return columnValues;
    }
}
