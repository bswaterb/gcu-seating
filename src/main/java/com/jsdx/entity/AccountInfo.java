package com.jsdx.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chaohui on 2020/11/5
 */
@Data
public class AccountInfo {
    @ApiModelProperty("账户")
    private String name;
    private String id;
    //1-学生 2-教师 3-管理员（暂定）
    private int auth;
}
