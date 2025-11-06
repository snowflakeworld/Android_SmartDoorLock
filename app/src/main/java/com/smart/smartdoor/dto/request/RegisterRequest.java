package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class RegisterRequest {
    private String cid;
    private String name;
    private String password;
    private String gender;
    private String birthday;
    private String citizenNumber;
    private String installPlace;
    private int districtId;
    private String unit;
    private String floor;
    private String index;
    private long businessId;
    private String authType;

    public RegisterRequest(String cid, String name, String password, String gender, String birthday, String citizenNumber, String installPlace,
                           int districtId, String unit, String floor, String index, long businessId, String authType) {
        this.cid = cid;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.birthday = birthday;
        this.citizenNumber = citizenNumber;
        this.installPlace = installPlace;
        this.districtId = districtId;
        this.unit = unit;
        this.floor = floor;
        this.index = index;
        this.businessId = businessId;
        this.authType = authType;
    }

    public String build() {
        birthday = birthday.replace(".", "-");
        String[] birthdaySplits = birthday.split("-");
        int year = Integer.parseInt(birthdaySplits[0]);
        int month = Integer.parseInt(birthdaySplits[1]);
        int day = Integer.parseInt(birthdaySplits[2]);
        birthday = String.format("%d-%02d-%02d", year, month, day);

        JSONObject obj = new JSONObject();
        try {
            obj.put("cid", cid);
            obj.put("name", name);
            obj.put("password", password);
            obj.put("gender", gender);
            obj.put("birthday", birthday);
            obj.put("citizenNumber", citizenNumber);
            obj.put("installPlace", installPlace);
            obj.put("districtId", districtId);
            obj.put("unit", unit);
            obj.put("floor", floor);
            obj.put("index", index);
            obj.put("businessId", businessId);
            obj.put("authType", authType);
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
