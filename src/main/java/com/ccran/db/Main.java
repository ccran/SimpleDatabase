package com.ccran.db;

import com.ccran.db.entity.Statement;
import com.ccran.db.status.MetaCommandResult;
import com.ccran.db.status.PrepareResult;

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
                case PREPARE_SUCCESS:
                    break;
                case PREPARE_UNRECOGNIZED_STATEMENT:
                    System.out.println(String.format("Unrecognized keyword at start of '%s'", input));
                    continue;
            }

            // 执行命令
            executeStatement(statement);
            System.out.println("Executed.");
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
    public static void executeStatement(Statement statement) {
        switch (statement.getType()) {
            case STATEMENT_INSERT:
                System.out.println("This is where we would do an insert.");
                break;
            case STATEMENT_SELECT:
                System.out.println("This is where we would do an select.");
                break;
        }
    }

    /**
     * @return 预装配语句返回结果
     * @description 预装配语句
     * @author ccran
     * @update 2019/11/20 20:20
     */
    public static PrepareResult prepareStatement(String input, Statement statement) {
        if (input.startsWith("insert")) {
            statement.setType(Statement.StatementType.STATEMENT_INSERT);
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
