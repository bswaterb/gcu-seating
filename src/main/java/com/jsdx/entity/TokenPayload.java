package com.jsdx.entity;

import lombok.Data;

/**
 * Created by chaohui on 2021/1/30
 */
@Data
public class TokenPayload {
    private String wxId;
    private String id;
    private String name;
    private String auth;
}
