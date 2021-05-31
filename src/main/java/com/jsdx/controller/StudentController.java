package com.jsdx.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.jsdx.annotation.CheckAuth;
import com.jsdx.entity.*;
import com.jsdx.entity.requestBodyEntity.student.CWPWithStudentWxId;
import com.jsdx.entity.requestBodyEntity.student.CWPWithStudentSeating;
import com.jsdx.entity.requestBodyEntity.student.ClassIdWithClassPassword;
import com.jsdx.entity.requestBodyEntity.student.ClassIdWithStudentId;
import com.jsdx.entity.requestBodyEntity.teacher.ClassIdWithStuId;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by chaohui on 2020/9/22
 */
@RestController
@RequestMapping("/student")
@Api(tags = "学生相关功能接口")
public class StudentController {
    @Autowired
    StudentService studentService;
    @Autowired
    RedisService redisService;
    @Autowired
    NormalService normalService;
    @Autowired
    AccountService accountService;

    @CheckAuth(auth = "student")
    @PostMapping("/joinAClass")
    @ApiOperation("学生加入已创建的课堂")
    public Result joinClass(@ApiParam("传入课室口令以及课室密码") @RequestBody ClassIdWithClassPassword entity, @RequestAttribute("payload")TokenPayload payload){
        String classId = entity.getClassId();
        String classPassword = entity.getClassPassword();

        Result<StudentSeating> retResult;

        //判断课堂ID和密码是否正确以及当前加入时间是否合法
        long nowTime = new Date().getTime()/1000;
        String classroomId = studentService.checkClassroom(classId,classPassword,nowTime);
        if(classroomId.equals("NotStart")){
            return Result.error(CommonEnum.CLASS_NOT_STARTED);
        }
        if(classroomId!=null){
            //如果课堂存在，则向Redis对应记录中加入信息
            StudentSeating studentSeating = new StudentSeating();
            //初始化对象信息
            studentSeating.setStuId(payload.getId());
            studentSeating.setStuName(payload.getName());
            studentSeating.setJoinTime(nowTime);
            studentSeating.setPerformanceScore(0);
            studentSeating.setBad(false);
            studentSeating.setGood(false);
            studentSeating.setSeat(false);
            int result = redisService.joinAClass(classId,studentSeating);
            if(result==1){
                return Result.success();
            } else if(result==-1){
                return Result.error(CommonEnum.CLASS_NOT_EXIST);
            }else if(result==0){
                return Result.error(CommonEnum.SEAT_NOT_EMPTY);
            }else{
                return Result.error(CommonEnum.FAILED);
            }
        }
        return Result.error(CommonEnum.CLASS_NOT_EXIST);
    }

    @CheckAuth(auth = "student")
    @PutMapping("/joinAClass/chooseASeat")
    @ApiOperation("学生选座")
    public Result chooseASeat(@ApiParam("传入课室口令、课室密码、学生选座信息") @RequestBody CWPWithStudentSeating cwpWithStudentSeating,@RequestAttribute("payload")TokenPayload payload){
        String classId = cwpWithStudentSeating.getClassIdWithClassPassword().getClassId();
        String classPassword = cwpWithStudentSeating.getClassIdWithClassPassword().getClassPassword();
        StudentSeating student = cwpWithStudentSeating.getStudentSeating();

        //判断课堂ID和密码是否正确以及当前加入时间是否合法
        long nowTime = new Date().getTime()/1000;
        String classroomId = studentService.checkClassroom(classId,classPassword,nowTime);

        //如果课堂存在，则向Redis对应记录中加入信息
        if(classroomId!=null){
            student.setGood(false);
            student.setBad(false);
            student.setPerformanceScore(0);
            student.setStuName(payload.getName());
            student.setStuId(payload.getId());
            int result = redisService.joinAClass(classId,student);
            if(result==1){
                return Result.success();
            }else if(result==-1){
                return Result.error(CommonEnum.CLASS_NOT_EXIST);
            }else if(result==-2){
                return Result.error(CommonEnum.ALREADY_SEATED);
            } else if(result==0){
                return Result.error(CommonEnum.SEAT_NOT_EMPTY);
            }else{
                return Result.error(CommonEnum.FAILED);
            }
        }
        return Result.error(CommonEnum.CLASS_NOT_EXIST);
    }


