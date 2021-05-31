package com.jsdx.service;

import com.jsdx.entity.StudentCheckIn;
import com.jsdx.entity.StudentSeating;
import com.jsdx.entity.requestBodyEntity.teacher.ClassIdWithStuId;
import com.jsdx.exception.CommonEnum;
import com.jsdx.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaohui on 2020/10/2
 */
@Service
public class RedisService_change {
    @Autowired
    RedisTemplate redisTemplate;

    //学生加入课堂
    public int joinAClass(String classId,StudentSeating student){
        ValueOperations<String,ArrayList<StudentSeating>> operations = getOperationInstance();
        //查询redis中是否有该课堂对应的key值
        if(operations.get(classId)==null){
            //课堂不存在
            return -1;
        }
        return 0;
    }

    //创建课堂
    public int createAClass(String classId){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        ValueOperations<String,ArrayList<StudentSeating>> operations = redisTemplate.opsForValue();
        ArrayList<StudentSeating> list = new ArrayList<>();
        operations.set(classId,list);
        return 1;
    }

    //获取指定课室入座情况
    public List<StudentSeating> getClassInfo(String classId){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        ValueOperations<String,ArrayList<StudentSeating>> operations = redisTemplate.opsForValue();
        ArrayList<StudentSeating> list = operations.get(classId);
        return list;
    }

    //删除指定课室缓存
    public boolean deleteExpiredClass(String classId){
        return redisTemplate.delete(classId);
    }

    //创建签到记录，如果学生加入课堂了则往list中添加其记录
    public int createACheckIn(String classId){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        ValueOperations<String,ArrayList<StudentCheckIn>> operations = redisTemplate.opsForValue();
        ArrayList<StudentCheckIn> list = new ArrayList<>();
        //签到记录特殊标识：${ClassId}-CheckIn
        StringBuilder builder = new StringBuilder(classId+"-CheckIn");
        operations.set(builder.toString(),list);
        return 1;
    }

    //学生完成签到后，向缓存中写入相关信息
    public int completeCheckIn(String classId,StudentCheckIn studentCheckIn){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        ValueOperations<String,ArrayList<StudentCheckIn>> operations = redisTemplate.opsForValue();
        StringBuilder builder = new StringBuilder(classId+"-CheckIn");
        ArrayList<StudentCheckIn> list = operations.get(builder.toString());
        list.add(studentCheckIn);
        operations.set(builder.toString(),list);
        return 1;
    }

    //查询是否有该学生的签到记录
    public boolean isStudentCheckIn(String classId,String stuId){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        ValueOperations<String,ArrayList<StudentCheckIn>> operations = redisTemplate.opsForValue();
        StringBuilder builder = new StringBuilder(classId+"-CheckIn");
        ArrayList<StudentCheckIn> list = operations.get(builder.toString());
        for(StudentCheckIn studentCheckIn:list){
            if(studentCheckIn.getStuId().equals(stuId)){
                return true;
            }
        }
        return false;
    }

    //删除指定教室中指定学生的入座信息
    public int deleteSeatingInfo(ClassIdWithStuId classIdWithStuId){
        String classId = classIdWithStuId.getClassId();
        String stuId = classIdWithStuId.getStuId();
        ValueOperations<String,ArrayList<StudentSeating>> operations = getOperationInstance();
        //获取redis中的对应教室缓存
        ArrayList<StudentSeating> list = operations.get(classId);
        if(list!=null){
            for(StudentSeating studentSeating:list){
                if(studentSeating.getStuId().equals(stuId)){
                    list.remove(studentSeating);
                    operations.set(classId,list);
                    //正常执行完毕
                    return 1;
                }
            }
            //没有搜寻到相关记录
            return 0;
        }
        //没有此教室存在
        return -1;
    }

    //记录学生上一次进入课室位置
    public int searchLastJoinedClass(String stuId){

        return 0;
    }


    //寻找缓存中是否有相应的key
    public boolean isKeyInRedis(String key){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        ValueOperations<String,ArrayList<StudentSeating>> operations = redisTemplate.opsForValue();
        if(operations.get(key)!=null){
            return true;
        }else{
            return false;
        }
    }

    //寻找value数组中是否有对应的学生
    public boolean isStudentInClass(String classId,String stuId) {
        ValueOperations<String, ArrayList<StudentSeating>> operations = getOperationInstance();
        ArrayList<StudentSeating> list = operations.get(classId);
        if (list != null) {
            for (StudentSeating studentSeating : list) {
                if (studentSeating.getStuId().equals(stuId)) {
                    return true;
                }
            }
        }
        return false;
    }

    //对学生上课状态进行评分
    public int markStudent(int status,String classId,String stuId){
        ValueOperations<String, ArrayList<StudentSeating>> operations = getOperationInstance();
        ArrayList<StudentSeating> list = operations.get(classId);
        int flag = 0;
        if(status==5){
            ClassIdWithStuId entity = new ClassIdWithStuId();
            entity.setClassId(classId);
            entity.setStuId(stuId);
            deleteSeatingInfo(entity);
            flag = 1;
        }else{
            StudentSeating oldInfo = null;
            if (list != null) {
                for (StudentSeating studentSeating : list) {
                    if (studentSeating.getStuId().equals(stuId)) {
                        oldInfo = studentSeating;
                    /*
                    status--
                    1:表现良好
                    2:表现不好
                    3:加分
                    4:减分
                    5:移出课堂
                     */
                        if(status==1){
                            if(studentSeating.isGood()==false){
                                studentSeating.setGood(true);
                            }else{
                                studentSeating.setGood(false);
                            }
                            list.remove(oldInfo);
                            list.add(studentSeating);
                            operations.set(classId,list);
                            flag = 1;
                            break;
                        }else if(status==2){
                            if(studentSeating.isBad()==false){
                                studentSeating.setBad(true);
                            }else{
                                studentSeating.setBad(false);
                            }
                            flag = 1;
                            list.remove(oldInfo);
                            list.add(studentSeating);
                            operations.set(classId,list);
                            break;
                        }else if(status==3){
                            studentSeating.setPerformanceScore(studentSeating.getPerformanceScore()+1);
                            flag = 1;
                            list.remove(oldInfo);
                            list.add(studentSeating);
                            operations.set(classId,list);
                            break;
                        }else if(status==4){
                            studentSeating.setPerformanceScore(studentSeating.getPerformanceScore()-1);
                            flag = 1;
                            list.remove(oldInfo);
                            list.add(studentSeating);
                            operations.set(classId,list);
                            break;
                        }else{
                            System.out.println("上课状态评分：传入的状态status有误");
                            break;
                        }
                    }
                }
            }
        }
        if(flag==1){
            return 1;
        }else{
            return 0;
        }
    }




    private ValueOperations<String, ArrayList<StudentSeating>> getOperationInstance(){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate.opsForValue();
    }

}
