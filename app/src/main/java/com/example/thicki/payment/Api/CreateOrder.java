package com.example.thicki.payment.Api;

import com.example.thicki.payment.Constant.AppInfo;
import com.example.thicki.payment.Helper.Helpers;
import android.util.Log;
import org.json.JSONObject;

import java.util.Date;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CreateOrder {
    private class CreateOrderData {
        String AppId;
        String AppUser;
        String AppTime;
        String Amount;
        String AppTransId;
        String EmbedData;
        String Items;
        String BankCode;
        String Description;
        String Mac;
        String CallbackUrl;

        private CreateOrderData(String amount, String appTransId) throws Exception {
            AppId = String.valueOf(AppInfo.APP_ID);
            AppUser = "Android_User";
            AppTime = String.valueOf(new Date().getTime());
            Amount = amount;
            // Sử dụng mã appTransId được truyền vào từ ViewModel
            AppTransId = appTransId; 
            
            EmbedData = "{}";
            Items = "[]";
            BankCode = "zalopayapp";
            Description = "Thanh toán lịch khám #" + AppTransId;
            CallbackUrl = "https://bud-acceptable-mealy.ngrok-free.dev/api/callback";

            String inputHMac = String.format("%s|%s|%s|%s|%s|%s|%s",
                    this.AppId, this.AppTransId, this.AppUser,
                    this.Amount, this.AppTime, this.EmbedData, this.Items);
            // Debug log
            Log.d("ZaloPay_CreateOrder", "=== CREATE ORDER REQUEST ===");
            Log.d("ZaloPay_CreateOrder", "AppId: " + this.AppId);
            Log.d("ZaloPay_CreateOrder", "AppTransId: " + this.AppTransId);
            Log.d("ZaloPay_CreateOrder", "AppUser: " + this.AppUser);
            Log.d("ZaloPay_CreateOrder", "Amount: " + this.Amount);
            Log.d("ZaloPay_CreateOrder", "AppTime: " + this.AppTime);
            Log.d("ZaloPay_CreateOrder", "EmbedData: " + this.EmbedData);
            Log.d("ZaloPay_CreateOrder", "Items: " + this.Items);
            Log.d("ZaloPay_CreateOrder", "Input HMAC: " + inputHMac);
            Mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac);
        }
    }

    // CẬP NHẬT: Hàm bây giờ nhận 2 tham số để khớp với DoctorViewModel
    public JSONObject createOrder(String amount, String appTransId) throws Exception {
        CreateOrderData input = new CreateOrderData(amount, appTransId);

        RequestBody formBody = new FormBody.Builder()
                .add("appid", input.AppId)
                .add("appuser", input.AppUser)
                .add("apptime", input.AppTime)
                .add("amount", input.Amount)
                .add("apptransid", input.AppTransId)
                .add("embeddata", input.EmbedData)
                .add("item", input.Items)
                .add("bankcode", input.BankCode)
                .add("description", input.Description)
                .add("mac", input.Mac)
                .add("callbackurl", input.CallbackUrl)
                .build();

        return HttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody);
    }
}
