package com.jsdx.entity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by chaohui on 2020/10/2
 */
@Data
public class StudentSeating implements Serializable {
    //就坐位置的横纵坐标
    @ApiModelProperty("就座位置的横坐标")
    String x;
    @ApiModelProperty("就座位置的纵坐标")
    String y;
    //就座学生名
    @ApiModelProperty("就座学生名")
    String stuName;
    //就座学生号
    @ApiModelProperty("就座学生号")
    String stuId;
    //就座时间
    @ApiModelProperty("就座时间")
    Long joinTime;


    @ApiModelProperty("课堂表现分")
    int performanceScore;

    @ApiModelProperty("表现良好")
    boolean good;

    @ApiModelProperty("表现不好")
    boolean bad;

    boolean seat;
}
