package com.smart.smartdoor.dto.response;

import com.smart.smartdoor.items.FeedbackItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedbackListResponse extends BaseResponse {
    private long totalCount;
    private int totalPage;
    private boolean hasNextPage;
    private boolean hasPrevPage;
    private List<FeedbackItem> feedbackItems;

    public FeedbackListResponse(String msg) {
        super(msg);

        try {
            JSONObject obj = new JSONObject(msg);
            totalCount = obj.getLong("totalCount");
            totalPage = obj.getInt("totalPage");
            hasNextPage = obj.getBoolean("hasNextPage");
            hasPrevPage = obj.getBoolean("hasPrevPage");
            feedbackItems = new ArrayList<>();
            JSONArray dataArr = obj.getJSONArray("data");
            for (int i = 0; i < dataArr.length(); ++i)
                feedbackItems.add(new FeedbackItem(dataArr.getJSONObject(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<FeedbackItem> getFeedbackItems() {
        return feedbackItems;
    }

    public void setFeedbackItems(List<FeedbackItem> feedbackItems) {
        this.feedbackItems = feedbackItems;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public boolean isHasPrevPage() {
        return hasPrevPage;
    }
}
