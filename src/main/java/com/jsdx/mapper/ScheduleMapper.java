package com.jsdx.mapper;

import com.jsdx.entity.Classroom;
import com.jsdx.entity.SeatingRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by chaohui on 2020/10/4
 */
@Mapper
public interface ScheduleMapper {
    //返回所有过期的课室号
    List<String> findExpiredClassId(long nowTime);
    //批量保存过期课堂信息
    int saveStudentSeatingRecords(List<SeatingRecord> recordList);
    //单个保存过期课堂信息
    int saveStudentSeatingRecord(SeatingRecord record);
    //获取教室口令对应的详细信息
    Classroom getClassInfo(String classId);
    //删除教室口令对应的记录
    int deleteExpiredClassInfo(String classId);
    //将过期的教室记录保存到另一表中
    int saveExpiredClassInfo(Classroom classroom);
}
