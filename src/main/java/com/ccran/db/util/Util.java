package com.ccran.db.util;

import com.ccran.db.entity.Row;

/**
 * @author ccran
 * @description 工具类
 * @create 2019-11-22 20:22
 **/
public class Util {
    // int=>bytes
    private static byte[] int2Bytes(int integer) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (integer >> 24);
        bytes[2] = (byte) (integer >> 16);
        bytes[1] = (byte) (integer >> 8);
        bytes[0] = (byte) integer;
        return bytes;
    }

    // bytes=>int
    private static int bytes2Int(byte[] bytes) {
        int int1 = bytes[0] & 0xff;
        int int2 = (bytes[1] & 0xff) << 8;
        int int3 = (bytes[2] & 0xff) << 16;
        int int4 = (bytes[3] & 0xff) << 24;
        return int1 | int2 | int3 | int4;
    }

    // 序列化Row=>byte[]
    public static byte[] serializeRow(Row row) {
        byte[] bytes = new byte[Row.ROW_SIZE];
        byte[] idBytes = Util.int2Bytes(row.getId());
        // id
        int p = 0, q = 0;
        while (p < Row.ID_SIZE) {
            bytes[p++] = idBytes[q++];
        }
        q = 0;
        // username
        byte[] userNameBytes = row.getUserName().getBytes();
        while (p < Row.ID_SIZE + Row.USERNAME_SIZE) {
            bytes[p++] = q < userNameBytes.length ? userNameBytes[q++] : 0;
        }
        q = 0;
        // email
        byte[] emailBytes = row.getEmail().getBytes();
        while (p < Row.ROW_SIZE) {
            bytes[p++] = q < emailBytes.length ? emailBytes[q++] : 0;
        }
        return bytes;
    }

    //反序列化byte[]为Row
    public static Row deserializeRow(byte[] bytes) {
        Row row = new Row();
        int p = 0, q = 0;
        // id
        byte[] idBytes = new byte[Row.ID_SIZE];
        while (p < Row.ID_SIZE) {
            idBytes[q++] = bytes[p++];
        }
        q = 0;
        row.setId(Util.bytes2Int(idBytes));
        // username
        byte[] userNameBytes = new byte[Row.USERNAME_SIZE];
        while (p < Row.ID_SIZE + Row.USERNAME_SIZE) {
            userNameBytes[q++] = bytes[p++];
        }
        q = 0;
        row.setUserName(new String(userNameBytes).trim());
        // email
        byte[] emailBytes = new byte[Row.EMAIL_SIZE];
        while (p < Row.ROW_SIZE) {
            emailBytes[q++] = bytes[p++];
        }
        row.setEmail(new String(emailBytes).trim());
        return row;
    }
}
