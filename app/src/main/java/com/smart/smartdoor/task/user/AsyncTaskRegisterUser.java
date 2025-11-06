package com.smart.smartdoor.task.user;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.smart.smartdoor.R;
import com.smart.smartdoor.dto.request.RegisterRequest;
import com.smart.smartdoor.dto.response.BaseResponse;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.NetworkUtils;
import com.kaopiz.kprogresshud.KProgressHUD;

public class AsyncTaskRegisterUser extends AsyncTask<Void, Void, String> {

    private Context context;
    private String cid;
    private String name;
    private String password;
    private String gender;
    private String birthday;
    private String citizenNumber;
    private String installPlace;
    private int districtId;
    private String unit;
    private String floor;
    private String index;
    private long businessId;
    private String authType;
    private Runnable runnable;

    private KProgressHUD progressDialog = null;

    private String rspCode = null;
    private String rspMsg = null;

    public AsyncTaskRegisterUser(Context context, String cid, String name, String password, String gender, String birthday, String citizenNumber, String installPlace,
                                 int districtId, String unit, String floor, String index, long businessId, String authType, Runnable runnable) {
        this.context = context;
        this.cid = cid;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.birthday = birthday;
        this.citizenNumber = citizenNumber;
        this.installPlace = installPlace;
        this.districtId = districtId;
        this.unit = unit;
        this.floor = floor;
        this.index = index;
        this.businessId = businessId;
        this.authType = authType;
        this.runnable = runnable;

        progressDialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .show();
    }

    @Override
    protected String doInBackground(Void... result) {
        try {
            RegisterRequest request = new RegisterRequest(cid, name, Global.hashString(password), gender, birthday, citizenNumber, installPlace, districtId,
                    unit, floor, index, businessId, authType);

            String urlAddress = NetworkUtils.baseUrl + "/" + NetworkUtils.urlRegister;
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
            Global.showMyToast((Activity) context, rspMsg, true);

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
