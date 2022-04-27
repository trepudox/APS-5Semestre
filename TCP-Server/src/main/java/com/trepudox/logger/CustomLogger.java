package com.trepudox.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLogger {


    private CustomLogger() {}

    public static CustomLogger getLogger() {
        return new CustomLogger();
    }

    public void info(String msg) {
        msgFormat(msg, LoggerLevel.INFO);
    }

    public void info(String msg, Object... args) {
        String finalMsg = String.format(msg, args);
        msgFormat(finalMsg, LoggerLevel.INFO);
    }

    public void error(String msg) {
        msgFormat(msg, LoggerLevel.ERROR);
    }

    public void error(String msg, Object... args) {
        String finalMsg = String.format(msg, args);
        msgFormat(finalMsg, LoggerLevel.ERROR);
    }

    private void msgFormat(String msg, LoggerLevel loggerLevel) {
        System.out.printf("%s AT %s - [%s] - %s%n", loggerLevel.name(), Thread.currentThread(), currentDateTime(), msg);
    }

    private String currentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSSSSS"));
    }

}
