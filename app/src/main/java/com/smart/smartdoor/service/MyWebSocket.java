package com.smart.smartdoor.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.smart.smartdoor.utils.Constants;
import com.smart.smartdoor.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MyWebSocket extends WebSocketListener {
    String cid = "";
    Handler handler = new Handler();
    private OkHttpClient client;
    private WebSocket webSocket;
    private MyWebSocketListener mListener = null;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                JSONObject obj = new JSONObject();
                obj.put("type", "ping");

                webSocket.send(obj.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.postDelayed(runnable, Constants.SOCKET_PING_INTERVAL);
        }
    };
    private String wsUrl = NetworkUtils.socketUrl;

    public MyWebSocket(OkHttpClient client, String cid, MyWebSocketListener listener) {
        this.client = client;
        this.cid = cid;
        this.mListener = listener;
    }

    public void connect() {
        Request request = new Request.Builder()
                .url(wsUrl + "?userId=" + cid)
                .build();
        webSocket = client.newWebSocket(request, this);
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing");
        }
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
        Log.d("WS", "Closed: " + code + " " + reason);
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosing(webSocket, code, reason);
        Log.d("WS", "Closing: " + code + " " + reason);
        webSocket.close(1000, reason);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        Log.e("WS", "Error: ", t);
        retryConnect();
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        processMessage(text);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
        super.onMessage(webSocket, bytes);
        Log.d("WS", "Received bytes: " + bytes.hex());
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        Log.d("WS", "Connected to server");

        startPingTimer();
    }

    private void processMessage(String message) {
        try {
            JSONObject obj = new JSONObject(message);
            String type = obj.getString("type");
            if (type.equals("pong")) {
                Log.d("WS", "Pong");
            } else if (type.equals("openRequest")) {
                String data = obj.getString("data");
                String content = obj.getString("message");
                if (MyBleManager.getInstance() != null && MyBleManager.getInstance().isConnected()) {
                    MyBleManager.getInstance().requestDoorOpen(Base64.decode(data, Base64.DEFAULT));
                }
                if (mListener != null) {
                    mListener.onShowSocketNotification("openRequest", content);
                }
            } else if (type.equals("openHistory")) {
                String content = obj.getString("message");
                if (mListener != null) {
                    mListener.onShowSocketNotification("openHistory", content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBatteryInfoReceived(String data) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "batteryInfo");
            obj.put("data", data);

            webSocket.send(obj.toString());
        } catch (Exception e) {

        }
    }

    public void onOpenHistoryReceived(String data) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "openHistory");
            obj.put("data", data);

            webSocket.send(obj.toString());
        } catch (Exception e) {

        }
    }

    private void startPingTimer() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 10 * 1000);
    }

    private void retryConnect() {
        // exponential backoff or fixed delay
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("WS", "Reconnecting");
                        connect();
                    }
                },
                5000 // retry after 5s
        );
    }

    public interface MyWebSocketListener {
        void onShowSocketNotification(String type, String message);
    }
}
