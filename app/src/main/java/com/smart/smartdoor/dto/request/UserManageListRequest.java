package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class UserManageListRequest {
    private String cid;
    private String password;

    public UserManageListRequest(String cid, String password) {
        this.cid = cid;
        this.password = password;
    }

    public String build() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("cid", cid);
            obj.put("password", password);
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
