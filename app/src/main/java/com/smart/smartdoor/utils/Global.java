package com.smart.smartdoor.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.smart.smartdoor.R;
import com.smart.smartdoor.utils.uilib.alerts.dialog.AlertSDialog;
import com.smart.smartdoor.utils.uilib.alerts.dialog.callbacks.OnClickCallback;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class Global {

    public static boolean isHomeFirstCreated = true;

    public static void initialize() {
        isHomeFirstCreated = true;
    }

    public static void setSharedStringValue(Context context, String name, String key, String value) {
        context.getSharedPreferences(name, 0).edit().putString(key, value).apply();
    }

    public static void setSharedIntValue(Context context, String name, String key, int value) {
        context.getSharedPreferences(name, 0).edit().putInt(key, value).apply();
    }

    public static void setSharedFloatValue(Context context, String name, String key, float value) {
        context.getSharedPreferences(name, 0).edit().putFloat(key, value).apply();
    }

    public static void setSharedLongValue(Context context, String name, String key, long value) {
        context.getSharedPreferences(name, 0).edit().putLong(key, value).apply();
    }

    public static void setSharedBooleanValue(Context context, String name, String key, boolean value) {
        context.getSharedPreferences(name, 0).edit().putBoolean(key, value).apply();
    }

    public static String getSharedStringValue(Context context, String name, String key, String defValue) {
        return context.getSharedPreferences(name, 0).getString(key, defValue);
    }

    public static int getSharedIntValue(Context context, String name, String key, int defValue) {
        return context.getSharedPreferences(name, 0).getInt(key, defValue);
    }

    public static float getSharedFloatValue(Context context, String name, String key, float defValue) {
        return context.getSharedPreferences(name, 0).getFloat(key, defValue);
    }

    public static long getSharedLongValue(Context context, String name, String key, long defValue) {
        return context.getSharedPreferences(name, 0).getLong(key, defValue);
    }

    public static boolean getSharedBooleanValue(Context context, String name, String key, boolean defValue) {
        return context.getSharedPreferences(name, 0).getBoolean(key, defValue);
    }

    public static boolean isConsistOfNumber(String s) {
        for (int i = 0; i < s.length(); ++i)
            if (!Character.isDigit(s.charAt(i)))
                return false;
        return true;
    }

    public static String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; ++i) {
                byte b = hash[i];
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fixTheAllViews(ViewGroup parent) {

    }

    public static void setError(Context context, EditText editText, String errorMsg, boolean focusable) {
        editText.setError(errorMsg);
        editText.requestFocus();
    }

    public static void setError(Context context, EditText editText, int errorMsgResId, boolean focusable) {
        editText.setError(getString(context, errorMsgResId));
        editText.requestFocus();
    }

    public static String getString(Context context, int resId) {
        try {
            return context.getString(resId);
        } catch (Exception e) {
            return "";
        }
    }

    public static int getColor(Context context, int resId) {
        try {
            return ContextCompat.getColor(context, resId);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void setOverscan(Context context, boolean isOverscan) {
        if (isOverscan) {
            ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN);
            ((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else {
            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN);
            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        View decorView = ((Activity) context).getWindow().getDecorView();
        if (isOverscan) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN));
        }
    }

    public static void setStatusBarColorResId(Context context, int colorResId, boolean isLight) {
        try {
            setStatusBarColor(context, getColor(context, colorResId), isLight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setStatusBarColorString(Context context, String color, boolean isLight) {
        try {
            setStatusBarColor(context, Color.parseColor(color), isLight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setStatusBarColor(Context context, int color, boolean isLight) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;

        try {
            View decorView = ((Activity) context).getWindow().getDecorView();
            if (isLight) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            ((Activity) context).getWindow().setStatusBarColor(color);
        } catch (Exception e) {

        }
    }

    public static void setNavigationBarColorResId(Context context, int colorResId, boolean isLight) {
        try {
            setNavigationBarColor(context, ContextCompat.getColor(context, colorResId), isLight);
        } catch (Exception e) {

        }
    }

    public static void setNavigationBarColorString(Context context, String color, boolean isLight) {
        try {
            setNavigationBarColor(context, Color.parseColor(color), isLight);
        } catch (Exception e) {

        }
    }

    public static void setNavigationBarColor(Context context, int color, boolean isLight) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;

        try {
            ((Activity) context).getWindow().setNavigationBarColor(color);
            View decorView = ((Activity) context).getWindow().getDecorView();
            if (isLight) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
        } catch (Exception e) {

        }
    }

    public static void setFullScreen(Context context) {
        View decorView = ((Activity) context).getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.heightPixels;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int dp(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    public static boolean setMobileDataState(Context context, boolean state) {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Method setMobileDataEnabledMethod = connectivityManager.getClass()
                    .getDeclaredMethod("setMobileDataEnabled", boolean.class);
            setMobileDataEnabledMethod.setAccessible(true);

            // Check current mobile data state
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//            boolean isMobileDataEnabled = (networkInfo != null && networkInfo.isConnected());

            // Set mobile data state
            setMobileDataEnabledMethod.invoke(connectivityManager, state);

            return true;
        } catch (Exception e) {
            Log.e("MobileDataToggle", "Error toggling mobile data", e);

            return false;
        }
    }

    private static boolean checkBatteryOptimization(Activity activity) {
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        String packageName = activity.getPackageName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                // Show system dialog
                try {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    activity.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    // fallback
                    Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    activity.startActivity(intent);
                }

                return false;
            } else {
//                showMyToast(activity, "Already whitelisted!", Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
//            Toast.makeText(this, "Not required for this Android version", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public static void showAlertDialog(Context context, String title, String message, String okButton, String cancelButton, OnClickCallback okHandler, OnClickCallback cancelHander) {
        AlertSDialog dialog = new AlertSDialog(context);
        if (!title.isEmpty())
            dialog.setTitle(title);
        dialog.setText(message);
        dialog.setAccentColor(Global.getColor(context, R.color.grey55));
        if (okButton != null) {
            dialog.setPositiveButton(okButton, okHandler != null ? okHandler : () -> {
            });
        }
        if (cancelButton != null) {
            dialog.setNegativeButton(cancelButton, cancelHander != null ? cancelHander : () -> {
            });
        }
        dialog.setOnDismissCallback(() -> {

        });
        dialog.show();
    }

    public static void showAlertDialog(Context context, String title, String message) {
        showAlertDialog(context, title, message, Global.getString(context, R.string.button_ok), null, null, null);
    }

    public static void showToast(Context context, String message, boolean isLong) {
        Toast.makeText(context, message, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void showMyToast(Context context, String message, boolean isLong) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, null);

        TextView toastText = layout.findViewById(R.id.toast_text);
        toastText.setText(message);

        Toast toast = new Toast(context);
        toast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void showMyToast(Context context, int resourceId, boolean isLong) {
        showMyToast(context, getString(context, resourceId), isLong);
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%d-%02d-%02d", year, month, day);
    }

    public static String getCurrentDatetime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return String.format("%d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
    }

    public static String getFormatDate(String date) {
        String[] splits = date.split("-");
        if (splits.length < 2)
            return date;
        return String.format("%d.%d.%d.", Integer.parseInt(splits[0]), Integer.parseInt(splits[1]), Integer.parseInt(splits[2]));
    }

    public static String getFormatDatetime(String datetime) {
        String[] splits = datetime.split(" ");
        String[] daySplits = splits[0].split("-");

        return String.format("%d.%d.%d ", Integer.parseInt(daySplits[0]), Integer.parseInt(daySplits[1]), Integer.parseInt(daySplits[2])) + splits[1];
    }

    public static void writeLog(String tag, String msg) {
        /*
        Log.d(tag, msg);
        try {
            Calendar calendar = Calendar.getInstance();
            String time = String.format("%d-%d-%d %02d:%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            String log = time + "@@" + tag + "@@" + msg + "\n";

            String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "log.txt";
            if (!new File(fileName).exists()) {
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write("\n".getBytes());
                fos.close();
            }

            RandomAccessFile f = new RandomAccessFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "log.txt", "rw");
            f.seek(f.length());
            f.writeUTF(new String(log.getBytes(), "UTF-8"));
            f.close();

        } catch (Exception e) {

        }
         */
    }

    public static void requestBatteryOptimization(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = activity.getPackageName();
            PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);

            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                activity.startActivityForResult(intent, Constants.REQUEST_CODE_BATTERY_OPTIMIZATION);
            }
        }
    }

    public static void showDatePickerDialog(Context context, TextView edit) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (!edit.getText().toString().isEmpty()) {
            String[] splits = edit.getText().toString().split("\\.");
            year = Integer.parseInt(splits[0]);
            month = Integer.parseInt(splits[1]) - 1;
            day = Integer.parseInt(splits[2]);
        }

        // Create the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // Update the TextView with the selected date
                    edit.setText(selectedYear + "." + (selectedMonth + 1) + "." + selectedDay + ".");
                }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
}