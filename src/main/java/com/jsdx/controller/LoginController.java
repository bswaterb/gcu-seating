package com.jsdx.controller;

import com.jsdx.entity.AccountInfo;
import com.jsdx.entity.LoginInfo;
import com.jsdx.exception.CommonEnum;
import com.jsdx.result.Result;
import com.jsdx.service.AccountService;
import com.jsdx.util.JWTUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chaohui on 2021/1/8
 */
@RestController
@Api(tags = "登录相关接口")
public class LoginController {
    @Autowired
    AccountService accountService;
    @PostMapping("/login")
    public Result login(@RequestBody LoginInfo login){
        //获取用户的微信ID
        String wxId = login.getWxId();
        //检索用户表，返回该用户的绑定信息
        AccountInfo account = accountService.getAccountInfo(wxId);
        //如果用户不存在
        if(account==null){
            return Result.error(CommonEnum.NOT_BIND);
        }
        //生成用户当前的token
        Map payloadMap = new HashMap();
        payloadMap.put("wxId",wxId);
        payloadMap.put("id",account.getId());
        payloadMap.put("name",account.getName());
        payloadMap.put("auth",String.valueOf(account.getAuth()));
        String token = "Bearer "+JWTUtils.getToken(payloadMap);
        //返回登录成功的标识
        Map resultMap = new HashMap();
        resultMap.put("name",account.getName());
        resultMap.put("id",account.getId());
        resultMap.put("auth",account.getAuth());
        resultMap.put("token",token);
        //将token保存到Redis内

        return Result.success(resultMap);
    }


}
