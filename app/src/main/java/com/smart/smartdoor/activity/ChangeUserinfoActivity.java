package com.smart.smartdoor.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import com.smart.smartdoor.R;
import com.smart.smartdoor.task.user.AsyncTaskChangeUserinfo;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.User;

public class ChangeUserinfoActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Global.setStatusBarColorResId(this, R.color.colorAccent, false);
        Global.setNavigationBarColorResId(this, R.color.white, true);

        setContentView(R.layout.activity_userinfo);

        initComponents();

        Global.fixTheAllViews((ViewGroup) getWindow().getDecorView());
    }

    private void initComponents() {
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        ((EditText) findViewById(R.id.edit_cid)).setText(User.instance.cId);
        findViewById(R.id.edit_birthday).setOnClickListener((view) -> {
            Global.showDatePickerDialog(ChangeUserinfoActivity.this, (EditText) view);
        });

        findViewById(R.id.btn_register).setOnClickListener(v -> confirmChange());

        ((EditText) findViewById(R.id.edit_name)).setText(User.instance.name);
        ((RadioButton) findViewById(R.id.radio_male)).setChecked(User.instance.gender.equals(Constants.GENDER_MALE));
        ((RadioButton) findViewById(R.id.radio_female)).setChecked(User.instance.gender.equals(Constants.GENDER_FEMALE));
        ((EditText) findViewById(R.id.edit_birthday)).setText(Global.getFormatDate(User.instance.birthday));
        ((EditText) findViewById(R.id.edit_citizen)).setText(User.instance.citizenNumber);
    }

    private void confirmChange() {
        String cId = ((EditText) findViewById(R.id.edit_cid)).getText().toString();
        String name = ((EditText) findViewById(R.id.edit_name)).getText().toString();
        String birthday = ((EditText) findViewById(R.id.edit_birthday)).getText().toString();
        String citizenNumber = ((EditText) findViewById(R.id.edit_citizen)).getText().toString();
        String gender = ((RadioButton) findViewById(R.id.radio_male)).isChecked() ? Constants.GENDER_MALE : Constants.GENDER_FEMALE;

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

        if (birthday.isEmpty()) {
//            Global.setError(this, findViewById(R.id.edit_birthday), Global.getString(this, R.string.register_error_birthday_empty), true);
            Global.showMyToast(this, R.string.register_error_birthday_empty, true);
            return;
        }

        if (citizenNumber.isEmpty()) {
            Global.setError(this, findViewById(R.id.edit_citizen), Global.getString(this, R.string.register_error_citizen_empty), true);
            return;
        }

        AsyncTaskChangeUserinfo task = new AsyncTaskChangeUserinfo(this, cId, name, User.instance.password, gender, birthday, citizenNumber, () -> {
            finish();
        });
        task.execute();
    }
}
