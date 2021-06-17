package com.example.helplah.models;

public class Operation<T> {

    public static final int ADDED = 0;
    public static final int MODIFIED = 1;
    public static final int REMOVED = 2;

    T object;
    int type;

    public Operation(T t, int type) {
        this.object = t;
        this.type = type;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
