package com.example.thicki.payment.Api;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpProvider {
    public static JSONObject sendPost(String URL, RequestBody formBody) {
        JSONObject data = new JSONObject();
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .callTimeout(10000, TimeUnit.MILLISECONDS)
                    .connectTimeout(10000, TimeUnit.MILLISECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(URL)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                Log.e("HttpProvider", "Error Response: " + response.message());
                return null;
            } else {
                String responseBody = response.body().string();
                data = new JSONObject(responseBody);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.e("HttpProvider", "Exception: " + e.getMessage());
        }

        return data;
    }
}
