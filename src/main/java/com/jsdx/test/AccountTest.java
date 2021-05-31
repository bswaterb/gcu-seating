package com.jsdx.test;

import com.jsdx.entity.StudentInfo;
import com.jsdx.entity.TeacherInfo;
import com.jsdx.util.CheckAccount;

import java.util.Date;

/**
 * Created by chaohui on 2021/1/10
 */
public class AccountTest {



    public static void main(String[] args) {
        System.out.println("###########开始"+new Date());
        for (int i=0;i<1;i++){
            new Thread(()->{
                StudentInfo studentInfo = CheckAccount.findStuInfoByAccount_7GuGu("202010098151","libiao20020302");
                System.out.println(studentInfo);
            }).start();
        }
        System.out.println("###########完毕"+new Date());
    }
}
