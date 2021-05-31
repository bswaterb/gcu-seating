package com.jsdx.entity;

import lombok.Data;

@Data
public class StudentCheckIn {
    //学生学号
    private String stuId;
    //签到时间
    private long checkInTime;


}
