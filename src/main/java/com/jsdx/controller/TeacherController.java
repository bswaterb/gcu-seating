package com.jsdx.controller;

import com.auth0.jwt.interfaces.Payload;
import com.jsdx.annotation.CheckAuth;
import com.jsdx.entity.Classroom;
import com.jsdx.entity.StudentSeating;
import com.jsdx.entity.TokenPayload;
import com.jsdx.entity.requestBodyEntity.student.CWPWithStudentSeating;
import com.jsdx.entity.requestBodyEntity.teacher.CheckIn;
import com.jsdx.entity.requestBodyEntity.teacher.ClassIdWithStuId;
import com.jsdx.entity.requestBodyEntity.teacher.StudentPoint;
import com.jsdx.entity.requestBodyEntity.teacher.TeacherIdWithClassroom;
import com.jsdx.exception.CommonEnum;
import com.jsdx.globaldata.AuthEnum;
import com.jsdx.result.Result;
import com.jsdx.service.NormalService;
import com.jsdx.service.RedisService;
import com.jsdx.service.StudentService;
import com.jsdx.service.TeacherService;
import com.jsdx.util.JWTUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by chaohui on 2020/9/22
 */

@RestController
@RequestMapping("/teacher")
@Api(tags = "教师相关功能接口")
public class TeacherController {
    @Autowired
    TeacherService teacherService;
    @Autowired
    RedisService redisService;
    @Autowired
    NormalService normalService;
    @Autowired
    StudentService studentService;

    @CheckAuth(auth = "teacher")
    @PostMapping("/createAClass")
    @ApiOperation("创建教室")
    public Result createClass(@ApiParam("传入课堂创建信息") @RequestBody Classroom classroom,@RequestAttribute("payload")TokenPayload payload){

        //从token内获取教师信息
        String teacherOpenId = payload.getWxId();
        classroom.setTeacherOpenId(teacherOpenId);

        int result = teacherService.createClass(classroom);
        if(result==1){
            //向redis中存入课堂信息
            redisService.createAClass(classroom.getClassId());
            return Result.success(classroom);

        }else{
            return Result.error(CommonEnum.CLASS_CREATE_FAILED);
        }

    }

    @CheckAuth(auth = "teacher")
    @PostMapping("/markPoint")
    @ApiOperation("标记学生")
    public Result markPoint(@RequestBody StudentPoint studentPoint){

        String classId = studentPoint.getClassId();
        String stuId = studentPoint.getStuId();
        int status = studentPoint.getStatus();

        //判断课堂组是否存在Redis缓存中
        if(!redisService.isKeyInRedis(classId+"-group")){
            return Result.error(CommonEnum.CLASS_NOT_EXIST);
        }
        //判断 课堂号-学号 key 是否存在缓存中
        if(!redisService.isKeyInRedis(classId+"-"+stuId)){
            return Result.error(CommonEnum.STU_NOT_EXIST);
        }

        int retResult = redisService.markStudent(status,classId,stuId);
        if(retResult==1){
            return Result.success();
        }else{
            return Result.error(CommonEnum.FAILED);
        }
    }

    @CheckAuth(auth = "teacher")
    @GetMapping("/history/getCreatedClass")
    @ApiOperation("获取历史课堂创建记录")
    public Result getCreatedClass(@RequestAttribute("payload") TokenPayload payload) {
        String openId = payload.getWxId();
        //获取该教师的课堂创建记录
        List<Map> list = teacherService.getCreatedClass(openId);
        if (list != null) {
            return Result.success(list);
        } else {
            return Result.error(CommonEnum.TEA_NOT_CREATE_CLASS);
        }
    }

    @CheckAuth(auth = "teacher")
    @GetMapping("/history/getHistoryClassInfo/{classId}")
    @ApiOperation("还原指定课室的就座记录")
    public Result getHistoryClassInfo(@PathVariable("classId") String classId){
        List<Map> list = normalService.getHistoryClassInfo(classId);
        if(list!=null){
            return Result.success(list);
        }else{
            return Result.error(CommonEnum.CLASS_NOT_EXIST);
        }
    }

    @CheckAuth(auth = "teacher")
    @PostMapping("/assistJoinClass")
    @ApiOperation("教师辅助学生加入课堂")
    public Result assistJoinClass(@RequestBody CWPWithStudentSeating entity){

        //获取课堂id和课堂密码
        String classId = entity.getClassIdWithClassPassword().getClassId();
        String classPassword = entity.getClassIdWithClassPassword().getClassPassword()==null?null:entity.getClassIdWithClassPassword().getClassPassword();
        //获取学生入座信息
        StudentSeating student = entity.getStudentSeating();
        //判断课堂ID和密码是否正确以及当前加入时间是否合法
        long nowTime = new Date().getTime()/1000;
        String classroomId = studentService.checkClassroom(classId,classPassword,nowTime);

        //如果课堂存在，则向Redis对应记录中加入信息
        if(classroomId!=null){
            student.setGood(false);
            student.setBad(false);
            student.setPerformanceScore(0);
            int result = redisService.joinAClass(classId,student);
            if(result==1){
                return Result.success();
            }else if(result==-1){
                return Result.error(CommonEnum.CLASS_NOT_EXIST);
            }else if(result==0){
                return Result.error(CommonEnum.SEAT_NOT_EMPTY);
            }else{
                return Result.error(CommonEnum.FAILED);
            }
        }
        return Result.error(CommonEnum.CLASS_NOT_EXIST);

    }

    @CheckAuth(auth = "teacher")
    @PostMapping("/createACheckIn")
    @ApiOperation("发起签到")
    public Result createCheckIn(@ApiParam("传入签到信息") @RequestBody CheckIn entity){
        String classId = entity.getClassId();
        String teacherId = entity.getTeacherId();
        long endTime = entity.getEndTime();

        Result retResult = null;
        //如果教师ID不存在，则不允许向下执行
        if(normalService.checkTeacherId(teacherId)==false){
            retResult = new Result<>(400,"teacherWxId invalid",null);
            return retResult;
        }
        //查询是否有冲突的签到记录
        if(teacherService.isCheckInExists(classId,new Date().getTime())==true){
            retResult = new Result<>(400,"check-in already exists",null);
            return retResult;
        }
        int result = teacherService.createCheckIn(classId,new Date().getTime(),endTime,teacherId);
        if(result==0){
            retResult = new Result<>(400,"create check-in failed",null);
            return retResult;
        }
        retResult = new Result<>(200,"create check-in success",null);
        return retResult;
    }
}
