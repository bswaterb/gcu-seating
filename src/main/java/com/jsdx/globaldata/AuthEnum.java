package com.jsdx.globaldata;

import io.swagger.models.auth.In;

/**
 * Created by chaohui on 2021/1/9
 */
public enum AuthEnum {
    AUTH_TEACHER(2),
    AUTH_STUDENT(1),
    AUTH_ADMIN(3);

    private int value;

    private AuthEnum(int value) {
        this.value = value;
    }


    public int getValue() {
        return value;
    }
}
