package com.jsdx.controller;

import com.jsdx.entity.JWAccount;
import com.jsdx.exception.BizException;
import com.jsdx.mapper.TestMapper;
import com.jsdx.result.Result;
import com.jsdx.util.CheckAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by chaohui on 2020/10/4
 */
@RestController
@RequestMapping("/test")
@Api(tags = "测试性接口，不要随便调用")
public class TestController {
    @Autowired
    TestMapper testMapper;

    @GetMapping("/error")
    public Result errorTest(){
        int i = 0;
        if(i==0){
            throw new BizException(405,"资源请求错误！");
        }
        return new Result(200,"success",null);
    }


    @GetMapping("/login")
    @ApiOperation("校验教务处账号密码是否正确")
    public Result login(@RequestBody JWAccount account){
        Result<String> retResult = null;
        String stuName = CheckAccount.findStuInfoByAccount(account.getId(),account.getPassword()).getStuName();
        if (stuName != null) {
            retResult = new Result<>(200,"success",stuName);
        }else{
            retResult = new Result<>(400,"failed",null);
        }
        return retResult;
    }

    @GetMapping("/testToken")
    public Result testToken(@RequestHeader("Authorization") String token){
        return Result.success(token);
    }

}
