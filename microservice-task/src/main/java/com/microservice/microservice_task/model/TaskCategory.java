package com.microservice.microservice_task.model;

public enum TaskCategory {
    WORK("Trabajo"),
    PERSONAL("Personal"),
    FAMILY("Familia"),
    HEALTH("Salud"),
    EDUCATION("Educación"),
    SHOPPING("Compras"),
    TRAVEL("Viajes"),
    FINANCE("Finanzas"),
    HOME("Hogar"),
    OTHER("Otro");

    private final String displayName;

    TaskCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}