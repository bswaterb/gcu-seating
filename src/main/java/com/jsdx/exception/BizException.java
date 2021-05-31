package com.jsdx.exception;

public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    protected int errorCode;
    protected String errorInfo;

    public BizException() {
        super();
    }

    public BizException(BaseErrorInfoInterface errorInfoInterface) {

        super(String.valueOf(errorInfoInterface.getResultCode()));
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorInfo = errorInfoInterface.getResultInfo();
    }

    public BizException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(String.valueOf(errorInfoInterface.getResultCode()), cause);
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorInfo = errorInfoInterface.getResultInfo();
    }

    public BizException(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public BizException(int errorCode, String errorInfo) {
        this.errorInfo = errorInfo;
        this.errorCode = errorCode;
    }

    public BizException(int errorCode, String errorInfo, Throwable cause) {
        super(String.valueOf(errorCode), cause);
        this.errorInfo = errorInfo;
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}
