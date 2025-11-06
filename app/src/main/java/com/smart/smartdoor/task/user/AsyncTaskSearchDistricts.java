package com.smart.smartdoor.task.user;

import android.content.Context;
import android.os.AsyncTask;

import com.smart.smartdoor.R;
import com.smart.smartdoor.dto.request.SearchDistrictRequest;
import com.smart.smartdoor.dto.response.BaseResponse;
import com.smart.smartdoor.dto.response.SearchDistrictResponse;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.NetworkUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

public class AsyncTaskSearchDistricts extends AsyncTask<Void, Void, String> {

    private Context context;
    private int districtId;
    private SearchDistrictListener listener = null;

    private KProgressHUD progressDialog = null;

    private String rspCode = null;
    private String rspMsg = null;
    private SearchDistrictResponse response = null;

    public AsyncTaskSearchDistricts(Context context, int districtId, boolean showProgressDialog, SearchDistrictListener listener) {
        this.context = context;
        this.districtId = districtId;
        this.listener = listener;

        if (showProgressDialog) {
            progressDialog = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(false)
                    .show();
        }
    }

    @Override
    protected String doInBackground(Void... result) {
        try {
            SearchDistrictRequest request = new SearchDistrictRequest(districtId);

            String urlAddress = NetworkUtils.baseUrl + "/" + NetworkUtils.urlSearchDistrict;
            String respStr = NetworkUtils.post(urlAddress, request.build());

            BaseResponse baseResponse = new BaseResponse(respStr);

            rspCode = baseResponse.getRspCode();
            rspMsg = baseResponse.getRspMsg();

            if (!rspCode.equals(Constants.RSP_CODE_SUCCESS))
                return rspCode;

            response = new SearchDistrictResponse(respStr);

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
                listener.onSearched(response);
        } else if (result.equals(Constants.RSP_CODE_EXCEPTION)) {
            if (listener != null)
                listener.onFailed();
            Global.showAlertDialog(context, Global.getString(context, R.string.dialog_title_failure), Global.getString(context, R.string.warning_network_connection_failed));
        } else {
            if (listener != null)
                listener.onFailed();
            Global.showAlertDialog(context, Global.getString(context, R.string.dialog_title_failure), rspMsg);
        }
    }
}
