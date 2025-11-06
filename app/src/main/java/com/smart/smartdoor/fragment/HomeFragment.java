package com.smart.smartdoor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.smart.smartdoor.R;
import com.smart.smartdoor.activity.HistoryActivity;
import com.smart.smartdoor.activity.MainActivity;
import com.smart.smartdoor.service.MyBleManager;
import com.smart.smartdoor.service.PermanentService;
import com.smart.smartdoor.task.door.AsyncTaskDoorStates;
import com.smart.smartdoor.task.door.AsyncTaskOpenDoorRequest;
import com.smart.smartdoor.task.user.AsyncTaskLoginUser;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.smart.smartdoor.utils.User;
import com.smart.smartdoor.utils.uilib.CircularProgressBar;
import com.smart.smartdoor.utils.uilib.RippleBackground;
import com.clj.fastble.BleManager;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.thecode.aestheticdialogs.AestheticDialog;

public class HomeFragment extends Fragment {

    View mRootView = null;

    CircularProgressBar mProgressBar;
    int mCurProgress = 0;
    Handler progressHandler = new Handler();
    Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCurProgress >= 100) {
                callUnlock();
                return;
            }
            mProgressBar.setProgressWithAnimation(mCurProgress, 20L, null, null);
            mCurProgress += 2;
            progressHandler.postDelayed(progressRunnable, 30);
        }
    };
    int mDoorConnectTryCount = 0;
    KProgressHUD waitingDialog = null;
    private Runnable connectSuccess = new Runnable() {
        @Override
        public void run() {
            Global.showMyToast(getActivity(), R.string.bluetooth_connect_success, true);
            waitingDialog.dismiss();
            waitingDialog = null;
        }
    };
    private Runnable connectFailure = new Runnable() {
        @Override
        public void run() {
            Global.showAlertDialog(getActivity(), Global.getString(getActivity(), R.string.dialog_title_failure), Global.getString(getActivity(), R.string.bluetooth_connect_failure));
            waitingDialog.dismiss();
            waitingDialog = null;
        }
    };
    private Handler connectHandler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_home, container, false);

        initComponents();
        refreshStates();

        Global.fixTheAllViews((ViewGroup) mRootView);

        if (Global.isHomeFirstCreated) {
            Global.isHomeFirstCreated = false;
            checkAutologin();
        }

        return mRootView;
    }

    private void initComponents() {
        mRootView.findViewById(R.id.group_btn).setPadding(0, Global.getStatusBarHeight(getActivity()), 0, 0);

        initUnlockButton();
        initStateButtons();
        initDoorConnectButton();
        initOpenHistoryButton();
    }

    private void initStateButtons() {
        mRootView.findViewById(R.id.btn_network).setOnClickListener(v -> checkDeviceStates(true));
        mRootView.findViewById(R.id.btn_bluetooth).setOnClickListener(v -> checkDeviceStates(true));
        mRootView.findViewById(R.id.btn_battery).setOnClickListener(v -> checkDeviceStates(true));
    }

    private void initDoorConnectButton() {
        mRootView.findViewById(R.id.btn_bluetooth_connect).setOnClickListener(v -> {
            if (User.instance.password.isEmpty()) {
                User.instance.login(getActivity(), () -> processDoorConnect());
            } else {
                processDoorConnect();
            }
        });
    }

    private void initOpenHistoryButton() {
        mRootView.findViewById(R.id.btn_history).setOnClickListener(v -> {
            if (User.instance.password.isEmpty()) {
                User.instance.login(getActivity(), () -> {
                    Intent intent = new Intent(getActivity(), HistoryActivity.class);
                    getActivity().startActivity(intent);
                });
            } else {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    private void initUnlockButton() {
        ((RippleBackground) mRootView.findViewById(R.id.parent_ripple)).startRippleAnimation();
        mProgressBar = mRootView.findViewById(R.id.progress);

        mRootView.findViewById(R.id.btn_unlock).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startUnlockProgress();
                        return false;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        cancelUnlockProgress();
                        return false;
                }
                return false;
            }
        });
    }

    private void startUnlockProgress() {
        mCurProgress = 0;
        progressHandler.removeCallbacks(progressRunnable);
        progressHandler.post(progressRunnable);
    }

    private void cancelUnlockProgress() {
        progressHandler.removeCallbacks(progressRunnable);
        mCurProgress = 0;
        mProgressBar.setProgressWithAnimation(0f, 500L, null, null);
    }

    private void callUnlock() {
        mProgressBar.setProgressWithAnimation(0, 500L, null, null);

        AsyncTaskOpenDoorRequest task = new AsyncTaskOpenDoorRequest(getActivity(), User.instance.cId, User.instance.password, new Runnable() {
            @Override
            public void run() {
                new AestheticDialog.Builder(getActivity(), AestheticDialog.DialogStyle.FLAT, AestheticDialog.DialogType.SUCCESS)
                        .setTitle(Global.getString(getActivity(), R.string.open_door_request_success_title))
                        .setMessage(Global.getString(getActivity(), R.string.open_door_request_success_content))
                        .setOnClickListener(dialog -> dialog.dismiss())
                        .show();
            }
        });
        task.execute();

    }

    private void processDoorConnect() {
        if (User.instance.roleType.equals(Constants.AUTH_USER)) {
            Global.showAlertDialog(getActivity(), Global.getString(getActivity(), R.string.bluetooth_connect_failure_title), Global.getString(getActivity(), R.string.bluetooth_connect_not_authorized));
            return;
        }

        if (MyBleManager.getInstance() == null) {
            Global.showAlertDialog(getActivity(), Global.getString(getActivity(), R.string.bluetooth_connect_failure_title), Global.getString(getActivity(), R.string.bluetooth_connect_service_not_running));
            return;
        }

        if (!BleManager.getInstance().isSupportBle()) {
            Global.showAlertDialog(getActivity(), Global.getString(getActivity(), R.string.bluetooth_connect_failure_title), Global.getString(getActivity(), R.string.bluetooth_ble_not_supported));
            return;
        }

        if (!BleManager.getInstance().isBlueEnable()) {
            Global.showMyToast(getActivity(), R.string.bluetooth_turn_on, true);
            BleManager.getInstance().enableBluetooth();
            return;
        }

        if (MyBleManager.getInstance().isConnected()) {
            Global.showMyToast(getActivity(), R.string.bluetooth_connect_already_done, true);
            return;
        }

        MyBleManager.getInstance().startConnect();

        startConnectWaiting();
    }

    private void startConnectWaiting() {
        mDoorConnectTryCount = 0;
        if (waitingDialog != null && waitingDialog.isShowing())
            waitingDialog.dismiss();

        waitingDialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(Global.getString(getActivity(), R.string.bluetooth_connect_processing))
                .setCancellable(false)
                .show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (mDoorConnectTryCount >= Constants.BLUETOOTH_CONNECT_WAIT_COUNT)
                            break;
                        if (MyBleManager.getInstance().isConnected())
                            break;

                        ++mDoorConnectTryCount;
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                }
                if (MyBleManager.getInstance().isConnected())
                    connectHandler.post(connectSuccess);
                else
                    connectHandler.post(connectFailure);
            }
        }).start();
    }

    private void checkAutologin() {
        if (Global.getSharedBooleanValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_AUTOLOGIN, false) &&
                !Global.getSharedStringValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_PASSWORD, "").isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AsyncTaskLoginUser task = new AsyncTaskLoginUser(getActivity(), User.instance.cId, Global.getSharedStringValue(getActivity(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_PASSWORD, ""), () -> {
                        PermanentService.startService(MainActivity.getInstance());
                        initLoginState();
                    });
                    task.execute();
                }
            }, 500);
        }
    }

    private void initLoginState() {
        if (User.instance.password.isEmpty()) {
            ((TextView) mRootView.findViewById(R.id.txt_address)).setText(Global.getString(getActivity(), R.string.home_address_undefined));
        } else {
            ((TextView) mRootView.findViewById(R.id.txt_address)).setText(User.instance.convDetailInfo);
        }

        checkDeviceStates(false);
    }

    private void checkDeviceStates(boolean isForceLogin) {
        if (User.instance.password.isEmpty()) {
            if (isForceLogin) {
                User.instance.login(getActivity(), () -> {
                    AsyncTaskDoorStates task = new AsyncTaskDoorStates(getActivity(), User.instance.cId, User.instance.password, User.instance.deviceId, new Runnable() {
                        @Override
                        public void run() {
                            refreshStates();
                        }
                    });
                    task.execute();
                });
            }
        } else {
            AsyncTaskDoorStates task = new AsyncTaskDoorStates(getActivity(), User.instance.cId, User.instance.password, User.instance.deviceId, new Runnable() {
                @Override
                public void run() {
                    refreshStates();
                }
            });
            task.execute();
        }
    }

    private void refreshStates() {
        // Socket Connect State
        if (User.instance.isSocketConnected) {
            ((ImageView) mRootView.findViewById(R.id.icon_network)).setImageResource(R.drawable.ic_wifi_flaticon);
            ((TextView) mRootView.findViewById(R.id.txt_network)).setText(R.string.connected);
        } else {
            ((ImageView) mRootView.findViewById(R.id.icon_network)).setImageResource(R.drawable.ic_wifi_exclamation_flaticon);
            ((TextView) mRootView.findViewById(R.id.txt_network)).setText(R.string.disconnected);
        }

        // Door Connect State
        if (User.instance.isDoorConnected) {
            ((ImageView) mRootView.findViewById(R.id.icon_bluetooth)).setImageResource(R.drawable.ic_bluetooth_circle_flaticon_outline);
            ((TextView) mRootView.findViewById(R.id.txt_bluetooth)).setText(R.string.connected);
        } else {
            ((ImageView) mRootView.findViewById(R.id.icon_bluetooth)).setImageResource(R.drawable.ic_bluetooth_alert_flaticon_outline);
            ((TextView) mRootView.findViewById(R.id.txt_bluetooth)).setText(R.string.disconnected);
        }

        // Door Battery State
        if (User.instance.doorBatteryLevel < 0) {
            ((ImageView) mRootView.findViewById(R.id.icon_battery)).setImageResource(R.drawable.ic_battery_alert_flaticon);
            ((TextView) mRootView.findViewById(R.id.txt_battery)).setText(R.string.unknown);
        } else {
            if (User.instance.doorBatteryLevel > 75)
                ((ImageView) mRootView.findViewById(R.id.icon_battery)).setImageResource(R.drawable.ic_battery_full_flaticon);
            else if (User.instance.doorBatteryLevel > 50)
                ((ImageView) mRootView.findViewById(R.id.icon_battery)).setImageResource(R.drawable.ic_battery_three_quarters_flaticon);
            else if (User.instance.doorBatteryLevel > 25)
                ((ImageView) mRootView.findViewById(R.id.icon_battery)).setImageResource(R.drawable.ic_battery_half_flaticon);
            else if (User.instance.doorBatteryLevel > 15)
                ((ImageView) mRootView.findViewById(R.id.icon_battery)).setImageResource(R.drawable.ic_battery_quarter_flaticon);
            else
                ((ImageView) mRootView.findViewById(R.id.icon_battery)).setImageResource(R.drawable.ic_battery_exclamation_flaticon);
            ((TextView) mRootView.findViewById(R.id.txt_battery)).setText(User.instance.doorBatteryLevel + "%");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initLoginState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;
    }
}