package com.jsdx.entity.requestBodyEntity.register;

import lombok.Data;

/**
 * Created by chaohui on 2021/3/8
 */
@Data
public class OpenIdWithStuInfo {
    private String openId;
    private String stuId;
    private String stuName;
}
