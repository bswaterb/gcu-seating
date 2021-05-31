package com.jsdx.service;

import com.jsdx.mapper.RegisterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by chaohui on 2020/10/4
 */
@Service
public class RegisterService {
    @Autowired
    RegisterMapper registerMapper;

    public int addStudentAccount(String wxId,String stuName,String stuId){
        return registerMapper.addStudentAccount(wxId,stuName,stuId);
    }

    public int addTeacherAccount(String wxId,String teacherName,String teacherId){
        return registerMapper.addTeacherAccount(wxId,teacherName,teacherId);
    }

   public boolean isAccountExist(String wxId){
        if(registerMapper.searchAccount(wxId)==1){
            return true;
        }else{
            return false;
        }
   }

   public boolean isTeacherAccountExist(String openId){
       if(registerMapper.searchTeacherAccount(openId)==1){
           return true;
       }else{
           return false;
       }
   }
}
