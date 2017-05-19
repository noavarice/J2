package com.company.interaction;

public class TerminalCommand {
    private String tableName;

    private int id = -1;

    private String columnValues = "";

    public TerminalCommand() {}

    public TerminalCommand(String tableName, CommandType type, int id, String columnValuePairs) {
        this.tableName = tableName;
        this.id = id;
        columnValues = columnValuePairs;
    }

    public String getTableName() { return tableName; }

    public int getId() {
        return id;
    }

    public String getColumnValues() {
        return columnValues;
    }
}
