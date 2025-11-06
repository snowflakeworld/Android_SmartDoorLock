package com.smart.smartdoor.dto.response;

import com.smart.smartdoor.items.DistrictItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchDistrictResponse extends BaseResponse {
    private int id;
    private String name;
    private int parentId;
    private List<DistrictItem> childDistricts;

    public SearchDistrictResponse(String msg) {
        super(msg);

        try {
            JSONObject obj = new JSONObject(msg);
            setId(obj.getInt("id"));
            setName(obj.getString("name"));
            setParentId(obj.getInt("parentId"));

            childDistricts = new ArrayList<>();
            JSONArray childArr = obj.getJSONArray("childDistricts");
            for (int i = 0; i < childArr.length(); ++i)
                childDistricts.add(new DistrictItem(childArr.getJSONObject(i)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<DistrictItem> getChildDistricts() {
        return childDistricts;
    }

    public void setChildDistricts(List<DistrictItem> childDistricts) {
        this.childDistricts = childDistricts;
    }
}
