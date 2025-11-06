package com.smart.smartdoor.dto.response;

import com.smart.smartdoor.items.HistoryItem;
import com.smart.smartdoor.items.UserItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserManageListResponse extends BaseResponse {
    private List<UserItem> userItems;

    public UserManageListResponse(String msg) {
        super(msg);

        try {
            JSONObject obj = new JSONObject(msg);
            userItems = new ArrayList<>();
            JSONArray dataArr = obj.getJSONArray("data");
            for (int i = 0; i < dataArr.length(); ++i)
                userItems.add(new UserItem(dataArr.getJSONObject(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(List<UserItem> userItems) {
        this.userItems = userItems;
    }
}
