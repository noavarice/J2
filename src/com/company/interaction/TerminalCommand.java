package com.company.interaction;

enum CommandType {
    NOT_VALID,
    INSERT,
    DELETE,
    UPDATE,
    SHOW,
}

public class TerminalCommand {
    private String tableName;

    private CommandType comType = CommandType.NOT_VALID;

    private int id = -1;

    private String columnValues = "";

    public TerminalCommand() {}

    public TerminalCommand(String tableName, CommandType type, int id, String columnValuePairs) {
        this.tableName = tableName;
        comType = type;
        this.id = id;
        columnValues = columnValuePairs;
    }

    public String getTableName() { return tableName; }

    public CommandType getCommandType() { return comType; }

    public int getId() {
        return id;
    }

    public String getColumnValues() {
        return columnValues;
    }
}
