package com.smart.smartdoor.dto.response;

import com.smart.smartdoor.items.BusinessItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusinessListResponse extends BaseResponse {
    private List<BusinessItem> businessItems;

    public BusinessListResponse(String msg) {
        super(msg);

        try {
            JSONObject obj = new JSONObject(msg);

            businessItems = new ArrayList<>();
            JSONArray childArr = obj.getJSONArray("businessList");
            for (int i = 0; i < childArr.length(); ++i)
                businessItems.add(new BusinessItem(childArr.getJSONObject(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<BusinessItem> getBusinessItems() {
        return businessItems;
    }

    public void setBusinessItems(List<BusinessItem> businessItems) {
        this.businessItems = businessItems;
    }
}
