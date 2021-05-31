package com.jsdx.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Payload;
import com.jsdx.annotation.CheckAuth;
import com.jsdx.entity.AccountInfo;
import com.jsdx.entity.Classroom;
import com.jsdx.entity.StudentSeating;
import com.jsdx.entity.TokenPayload;
import com.jsdx.exception.CommonEnum;
import com.jsdx.result.Result;
import com.jsdx.service.AccountService;
import com.jsdx.service.NormalService;
import com.jsdx.service.RedisService;
import com.jsdx.service.StudentService;
import com.jsdx.util.JWTUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chaohui on 2020/10/2
 */
@RestController
@RequestMapping("/normal")
@Api(tags="通用接口")
public class  NormalController {
    @Autowired
    RedisService redisService;
    @Autowired
    AccountService accountService;
    @Autowired
    NormalService normalService;
    @Autowired
    StudentService studentService;

    @CheckAuth(auth = "student")
    @GetMapping("/getClassInfo/{classId}")
    @ApiOperation("获取指定教室的当前成员列表")
    public Result getClassInfo(@ApiParam("课室口令号") @PathVariable("classId") String classId){
        List<StudentSeating> list = redisService.getClassInfo(classId);
        Result retResult;
        if(list!=null){
            return Result.success(list);
        }
        return Result.error(CommonEnum.CLASS_NOT_EXIST);
    }

    @CheckAuth(auth = "student")
    @GetMapping("/getAccountInfo/{open_id}")
    @ApiOperation("根据传入的open_id获取用户信息")
    public Result getStuAccountInfo(@ApiParam("用户open_id") @PathVariable("open_id")String wxId){
        AccountInfo account = accountService.getAccountInfo(wxId);
        if(account!=null){
            Map<String,Object> result = new HashMap<>();
            //openId
            result.put("wx_id",wxId);
            //姓名
            result.put("name",account.getName());
            //学号或工号
            result.put("id",account.getId());
            //权限身份
            result.put("auth",account.getAuth());
            return Result.success(result);
        }
        return Result.error(CommonEnum.ACCOUNT_NOT_EXIST);
    }

    @CheckAuth(auth = "student")
    @GetMapping("/searchClass/{classId}")
    @ApiOperation("根据课堂口令获取基本信息")
    public Result searchClass(@PathVariable("classId") String classId, @RequestAttribute("payload")TokenPayload payload){

        Classroom classroom = studentService.searchClassById(classId);
        if(classroom==null){
            return Result.error(CommonEnum.CLASS_NOT_EXIST);
        }else{
            Map map = new HashMap();
            map.put("classId",classId);
            map.put("className",classroom.getClassName());
            map.put("startTime",classroom.getStartTime());
            map.put("endTime",classroom.getEndTime());
            map.put("classroomId",classroom.getClassroomId());
            map.put("teacherName",normalService.findTeacherNameById(classroom.getTeacherOpenId()));
            map.put("classroomSize",classroom.getClassroomSize());
            map.put("hasPassword",classroom.getClassPassword()==null?false:true);
            //获取账户权限
            int auth = Integer.parseInt(payload.getAuth());
            //如果是教师账户，再传入密码信息
            if(auth==2){
                map.put("classPassword",classroom.getClassPassword());
            }
            return Result.success(map);
        }
    }




}
