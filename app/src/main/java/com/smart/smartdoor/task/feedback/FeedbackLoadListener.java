package com.smart.smartdoor.task.feedback;

import com.smart.smartdoor.items.FeedbackItem;

import java.util.List;

public interface FeedbackLoadListener {
    void onResult(List<FeedbackItem> data, long totalCount, int totalPage, int curPage, boolean hasNextPage);
}
