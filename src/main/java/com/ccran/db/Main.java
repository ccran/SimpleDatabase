package com.ccran.db;

import java.util.Scanner;

/**
 * @author chenran
 * @description Main
 * @create 2019-11-20 19:51
 **/
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printPrompt();
            String input = scanner.nextLine().trim();
            if (".exit".equals(input)) {
                System.out.println("~");
                return;
            } else {
                System.out.println(String.format("Unrecognized command '%s' .", input));
            }
        }
    }

    public static void printPrompt() {
        System.out.print("db > ");
    }
}
