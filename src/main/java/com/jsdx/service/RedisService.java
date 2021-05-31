package com.jsdx.service;

import com.jsdx.entity.SeatingRecord;
import com.jsdx.entity.StudentCheckIn;
import com.jsdx.entity.StudentSeating;
import com.jsdx.entity.requestBodyEntity.teacher.ClassIdWithStuId;
import com.jsdx.exception.BizException;
import com.jsdx.exception.CommonEnum;
import com.jsdx.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by chaohui on 2020/10/2
 */
@Service
public class RedisService {
    @Autowired
    RedisTemplate redisTemplate;

    //学生加入课堂
    public int joinAClass(String classId,StudentSeating student){
        //判断redis中是否存在对应教室
        if(!isKeyInRedis(classId+"-group")){
            return -1;
        }
        //构造自己的redis数据key
        String key = classId+"-"+student.getStuId();
        //校验自己是否已选座
        if(isKeyInRedis(key)){
            StudentSeating seating = (StudentSeating) getRedisValue(key);
            if(seating.isSeat()==true){
                return -2;
            }
        }
        //将索引添加到课堂组集合中
        HashMap valueMap = (HashMap)getRedisValue(classId+"-group");
        Set set = (HashSet) valueMap.get("groupIndexSet");

        set.add(key);
        valueMap.put("groupIndexSet",set);

        //检测用户是否选定座位
        if(student.getY()!=null&&student.getX()!=null){
            //校验位置合法性
            int x = Integer.parseInt(student.getX());
            int y = Integer.parseInt(student.getY());
            //查看该课室的座位表该位置是否已有人座
            int seatingTable[][] = (int[][]) valueMap.get("groupSeatingTable");
            if(x<0||y<0||x>=seatingTable.length||y>=seatingTable[x].length){
                return -1;
            }
            if (seatingTable[x][y] == 0) {
                //将该座位设为已坐
                seatingTable[x][y] = 1;
                valueMap.put("groupSeatingTable", seatingTable);
                student.setSeat(true);
            } else {
                return 0;
            }
        }

        addRedisValue(classId+"-"+student.getStuId(),student);
        addRedisValue(classId+"-group",valueMap);
        return 1;
    }

    //创建课堂
    public int createAClass(String classId){
        ValueOperations<String,Map> operations = getOperationInstance();
        Map value = new HashMap();
        //组索引集合
        Set<String> indexSet = new HashSet<>();
        //组座位表
        int seatingTable[][] = new int[30][20];
        value.put("groupIndexSet",indexSet);
        value.put("groupSeatingTable",seatingTable);
        operations.set(classId+"-group",value);
        return 1;
    }

    //获取指定课室入座情况
    public List<StudentSeating> getClassInfo(String classId){
        ValueOperations<String,Map> operations = getOperationInstance();
        ArrayList<StudentSeating> list = new ArrayList<>();
        //从课堂组获取全部相关联的学生个人key集合
        Map groupMap = (HashMap) operations.get(classId+"-group");
        Set<String> indexSet = (HashSet)groupMap.get("groupIndexSet");
        //遍历key集合，将学生信息封装到list中
        for(String indexId:indexSet){
            StudentSeating seating = (StudentSeating)getRedisValue(indexId);
            list.add(seating);
        }
        return list;
    }

    //删除指定缓存
    public boolean deleteRedisCache(String key){
        return redisTemplate.delete(key);
    }

    //删除课堂组信息
    public boolean deleteClassGroup(String classId){
        if(classId==null){
            return false;
        }
        //获取课堂组索引集合
        Set<String> indexSet = (HashSet) getRedisValue(classId+"-group");
        //删除该组对应的全部索引
        for(String indexId:indexSet){
            deleteRedisCache(indexId);
        }
        //删除组信息
        deleteRedisCache(classId+"-group");
        return true;
    }

    //删除指定学生个人key
    public boolean deleteStuClassKey(String classId,String stuId){
        if(classId==null||stuId==null){
            return false;
        }
        Map value = new HashMap();
        //先删除学生在课堂组中的索引
        HashSet indexSet = (HashSet) ((Map)getRedisValue(classId+"-group")).get("groupIndexSet");
        indexSet.remove(classId+"-"+stuId);
        //再修改课堂组中该学生的座位占用情况
        StudentSeating studentSeating = (StudentSeating)getRedisValue(classId+"-"+stuId);
        String x = studentSeating.getX();
        String y = studentSeating.getY();
        int seatingTable[][] = (int[][])((Map)getRedisValue(classId+"-group")).get("groupSeatingTable");
        if(x!=null&&y!=null){
            seatingTable[Integer.parseInt(x)][Integer.parseInt(y)] = 0;
        }
        value.put("groupIndexSet",indexSet);
        value.put("groupSeatingTable",seatingTable);
        addRedisValue(classId+"-group",value);

        //再删除学生个人key
        deleteRedisCache(classId+"-"+stuId);
        return true;
    }

    //记录学生上一次进入课室位置
    public int searchLastJoinedClass(String stuId){

        return 0;
    }


    //寻找缓存中是否有相应的key
    public boolean isKeyInRedis(String key){
        ValueOperations<String,ArrayList<StudentSeating>> operations = getOperationInstance();
        if(operations.get(key)!=null){
            return true;
        }else{
            return false;
        }
    }

    //寻找学生是否在课室组中
    public boolean isStudentInClass(String classId,String stuId){
        if(classId==null||stuId==null){
            throw new BizException("传入的参数为空！");
        }
        HashSet indexSet = (HashSet)getRedisValue(classId+"-group");
        if(indexSet.contains(classId+"-"+stuId)){
            return true;
        }else{
            return false;
        }
    }

    //对学生上课状态进行评分
    public int markStudent(int status,String classId,String stuId){
        String personKey = classId+"-"+stuId;
        if(status==5){
            //移出课堂
            deleteStuClassKey(classId,stuId);
        }else{
            StudentSeating studentSeating = (StudentSeating) getRedisValue(personKey);
            /*
            status--
            1:表现良好
            2:表现不好
            3:加分
            4:减分
            5:移出课堂
             */
            if (status == 1) {
                if (studentSeating.isGood() == false) {
                    studentSeating.setGood(true);
                } else {
                    studentSeating.setGood(false);
                }
            } else if (status == 2) {
                if (studentSeating.isBad() == false) {
                    studentSeating.setBad(true);
                } else {
                    studentSeating.setBad(false);
                }
            } else if (status == 3) {
                studentSeating.setPerformanceScore(studentSeating.getPerformanceScore() + 1);
            } else if (status == 4) {
                studentSeating.setPerformanceScore(studentSeating.getPerformanceScore() - 1);
            } else {
                throw new BizException(CommonEnum.ARGS_NOT_MATCH);
            }
            addRedisValue(personKey,studentSeating);
        }
        return 1;
    }

    //获取课堂组索引集合
    public Set getIndexSet(String classId){
        return (Set) ((Map)getRedisValue(classId+"-group")).get("groupIndexSet");
    }

    public Object getRedisValue(String key){
        ValueOperations<String, Object> operations = getOperationInstance();
        Object o = operations.get(key);
        return o;
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

    private void addRedisValue(String key,Object value){
        ValueOperations<String, Object> operations = getOperationInstance();
        operations.set(key,value);
    }


    private ValueOperations getOperationInstance(){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate.opsForValue();
    }

}
