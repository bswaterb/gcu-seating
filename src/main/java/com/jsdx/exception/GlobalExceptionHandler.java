package com.jsdx.exception;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.jsdx.result.Result;
import com.jsdx.util.JWTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = BizException.class)
    public Result bizExceptionHandler(HttpServletRequest request,BizException e){
        logger.error("发生业务异常，原因是：{}",e.getErrorInfo());
        return Result.error(e.getErrorCode(),e.getErrorInfo());
    }

    @ExceptionHandler(value = NullPointerException.class)
    public Result exceptionHandler(HttpServletRequest request,NullPointerException e){
        logger.error("发生空指针异常！原因是：",e);
        return Result.error(CommonEnum.BODY_NOT_MATCH);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public Result exceptionHandler(HttpServletRequest request,HttpRequestMethodNotSupportedException e){
        logger.error("用户请求 {} 时使用了不支持的请求方式：{}",request.getRequestURL(),request.getMethod());
        return Result.error(CommonEnum.REQUEST_METHOD_ERROR);
    }

    @ExceptionHandler(value = TokenExpiredException.class)
    public Result exceptionHandler(HttpServletRequest request,TokenExpiredException e){
        String token = request.getHeader("Authorization").substring(7);
        logger.warn("用户 {} 请求 {} 时携带了已过期的token：{}", JWTUtils.getPayloadClaimWithoutVerify(token,"name"),request.getRequestURL(),token);
        return Result.error(CommonEnum.TOKEN_EXPIRE);
    }

    @ExceptionHandler(value = Exception.class)
    public Result exceptionHandler(HttpServletRequest request,Exception e){
        logger.error("未知异常！原因是：",e);
        return Result.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }


}
