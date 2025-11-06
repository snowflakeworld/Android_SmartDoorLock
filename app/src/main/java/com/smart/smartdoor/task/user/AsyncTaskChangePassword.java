package com.smart.smartdoor.task.user;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.smart.smartdoor.R;
import com.smart.smartdoor.dto.request.ChangePasswordRequest;
import com.smart.smartdoor.dto.response.BaseResponse;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.NetworkUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

public class AsyncTaskChangePassword extends AsyncTask<Void, Void, String> {

    private final Context context;
    private final String cId;
    private final String curPassword;
    private final String newPassword;
    private final Runnable runnable;

    private final KProgressHUD progressDialog;

    private String rspCode = null;
    private String rspMsg = null;

    public AsyncTaskChangePassword(Context context, String cId, String curPassword, String newPassword, Runnable runnable) {
        this.context = context;
        this.cId = cId;
        this.curPassword = curPassword;
        this.newPassword = newPassword;
        this.runnable = runnable;

        progressDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .show();
    }

    @Override
    protected String doInBackground(Void... result) {
        try {
            ChangePasswordRequest request = new ChangePasswordRequest(cId, Global.hashString(curPassword), Global.hashString(newPassword));

            String urlAddress = NetworkUtils.baseUrl + "/" + NetworkUtils.urlChangePassword;
            String respStr = NetworkUtils.post(urlAddress, request.build());

            BaseResponse baseResponse = new BaseResponse(respStr);

            rspCode = baseResponse.getRspCode();
            rspMsg = baseResponse.getRspMsg();

            if (!rspCode.equals(Constants.RSP_CODE_SUCCESS))
                return rspCode;

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
            Global.showMyToast(context, rspMsg, true);
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
