package com.jsdx.mapper;

import com.jsdx.entity.Classroom;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by chaohui on 2020/10/2
 */
@Mapper
public interface StudentMapper {
    //校验加入课室是否正确并返回课室号
    String checkClassroom(String classId,String classPassword,long nowTime);

    String checkClassroomWithoutPassword(String classId,long nowTime);

    //查看课堂是否存在
    boolean isClassroomExist(String classId);
    //查看课堂是否已经开始
    boolean isClassroomStarted(String classId,long nowTime);

    //课室当前时段是否正在发起签到
    int isClassroomCheckInNow(String classId);

    Classroom searchClassById(String classId);

    List<Map> searchJoinedClass(String stuId);

}
