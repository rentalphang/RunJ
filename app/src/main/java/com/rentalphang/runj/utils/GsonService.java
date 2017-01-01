package com.rentalphang.runj.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public class GsonService {

    private static final Pattern REG_UNICODE = Pattern.compile("[0-9A-Fa-f]{4}");

    public static <T> T parseJson(String jsonString, Class<T> clazz) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, clazz);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("解析json失败");
        }
        return t;

    }

    public static String unicode2String(String str) {
        StringBuilder unicodeStr = new StringBuilder();
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char unicodeChar1 = str.charAt(i);
            if (unicodeChar1 == '\\' && i < len - 1) {
                char unicodeChar2 = str.charAt(++i);
                if (unicodeChar2 == 'u' && i <= len - 5) {
                    String tmp = str.substring(i + 1, i + 5);
                    Matcher matcher = REG_UNICODE.matcher(tmp);
                    if (matcher.find()) {
                        unicodeStr.append((char) Integer.parseInt(tmp, 16));
                        i = i + 4;
                    } else {
                        unicodeStr.append(unicodeChar1).append(unicodeChar2);
                    }
                } else {
                    unicodeStr.append(unicodeChar1).append(unicodeChar2);
                }
            } else {
                unicodeStr.append(unicodeChar1);
            }
        }
        return unicodeStr.toString();
    }
}