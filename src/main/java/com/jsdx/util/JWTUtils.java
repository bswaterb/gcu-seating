package com.jsdx.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jsdx.exception.BizException;
import com.jsdx.exception.CommonEnum;
import com.jsdx.globaldata.AuthEnum;


import java.util.Calendar;
import java.util.Map;

/**
 * Created by chaohui on 2020/9/20
 */
public class JWTUtils {
    //定义统一密钥
    private static String secretKey = "!Q@EW9&980";

    /**
     * 生成token
     * @param map  传入payload
     * @return
     */
    public static String getToken(Map<String,String> map){
        //创建JWTBuilder
        JWTCreator.Builder builder = JWT.create();
        //遍历传入的map内的信息，为token添加payload/负载
        map.forEach((k,v)->{
            builder.withClaim(k,v);
        });
        //设置过期时间，默认30分钟
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,30);
        //返回生成的token
        return builder.withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256(secretKey));
    }

    /**
     * 验证token
     * @param token  用户传入的token
     */
    public static void verify(String token){
        try{
            JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
        }catch (IllegalArgumentException e){
            throw new BizException(4001,"token错误！");
        }catch (TokenExpiredException e){
            throw new BizException(4002,"token已过期！");
        }catch (Exception e){
            throw new BizException(4000,"token校验过程出现错误！");
        }
    }


    /**
     * 获取token解码后的信息
     * @param token
     * @return
     */
    private static DecodedJWT getTokenInfo(String token){
        DecodedJWT decoder = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(token);
        return decoder;
    }

    public static int checkAuth(String token,AuthEnum auth){
        //获取token记录的用户权限
        int tokenAuth = Integer.parseInt(getAccountAuth(token));
        //比较用户具有的权限与所请求功能需要的权限
        if(tokenAuth>=auth.getValue()){
            return 1;
        }else{
            return 0;
        }

    }

    public static boolean checkOwnerWithOpenId(String openId,String token){
        if(openId==null||token==null){
            throw new BizException("传参空指针异常！");
        }
        String tokenPayloadInfo = getPayloadClaim(token,"wxId");
        if(tokenPayloadInfo.equals(openId)){
            return true;
        }else{
            return false;
        }
    }

    public static boolean checkOwnerWithId(String id,String token){
        if(id==null||token==null){
            throw new BizException("传参空指针异常！");
        }
        String tokenPayloadInfo = getPayloadClaim(token,"id");
        if(tokenPayloadInfo.equals(id)){
            return true;
        }else{
            return false;
        }
    }

    private static String getAccountAuth(String token){
        return getPayloadClaim(token,"auth");
    }


    //检验不带 Bearer 头的 token，需要 token 未过期
    public static String getPayloadClaim(String token,String claim){
        return getTokenInfo(token).getClaim(claim).asString();
    }

    //校验不带 Bearer 头的 token ，不校验token是否过期
    public static String  getPayloadClaimWithoutVerify(String token,String claim){
        return JWT.decode(token).getClaim(claim).asString();
    }
}
