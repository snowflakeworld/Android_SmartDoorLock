package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class ChangePasswordRequest {
    private String cid;
    private String curPassword;
    private String newPassword;

    public ChangePasswordRequest(String cid, String curPassword, String newPassword) {
        this.cid = cid;
        this.curPassword = curPassword;
        this.newPassword = newPassword;
    }

    public String build() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("cid", cid);
            obj.put("curPassword", curPassword);
            obj.put("newPassword", newPassword);
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
