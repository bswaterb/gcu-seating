package com.jsdx.schedule;

import com.jsdx.mapper.ScheduleMapper;
import com.jsdx.entity.Classroom;
import com.jsdx.entity.SeatingRecord;
import com.jsdx.entity.StudentSeating;
import com.jsdx.service.AccountService;
import com.jsdx.service.NormalService;
import com.jsdx.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by chaohui on 2020/10/4
 */
@Component
public class CleanRedisCache {
    @Autowired
    ScheduleMapper scheduleMapper;
    @Autowired
    RedisService redisService;
    @Autowired
    NormalService normalService;

    private static final Logger logger = LoggerFactory.getLogger(CleanRedisCache.class);

    @Scheduled(fixedDelay = 60000)
    //删除已下课的课堂缓存并将其存入数据库中保存,1分钟执行一次
    public void deleteExpiredClass(){
        //获取当前时间戳
        long nowTime = new Date().getTime()/1000;
        //在数据库中寻找结束时间比当前时间戳小的classId
        List<String> classIdList;
        classIdList = scheduleMapper.findExpiredClassId(nowTime);
        //如果存在已过期的课堂，则进行数据转移操作
        if(!classIdList.isEmpty()){
            for(String classId:classIdList){
                //获取课堂组索引集合
                HashSet<String> indexSet = (HashSet)((Map)redisService.getRedisValue(classId+"-group")).get("groupIndexSet");
                //遍历索引集合，将索引对应的数据全部存入seatingList中
                if(indexSet.isEmpty()){
                    logger.warn("课室号：{} 未检测到有有效的入座记录",classId);

                }else{
                    List<SeatingRecord> recordList = new ArrayList<>();
                    String classroomId = normalService.checkClassroom(classId);
                    String className = normalService.findClassNameById(classId);
                    for(String stuClassKey:indexSet){
                        StudentSeating seating = (StudentSeating) redisService.getRedisValue(stuClassKey);
                        SeatingRecord record = new SeatingRecord(classId,classroomId,className,seating.getStuName(),seating.getStuId(),seating.getX(),seating.getY(),seating.getJoinTime(),seating.getPerformanceScore(),seating.isGood(),seating.isBad());
                        recordList.add(record);
                    }
                    //将该过期的课堂就座信息写入sql数据库中持久化
                    scheduleMapper.saveStudentSeatingRecords(recordList);
                }
                //转移表记录
                Classroom classroom = scheduleMapper.getClassInfo(classId);
                scheduleMapper.deleteExpiredClassInfo(classId);
                logger.info("课堂 {} 已过期，正在转移数据至过期表",classId);
                scheduleMapper.saveExpiredClassInfo(classroom);
                logger.info("课堂 {} 数据转移完成",classId);
                //删除redis中的所有相关联缓存
                redisService.deleteClassGroup(classId);
            }
        }



    }
}
