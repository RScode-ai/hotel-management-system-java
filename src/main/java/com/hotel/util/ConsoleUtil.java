package com.hotel.util;

public class ConsoleUtil {

    public static void printHeader(String title) {
        System.out.println("========================================");
        System.out.println(title);
        System.out.println("=".repeat(title.length()));
    }

    public static void printSeparator() {
        System.out.println("----------------------------------------");
    }

    public static void printError(String message) {
        System.out.println("ERROR: " + message);
    }

    public static void printSuccess(String message) {
        System.out.println(">> " + message);
    }
}
