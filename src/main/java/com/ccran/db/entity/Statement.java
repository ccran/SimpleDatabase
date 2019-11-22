package com.ccran.db.entity;

/**
 * @author ccran
 * @description 命令
 * @create 2019-11-20 20:12
 **/
public class Statement {
    private StatementType type;//语句类型
    private Row rowToInsert;//插入行

    public StatementType getType() {
        return type;
    }

    public void setType(StatementType type) {
        this.type = type;
    }

    public Row getRowToInsert() {
        return rowToInsert;
    }

    public void setRowToInsert(Row rowToInsert) {
        this.rowToInsert = rowToInsert;
    }

    public enum StatementType {
        STATEMENT_INSERT, STATEMENT_SELECT
    }
}
