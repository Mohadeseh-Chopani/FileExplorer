package com.example.fileexplorer;

public enum ViewType {
    ROW(0),GRID(1);

    int value;

    ViewType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
