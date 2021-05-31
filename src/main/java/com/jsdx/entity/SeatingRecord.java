package com.jsdx.entity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chaohui on 2020/10/11
 */
@Data
public class SeatingRecord {
    //课室口令
    @ApiModelProperty("课室口令")
    String classId;
    //就座课室
    @ApiModelProperty("教室号")
    String classroomId;
    @ApiModelProperty("课堂名称")
    String className;
    //就座学生名
    @ApiModelProperty("学生名")
    String stuName;
    //就座学生号
    @ApiModelProperty("学生号")
    String stuId;
    //学生就座位置的横纵坐标
    @ApiModelProperty("就座位置的横坐标")
    String x;
    @ApiModelProperty("就座位置的纵坐标")
    String y;
    //就座时间
    @ApiModelProperty("就座时间")
    Long joinTime;

    @ApiModelProperty("课堂表现分")
    int performanceScore;

    @ApiModelProperty("表现良好")
    boolean good;

    @ApiModelProperty("表现不好")
    boolean bad;

    public SeatingRecord(String classId, String classroomId, String className, String stuName, String stuId, String x, String y, Long joinTime, int performanceScore, boolean good, boolean bad) {
        this.classId = classId;
        this.classroomId = classroomId;
        this.className = className;
        this.stuName = stuName;
        this.stuId = stuId;
        this.x = x;
        this.y = y;
        this.joinTime = joinTime;
        this.performanceScore = performanceScore;
        this.good = good;
        this.bad = bad;
    }

    public SeatingRecord(){

    }
}
