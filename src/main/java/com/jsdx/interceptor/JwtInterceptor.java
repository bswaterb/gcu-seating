package com.jsdx.interceptor;


import com.jsdx.entity.TokenPayload;
import com.jsdx.exception.BizException;
import com.jsdx.util.JWTUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.apache.tomcat.util.http.MimeHeaders;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by chaohui on 2020/12/13
 */
@Slf4j
public class JwtInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        
        // 获取请求头信息authorization信息
        final String authHeader = request.getHeader("Authorization");
        if (authHeader==null || authHeader.isEmpty() || !authHeader.startsWith("Bearer")) {
            throw new BizException(400,"用户未携带token访问接口 "+getRequestUrl());
        }

        //获取token
        final String token = authHeader.substring(7);
        //检查token是否有效
        JWTUtils.verify(token);

        //移除请求头中 Authorization 的 Bearer 头
        //cutBearerToken(request);

        //构造用户payload对象
        TokenPayload payload = new TokenPayload();
        payload.setWxId(JWTUtils.getPayloadClaim(token,"wxId"));
        payload.setId(JWTUtils.getPayloadClaim(token,"id"));
        payload.setName(JWTUtils.getPayloadClaim(token,"name"));
        payload.setAuth(JWTUtils.getPayloadClaim(token,"auth"));

        request.setAttribute("payload",payload);
        //放行
        return true;
    }

    private static void addHeaders(HttpServletRequest request,String key,String value) throws Exception{
        MimeHeaders headers = getAccessibleHeaders(request);
        headers.addValue(key).setString(value);
    }

    private static void removeHeaders(HttpServletRequest request,String key) throws Exception{
        MimeHeaders headers = getAccessibleHeaders(request);
        headers.removeHeader(key);
    }

    private static void cutBearerToken(HttpServletRequest request) throws Exception{
        String token = request.getHeader("Authorization");
        removeHeaders(request,"Authorization");
        addHeaders(request,"Authorization",token.substring(7));
    }

    private static MimeHeaders getAccessibleHeaders(HttpServletRequest request) throws Exception{
        Class requestClass = request.getClass();
        Field request1 = requestClass.getDeclaredField("request");
        request1.setAccessible(true);
        Object o = request1.get(request);
        Field coyoteRequest = o.getClass().getDeclaredField("coyoteRequest");
        coyoteRequest.setAccessible(true);
        Object o1 = coyoteRequest.get(o);
        Field headers = o1.getClass().getDeclaredField("headers");
        headers.setAccessible(true);
        MimeHeaders o2 = (MimeHeaders)headers.get(o1);
        return o2;
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