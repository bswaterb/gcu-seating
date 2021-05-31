package com.jsdx.entity.requestBodyEntity.teacher;

import lombok.Data;

/**
 * Created by chaohui on 2020/11/9
 */
@Data
public class CheckIn {
    private String classId;
    private long endTime;
    private String teacherId;
}
