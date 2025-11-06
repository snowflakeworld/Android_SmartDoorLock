package com.smart.smartdoor.utils;

public class TestFlag {
    public static boolean isEmulator = true;
    public static String urlAddress = isEmulator ? "http://192.168.131.108:8080" : "http://localhost:8080";
    public static String urlSocketAddress = isEmulator ? "ws://192.168.131.108:8080" : "ws://localhost:8080";
}
