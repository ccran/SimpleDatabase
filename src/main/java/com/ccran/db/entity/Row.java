package com.ccran.db.entity;

import com.ccran.db.util.Util;

import java.util.Arrays;

/**
 * @author ccran
 * @description 插入行
 * @create 2019-11-21 20:22
 **/
public class Row {
    //测试序列化与反序列化
    public static void main(String[] args) {
        Row row = new Row();
        row.setId(1);
        row.setUserName("abc");
        row.setEmail("bcd");
        System.out.println(row);
        byte[] bytes = Util.serializeRow(row);
        System.out.println(Arrays.toString(bytes));
        System.out.println(Util.deserializeRow(bytes));
    }

    public static final int ID_SIZE = 4;
    public static final int USERNAME_SIZE = 32;
    public static final int EMAIL_SIZE = 255;
    public static final int ROW_SIZE = ID_SIZE + USERNAME_SIZE + EMAIL_SIZE;

    private int id;
    private String userName;
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Row{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
