package com.smart.smartdoor.items;

import org.json.JSONObject;

public class HistoryItem {
    private long id;
    private String name;
    private String action;
    private String mode;
    private String createAt;

    public HistoryItem(JSONObject obj) {
        try {
            this.id = obj.getInt("id");
            this.name = obj.getString("name");
            this.action = obj.getString("action");
            this.mode = obj.getString("mode");
            this.createAt = obj.getString("createAt");
        } catch (Exception e) {

        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
