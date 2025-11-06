package com.smart.smartdoor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.smart.smartdoor.R;
import com.smart.smartdoor.task.user.AsyncTaskLoginUser;
import com.smart.smartdoor.task.user.AsyncTaskCheckRegister;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.User;

public class LoginActivity extends BaseActivity {
    public static Runnable mRunnable = null;
    EditText mEditCid = null;
    EditText mEditPassword = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Global.setStatusBarColorResId(this, R.color.colorAccent, false);
        Global.setNavigationBarColorResId(this, R.color.white, true);

        setContentView(R.layout.activity_login);

        initComponents();

        Global.fixTheAllViews((ViewGroup) getWindow().getDecorView());
    }

    private void initComponents() {
        mEditCid = findViewById(R.id.edit_cid);
        mEditPassword = findViewById(R.id.edit_password);

        mEditCid.setText(User.instance.cId != null ? User.instance.cId : "");
        if (Global.getSharedBooleanValue(this, Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_SAVE_PASSWORD, false) && Global.getSharedStringValue(this, Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_PASSWORD, "").length() > 0)
            mEditPassword.setText(Global.getSharedStringValue(this, Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_PASSWORD, ""));
        else
            mEditPassword.setText("");
        mEditPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                confirmLogin();
                return true;
            }
            return false;
        });

        ((CheckBox) findViewById(R.id.check_save_password)).setChecked(Global.getSharedBooleanValue(this, Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_SAVE_PASSWORD, false));
        ((CheckBox) findViewById(R.id.check_auto_login)).setChecked(Global.getSharedBooleanValue(this, Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_AUTOLOGIN, false));

        findViewById(R.id.check_save_password).setOnClickListener(v -> Global.setSharedBooleanValue(getBaseContext(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_SAVE_PASSWORD, ((CheckBox) findViewById(R.id.check_save_password)).isChecked()));

        findViewById(R.id.check_auto_login).setOnClickListener(v -> Global.setSharedBooleanValue(getBaseContext(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_AUTOLOGIN, ((CheckBox) findViewById(R.id.check_auto_login)).isChecked()));

        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btn_register).setOnClickListener(v -> checkRegister());

        findViewById(R.id.btn_login).setOnClickListener(v -> confirmLogin());

    }

    private void confirmLogin() {
        String cId = ((EditText) findViewById(R.id.edit_cid)).getText().toString();
        String password = ((EditText) findViewById(R.id.edit_password)).getText().toString();

        if (cId.isEmpty()) {
            Global.setError(this, findViewById(R.id.edit_cid), Global.getString(this, R.string.login_error_cid_empty), true);
            return;
        }
        if (!Global.isConsistOfNumber(cId)) {
            Global.setError(this, findViewById(R.id.edit_cid), Global.getString(this, R.string.login_error_cid_invalid), true);
            return;
        }

        if (password.isEmpty()) {
            Global.setError(this, findViewById(R.id.edit_password), Global.getString(this, R.string.login_error_password_empty), true);
            return;
        }

        if (password.length() < Constants.PASSWORD_MIN_LENGTH) {
            Global.setError(this, findViewById(R.id.edit_password), Global.getString(this, R.string.login_error_password_leak_length), true);
            return;
        }

        AsyncTaskLoginUser task = new AsyncTaskLoginUser(this, cId, password, () -> {
            runOnUiThread(mRunnable);

            finish();
        });
        task.execute();
    }

    private void checkRegister() {
        AsyncTaskCheckRegister task = new AsyncTaskCheckRegister(this, User.instance.cId, () -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);

            setResult(Constants.RESULT_CODE_LOGIN_CANCEL);
            finish();
        });
        task.execute();
    }
}
