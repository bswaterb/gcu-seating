package com.jsdx.entity.requestBodyEntity.student;

import com.jsdx.entity.StudentSeating;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by chaohui on 2020/11/9
 */
@Data
public class CWPWithStudentSeating {

    private ClassIdWithClassPassword classIdWithClassPassword;
    private StudentSeating studentSeating;

}
