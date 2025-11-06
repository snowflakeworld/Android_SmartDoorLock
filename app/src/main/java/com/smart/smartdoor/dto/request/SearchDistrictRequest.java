package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class SearchDistrictRequest {
    private int districtId;

    public SearchDistrictRequest(int districtId) {
        this.districtId = districtId;
    }

    public String build() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("districtId", districtId);
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
