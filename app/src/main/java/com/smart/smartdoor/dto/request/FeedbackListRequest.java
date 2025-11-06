package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class FeedbackListRequest {
    private String cid;
    private int page;
    private int size;

    public FeedbackListRequest(String cid, int page, int size) {
        this.cid = cid;
        this.page = page;
        this.size = size;
    }

    public String build() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("cid", cid);
            obj.put("page", page);
            obj.put("size", size);
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
