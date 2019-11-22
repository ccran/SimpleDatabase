package com.ccran.db.entity;

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

    private int numRows;//行数
    private byte[] pages;
}
