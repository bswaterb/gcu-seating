package com.jsdx.service;

import com.jsdx.mapper.AccountMapper;
import com.jsdx.mapper.NormalMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by chaohui on 2020/10/11
 */
@Service
public class NormalService {
    @Autowired
    NormalMapper normalMapper;
    @Autowired
    AccountMapper accountMapper;

    //通过classId查询教室号
    public String checkClassroom(String classId){
        //通过classId查找classroomId
        String classroomId = normalMapper.findClassroomIdByClassId(classId);

        if(classroomId==null){
            return null;
        }else{
            return classroomId;
        }
    }

    //查询是否有teacherId的绑定记录
    public boolean checkTeacherId(String teacherId){
        if(normalMapper.checkTeacherId(teacherId)==1){
            return true;
        }else{
            return false;
        }
    }

    //通过openId获得教师姓名
    public String findTeacherNameById(String openId){
        return normalMapper.findTeacherNameByOpenId(openId);
    }

    //查找账号权限
    public int authVerify(String openId){
        Map resultMap = accountMapper.getStudentAccountInfo(openId);
        if(resultMap==null){
            resultMap = accountMapper.getTeacherAccountInfo(openId);
            if(resultMap==null){
                //账号不存在
                return 0;
            }else {
                //教师账号
                return 2;
            }
        }else{
            //学生身份
            return 1;
        }
    }

    //通过classId获得className
    public String findClassNameById(String classId){
        return normalMapper.findClassNameById(classId);
    }

    //通过openId获得teacherId
    public String findTeacherIdByOpenId(String openId){
        return normalMapper.findTeacherIdByOpenId(openId);
    }

    //还原历史课堂记录
    public List<Map> getHistoryClassInfo(String classId){
        return normalMapper.getHistoryClassInfo(classId);
    }
}
