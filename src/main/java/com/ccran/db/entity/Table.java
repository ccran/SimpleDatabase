package com.ccran.db.entity;

import com.ccran.db.result.ExecuteResult;
import com.ccran.db.util.Util;

/**
 * @author ccran
 * @description 实体表
 * @create 2019-11-22 20:22
 **/
public class Table {
    public static int PAGE_SIZE = 4096;//一页的大小
    public static int TABLE_MAX_PAGES = 100;//表的最大页
    public static int ROWS_PER_PAGE = PAGE_SIZE / Row.ROW_SIZE; //一页可以存放的Row行数
    public static int TABLE_MAX_ROWS = ROWS_PER_PAGE * TABLE_MAX_PAGES;//表可以存放的最多行数

    private int numRows;//目前行数
    private Page[] pages;//页

    class Page {
        byte[] content;

        Page() {
            content = new byte[PAGE_SIZE];
        }
    }

    public Table() {
        numRows = 0;
        pages = new Page[TABLE_MAX_PAGES];
    }

    /**
     * 插入一行
     *
     * @param statement
     * @return
     */
    public ExecuteResult executeInsert(Statement statement) {
        // 满了
        if (numRows >= TABLE_MAX_ROWS)
            return ExecuteResult.EXECUTE_TABLE_FULL;
        // 序列化
        byte[] serializeBytes = Util.serializeRow(statement.getRowToInsert());
        int page_num = numRows / ROWS_PER_PAGE;//找到页数
        if (pages[page_num] == null) {//没有页则创建页
            pages[page_num] = new Page();
        }
        // 找到字节偏移
        int byte_offset = (numRows % ROWS_PER_PAGE) * Row.ROW_SIZE;
        // 填充数据
        for (int i = 0; i < Row.ROW_SIZE; i++) {
            pages[page_num].content[byte_offset + i] = serializeBytes[i];
        }
        //System.out.println(Arrays.toString(pages[page_num].content));
        // 行数+1
        numRows++;
        return ExecuteResult.EXECUTE_SUCCESS;
    }

    /**
     * 查询所有内容
     *
     * @return
     */
    public ExecuteResult executeSelect() {
        for (int i = 0; i < numRows; i++) {
            byte[] deserializeBytes = new byte[Row.ROW_SIZE];
            int page_num = i / ROWS_PER_PAGE;//找到页数
            int byte_offset = (i % ROWS_PER_PAGE) * Row.ROW_SIZE;// 找到字节偏移
            for (int j = 0; j < Row.ROW_SIZE; j++) {//赋值
                deserializeBytes[j] = pages[page_num].content[byte_offset + j];
            }
            System.out.println(Util.deserializeRow(deserializeBytes));//输出对象
        }
        return ExecuteResult.EXECUTE_SUCCESS;
    }

    public static void main(String[] args) {
        Statement insert1 = new Statement(Statement.StatementType.STATEMENT_INSERT,
                new Row(1, "ccran", "ccran@qq.com"));
        Statement insert2 = new Statement(Statement.StatementType.STATEMENT_INSERT,
                new Row(2, "miko", "miko@qq.com"));
        Table table = new Table();
        table.executeInsert(insert1);
        table.executeInsert(insert2);
        table.executeSelect();
    }
}
