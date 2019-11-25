package com.ccran.db;

import com.ccran.db.entity.Row;
import com.ccran.db.entity.Statement;
import com.ccran.db.entity.Table;
import com.ccran.db.result.ExecuteResult;
import com.ccran.db.result.MetaCommandResult;
import com.ccran.db.result.PrepareResult;

import java.util.Scanner;

/**
 * @author ccran
 * @description Main
 * @create 2019-11-20 19:51
 **/
public class Main {
    public static void main(String[] args) {
        // 输入流
        Scanner scanner = new Scanner(System.in);
        // 创建Table
        Table table = new Table();
        // 循环处理
        while (true) {
            // 命令行提示输出
            printPrompt();
            String input = scanner.nextLine().trim();
            // 元信息处理
            if (input.startsWith(".")) {
                switch (doMetaCommand(input)) {
                    case META_COMMAND_EXIT:
                        return;
                    case META_COMMAND_SUCCESS:
                        continue;
                    case META_COMMAND_UNRECOGNIZED_COMMAND:
                        System.out.println(String.format("Unrecognized command '%s'", input));
                        continue;
                }
            }

            // 预装配命令
            Statement statement = new Statement();
            switch (prepareStatement(input, statement)) {
                case PREPARE_STRING_TOO_LONG:
                    System.out.println("String is too long.");
                    continue;
                case PREPARE_NEGATIVE_ID:
                    System.out.println("ID must be positive.");
                    continue;
                case PREPARE_SYNTAX_ERROR:
                    System.out.println("Syntax error. Could not parse statement.");
                    continue;
                case PREPARE_SUCCESS:
                    System.out.println("PrepareStatement success." + statement);
                    break;
                case PREPARE_UNRECOGNIZED_STATEMENT:
                    System.out.println(String.format("Unrecognized keyword at start of '%s'", input));
                    continue;
            }
            // insert -1 ffffffffffffffffffffffffffffffgga foo@bar.com
            // insert 2 bob bob@example.com
            // 执行命令
            switch (executeStatement(table, statement)) {
                case EXECUTE_SUCCESS:
                    System.out.println("Executed.");
                    break;
                case EXECUTE_TABLE_FULL:
                    System.out.println("Error: Table full.");
                    break;
            }
        }
    }

    /**
     * @description 输出命令行提示符
     * @author ccran
     * @update 2019/11/20 20:13
     */
    public static void printPrompt() {
        System.out.print("db > ");
    }

    /**
     * @description 执行命令
     * @author ccran
     * @update 2019/11/20 20:26
     */
    public static ExecuteResult executeStatement(Table table, Statement statement) {
        switch (statement.getType()) {
            case STATEMENT_INSERT:
                return table.executeInsert(statement);
            case STATEMENT_SELECT:
                return table.executeSelect();
        }
        return ExecuteResult.EXECUTE_SUCCESS;
    }

    /**
     * @return 预装配语句返回结果
     * @description 预装配语句
     * @author ccran
     * @update 2019/11/20 20:20
     */
    public static PrepareResult prepareStatement(String input, Statement statement) {
        // 插入语句
        if (input.startsWith("insert")) {
            statement.setType(Statement.StatementType.STATEMENT_INSERT);
            // 获取插入行
            Row row = new Row();
            // 严格根据空格分隔
            String[] sepInput = input.split(" ");
            if (sepInput.length != 4) {
                return PrepareResult.PREPARE_SYNTAX_ERROR;
            }
            // id转换
            try {
                row.setId(Integer.parseInt(sepInput[1]));
            } catch (NumberFormatException e) {
                return PrepareResult.PREPARE_SYNTAX_ERROR;
            }
            // 判别ID是否为负数
            if(row.getId()<0){
                return PrepareResult.PREPARE_NEGATIVE_ID;
            }
            // 判别长度是否大于限定长度
            if (sepInput[2].length() > Row.USERNAME_SIZE || sepInput[3].length() > Row.EMAIL_SIZE) {
                return PrepareResult.PREPARE_STRING_TOO_LONG;
            }
            row.setUserName(sepInput[2]);
            row.setEmail(sepInput[3]);
            // 设置给statement
            statement.setRowToInsert(row);
            return PrepareResult.PREPARE_SUCCESS;
        } else if (input.startsWith("select")) {
            statement.setType(Statement.StatementType.STATEMENT_SELECT);
            return PrepareResult.PREPARE_SUCCESS;
        }
        return PrepareResult.PREPARE_UNRECOGNIZED_STATEMENT;
    }

    /**
     * @return 返回元信息处理结果
     * @description 元信息处理
     * @author ccran
     * @update 2019/11/20 20:15
     */
    public static MetaCommandResult doMetaCommand(String input) {
        if (".exit".equals(input)) {
            return MetaCommandResult.META_COMMAND_EXIT;
        } else {
            return MetaCommandResult.META_COMMAND_UNRECOGNIZED_COMMAND;
        }
    }
}
