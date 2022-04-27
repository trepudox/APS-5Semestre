package com.trepudox.logger;

public enum LoggerLevel {

    INFO("Info"),
    ERROR("Error");

    private String label;

    private LoggerLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

}
