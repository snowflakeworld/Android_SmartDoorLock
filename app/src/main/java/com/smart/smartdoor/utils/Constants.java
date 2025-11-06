package com.smart.smartdoor.utils;

public class Constants {
    public static final int STATE_DISABLE = 0;
    public static final int STATE_REQUEST = 1;
    public static final int STATE_DELETE = 2;
    public static final int STATE_NORMAL = 3;

    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";

    public static final String PLACE_HOME = "home";
    public static final String PLACE_BUSINESS = "business";

    public static final String AUTH_USER = "user";
    public static final String AUTH_ADMIN = "admin";

    public static final String ACTION_REQUEST = "request";
    public static final String ACTION_OPEN = "open";
    public static final String ACTION_DISPLAY_REQUEST = "Request";
    public static final String ACTION_DISPLAY_OPEN = "Open";

    public static final String OPEN_TYPE_PALM = "palm";
    public static final String OPEN_TYPE_FINGER = "finger";
    public static final String OPEN_TYPE_FACE = "face";
    public static final String OPEN_TYPE_PHONE = "phone";
    public static final String OPEN_TYPE_KEY = "key";
    public static final String OPEN_TYPE_PIN = "PIN";

    public static final int FEEDBACK_PAGE_SIZE = 10;

    public static final int REQUEST_CODE_LOGIN = 82;
    public static final int RESULT_CODE_LOGIN_SUCCESS = 83;
    public static final int RESULT_CODE_LOGIN_CANCEL = 84;

    public static final int REQUEST_CODE_BATTERY_OPTIMIZATION = 1001;

    public static final int ALARM_SERVICE_INTERVAL = 1 * 60 * 1000; // every 1 min
    public static final int ALARM_ACQUIRE_TIME = 1 * 30 * 1000; // 30s

    public static final int SOCKET_PING_INTERVAL = 1 * 30 * 1000; // 30s

    public static final int BLUETOOTH_TURN_ON_WAIT_COUNT = 8;
    public static final int BLUETOOTH_CONNECT_WAIT_COUNT = 10;

    public static final int BLUETOOTH_BATTERY_PING_INTERVAL = 1 * 60 * 1000; // every 1 min

    public static final int PASSWORD_MIN_LENGTH = 8;

    public static final String RSP_CODE_SUCCESS = "00";
    public static final String RSP_CODE_EXCEPTION = "99";
    public static final String RSP_CODE_UNKNOWN = "-1";

    public static final int CONNECT_TIMEOUT = 5000;
    public static final int READ_TIMEOUT = 5000;

    public final static String SHARED_LOGIN_NAME = "login";
    public final static String SHARED_LOGIN_KEY_RESP_DATA = "resp_data";
    public final static String SHARED_LOGIN_KEY_SAVE_PASSWORD = "save_password";
    public final static String SHARED_LOGIN_KEY_AUTOLOGIN = "auto_login";
    public final static String SHARED_LOGIN_KEY_PASSWORD = "my_password";

    public final static String SHARED_SETTING_NAME = "setting";
    public static final String SHARED_SETTING_KEY_SHOW_NOTIFY = "show_notify";
    public static final String SHARED_SETTING_KEY_USE_FINGER = "use_finger";
}
