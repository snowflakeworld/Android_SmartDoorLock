package com.smart.smartdoor.dto.request;

import org.json.JSONObject;

public class HistoryRequest {
    private String cid;
    private String fromDate;
    private String toDate;

    public HistoryRequest(String cid, String fromDate, String toDate) {
        this.cid = cid;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String build() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("cid", cid);
            obj.put("fromDate", fromDate);
            obj.put("toDate", toDate);
            return obj.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
