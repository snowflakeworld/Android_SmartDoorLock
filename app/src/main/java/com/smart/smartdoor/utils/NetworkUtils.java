package com.smart.smartdoor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class NetworkUtils {
    public static final String baseUrl = TestFlag.urlAddress + "/smart_app";
    public static final String urlLogin = "login";
    public static final String urlCheckRegister = "check_register";
    public static final String urlRegister = "register";
    public static final String urlSearchDistrict = "search_district";
    public static final String urlGetBusinessList = "get_business_list";
    public static final String urlChangeUserinfo = "change_userinfo";
    public static final String urlRequestAdminAuth = "request_admin_auth";
    public static final String urlChangePassword = "change_password";
    public static final String urlCheckDoorStates = "check_door_states";
    public static final String urlOpenDoorRequest = "open_door_request";
    public static final String urlHistory = "history";
    public static final String urlFeedbackList = "message_list";
    public static final String urlFeedbackSend = "message_send";
    public static final String urlUserManageList = "user_manage_list";
    public static final String urlUserManageProcess = "user_manage_process";

    public static final String socketUrl = TestFlag.urlSocketAddress + "/smart_socket_ws";

    public static String post(String url, String json) throws IOException {
        return executeRequest("POST", url, json, null);
    }

    private static String executeRequest(String method, String urlString, String body, Map<String, String> headers, int... timeouts) throws IOException {
        int connectTimeout = Constants.CONNECT_TIMEOUT;
        int readTimeout = Constants.READ_TIMEOUT;
        if (timeouts.length > 0)
            connectTimeout = timeouts[0];
        if (timeouts.length > 1)
            readTimeout = timeouts[1];

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        if (body != null && (method.equals("POST") || method.equals("PUT"))) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            throw new IOException("GET request failed with response code: " + responseCode);
        }
    }
}
