package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class UserManageProcessRequest {
    private String cid;
    private String password;
    private long roleId;
    private int state;

    public UserManageProcessRequest(String cid, String password, long roleId, int state) {
        this.cid = cid;
        this.password = password;
        this.roleId = roleId;
        this.state = state;
    }

    public String build() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("cid", cid);
            obj.put("password", password);
            obj.put("roleId", roleId);
            obj.put("state", state);
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
