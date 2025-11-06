package com.smart.smartdoor.task.feedback;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.smart.smartdoor.R;
import com.smart.smartdoor.dto.request.FeedbackListRequest;
import com.smart.smartdoor.dto.response.BaseResponse;
import com.smart.smartdoor.dto.response.FeedbackListResponse;
import com.smart.smartdoor.items.FeedbackItem;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.NetworkUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

public class AsyncTaskFeedbackList extends AsyncTask<Void, Void, String> {

    private Context context;
    private String cId;
    private int page;
    private int size;
    private FeedbackLoadListener listener = null;

    private KProgressHUD progressDialog = null;

    private String rspCode = null;
    private String rspMsg = null;
    private List<FeedbackItem> feedbackItems = new ArrayList<>();
    private int totalPage = 0;
    private long totalCount = 0;
    private boolean hasNextPage = false;

    public AsyncTaskFeedbackList(Context context, String cId, int page, int size, FeedbackLoadListener listener) {
        this.context = context;
        this.cId = cId;
        this.page = page;
        this.size = size;
        this.listener = listener;

        progressDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .show();
    }

    @Override
    protected String doInBackground(Void... result) {
        try {
            FeedbackListRequest request = new FeedbackListRequest(cId, page, size);

            String urlAddress = NetworkUtils.baseUrl + "/" + NetworkUtils.urlFeedbackList;
            String respStr = NetworkUtils.post(urlAddress, request.build());

            BaseResponse baseResponse = new BaseResponse(respStr);

            rspCode = baseResponse.getRspCode();
            rspMsg = baseResponse.getRspMsg();

            if (!rspCode.equals(Constants.RSP_CODE_SUCCESS))
                return rspCode;

            FeedbackListResponse response = new FeedbackListResponse(respStr);
            totalPage = response.getTotalPage();
            totalCount = response.getTotalCount();
            hasNextPage = response.isHasNextPage();

            for (FeedbackItem feedback : response.getFeedbackItems())
                feedbackItems.add(feedback);

            return rspCode;
        } catch (Exception e) {
            return Constants.RSP_CODE_EXCEPTION;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        progressDialog.dismiss();

        if (result.equals(Constants.RSP_CODE_SUCCESS)) {
            if (listener != null)
                listener.onResult(feedbackItems, totalCount, totalPage, page, hasNextPage);
        } else if (result.equals(Constants.RSP_CODE_EXCEPTION)) {
            Global.showAlertDialog(context, Global.getString(context, R.string.dialog_title_failure), Global.getString(context, R.string.warning_network_connection_failed));
        } else {
            Global.showAlertDialog(context, Global.getString(context, R.string.dialog_title_failure), rspMsg);
        }
    }
}
