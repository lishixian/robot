package com.example.robot001.data;

public class Active {

    private final String name;
    private final Class<?> actionClass;

    public Active(String name, Class<?> actionClass) {
        this.name = name;
        this.actionClass = actionClass;
    }

    public String getName() {
        return name;
    }

    public Class<?> getActionClass() {
        return actionClass;
    }
}

