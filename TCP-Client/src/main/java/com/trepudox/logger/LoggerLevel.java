package com.trepudox.logger;

public enum LoggerLevel {

    INFO("Info"),
    ERROR("Error"),
    MESSAGE("Message");

    private String label;

    private LoggerLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

}
