package com.example.thicki.payment.Helper;

import android.annotation.SuppressLint;
import com.example.thicki.payment.Helper.HMac.HMacUtil;
import org.jetbrains.annotations.NotNull;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class Helpers {
    @NotNull
    @SuppressLint("DefaultLocale")
    public static String getAppTransId() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
        String dateString = format.format(new Date());
        // Tạo mã ID cực kỳ duy nhất: yyMMdd_miliGiây_SốNgẫuNhiên
        // Ví dụ: 240402_1712045678901_99
        long timestamp = System.currentTimeMillis();
        int random = new Random().nextInt(100);
        return String.format("%s_%d%d", dateString, timestamp, random);
    }

    @NotNull
    public static String getMac(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        return Objects.requireNonNull(HMacUtil.HMacHexStringEncode(HMacUtil.HMACSHA256, key, data));
    }
}
