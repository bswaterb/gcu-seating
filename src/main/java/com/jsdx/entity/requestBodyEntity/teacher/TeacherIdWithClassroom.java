package com.jsdx.entity.requestBodyEntity.teacher;

import com.jsdx.entity.Classroom;
import lombok.Data;

/**
 * Created by chaohui on 2020/11/9
 */
@Data
public class TeacherIdWithClassroom {
    private String teacherOpenId;
    private Classroom classroom;
}
