package com.hoverfly.demo.model;

public class Error {
    private int errorId;
    private String errorMsg;
    private String errorDesc;

    public Error() {
    }

    public Error(int errorId, String errorMsg, String errorDesc) {
        this.errorId = errorId;
        this.errorMsg = errorMsg;
        this.errorDesc = errorDesc;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }
}
