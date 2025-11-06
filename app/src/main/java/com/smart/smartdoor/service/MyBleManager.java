package com.smart.smartdoor.service;

import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Handler;

import com.smart.smartdoor.utils.Constants;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

public class MyBleManager {
    private static MyBleManager instance = null;
    private String BLUETOOTH_ADDRESS = "";
    private String BATTERY_SERVICE_UUID = "";
    private String BATTERY_REQUEST_UUID = "";
    private String BATTERY_NOTIFY_UUID = "";
    private String OPEN_SERVICE_UUID = "";
    private String OPEN_REQUEST_UUID = "";
    private String OPEN_NOTIFY_UUID = "";
    private BleConnectListener mListener = null;
    private BleDevice mConnectedDevice = null;
    private int mWaitingCnt = 0;
    private Handler handler = new Handler();
    private Runnable failureRunnable = new Runnable() {
        @Override
        public void run() {
            if (mListener != null)
                mListener.onDeviceConnectFailure();
        }
    };
    private Handler batteryHandler = new Handler();
    private Runnable batteryRunnable = new Runnable() {
        @Override
        public void run() {
            requestBatteryState();
            batteryHandler.postDelayed(batteryRunnable, Constants.BLUETOOTH_BATTERY_PING_INTERVAL);
        }
    };
    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            BleManager.getInstance().connect(BLUETOOTH_ADDRESS, new BleGattCallback() {
                @Override
                public void onStartConnect() {

                }

                @Override
                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                    mListener.onDeviceConnectFailure();
                    mConnectedDevice = null;
                }

                @Override
                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    mConnectedDevice = bleDevice;
                    buildServiceChannels();
                    mListener.onDeviceConnectSuccess();
                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                    mConnectedDevice = null;
                    mListener.onDeviceConnectFailure();
                }
            });
        }
    };

    public MyBleManager() {
        instance = this;
    }

    public static MyBleManager getInstance() {
        return instance;
    }

    public void setBleConnectListener(BleConnectListener listener) {
        mListener = listener;
    }

    public void setBluetoothAddress(String bluetoothAddress) {
        BLUETOOTH_ADDRESS = bluetoothAddress;
    }

    public void setBatteryUUIDs(String batteryServiceUUID, String batteryRequestUUID, String batteryNotifyUUID) {
        BATTERY_SERVICE_UUID = batteryServiceUUID;
        BATTERY_REQUEST_UUID = batteryRequestUUID;
        BATTERY_NOTIFY_UUID = batteryNotifyUUID;
    }

    public void setOpenUUIDs(String openServiceUUID, String openRequestUUID, String openNotifyUUID) {
        OPEN_SERVICE_UUID = openServiceUUID;
        OPEN_REQUEST_UUID = openRequestUUID;
        OPEN_NOTIFY_UUID = openNotifyUUID;
    }

    public void buildServiceChannels() {
        if (mConnectedDevice == null)
            return;

        BleManager.getInstance().notify(mConnectedDevice, BATTERY_SERVICE_UUID, BATTERY_NOTIFY_UUID, new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {

            }

            @Override
            public void onNotifyFailure(BleException exception) {

            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                mListener.onBatteryNotify(data);
            }
        });

        BleManager.getInstance().notify(mConnectedDevice, OPEN_SERVICE_UUID, OPEN_NOTIFY_UUID, new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {

            }

            @Override
            public void onNotifyFailure(BleException exception) {

            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                mListener.onOpenHistoryNotify(data);
            }
        });

        startBatteryPingHandler();
    }

    private void startBatteryPingHandler() {
        batteryHandler.removeCallbacks(batteryRunnable);
        batteryHandler.post(batteryRunnable);
    }

    public boolean isConnected() {
        try {
            return BleManager.getInstance().isConnected(BLUETOOTH_ADDRESS);
        } catch (Exception e) {
            return false;
        }
    }

    public void startConnect() {
        if (!BleManager.getInstance().isBlueEnable()) {
            BleManager.getInstance().enableBluetooth();

            startConnectBluetoothEnablingThread();
        } else {
            handler.post(connectRunnable);
        }
    }

    private void startConnectBluetoothEnablingThread() {
        mWaitingCnt = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (BleManager.getInstance().isBlueEnable()) {
                            handler.post(connectRunnable);
                            break;
                        }

                        if (mWaitingCnt >= Constants.BLUETOOTH_TURN_ON_WAIT_COUNT) {
                            handler.post(failureRunnable);
                            break;
                        }

                        Thread.sleep(1000);
                        ++mWaitingCnt;
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }

    public void requestBatteryState() {
        if (mConnectedDevice == null)
            return;

        BleManager.getInstance().write(mConnectedDevice, BATTERY_SERVICE_UUID, BATTERY_REQUEST_UUID, new byte[10], new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {

            }

            @Override
            public void onWriteFailure(BleException exception) {

            }
        });
    }

    public void requestDoorOpen(byte[] data) {
        if (mConnectedDevice == null)
            return;

        BleManager.getInstance().write(mConnectedDevice, OPEN_SERVICE_UUID, OPEN_REQUEST_UUID, data, new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {

            }

            @Override
            public void onWriteFailure(BleException exception) {

            }
        });
    }

    public interface BleConnectListener {
        void onDeviceConnectSuccess();

        void onDeviceConnectFailure();

        void onBatteryNotify(byte[] data);

        void onOpenHistoryNotify(byte[] data);
    }
}
