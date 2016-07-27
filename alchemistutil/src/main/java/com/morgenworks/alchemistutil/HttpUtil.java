package com.morgenworks.alchemistutil;

/**
 * This is Created by wizard on 7/27/16.
 */
public class HttpUtil {
    public static final int HTTP_200 = 200;
    public static final int HTTP_201 = 201;

    public static boolean responseCodeIsOK(int code) {
        return code == HTTP_200 || code == HTTP_201;
    }
}
