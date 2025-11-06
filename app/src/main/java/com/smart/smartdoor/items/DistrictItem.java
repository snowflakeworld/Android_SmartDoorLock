package com.smart.smartdoor.items;

import org.json.JSONObject;

public class DistrictItem {
    private int id;
    private String name;
    private int parentId;
    private int dispOrder;
    private int hasChild;
    private int state;

    public DistrictItem(JSONObject obj) {
        try {
            this.id = obj.getInt("id");
            this.name = obj.getString("name");
            this.parentId = obj.getInt("parentId");
            this.dispOrder = obj.getInt("dispOrder");
            this.hasChild = obj.getInt("hasChild");
            this.state = obj.getInt("state");
        } catch (Exception e) {

        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getDispOrder() {
        return dispOrder;
    }

    public void setDispOrder(int dispOrder) {
        this.dispOrder = dispOrder;
    }

    public int getHasChild() {
        return hasChild;
    }

    public void setHasChild(int hasChild) {
        this.hasChild = hasChild;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
