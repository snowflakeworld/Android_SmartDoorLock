package com.smart.smartdoor.items;

import org.json.JSONObject;

public class FeedbackItem {
    private long id;
    private String msg;
    private String createAt;
    private int isUser;

    public FeedbackItem(JSONObject obj) {
        try {
            this.id = obj.getLong("id");
            this.msg = obj.getString("msg");
            this.createAt = obj.getString("createAt");
            this.isUser = obj.getInt("isUser");
        } catch (Exception e) {

        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public int getIsUser() {
        return isUser;
    }

    public void setIsUser(int isUser) {
        this.isUser = isUser;
    }
}
