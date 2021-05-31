package com.jsdx.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chaohui on 2020/9/27
 */
public class FileIO {


    public static Map<String,String> readClassroomInfo() throws Exception{
        Map<String, String> classMap;

        String data = "";
        ClassPathResource cpr = new ClassPathResource("static/ClassroomInfo.txt");
        try {
            byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
            data = new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(data);
        //获取文件输入流
//        File file = new File("src\\main\\java\\com\\jsdx\\util\\static\\ClassroomInfo.txt");
//        BufferedReader in = new BufferedReader(new FileReader(file));
//        String str;
//        String allContents = "";
        //将文件中所有内容读写到allContents中
//        while ((str = in.readLine()) != null) {
//            allContents += str;
//        }
//        in.close();
        //以空格为特征对data进行分隔
        String info[] =data.split(" ");
        classMap = new HashMap<>();
        //向map中添加教室信息
        for (int i = 0; i < info.length; i += 6) {
            classMap.put(info[i], info[4]);
        }

        return classMap;
    }


    public static void main(String[] args) {
        FileIO fileIO = new FileIO();
        try{
            fileIO.readClassroomInfo();
            fileIO.readClassroomInfo();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
