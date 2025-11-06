package com.smart.smartdoor.items;

import org.json.JSONObject;

public class UserItem {
    private long id;
    private long cid;
    private String name;
    private String roleType;
    private int state;
    private String updateAt;

    public UserItem(JSONObject obj) {
        try {
            this.id = obj.getInt("id");
            this.cid = obj.getLong("cid");
            this.name = obj.getString("name");
            this.roleType = obj.getString("roleType");
            this.state = obj.getInt("state");
            this.updateAt = obj.getString("updateAt");
        } catch (Exception e) {

        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}
