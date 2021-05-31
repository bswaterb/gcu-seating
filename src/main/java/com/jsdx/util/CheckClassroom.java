package com.jsdx.util;

import java.util.Map;

/**
 * Created by chaohui on 2020/9/23
 */
public class CheckClassroom {
    private static Map<String,String> classMap;
    public static String checkSize(String classroomId){
        if(classMap==null){
            try{
                classMap = FileIO.readClassroomInfo();
                System.out.println("第一次读取");
            }catch (Exception e){
                e.printStackTrace();
                classMap = null;
                return "程序出错！"+e.toString();
            }
        }
        return classMap.get(classroomId);
    }

    public static void main(String[] args) {
        try{
            System.out.println(checkSize("A1-102"));
            System.out.println(checkSize("A2-202"));
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
