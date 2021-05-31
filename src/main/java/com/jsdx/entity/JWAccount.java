package com.jsdx.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chaohui on 2020/9/22
 */
//教务系统登录用的账号密码
@Data
public class JWAccount {
    @ApiModelProperty("教务处账号")
    String id;
    @ApiModelProperty("教务处密码")
    String password;
}
