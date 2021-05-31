package com.jsdx.result;

import com.jsdx.exception.BaseErrorInfoInterface;
import com.jsdx.exception.CommonEnum;
import lombok.Data;

/**
 * Created by chaohui on 2020/10/3
 */
@Data
public class Result<E> {
    int resultCode;
    String resultInfo;
    E result;

    public Result(int resultCode,String resultInfo,E result){
        this.result = result;
        this.resultInfo = resultInfo;
        this.resultCode = resultCode;
    }

    public static Result success(){
        return success(null);
    }

    public static Result success(Object data){
        Result result = new Result(CommonEnum.SUCCESS.getResultCode(),CommonEnum.SUCCESS.getResultInfo(),data);
        return result;
    }

    public static Result error(BaseErrorInfoInterface errorInfoInterface){
        Result result = new Result(errorInfoInterface.getResultCode(),errorInfoInterface.getResultInfo(),null);
        return result;
    }

    public static Result error(int code,String info){
        Result result = new Result(code,info,null);
        return result;
    }

    public static Result error(String info){
        Result result = new Result(-1,info,null);
        return result;
    }
}
