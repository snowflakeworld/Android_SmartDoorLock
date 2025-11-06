package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class FeedbackSendRequest {
    private String cid;
    private String msg;

    public FeedbackSendRequest(String cid, String msg) {
        this.cid = cid;
        this.msg = msg;
    }

    public String build() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("cid", cid);
            obj.put("msg", msg);
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
