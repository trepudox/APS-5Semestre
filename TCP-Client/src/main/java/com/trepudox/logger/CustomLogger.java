package com.trepudox.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLogger {


    private CustomLogger() {}

    public static CustomLogger getLogger() {
        return new CustomLogger();
    }

    public void info(String msg) {
        System.out.printf("INFO [%s] - %s%n", currentDateTime(), msg);
    }

    public void info(String msg, Object... args) {
        String finalMsg = String.format(msg, args);
        System.out.printf("INFO AT %s - [%s] - %s%n", Thread.currentThread(), currentDateTime(), finalMsg);
    }

    private String currentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSSSSS"));
    }

}
