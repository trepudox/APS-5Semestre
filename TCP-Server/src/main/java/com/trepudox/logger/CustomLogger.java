package com.trepudox.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLogger {

    private CustomLogger() {}

    public static CustomLogger getLogger() {
        return new CustomLogger();
    }

    public void info(String msg) {
        printFormattedMessage(msg, LoggerLevel.INFO);
    }

    public void info(String msg, Object... args) {
        String finalMsg = String.format(msg, args);
        printFormattedMessage(finalMsg, LoggerLevel.INFO);
    }

    public void error(String msg) {
        printFormattedMessage(msg, LoggerLevel.ERROR);
    }

    public void error(String msg, Object... args) {
        String finalMsg = String.format(msg, args);
        printFormattedMessage(finalMsg, LoggerLevel.ERROR);
    }

    public void message(String msg) {
        printFormattedMessage(msg, LoggerLevel.MESSAGE);
    }

    public void message(String msg, Object... args) {
        String finalMsg = String.format(msg, args);
        printFormattedMessage(finalMsg, LoggerLevel.MESSAGE);
    }

    private void printFormattedMessage(String msg, LoggerLevel loggerLevel) {
        System.out.printf("%s AT %s - [%s] - %s%n", loggerLevel.name(), Thread.currentThread(), currentDateTime(), msg);
    }

    private static String currentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSSSSS"));
    }

}
