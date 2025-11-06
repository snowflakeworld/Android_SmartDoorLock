package com.smart.smartdoor.dto.response;

import org.json.JSONObject;

public class BaseResponse {
    private final static String KEY_RSP_CODE = "rspCode";
    private final static String KEY_RSP_MSG = "rspMsg";

    private String rspCode;
    private String rspMsg;

    public BaseResponse(String msg) {
        try {
            JSONObject obj = new JSONObject(msg);
            setRspCode(obj.getString(KEY_RSP_CODE));
            setRspMsg(obj.getString(KEY_RSP_MSG));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRspCode() {
        return rspCode;
    }

    public void setRspCode(String rspCode) {
        this.rspCode = rspCode;
    }

    public String getRspMsg() {
        return rspMsg;
    }

    public void setRspMsg(String rspMsg) {
        this.rspMsg = rspMsg;
    }
}
