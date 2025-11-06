package com.smart.smartdoor.items;

import org.json.JSONObject;

public class BusinessItem {
    private int id;
    private String name;
    private int dispOrder;
    private int state;

    public BusinessItem(JSONObject obj) {
        try {
            this.id = obj.getInt("id");
            this.name = obj.getString("name");
            this.dispOrder = obj.getInt("dispOrder");
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

    public int getDispOrder() {
        return dispOrder;
    }

    public void setDispOrder(int dispOrder) {
        this.dispOrder = dispOrder;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
