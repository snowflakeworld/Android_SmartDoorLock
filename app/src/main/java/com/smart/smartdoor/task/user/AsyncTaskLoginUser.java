package com.smart.smartdoor.task.user;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.smart.smartdoor.R;
import com.smart.smartdoor.dto.request.LoginRequest;
import com.smart.smartdoor.dto.response.BaseResponse;
import com.smart.smartdoor.dto.response.LoginResponse;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.NetworkUtils;
import com.smart.smartdoor.utils.User;
import com.kaopiz.kprogresshud.KProgressHUD;

public class AsyncTaskLoginUser extends AsyncTask<Void, Void, String> {

    private Context context;
    private String cId;
    private String password;
    private Runnable runnable;

    private KProgressHUD progressDialog = null;

    private String rspCode = null;
    private String rspMsg = null;

    public AsyncTaskLoginUser(Context context, String cId, String password, Runnable runnable) {
        this.context = context;
        this.cId = cId;
        this.password = password;
        this.runnable = runnable;

        progressDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .show();
    }

    @Override
    protected String doInBackground(Void... result) {
        try {
            LoginRequest request = new LoginRequest(cId, Global.hashString(password));

            String urlAddress = NetworkUtils.baseUrl + "/" + NetworkUtils.urlLogin;
            String respStr = NetworkUtils.post(urlAddress, request.build());

            BaseResponse baseResponse = new BaseResponse(respStr);

            rspCode = baseResponse.getRspCode();
            rspMsg = baseResponse.getRspMsg();

            if (!rspCode.equals(Constants.RSP_CODE_SUCCESS))
                return rspCode;

            LoginResponse loginResponse = new LoginResponse(respStr);

            Global.setSharedStringValue(context, Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_RESP_DATA, respStr);

            User.instance.password = password;
            User.instance.name = loginResponse.getName();
            User.instance.gender = loginResponse.getGender();
            User.instance.birthday = loginResponse.getBirthday();
            User.instance.citizenNumber = loginResponse.getCitizenNumber();
            User.instance.installPlace = loginResponse.getInstallPlace();
            User.instance.roleType = loginResponse.getRoleType();
            User.instance.districtId = loginResponse.getDistrictId();
            User.instance.districtInfo = loginResponse.getDistrictInfo();
            User.instance.detailInfo = loginResponse.getDetailInfo();
            User.instance.convDetailInfo = loginResponse.getConvDetailInfo();
            User.instance.deviceId = loginResponse.getDeviceId();

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
            if (Global.getSharedBooleanValue(context, Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_SAVE_PASSWORD, false)) {
                Global.setSharedStringValue(context, Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_PASSWORD, password);
            } else {
                Global.setSharedStringValue(context, Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_PASSWORD, "");
            }

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
