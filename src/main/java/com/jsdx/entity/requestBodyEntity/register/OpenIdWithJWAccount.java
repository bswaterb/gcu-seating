package com.jsdx.entity.requestBodyEntity.register;

import com.jsdx.entity.JWAccount;
import lombok.Data;

/**
 * Created by chaohui on 2020/11/9
 */
@Data
public class OpenIdWithJWAccount {
    private String openId;
    private JWAccount jwAccount;

}
