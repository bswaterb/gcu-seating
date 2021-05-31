package com.jsdx.util;

import java.util.Random;

/**
 * Created by chaohui on 2020/9/22
 */
public class RandomID {
    /**
     * 生成指定长度的随机ID
     * @param length
     * @return
     */
    public static String createRandomID(int length){
        String id = "";
        String str = "ABCDEFGHJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer sb=new StringBuffer();
        Random random = new Random();
        for(int i=0;i<length;i++){
            int number=random.nextInt(35);
            sb.append(str.charAt(number));
        }
        id = sb.toString();
        return id;
    }
}