    @CheckAuth(auth = "student")
    @GetMapping("/history/searchJoinedClass/{wxId}")
    @ApiOperation("查看历史加入课堂")
    public Result searchJoinedClass(@PathVariable("wxId") String wxId){
        List<Map> list = studentService.searchJoinedClass(wxId);
        if(!list.isEmpty()){
            return Result.success(list);
        }
        return Result.error(CommonEnum.NO_DATA_FIND);
    }


    @CheckAuth(auth = "student")
    @GetMapping("/checkIn/searchCheckIn/{classId}")
    @ApiOperation("寻找classId的课堂当前是否有正在进行的签到记录")
    public Result searchCheckIn(@PathVariable("classId") String classId){

        Result retResult;
        int result = studentService.isClassroomCheckInNow(classId);
        //如果数据库表当前有查询记录
        if(result==1){
            retResult = new Result(200,"have check-in now",true);
        }else{
            retResult = new Result(400,"don't have check-in now",false);
        }

        return retResult;
    }

    @CheckAuth(auth = "student")
    @GetMapping("/checkIn/amICheckIn/{classId}/{stuId}")
    @ApiOperation("查看stuId对应的学生在classId对应的课堂中是否完成签到")
    public Result amICheckIn(@PathVariable("classId")String classId,@PathVariable("stuId")String stuId){
        Result retResult;
        int existResult = studentService.isClassroomCheckInNow(classId);
        if(existResult==1){
            //学生已完成签到
            if(redisService.isStudentCheckIn(classId, stuId)==true){
                retResult = new Result(200,"You have checked-in",true);
            }else{
                retResult = new Result(400,"You haven't checked-in",false);
            }
        }else{
            retResult = new Result(401,"The class isn't check-in now",false);
        }
        return retResult;
    }


    @CheckAuth(auth = "student")
    @PostMapping("/checkIn/completeCheckIn")
    @ApiOperation("学生完成签到")
    public Result completeCheckIn(@ApiParam("传入课室口令、学生学号") @RequestBody ClassIdWithStudentId entity){
        String stuId = entity.getStudentId();
        String classId = entity.getClassId();

        Result retResult = null;
        long nowTime = new Date().getTime();
        int result = studentService.isClassroomCheckInNow(classId);
        //classId对应的教室存在
        if (result == 1) {
            //如果学生已签到
            if(redisService.isStudentCheckIn(classId, stuId)==true){
                retResult = new Result(400,"You have checked-in",false);
            }else {
                StudentCheckIn studentCheckIn = new StudentCheckIn();
                studentCheckIn.setCheckInTime(nowTime);
                studentCheckIn.setStuId(stuId);
                redisService.completeCheckIn(classId, studentCheckIn);
                retResult = new Result(200, "check-in success", stuId);
            }
        }else{
            //classId对应的教室不存在
            retResult = new Result(400,"The class isn't check-in now",false);
        }

        return retResult;
    }

//    @GetMapping("/searchClass/{classId}")
//    @ApiOperation("根据课堂口令获取基本信息")
//    public Result searchClass(@PathVariable("classId") String classId){
//        Result retResult;
//        Classroom classroom = studentService.searchClassById(classId);
//        if(classroom==null){
//            retResult = new Result(400,"classroom doesn't exist",null);
//        }else{
//            Map map = new HashMap();
//            map.put("classId",classId);
//            map.put("className",classroom.getClassName());
//            map.put("startTime",classroom.getStartTime());
//            map.put("endTime",classroom.getEndTime());
//            map.put("classroomId",classroom.getClassroomId());
//            map.put("teacherName",normalService.findTeacherNameById(classroom.getTeacherId()));
//            map.put("classroomSize",classroom.getClassroomSize());
//            if(classroom!=null){
//                retResult = new Result(200,"success",map);
//            }else{
//                retResult = new Result(400,"failed",null);
//            }
//        }
//        return retResult;
//    }

}
