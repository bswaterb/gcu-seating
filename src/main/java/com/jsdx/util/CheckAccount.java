package com.jsdx.util;

import com.jsdx.entity.StudentInfo;
import com.jsdx.entity.TeacherInfo;
import com.jsdx.service.AccountService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLConnection;

/**
 * Created by chaohui on 2020/9/22
 * RPC调用远程接口进行模拟登录获取信息
 */
public class CheckAccount {
    private static final Logger logger = LoggerFactory.getLogger(CheckAccount.class);
    /**
     * 校验输入的学生账号和密码是否正确
     * @param id  学号
     * @param password 教务系统密码
     * @return  是否正确
     * @throws Exception
     */
    public static boolean isAValidAccount(String id,String password) throws Exception{
        //构造请求链接
        String url = "http://www.nishishei.xyz/JW/login";
        String param1 = "username="+id;
        String param2 = "password="+password;
        url = url+"?"+param1+"&"+param2;

        Connection.Response login = Jsoup.connect(url)
                .ignoreContentType(true) // 忽略类型验证
                .followRedirects(false) // 禁止重定向
                .postDataCharset("utf-8")
                .method(Connection.Method.GET)
                .execute();


        login.charset("UTF-8");
        System.out.println("RequestStatusCode:");
        System.out.println(login.statusCode());
        System.out.println("RequestBody:");
        System.out.println(login.body());
        //获取结果状态码 200代表正常获取  201等均代表失败
        String resultCode = login.body().substring(8,11);

        //获取账号对应的姓名
        int nameIndex = login.body().indexOf("name");
        int ageIndex = login.body().indexOf("age");
        String stuName = login.body().substring(nameIndex+7,ageIndex-3);

        System.out.println(resultCode);


        if(!resultCode.equals("200")){
            System.out.println("ResultFailed!");
            return false;
        }else{
            System.out.println("ResultSuccess!");
            return true;
        }
    }

    /**
     * 校验输入的学生账号和密码是否正确，若正确则返回学生的姓名，不正确则返回 “ResultFailed”
     * @param id
     * @param password
     * @return
     * @throws Exception
     */
    public static StudentInfo findStuInfoByAccount(String id,String password){
        //构造请求链接
        String url = "http://www.nishishei.xyz/JW/login";
        String param1 = "username="+id;
        String param2 = "password="+password;
        url = url+"?"+param1+"&"+param2;
        System.out.println("账号校验："+url);
        Connection.Response login = null;

        try{
            login = Jsoup.connect(url)
                    .ignoreContentType(true) // 忽略类型验证
                    .followRedirects(false) // 禁止重定向
                    .postDataCharset("utf-8")
                    .method(Connection.Method.GET)
                    .execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(login==null){
            logger.warn("教务系统账号校验异常，账号{} 密码{}",id,password);
            return null;
        }else{
            System.out.println("RequestStatusCode:");
            System.out.println(login.statusCode());
            System.out.println("RequestBody:");
            System.out.println(login.body());
            //获取结果状态码 200代表正常获取  201等均代表失败
            String resultCode = login.body().substring(8,11);
            if(!resultCode.equals("200")){
                System.out.println("绑定失败，账号 "+id+" 密码 "+password);
                return null;
            }

            //获取账号对应的姓名
            int accountIndex = login.body().indexOf("account");
            int nameIndex = login.body().indexOf("name");
            int ageIndex = login.body().indexOf("age");
            String stuId = login.body().substring(accountIndex+10,nameIndex-3);
            String stuName = login.body().substring(nameIndex+7,ageIndex-3);
            System.out.println(stuId);
            System.out.println(resultCode);

            //构造学生信息对象
            StudentInfo studentInfo = new StudentInfo();
            studentInfo.setStuId(stuId);
            studentInfo.setStuName(stuName);

            return studentInfo;
        }
    }


    public static StudentInfo findStuInfoByAccount_7GuGu(String id,String password){
        //构造请求链接
        String baseURL = "https://gu-studio.cn:8090/get_info";
        String param1 = "username="+id;
        String param2 = "password="+password;
        String requestURL = baseURL+"?"+param1+"&"+param2+"&user_type=0";
        Connection.Response login = null;
        logger.info("学生账号校验：{}",requestURL);
        StudentInfo studentInfo = null;
        try{
            login = Jsoup.connect(requestURL)
                    .ignoreContentType(true) // 忽略类型验证
                    .followRedirects(false) // 禁止重定向
                    .postDataCharset("utf-8")
                    .method(Connection.Method.GET)
                    .execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(login==null){
            logger.warn("教务系统校验服务器异常，账号{} 密码{}",id,password);
            return null;
        }else{
            String requestBody = login.body();

            if(requestBody.contains("error")){
                int errorIndex = requestBody.indexOf("error");
                String errorMsg = requestBody.substring(errorIndex+8,requestBody.length()-3);
                logger.warn("账号校验服务失败，校验账号 {}，错误原因 {}",id,unicodeToChinese(errorMsg));
                errorMsg = unicodeToChinese(errorMsg);
                studentInfo = new StudentInfo();
                studentInfo.setStuName(errorMsg);
                return studentInfo;
            }

            System.out.println(login.body());
            //获取账号对应的姓名
            int nameIndex = login.body().indexOf("real_name");
            int ageIndex = login.body().indexOf("sex");
            String stuName = login.body().substring(nameIndex+12,ageIndex-3);

            //构造学生信息对象
            studentInfo = new StudentInfo();
            studentInfo.setStuId(id);
            studentInfo.setStuName(unicodeToChinese(stuName));

            return studentInfo;
        }
    }

    public static TeacherInfo findTeacherInfoByAccount(String id,String password) {
        //构造请求链接
        String baseURL = "https://gu-studio.cn:8090/get_info";
        String param1 = "username=" + id;
        String param2 = "password=" + password;
        String requestURL = baseURL + "?" + param1 + "&" + param2 + "&user_type=1";
        Connection.Response login = null;
        logger.info("教师账号校验：{}",requestURL);
        TeacherInfo teacherInfo = null;
        try {
            login = Jsoup.connect(requestURL)
                    .ignoreContentType(true) // 忽略类型验证
                    .followRedirects(false) // 禁止重定向
                    .postDataCharset("utf-8")
                    .method(Connection.Method.GET)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (login == null) {
            logger.warn("校验账号密码服务出现异常！异常URL：{}",requestURL);
            return null;
        } else {
            String requestBody = login.body();

            if (requestBody.contains("error")) {
                int errorIndex = requestBody.indexOf("error");
                String errorMsg = requestBody.substring(errorIndex + 8, requestBody.length() - 3);
                errorMsg = unicodeToChinese(errorMsg);
                logger.warn("教师账号绑定异常：{}",errorMsg);
                teacherInfo = new TeacherInfo();
                teacherInfo.setTeacherName(errorMsg);
                return teacherInfo;
            }

            //获取账号对应的姓名
            int nameIndex = login.body().indexOf("real_name");
            int ageIndex = login.body().indexOf("sex");
            String teacherName = login.body().substring(nameIndex + 12, ageIndex - 3);


            teacherInfo = new TeacherInfo();
            teacherInfo.setTeacherId(id);
            teacherInfo.setTeacherName(unicodeToChinese(teacherName));
            System.out.println(teacherInfo);
            return teacherInfo;
        }
    }

    private static String unicodeToChinese(String str){
        StringBuffer string = new StringBuffer();
        if(str.contains("!")){
            str = str.substring(0,str.length()-1);
        }
        String[] hex = str.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }
}