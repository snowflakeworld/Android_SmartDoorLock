package com.smart.smartdoor.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Base64;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.smart.smartdoor.R;
import com.smart.smartdoor.dto.response.LoginResponse;
import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.Global;
import com.clj.fastble.BleManager;

import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class MyForegroundService extends Service implements MyBleManager.BleConnectListener, MyWebSocket.MyWebSocketListener {

    private static final String SERVICE_CHANNEL_ID = "SmartDoorServiceChannel";
    private static final String SERVICE_CHANNEL_NAME = "SmartDoorService";
    private static final String MESSAGE_CHANNEL_ID = "SmartDoorMessageChannel";
    private static final String MESSAGE_CHANNEL_NAME = "SmartDoorMessage";
    private static final int NOTIFICATION_ID = 82;

    private static MyForegroundService instance = null;

    private OkHttpClient client = null;
    private MyWebSocket myWebSocket = null;
    private MyBleManager myBleManager = null;

    public static MyForegroundService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    SERVICE_CHANNEL_ID,
                    SERVICE_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationChannel messageChannel = new NotificationChannel(
                    MESSAGE_CHANNEL_ID,
                    MESSAGE_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            messageChannel.setSound(soundUri, audioAttributes);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
                manager.createNotificationChannel(messageChannel);
            }
        }

        // Foreground notification
        Notification notification = new NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
                .setContentTitle(Global.getString(this, R.string.foreground_service_title))
                .setContentText(Global.getString(this, R.string.foreground_service_content))
                .setSmallIcon(R.drawable.ic_smart_home_key_flaticon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .build();

        startForeground(NOTIFICATION_ID, notification);

        Global.writeLog("Foreground Service", "Created");

        // Schedule first alarm
        scheduleNextJob();

        // Connect Websocket
        connectWebSocket();

        // Intialize Ble
        initializeBle();
    }

    private void connectWebSocket() {
        String loginData = Global.getSharedStringValue(getBaseContext(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_RESP_DATA, "");
        if (loginData.isEmpty())
            return;

        LoginResponse loginResponse = new LoginResponse(loginData);

        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();

        myWebSocket = new MyWebSocket(client, loginResponse.getCid(), this);
        myWebSocket.connect();
    }

    private void scheduleNextJob() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        long triggerAt = System.currentTimeMillis() + Constants.ALARM_SERVICE_INTERVAL;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
            );
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // restart if killed
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (myWebSocket != null) {
                myWebSocket.close();
                myWebSocket = null;
            }

            if (client != null) {
                client.dispatcher().executorService().shutdown();
                client = null;
            }

            // Remove the persistent notification
            stopForeground(true);
        } catch (Exception e) {

        }
    }

    private void initializeBle() {
        String loginData = Global.getSharedStringValue(getBaseContext(), Constants.SHARED_LOGIN_NAME, Constants.SHARED_LOGIN_KEY_RESP_DATA, "");
        if (loginData.isEmpty())
            return;

        LoginResponse loginResponse = new LoginResponse(loginData);
        if (loginResponse.getRoleType().equals(Constants.AUTH_USER))
            return;

        String bluetoothAddress = null;
        String batteryServiceUuid = null, batteryRequestUuid = null, batteryNotifyUuid = null;
        String openServiceUuid = null, openRequestUuid = null, openNotifyUuid = null;

        try {
            JSONObject propertyInfo = new JSONObject(loginResponse.getPropertyInfo());
            bluetoothAddress = propertyInfo.getString("bluetooth_address");
            batteryServiceUuid = propertyInfo.getString("battery_service_uuid");
            batteryRequestUuid = propertyInfo.getString("battery_request_uuid");
            batteryNotifyUuid = propertyInfo.getString("battery_notify_uuid");
            openServiceUuid = propertyInfo.getString("open_service_uuid");
            openRequestUuid = propertyInfo.getString("open_request_uuid");
            openNotifyUuid = propertyInfo.getString("open_notify_uuid");
        } catch (Exception e) {

        }

        BleManager.getInstance().init(getApplication());
        myBleManager = new MyBleManager();
        myBleManager.setBleConnectListener(this);
        myBleManager.setBluetoothAddress(bluetoothAddress);
        myBleManager.setBatteryUUIDs(batteryServiceUuid, batteryRequestUuid, batteryNotifyUuid);
        myBleManager.setOpenUUIDs(openServiceUuid, openRequestUuid, openNotifyUuid);

        if (!myBleManager.isConnected())
            myBleManager.startConnect();
        else
            myBleManager.requestBatteryState();

        System.out.println("Device Address: " + bluetoothAddress + " " + myBleManager.isConnected());
    }

    @Override
    public void onDeviceConnectSuccess() {

    }

    @Override
    public void onDeviceConnectFailure() {

    }

    @Override
    public void onBatteryNotify(byte[] data) {
        myWebSocket.onBatteryInfoReceived(Base64.encodeToString(data, Base64.DEFAULT));
    }

    @Override
    public void onOpenHistoryNotify(byte[] data) {
        myWebSocket.onOpenHistoryReceived(Base64.encodeToString(data, Base64.DEFAULT));
    }

    @Override
    public void onShowSocketNotification(String type, String message) {
        if (!Global.getSharedBooleanValue(getBaseContext(), Constants.SHARED_SETTING_NAME, Constants.SHARED_SETTING_KEY_SHOW_NOTIFY, true))
            return;

        NotificationManager nm = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = (int) System.currentTimeMillis(); // unique ID for each one

        Notification notification = new NotificationCompat.Builder(getBaseContext(), MESSAGE_CHANNEL_ID)
                .setContentTitle(type.equals("openRequest") ? Global.getString(getBaseContext(), R.string.notify_title_open_request) : Global.getString(getBaseContext(), R.string.notify_title_open_history))
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_comment_flaticon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        nm.notify(notificationId, notification);
    }
}
