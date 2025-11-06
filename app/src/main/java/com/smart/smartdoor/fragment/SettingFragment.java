package com.smart.smartdoor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.smart.smartdoor.R;
import com.smart.smartdoor.activity.ChangeUserinfoActivity;
import com.smart.smartdoor.activity.UserManageActivity;
import com.smart.smartdoor.service.MyForegroundService;
import com.smart.smartdoor.service.PermanentService;
import com.smart.smartdoor.task.user.AsyncTaskChangePassword;
import com.smart.smartdoor.task.user.AsyncTaskRequestAdminAuth;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.User;
import com.smart.smartdoor.utils.uilib.alerts.dialog.CustomSDialog;
import com.smart.smartdoor.utils.uilib.alerts.dialog.callbacks.OnClickCallback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingFragment extends Fragment {

    View mRootView = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_setting, container, false);

        initComponents();

        Global.fixTheAllViews((ViewGroup) mRootView);

        new Handler().postDelayed(() -> {
            if (User.instance.password.isEmpty()) {
                User.instance.login(getActivity(), () -> PermanentService.startService(getActivity()));
            }
        }, 300);

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
    }

    private void initComponents() {
        ViewGroup.MarginLayoutParams mParam = (ViewGroup.MarginLayoutParams) mRootView.findViewById(R.id.topbar).getLayoutParams();
        mParam.topMargin = Global.getStatusBarHeight(getActivity());
        mRootView.findViewById(R.id.topbar).setLayoutParams(mParam);

        mRootView.findViewById(R.id.btn_back).setOnClickListener(v -> getActivity().onBackPressed());

        mRootView.findViewById(R.id.btn_user).setOnClickListener(v -> {
            if (User.instance.password.isEmpty()) {
                User.instance.login(getActivity(), () -> PermanentService.startService(getActivity()));
            } else {
                userLogout();
            }
        });

        mRootView.findViewById(R.id.btn_user_manage).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserManageActivity.class);
            getActivity().startActivity(intent);
        });

        mRootView.findViewById(R.id.btn_request_admin).setOnClickListener(v -> requestAdminAuth());

        mRootView.findViewById(R.id.btn_change_info).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangeUserinfoActivity.class);
            getActivity().startActivity(intent);
        });

        mRootView.findViewById(R.id.btn_change_password).setOnClickListener(v -> showPasswordChangeDialog());

        ((SwitchMaterial) mRootView.findViewById(R.id.switch_show_notice)).setChecked(Global.getSharedBooleanValue(getActivity(), Constants.SHARED_SETTING_NAME, Constants.SHARED_SETTING_KEY_SHOW_NOTIFY, true));
        ((SwitchMaterial) mRootView.findViewById(R.id.switch_fingerprint)).setChecked(Global.getSharedBooleanValue(getActivity(), Constants.SHARED_SETTING_NAME, Constants.SHARED_SETTING_KEY_USE_FINGER, false));
        ((SwitchMaterial) mRootView.findViewById(R.id.switch_save_password)).setChecked(Global.getSharedBooleanValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_SAVE_PASSWORD, false));
        ((SwitchMaterial) mRootView.findViewById(R.id.switch_auto_login)).setChecked(Global.getSharedBooleanValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_AUTOLOGIN, false));

        mRootView.findViewById(R.id.switch_show_notice).setOnClickListener(v -> {
            Global.setSharedBooleanValue(getActivity(), Constants.SHARED_SETTING_NAME, Constants.SHARED_SETTING_KEY_SHOW_NOTIFY, ((SwitchMaterial) mRootView.findViewById(R.id.switch_show_notice)).isChecked());
        });
        mRootView.findViewById(R.id.btn_show_notice).setOnClickListener(v -> {
            Global.setSharedBooleanValue(getActivity(), Constants.SHARED_SETTING_NAME, Constants.SHARED_SETTING_KEY_SHOW_NOTIFY, !((SwitchMaterial) mRootView.findViewById(R.id.switch_show_notice)).isChecked());
            ((SwitchMaterial) mRootView.findViewById(R.id.switch_show_notice)).setChecked(Global.getSharedBooleanValue(getActivity(), Constants.SHARED_SETTING_NAME, Constants.SHARED_SETTING_KEY_SHOW_NOTIFY, true));
        });

        mRootView.findViewById(R.id.switch_fingerprint).setOnClickListener(v -> {
            Global.setSharedBooleanValue(getActivity(), Constants.SHARED_SETTING_NAME, Constants.SHARED_SETTING_KEY_USE_FINGER, ((SwitchMaterial) mRootView.findViewById(R.id.switch_fingerprint)).isChecked());
        });
        mRootView.findViewById(R.id.btn_use_fingerprint).setOnClickListener(v -> {
            Global.setSharedBooleanValue(getActivity(), Constants.SHARED_SETTING_NAME, Constants.SHARED_SETTING_KEY_USE_FINGER, !((SwitchMaterial) mRootView.findViewById(R.id.switch_fingerprint)).isChecked());
            ((SwitchMaterial) mRootView.findViewById(R.id.switch_fingerprint)).setChecked(Global.getSharedBooleanValue(getActivity(), Constants.SHARED_SETTING_NAME, Constants.SHARED_SETTING_KEY_USE_FINGER, false));
        });

        mRootView.findViewById(R.id.switch_save_password).setOnClickListener(v -> {
            Global.setSharedBooleanValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_SAVE_PASSWORD, ((SwitchMaterial) mRootView.findViewById(R.id.switch_save_password)).isChecked());
        });
        mRootView.findViewById(R.id.btn_save_password).setOnClickListener(v -> {
            Global.setSharedBooleanValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_SAVE_PASSWORD, !((SwitchMaterial) mRootView.findViewById(R.id.switch_save_password)).isChecked());
            ((SwitchMaterial) mRootView.findViewById(R.id.switch_save_password)).setChecked(Global.getSharedBooleanValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_SAVE_PASSWORD, false));
        });

        mRootView.findViewById(R.id.switch_auto_login).setOnClickListener(v -> {
            Global.setSharedBooleanValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_AUTOLOGIN, ((SwitchMaterial) mRootView.findViewById(R.id.switch_auto_login)).isChecked());
        });
        mRootView.findViewById(R.id.btn_auto_login).setOnClickListener(v -> {
            Global.setSharedBooleanValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_AUTOLOGIN, !((SwitchMaterial) mRootView.findViewById(R.id.switch_auto_login)).isChecked());
            ((SwitchMaterial) mRootView.findViewById(R.id.switch_auto_login)).setChecked(Global.getSharedBooleanValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_AUTOLOGIN, false));
        });
    }

    private void initLoginButton() {
        if (User.instance.password.isEmpty()) {
            ((ImageView) mRootView.findViewById(R.id.icon)).setImageResource(R.drawable.ic_user_flaticon_outline);
            ((TextView) mRootView.findViewById(R.id.text)).setText(Global.getString(getActivity(), R.string.home_button_login));
//            ((TextView) mRootView.findViewById(R.id.txt_address)).setText(Global.getString(getActivity(), R.string.home_address_undefined));
            mRootView.findViewById(R.id.parent_btn_user_manage).setVisibility(View.GONE);
            mRootView.findViewById(R.id.parent_btn_request_admin).setVisibility(View.GONE);
        } else {
            ((ImageView) mRootView.findViewById(R.id.icon)).setImageResource(R.drawable.ic_user_logout_flaticon);
            ((TextView) mRootView.findViewById(R.id.text)).setText(Global.getString(getActivity(), R.string.home_button_logout));
//            ((TextView) mRootView.findViewById(R.id.txt_address)).setText(User.instance.convDetailInfo);
            if (User.instance.roleType.equals(Constants.AUTH_ADMIN)) {
                mRootView.findViewById(R.id.parent_btn_user_manage).setVisibility(View.VISIBLE);
                mRootView.findViewById(R.id.parent_btn_request_admin).setVisibility(View.GONE);
            } else {
                mRootView.findViewById(R.id.parent_btn_user_manage).setVisibility(View.GONE);
                mRootView.findViewById(R.id.parent_btn_request_admin).setVisibility(View.VISIBLE);
            }
        }
    }

    private void userLogout() {
        Global.showAlertDialog(getActivity(), Global.getString(getActivity(), R.string.logout_title), Global.getString(getActivity(), R.string.logout_confirm),
                Global.getString(getActivity(), R.string.button_ok), Global.getString(getActivity(), R.string.button_cancel), new OnClickCallback() {
                    @Override
                    public void onClick() {
                        Global.showMyToast(getActivity(), Global.getString(getActivity(), R.string.logout_message), true);
                        User.instance.initialize();
                        Global.setSharedStringValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_RESP_DATA, "");
                        initLoginButton();

                        // Stop Foreground Service
                        Intent stopIntent = new Intent(getActivity(), MyForegroundService.class);
                        getActivity().stopService(stopIntent);

                        // Go Home
                        getActivity().onBackPressed();
                    }
                }, null);
    }

    private void requestAdminAuth() {
        Global.showAlertDialog(getActivity(), Global.getString(getActivity(), R.string.request_admin_auth_title), Global.getString(getActivity(), R.string.request_admin_auth_content),
                Global.getString(getActivity(), R.string.button_ok), Global.getString(getActivity(), R.string.button_cancel), new OnClickCallback() {
                    @Override
                    public void onClick() {
                        AsyncTaskRequestAdminAuth task = new AsyncTaskRequestAdminAuth(getActivity(), User.instance.cId, User.instance.password, User.instance.deviceId, () -> {
                        });
                        task.execute();
                    }
                }, null);
    }

    private void showPasswordChangeDialog() {
        CustomSDialog passwordDialog = new CustomSDialog(getActivity(), R.layout.dialog_change_password);
        passwordDialog.updateCorner(Global.getColor(getActivity(), R.color.white));
        View dialogView = passwordDialog.getView();
        ((TextView) dialogView.findViewById(R.id.sdialog_title)).setText(R.string.change_password_title);
        ((TextView) dialogView.findViewById(R.id.sdialog_negative)).setText(R.string.button_cancel);
        ((MaterialButton) dialogView.findViewById(R.id.sdialog_positive)).setText(R.string.button_ok);

        dialogView.findViewById(R.id.sdialog_negative).setOnClickListener(v -> {
            passwordDialog.dismiss();
        });
        dialogView.findViewById(R.id.sdialog_positive).setOnClickListener(v -> {
            String curPassword = ((EditText) dialogView.findViewById(R.id.edit_password_now)).getText().toString();
            String newPassword = ((EditText) dialogView.findViewById(R.id.edit_password_new)).getText().toString();
            String newConfirmPassword = ((EditText) dialogView.findViewById(R.id.edit_password_new_confirm)).getText().toString();

            if (curPassword.isEmpty()) {
                Global.setError(getActivity(), dialogView.findViewById(R.id.edit_password_now), Global.getString(getActivity(), R.string.change_error_password_empty), true);
                return;
            }

            if (!curPassword.equals(User.instance.password)) {
                Global.setError(getActivity(), dialogView.findViewById(R.id.edit_password_now), Global.getString(getActivity(), R.string.change_error_current_password_not_equal), true);
                return;
            }

            if (newPassword.isEmpty()) {
                Global.setError(getActivity(), dialogView.findViewById(R.id.edit_password_new), Global.getString(getActivity(), R.string.change_error_new_password_not_confirm), true);
                return;
            }

            if (newConfirmPassword.isEmpty()) {
                Global.setError(getActivity(), dialogView.findViewById(R.id.edit_password_new_confirm), Global.getString(getActivity(), R.string.change_error_new_password_not_confirm), true);
                return;
            }

            if (newPassword.length() < Constants.PASSWORD_MIN_LENGTH) {
                Global.setError(getActivity(), dialogView.findViewById(R.id.edit_password_new), Global.getString(getActivity(), R.string.change_error_password_leak_length), true);
                return;
            }

            if (!newPassword.equals(newConfirmPassword)) {
                Global.setError(getActivity(), dialogView.findViewById(R.id.edit_password_new_confirm), Global.getString(getActivity(), R.string.change_error_new_password_not_confirm), true);
                return;
            }

            AsyncTaskChangePassword task = new AsyncTaskChangePassword(getActivity(), User.instance.cId, curPassword, newPassword, () -> {
                passwordDialog.dismiss();
            });
            task.execute();
        });

        Global.fixTheAllViews((ViewGroup) dialogView);

        passwordDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        initLoginButton();
    }
}