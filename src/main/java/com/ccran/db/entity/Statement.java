package com.ccran.db.entity;

/**
 * @author ccran
 * @description 命令
 * @create 2019-11-20 20:12
 **/
public class Statement {
    StatementType type;

    public StatementType getType() {
        return type;
    }

    public void setType(StatementType type) {
        this.type = type;
    }

    public enum StatementType {
        STATEMENT_INSERT, STATEMENT_SELECT
    }
}
