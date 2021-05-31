package com.jsdx.aop;

import com.jsdx.annotation.CheckAuth;
import com.jsdx.entity.TokenPayload;
import com.jsdx.exception.BizException;
import com.jsdx.exception.CommonEnum;
import com.jsdx.util.JWTUtils;

import io.swagger.models.auth.In;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chaohui on 2021/1/28
 * 在打了@CheckAuth注解的接口具体方法被请求前，会先到此处进行鉴权
 * 当前权限清单：
 *  1 - 普通学生 - student
 *  2 - 教师 - teacher
 *  3 - 管理员 - admin
 */


@Aspect
@Component
public class AuthHandle {
    private static final Logger logger = LoggerFactory.getLogger(AuthHandle.class);
    @Pointcut(value = "execution(public * com.jsdx.controller..*(..))")
    public void start(){

    }

    @Before("start()")
    public void authAction(JoinPoint joinPoint){

        MethodSignature joinPointObject = (MethodSignature) joinPoint.getSignature();
        //获得请求的方法
        Method method = joinPointObject.getMethod();

        //如果该方法被标注了鉴权注解
        if(hasAnnotationOnMethod(method, CheckAuth.class)){
            TokenPayload payload = getPayload();
            String id = payload.getId();
            String name = payload.getName();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date());
            String url = getRequestUrl();
            int apiAuth = Integer.parseInt(getMethodAuth(method));
            int userAuth = Integer.parseInt(payload.getAuth());

            logger.info("用户 {} ({}) 于 {} 访问接口 {}, token为 {},用户访问权限为 {}",name,id,date,url,getHeaderToken().substring(7),userAuth);
            if(userAuth<apiAuth){
                throw new BizException(CommonEnum.AUTH_NOT_PASS);
            }
        }
    }

    private boolean hasAnnotationOnMethod(Method method,Class annotationClazz){
        //使用反射获取注解信息
        Annotation annotation = method.getAnnotation(annotationClazz);
        if(annotation == null){
            return false;
        }else{
            return true;
        }
    }

    //获取接口标注的权限
    private String getMethodAuth(Method method){
        String auth = "";
        if(hasAnnotationOnMethod(method,CheckAuth.class)){
            CheckAuth checkAuth = (CheckAuth) method.getAnnotation(CheckAuth.class);
            auth = checkAuth.auth();
            if(auth.equals("teacher")){
                auth = "2";
            }else if(auth.equals("student")){
                auth = "1";
            }else if(auth.equals("admin")){
                auth = "3";
            }
        }else{
            throw new IllegalArgumentException();
        }
        return auth;

    }

    private String getHeaderToken(){
        HttpServletRequest request = getServletRequest();
        String token = request.getHeader("Authorization");
        return token;
    }

    private TokenPayload getPayload(){
        HttpServletRequest request = getServletRequest();
        return (TokenPayload)request.getAttribute("payload");
    }

    private String getRequestUrl(){
        HttpServletRequest request = getServletRequest();
        return request.getRequestURL().toString();
    }

    private HttpServletRequest getServletRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request;
    }

}
