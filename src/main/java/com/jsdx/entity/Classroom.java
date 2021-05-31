package com.jsdx.entity;


import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;


/**
 * Created by chaohui on 2020/9/22
 */
@Data
public class Classroom {
    //课堂名  前端输入
    @ApiModelProperty("前端输入 课堂名 例如：'高等数学'")
    private String className;

    //课堂ID  后端生成
    @ApiModelProperty("后端生成 随机的五位课堂口令")
    private String classId;

    //开始时间  前端输入
    @ApiModelProperty("前端输入 课堂开始时间 以时间戳形式传入")
    private long startTime;

    //结束时间  前端输入
    @ApiModelProperty("前端输入 课堂结束时间 以时间戳形式传入")
    private long endTime;

    //课室类型  后端识别生成
    @ApiModelProperty("后端生成 课室的容量")
    private String classroomSize;

    //教室号  前端输入
    @ApiModelProperty("前端输入 课室号 例如'A1-102'")
    private String classroomId;

    //课堂密码 后端生成
    @ApiModelProperty("后端生成 随机的五位课堂密码")
    private String classPassword;

    //教师ID 后端生成
    @ApiModelProperty("后端生成 教师openId")
    private String teacherOpenId;
}
