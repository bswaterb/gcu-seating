package com.jsdx.service;

import com.jsdx.exception.GlobalExceptionHandler;
import com.jsdx.mapper.AccountMapper;
import com.jsdx.entity.AccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by chaohui on 2020/10/4
 */
@Service
public class AccountService {
    @Autowired
    AccountMapper accountMapper;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    //通过wxId获取用户绑定信息
    public AccountInfo getAccountInfo(String wxId){
        AccountInfo account = new AccountInfo();
        Map resultMap = null;
        //先在学生信息表中查找账号信息
        resultMap = accountMapper.getStudentAccountInfo(wxId);
        if(resultMap==null){
            //学生表中无信息则到教师表中查找账号信息
            resultMap = accountMapper.getTeacherAccountInfo(wxId);
            if(resultMap==null){
                return null;
            }else{
                account.setId((String) resultMap.get("teacher_id"));
                account.setName((String) resultMap.get("teacher_name"));
                account.setAuth(2);
            }
        }else{
            account.setId((String) resultMap.get("stu_id"));
            account.setName((String) resultMap.get("stu_name"));
            account.setAuth(1);
        }
        return account;
    }
}
