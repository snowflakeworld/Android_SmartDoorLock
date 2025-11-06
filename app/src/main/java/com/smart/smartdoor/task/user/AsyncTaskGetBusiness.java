package com.smart.smartdoor.task.user;

import android.content.Context;
import android.os.AsyncTask;

import com.smart.smartdoor.R;
import com.smart.smartdoor.dto.response.BaseResponse;
import com.smart.smartdoor.dto.response.BusinessListResponse;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.NetworkUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

public class AsyncTaskGetBusiness extends AsyncTask<Void, Void, String> {

    private Context context;
    private GetBusinessListListener listener = null;

    private KProgressHUD progressDialog = null;

    private String rspCode = null;
    private String rspMsg = null;
    private BusinessListResponse response = null;

    public AsyncTaskGetBusiness(Context context, GetBusinessListListener listener) {
        this.context = context;
        this.listener = listener;

        progressDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .show();
    }

    @Override
    protected String doInBackground(Void... result) {
        try {
            String urlAddress = NetworkUtils.baseUrl + "/" + NetworkUtils.urlGetBusinessList;
            String respStr = NetworkUtils.post(urlAddress, "{}");

            BaseResponse baseResponse = new BaseResponse(respStr);

            rspCode = baseResponse.getRspCode();
            rspMsg = baseResponse.getRspMsg();

            if (!rspCode.equals(Constants.RSP_CODE_SUCCESS))
                return rspCode;

            response = new BusinessListResponse(respStr);

            return rspCode;
        } catch (Exception e) {
            return Constants.RSP_CODE_EXCEPTION;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        if (result.equals(Constants.RSP_CODE_SUCCESS)) {
            if (listener != null)
                listener.onSuccess(response);
        } else if (result.equals(Constants.RSP_CODE_EXCEPTION)) {
            Global.showAlertDialog(context, Global.getString(context, R.string.dialog_title_failure), Global.getString(context, R.string.warning_network_connection_failed));
        } else {
            Global.showAlertDialog(context, Global.getString(context, R.string.dialog_title_failure), rspMsg);
        }
    }
}
