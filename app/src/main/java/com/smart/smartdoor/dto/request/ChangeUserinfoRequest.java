package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class ChangeUserinfoRequest {
    private String cid;
    private String name;
    private String password;
    private String gender;
    private String birthday;
    private String citizenNumber;

    public ChangeUserinfoRequest(String cid, String name, String password, String gender, String birthday, String citizenNumber) {
        this.cid = cid;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.birthday = birthday;
        this.citizenNumber = citizenNumber;
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
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
