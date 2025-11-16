package com.microservice.microservice_task.model;

public enum TaskPriority {
    LOW("Baja", 1),
    MEDIUM("Media", 2),
    HIGH("Alta", 3),
    URGENT("Urgente", 4);

    private final String displayName;
    private final int level;

    TaskPriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }
}