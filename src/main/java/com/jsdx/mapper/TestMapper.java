package com.jsdx.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * Created by chaohui on 2020/11/25
 */
@Mapper
public interface TestMapper {
    void setBool(boolean bool);
}
