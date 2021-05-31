package com.jsdx.mapper;

import com.jsdx.entity.Classroom;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by chaohui on 2020/10/1
 */
@Mapper
public interface TeacherMapper {


    int createClass(Classroom classroom);

    int createCheckIn(String classId,long checkInTimeStart,long checkInTimeEnd,String teacherWxId);

    //课室当前时段是否正在发起签到
    int isCheckInExists(String classId,long nowTime);

    List<Map> getCreatedClass(String teacherOpenId);

}
