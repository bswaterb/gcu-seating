package com.jsdx.service;

import com.jsdx.mapper.NormalMapper;
import com.jsdx.mapper.TeacherMapper;
import com.jsdx.entity.Classroom;
import com.jsdx.util.CheckClassroom;
import com.jsdx.util.RandomID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by chaohui on 2020/10/1
 */
@Service
public class TeacherService {
    @Autowired
    TeacherMapper teacherMapper;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public int createClass(Classroom classroom){
        //设置随机课堂ID
        classroom.setClassId(RandomID.createRandomID(6));

        //如果传入的实体中不包含课室大小，则自动识别该课室大小，否则作为自定义大小处理
        if(classroom.getClassroomSize()==null){
            classroom.setClassroomSize(CheckClassroom.checkSize(classroom.getClassroomId()));
        }

        logger.info("创建课室：{}，课堂名：{}，教师号：{}，教室大小：{}",classroom.getClassId(),classroom.getClassName(),classroom.getTeacherOpenId(),classroom.getClassroomSize());


        int result = teacherMapper.createClass(classroom);
        if(result==1){
            return 1;
        }else{
            return 0;
        }
    }

    //创建签到
    public int createCheckIn(String classId,long checkInTimeStart,long checkInTimeEnd,String teacherWxId){
        return teacherMapper.createCheckIn(classId,checkInTimeStart,checkInTimeEnd,teacherWxId);
    }

    //签到是否正在进行
    public boolean isCheckInExists(String classId,long nowTime){
        int result = teacherMapper.isCheckInExists(classId,nowTime);
        if(result==0){
            return false;
        }else{
            return true;
        }
    }

    public List<Map> getCreatedClass(String teacherOpenId){
        return teacherMapper.getCreatedClass(teacherOpenId);
    }
}
