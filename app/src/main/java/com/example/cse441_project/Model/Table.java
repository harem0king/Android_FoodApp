package com.example.cse441_project.Model;

public class Table {
    private String tableId;     // MaBan
    private String tableName;   // TenBan
    private String status;       // TinhTrang

    // Default constructor required for calls to DataSnapshot.getValue(Table.class)
    public Table() {
    }

    // Constructor with parameters
    public Table(String tableId, String tableName, String status) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.status = status;
    }

    // Getter and setter for tableId (MaBan)
    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    // Getter and setter for tableName (TenBan)
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    // Getter and setter for status (TinhTrang)
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
