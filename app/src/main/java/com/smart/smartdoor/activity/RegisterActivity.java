package com.smart.smartdoor.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.smart.smartdoor.R;
import com.smart.smartdoor.dto.response.BusinessListResponse;
import com.smart.smartdoor.items.BusinessItem;
import com.smart.smartdoor.task.user.AsyncTaskGetBusiness;
import com.smart.smartdoor.task.user.AsyncTaskRegisterUser;
import com.smart.smartdoor.task.user.GetBusinessListListener;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.User;
import com.smart.smartdoor.utils.uilib.DistrictSelectDialog;
import com.smart.smartdoor.utils.uilib.MyBottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends BaseActivity {

    private int selDistrictId = -1;
    private String selDistrictInfo = "";
    private long selBusinessId = -1;
    private String selBusinessName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Global.setStatusBarColorResId(this, R.color.colorAccent, false);
        Global.setNavigationBarColorResId(this, R.color.white, true);

        setContentView(R.layout.activity_register);

        initComponents();

        Global.fixTheAllViews((ViewGroup) getWindow().getDecorView());
    }

    private void initComponents() {
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        ((EditText) findViewById(R.id.edit_cid)).setText(User.instance.cId);
        ((EditText) findViewById(R.id.edit_birthday)).setOnClickListener((view) -> {
            Global.showDatePickerDialog(RegisterActivity.this, (EditText) view);
        });

        ((RadioGroup) findViewById(R.id.radio_group_type)).setOnCheckedChangeListener((group, checkedId) -> resetInstallType(((RadioButton) findViewById(R.id.radio_home)).isChecked()));
        ((EditText) findViewById(R.id.edit_district)).setOnClickListener((view) -> {
            showDistrictSelectDialog();
        });
        ((EditText) findViewById(R.id.edit_business)).setOnClickListener((view) -> {
            showBusinessListDialog();
        });

        findViewById(R.id.btn_register).setOnClickListener(v -> confirmRegister());

        resetInstallType(true);
    }

    private void resetInstallType(boolean isHouse) {
        if (isHouse) {
            findViewById(R.id.parent_house_address).setVisibility(View.VISIBLE);
            findViewById(R.id.edit_parent_business).setVisibility(View.GONE);
        } else {
            findViewById(R.id.parent_house_address).setVisibility(View.GONE);
            findViewById(R.id.edit_parent_business).setVisibility(View.VISIBLE);
        }
        selDistrictId = -1;
        selDistrictInfo = "";
        selBusinessId = -1;
        selBusinessName = "";
        ((EditText) findViewById(R.id.edit_district)).setText("");
        ((EditText) findViewById(R.id.edit_unit)).setText("");
        ((EditText) findViewById(R.id.edit_floor)).setText("");
        ((EditText) findViewById(R.id.edit_index)).setText("");
        ((EditText) findViewById(R.id.edit_business)).setText("");
    }

    private void showDistrictSelectDialog() {
        DistrictSelectDialog dialog = new DistrictSelectDialog(this);
        dialog.setListener(new DistrictSelectDialog.DistrictSelectListener() {
            @Override
            public void onSelectDistrict(int districtId, String districtInfo) {
                selDistrictId = districtId;
                selDistrictInfo = districtInfo;

                ((EditText) findViewById(R.id.edit_district)).setText(selDistrictInfo);
            }
        });
        dialog.show();
    }

    private void showBusinessListDialog() {
        AsyncTaskGetBusiness task = new AsyncTaskGetBusiness(this, new GetBusinessListListener() {
            @Override
            public void onSuccess(BusinessListResponse response) {
                if (response.getBusinessItems().size() == 0) {
                    Global.showMyToast(RegisterActivity.this, R.string.register_select_business_empty, true);
                    return;
                }

                List<String> businessStrList = new ArrayList<>();
                for (BusinessItem item : response.getBusinessItems())
                    businessStrList.add(item.getName());

                MyBottomSheetDialog dialog = new MyBottomSheetDialog(RegisterActivity.this, Global.getString(RegisterActivity.this, R.string.register_select_business_title), businessStrList, (pos) -> {
                    selBusinessId = response.getBusinessItems().get(pos).getId();
                    selBusinessName = response.getBusinessItems().get(pos).getName();
                    ((EditText) findViewById(R.id.edit_business)).setText(selBusinessName);
                });
                dialog.show();
            }
        });
        task.execute();
    }

    private void confirmRegister() {
        String cId = ((EditText) findViewById(R.id.edit_cid)).getText().toString();
        String name = ((EditText) findViewById(R.id.edit_name)).getText().toString();
        String password = ((EditText) findViewById(R.id.edit_password)).getText().toString();
        String passwordConfirm = ((EditText) findViewById(R.id.edit_password_confirm)).getText().toString();
        String birthday = ((EditText) findViewById(R.id.edit_birthday)).getText().toString();
        String citizenNumber = ((EditText) findViewById(R.id.edit_citizen)).getText().toString();
        String unit = ((EditText) findViewById(R.id.edit_unit)).getText().toString();
        String floor = ((EditText) findViewById(R.id.edit_floor)).getText().toString();
        String index = ((EditText) findViewById(R.id.edit_index)).getText().toString();
        String gender = ((RadioButton) findViewById(R.id.radio_male)).isChecked() ? Constants.GENDER_MALE : Constants.GENDER_FEMALE;
        String installPlace = ((RadioButton) findViewById(R.id.radio_home)).isChecked() ? Constants.PLACE_HOME : Constants.PLACE_BUSINESS;
        String authType = ((RadioButton) findViewById(R.id.radio_auth_user)).isChecked() ? Constants.AUTH_USER : Constants.AUTH_ADMIN;

        if (cId.isEmpty()) {
//            Global.setError(this, findViewById(R.id.edit_cid), Global.getString(this, R.string.register_error_cid_empty), true);
            Global.showMyToast(this, R.string.register_error_cid_empty, true);
            return;
        }
        if (!Global.isConsistOfNumber(cId)) {
//            Global.setError(this, findViewById(R.id.edit_cid), Global.getString(this, R.string.register_error_cid_invalid), true);
            Global.showMyToast(this, R.string.register_error_cid_invalid, true);
            return;
        }

        if (name.isEmpty()) {
            Global.setError(this, findViewById(R.id.edit_name), Global.getString(this, R.string.register_error_name_empty), true);
            return;
        }

        if (password.isEmpty()) {
            Global.setError(this, findViewById(R.id.edit_password), Global.getString(this, R.string.register_error_password_empty), true);
            return;
        }

        if (password.length() < Constants.PASSWORD_MIN_LENGTH) {
            Global.setError(this, findViewById(R.id.edit_password), Global.getString(this, R.string.register_error_password_leak_length), true);
            return;
        }

        if (passwordConfirm.isEmpty() || !password.equals(passwordConfirm)) {
            Global.setError(this, findViewById(R.id.edit_password_confirm), Global.getString(this, R.string.register_error_password_confirm_incorrect), true);
            return;
        }

        if (birthday.isEmpty()) {
//            Global.setError(this, findViewById(R.id.edit_birthday), Global.getString(this, R.string.register_error_birthday_empty), true);
            Global.showMyToast(this, R.string.register_error_birthday_empty, true);
            return;
        }

        if (citizenNumber.isEmpty()) {
            Global.setError(this, findViewById(R.id.edit_citizen), Global.getString(this, R.string.register_error_citizen_empty), true);
            return;
        }

        if (selDistrictId == -1) {
//            Global.setError(this, findViewById(R.id.edit_district), Global.getString(this, R.string.register_error_district_empty), true);
            Global.showMyToast(this, R.string.register_error_district_empty, true);
            return;
        }

        if (installPlace.equals(Constants.PLACE_HOME)) {
            if (unit.isEmpty()) {
                Global.setError(this, findViewById(R.id.edit_unit), Global.getString(this, R.string.register_error_unit_empty), true);
                return;
            }
            if (floor.isEmpty()) {
                Global.setError(this, findViewById(R.id.edit_floor), Global.getString(this, R.string.register_error_floor_empty), true);
                return;
            }
            if (index.isEmpty()) {
                Global.setError(this, findViewById(R.id.edit_index), Global.getString(this, R.string.register_error_index_empty), true);
                return;
            }
            selBusinessId = -1;
        } else {
            if (selBusinessId == -1) {
//                Global.setError(this, findViewById(R.id.edit_business), Global.getString(this, R.string.register_error_business_empty), true);
                Global.showMyToast(this, R.string.register_error_business_empty, true);
                return;
            }
        }

        Global.showAlertDialog(this, Global.getString(this, R.string.register_confirm_dialog_title), Global.getString(this, R.string.register_confirm_dialog_content),
                Global.getString(this, R.string.button_register), Global.getString(this, R.string.button_cancel), () -> {
                    AsyncTaskRegisterUser task = new AsyncTaskRegisterUser(this, cId, name, password, gender, birthday, citizenNumber, installPlace, selDistrictId,
                            unit, floor, index, selBusinessId, authType, () -> {
                        finish();
                    });
                    task.execute();
                }, null);
    }
}
