package com.smart.smartdoor.task.user;

import com.smart.smartdoor.dto.response.BusinessListResponse;

public interface GetBusinessListListener {
    void onSuccess(BusinessListResponse response);
}
