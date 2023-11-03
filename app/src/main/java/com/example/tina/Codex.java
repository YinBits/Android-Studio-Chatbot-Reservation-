package com.example.tina;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Base64;

public class Codex {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encode(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decode(String str) {
        return new String(Base64.getDecoder().decode(str));
    }

}
