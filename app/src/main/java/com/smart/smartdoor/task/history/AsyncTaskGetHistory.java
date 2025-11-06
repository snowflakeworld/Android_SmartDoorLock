package com.smart.smartdoor.task.history;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.smart.smartdoor.R;
import com.smart.smartdoor.dto.request.HistoryRequest;
import com.smart.smartdoor.dto.response.BaseResponse;
import com.smart.smartdoor.dto.response.HistoryResponse;
import com.smart.smartdoor.items.HistoryItem;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.NetworkUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.List;

public class AsyncTaskGetHistory extends AsyncTask<Void, Void, String> {

    private Context context;
    private String cId;
    private String fromDate;
    private String toDate;
    private List<HistoryItem> historyItems;
    private Runnable runnable;

    private KProgressHUD progressDialog = null;

    private String rspCode = null;
    private String rspMsg = null;

    public AsyncTaskGetHistory(Context context, String cId, String fromDate, String toDate, List<HistoryItem> historyItems, Runnable runnable) {
        this.context = context;
        this.cId = cId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.historyItems = historyItems;
        this.runnable = runnable;

        progressDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .show();
    }

    @Override
    protected String doInBackground(Void... result) {
        try {
            HistoryRequest request = new HistoryRequest(cId, fromDate, toDate);

            String urlAddress = NetworkUtils.baseUrl + "/" + NetworkUtils.urlHistory;
            String respStr = NetworkUtils.post(urlAddress, request.build());

            BaseResponse baseResponse = new BaseResponse(respStr);

            rspCode = baseResponse.getRspCode();
            rspMsg = baseResponse.getRspMsg();

            if (!rspCode.equals(Constants.RSP_CODE_SUCCESS))
                return rspCode;

            HistoryResponse response = new HistoryResponse(respStr);
            historyItems.clear();
            for (HistoryItem history : response.getHistoryItems())
                historyItems.add(history);

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
            if (runnable != null) {
                ((Activity) context).runOnUiThread(runnable);
            }
        } else if (result.equals(Constants.RSP_CODE_EXCEPTION)) {
            Global.showAlertDialog(context, Global.getString(context, R.string.dialog_title_failure), Global.getString(context, R.string.warning_network_connection_failed));
        } else {
            Global.showAlertDialog(context, Global.getString(context, R.string.dialog_title_failure), rspMsg);
        }
    }
}
