package com.jsdx.mapper;

import com.jsdx.entity.AccountInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * Created by chaohui on 2020/10/4
 */
@Mapper
public interface AccountMapper {
    Map getStudentAccountInfo(String wxId);
    Map getTeacherAccountInfo(String wxId);
    String findStuIdByWxId(String wxId);
}
