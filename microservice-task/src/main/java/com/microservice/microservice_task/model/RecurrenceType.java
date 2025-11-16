package com.microservice.microservice_task.model;

public enum RecurrenceType {
    DAILY("Diario"),
    WEEKLY("Semanal"),
    MONTHLY("Mensual"),
    YEARLY("Anual");

    private final String displayName;

    RecurrenceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}