package com.jsdx.entity.requestBodyEntity.student;

import lombok.Data;

/**
 * Created by chaohui on 2020/11/15
 */
@Data
public class CWPWithStudentWxId {
    private ClassIdWithClassPassword classIdWithClassPassword;
    private String studentWxId;
}
