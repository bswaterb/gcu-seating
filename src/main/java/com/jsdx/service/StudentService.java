package com.jsdx.service;

import com.jsdx.entity.Classroom;
import com.jsdx.mapper.AccountMapper;
import com.jsdx.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by chaohui on 2020/10/2
 */
@Service
public class StudentService {
    @Autowired
    StudentMapper studentMapper;
    @Autowired
    AccountMapper accountMapper;


    public String checkClassroom(String classId,String classPassword,long nowTime){
        String classroomId;
        if(classPassword==null){
            //检查课堂是否存在
            if(studentMapper.isClassroomExist(classId)==false){
                return null;
            }
            //检查课堂是否存在对应的教室号（要求已到课堂开始时间）
            classroomId = studentMapper.checkClassroomWithoutPassword(classId,nowTime);
        }else{
            classroomId = studentMapper.checkClassroom(classId,classPassword,nowTime);
        }

        if(classroomId==null){
            //检查课堂是否是未开始状态
            if(studentMapper.isClassroomStarted(classId,nowTime)){
                return "NotStarted";
            }

            return null;
        }else{
            return classroomId;
        }
    }

    //寻找当前是否有正在进行的签到记录
    public int isClassroomCheckInNow(String classId){
        return studentMapper.isClassroomCheckInNow(classId);

    }

    //根据classId获取课堂基本信息
    public Classroom searchClassById(String classId){
        return studentMapper.searchClassById(classId);
    }

    public List<Map> searchJoinedClass(String wxId){
        //获取wxId对应的stuId
        String stuId = accountMapper.findStuIdByWxId(wxId);
        List<Map> list = studentMapper.searchJoinedClass(stuId);
        return list;
    }
}
