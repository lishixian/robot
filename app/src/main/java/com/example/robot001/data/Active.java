package com.example.robot001.data;

public class Active {

    private String name;
    private Class action;

    public Active(String name, Class action) {
        this.name = name;
        this.action = action;

    }

    public String getName() {
        return name;
    }

    public Class getAction() {
        return action;
    }
}

