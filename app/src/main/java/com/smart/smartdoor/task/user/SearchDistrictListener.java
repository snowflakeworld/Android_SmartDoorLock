package com.smart.smartdoor.task.user;

import com.smart.smartdoor.dto.response.SearchDistrictResponse;

public interface SearchDistrictListener {
    void onSearched(SearchDistrictResponse response);
    void onFailed();
}
