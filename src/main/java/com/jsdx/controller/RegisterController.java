package com.jsdx.controller;

import com.jsdx.entity.StudentInfo;
import com.jsdx.entity.TeacherInfo;
import com.jsdx.entity.requestBodyEntity.register.OpenIdWithJWAccount;
import com.jsdx.entity.requestBodyEntity.register.OpenIdWithStuInfo;
import com.jsdx.exception.CommonEnum;
import com.jsdx.result.Result;
import com.jsdx.schedule.CleanRedisCache;
import com.jsdx.service.RegisterService;
import com.jsdx.util.CheckAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by chaohui on 2020/10/4
 */
@RestController
@RequestMapping("/register")
@Api(tags = "注册相关接口")
public class RegisterController {
    @Autowired
    RegisterService registerService;

//    @PostMapping("/stuAccount")
//    @ApiOperation("注册学生账户")
//    public Result<String> registerStudentAccount(@ApiParam("传入openId以及教务处账号密码") @RequestBody OpenIdWithJWAccount account){
//        String openId = account.getOpenId();
//        String id = account.getJwAccount().getId();
//        String password = account.getJwAccount().getPassword();
//        if(openId==null||id==null||password==null){
//            return Result.error(CommonEnum.BODY_NOT_MATCH);
//        }
//        //判断该账号在数据库中是否已存在
//        if(registerService.isAccountExist(account.getOpenId())==true){
//            //如果账号已存在，则拒绝继续注册
//            return Result.error(CommonEnum.ACCOUNT_ALREADY_EXIST);
//        }
//        //判断教务处账号密码是否正确
//        //RPC调用远程接口进行模拟登录获取信息
//        //应项目经理要求，暂时关闭账号验证功能 21.3.8
//        StudentInfo stuInfo = CheckAccount.findStuInfoByAccount_7GuGu(account.getJwAccount().getId(), account.getJwAccount().getPassword());
//
//        if (stuInfo != null) {
//            System.out.println(stuInfo.getStuName());
//            if (stuInfo.getStuName().equals("教务系统外网异常")) {
//                return Result.error(CommonEnum.JW_TIMEOUT);
//            } else if (stuInfo.getStuName().contains("已锁定无法登录，次日自动解锁！")) {
//                return Result.error(CommonEnum.JW_ACCOUNT_LOCKED);
//            } else if (stuInfo.getStuName().contains("密码错误")) {
//                return Result.error(CommonEnum.JW_PASSWORD_ERROR);
//            } else {
//                //向数据库中添加该学生账号记录，绑定微信open_id与学生姓名、学生学号
//                registerService.addStudentAccount(account.getOpenId(), stuInfo.getStuName(), stuInfo.getStuId());
//                return Result.success(stuInfo);
//            }
//        } else {
//            return Result.error(CommonEnum.UNKNOWN_ERROR);
//        }
//    }

    @PostMapping("/stuAccount")
    @ApiOperation("注册学生账户")
    public Result<String> registerStudentAccount(@ApiParam("传入openId、学生姓名以及学生学号") @RequestBody OpenIdWithStuInfo account){
        String openId = account.getOpenId();
        String id = account.getStuId();
        String stuName = account.getStuName();
        //判断该账号在数据库中是否已存在
        if(registerService.isAccountExist(openId)==true){
            //如果账号已存在，则拒绝继续注册
            return Result.error(CommonEnum.ACCOUNT_ALREADY_EXIST);
        }
        //判断教务处账号密码是否正确
        //RPC调用远程接口进行模拟登录获取信息
        //应项目经理要求，暂时关闭账号验证功能 21.3.8，老方法在上述被注释掉的代码中
        //StudentInfo stuInfo = CheckAccount.findStuInfoByAccount_7GuGu(account.getJwAccount().getId(), account.getJwAccount().getPassword());


        //向数据库中添加该学生账号记录，绑定微信open_id与学生姓名、学生学号
        registerService.addStudentAccount(openId, stuName, id);

        return Result.success();

    }

    @PostMapping("/teacherAccount")
    @ApiOperation("注册教师账户")
    public Result registerTeacherAccount(@ApiParam("传入openId以及教务处账号密码") @RequestBody OpenIdWithJWAccount account){
        String openId = account.getOpenId();
        String id = account.getJwAccount().getId();
        String password = account.getJwAccount().getPassword();
        if(openId==null||id==null||password==null){
            return Result.error(CommonEnum.BODY_NOT_MATCH);
        }else{
            //如果账户已注册
            if(registerService.isTeacherAccountExist(openId)==true){
                return Result.error(CommonEnum.ACCOUNT_ALREADY_EXIST);
            }
            //RPC调用远程接口进行模拟登录获取信息
            TeacherInfo teacherInfo = CheckAccount.findTeacherInfoByAccount(id,password);

            if(teacherInfo.getTeacherName().equals("教务系统外网异常")){
                return Result.error(CommonEnum.JW_TIMEOUT);
            }else if(teacherInfo.getTeacherName().contains("已锁定无法登录，次日自动解锁！")){
                return Result.error(CommonEnum.JW_ACCOUNT_LOCKED);
            }else if(teacherInfo.getTeacherName().contains("密码错误")){
                return Result.error(CommonEnum.JW_PASSWORD_ERROR);
            }else if(teacherInfo.getTeacherName().contains("用户名不存在")){
                return Result.error(CommonEnum.JW_ACCOUNT_NOT_EXIST);
            }
            else{
                //向数据库中添加该学生账号记录，绑定微信open_id与学生姓名、学生学号
                registerService.addTeacherAccount(account.getOpenId(),teacherInfo.getTeacherName(),teacherInfo.getTeacherId());
                return Result.success(teacherInfo);
            }
        }
    }
}
