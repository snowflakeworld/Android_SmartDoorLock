package com.smart.smartdoor.dto.response;

import com.smart.smartdoor.items.HistoryItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryResponse extends BaseResponse {
    private List<HistoryItem> historyItems;

    public HistoryResponse(String msg) {
        super(msg);

        try {
            JSONObject obj = new JSONObject(msg);
            historyItems = new ArrayList<>();
            JSONArray dataArr = obj.getJSONArray("data");
            for (int i = 0; i < dataArr.length(); ++i)
                historyItems.add(new HistoryItem(dataArr.getJSONObject(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<HistoryItem> getHistoryItems() {
        return historyItems;
    }

    public void setHistoryItems(List<HistoryItem> historyItems) {
        this.historyItems = historyItems;
    }
}
