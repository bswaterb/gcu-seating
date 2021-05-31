package com.jsdx.exception;


public enum CommonEnum implements BaseErrorInfoInterface{
    SUCCESS(200,"成功！"),
    BODY_NOT_MATCH(400,"请求体结构有误或传参不完整！"),
    SIGNATURE_NOT_MATCH(401,"携带的数字签名有误！"),
    NOT_BIND(402,"用户不存在，请先绑定账户！"),
    CLASS_NOT_EXIST(403,"课堂不存在或所需要的信息为空！"),
    NOT_FOUND(404,"请求的资源不存在！"),
    AUTH_NOT_PASS(405,"用户权限不足！"),
    CLASS_CREATE_FAILED(406,"课室创建失败！"),
    ARGS_NOT_MATCH(407,"方法传参有误！"),
    ACCOUNT_ALREADY_EXIST(408,"账户已绑定，无法重复绑定"),
    STU_NOT_EXIST(409,"学生不在课堂中！"),
    FAILED(410,"操作失败"),
    JOIN_CLASS_FAILED(411,"加入课堂失败"),
    SEAT_NOT_EMPTY(412,"该位置已有人入座"),
    TEA_NOT_CREATE_CLASS(413,"教师没有已过期的创建记录"),
    STU_ALREADY_IN_CLASS(414,"学生已在此课堂中选座"),
    ACCOUNT_TOKEN_NOT_MATCH(415,"用户与token不匹配"),
    NO_DATA_FIND(416,"未检索到数据"),
    ACCOUNT_NOT_EXIST(417,"账户不存在！"),
    JW_ACCOUNT_NOT_EXIST(418,"用户不存在！"),
    TOKEN_EXPIRE(419,"token已过期"),
    ALREADY_SEATED(420,"您已入座，无法再次入座"),
    CLASS_NOT_STARTED(421,"课堂尚未开始"),
    INTERNAL_SERVER_ERROR(500,"服务器内部错误！"),
    SERVER_BUSY(503,"服务器忙，请稍后再试！"),
    JW_BUSY(4001,"教务系统繁忙，请稍后再试"),
    JW_TIMEOUT(4002,"当前时间段禁止访问教务系统！"),
    JW_PASSWORD_ERROR(4003,"教务系统账号密码错误，请谨慎重试"),
    JW_ACCOUNT_LOCKED(4004,"当前账号密码错误次数过多已被锁定，次日自动解锁"),
    REQUEST_METHOD_ERROR(5000,"该接口不支持当前的请求方式"),
    UNKNOWN_ERROR(8888,"未知错误，请联系开发者反馈")
    ;

    private int resultCode;
    private String resultInfo;

    CommonEnum(int resultCode, String resultInfo) {
        this.resultCode = resultCode;
        this.resultInfo = resultInfo;
    }

    @Override
    public int getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultInfo() {
        return resultInfo;
    }
}
