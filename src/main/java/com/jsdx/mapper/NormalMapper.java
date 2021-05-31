package com.jsdx.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by chaohui on 2020/10/11
 */
@Mapper
public interface NormalMapper {
    //返回classId对应的classroomId
    String findClassroomIdByClassId(String classId);
    //查询teacherId是否存在用户表中
    int checkTeacherId(String teacherId);

    //根据teacherId查询teacherName
    String findTeacherNameByOpenId(String openId);

    String findClassNameById(String classId);

    String findTeacherIdByOpenId(String openId);

    List<Map> getHistoryClassInfo(String classId);
}
