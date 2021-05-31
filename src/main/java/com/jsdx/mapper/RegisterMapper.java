package com.jsdx.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * Created by chaohui on 2020/10/4
 */
@Mapper
public interface RegisterMapper {
    //添加学生账户
    int addStudentAccount(String wxId,String stuName,String stuId);
    //判断该wxId是否已存在用户表中
    int searchAccount(String wxId);

    int addTeacherAccount(String wxId,String teacherName,String teacherId);

    int searchTeacherAccount(String openId);
}
