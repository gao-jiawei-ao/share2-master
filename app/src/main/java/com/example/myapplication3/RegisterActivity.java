package com.example.myapplication3;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myapplication3.Utils.StringUtils;
import com.example.myapplication3.entity.LoginResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    private Button register;
    private EditText et_username;
    private EditText et_password1;
    private EditText et_password2;
    private Boolean bpwd_switch1 = false;
    private Boolean bpwd_switch2 = false;
    private ImageView pwd_switch1;
    private ImageView pwd_switch2;
    private String username;
    private String password1;
    private String password2;
    private final Gson gson = new Gson();

    @Override
    protected int initLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        register = findViewById(R.id.register);
        et_username = findViewById(R.id.username);
        et_password1 = findViewById(R.id.password1);
        et_password2 = findViewById(R.id.password2);
        pwd_switch1 = findViewById(R.id.pwd_switch1);
        pwd_switch2 = findViewById(R.id.pwd_switch2);
    }

    @Override
    protected void initData() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et_username.getText().toString().trim();
                password1 = et_password1.getText().toString().trim();
                password2 = et_password2.getText().toString().trim();
                if (password1.equals(password2)) {
                    Register(username, password1, password2);
                } else
                    ShowToast("????????????????????????");
            }
        });
        pwd_switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bpwd_switch1 = !bpwd_switch1;
                if (bpwd_switch1) {
                    pwd_switch1.setImageResource(
                            R.drawable.ic_baseline_visibility_24);
                    et_password1.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    pwd_switch1.setImageResource(
                            R.drawable.ic_baseline_visibility_off_24);
                    et_password1.setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_CLASS_TEXT);
                    et_password1.setTypeface(Typeface.DEFAULT);
                }
            }
        });
        pwd_switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bpwd_switch2 = !bpwd_switch2;
                if (bpwd_switch2) {
                    pwd_switch2.setImageResource(
                            R.drawable.ic_baseline_visibility_24);
                    et_password2.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    pwd_switch2.setImageResource(
                            R.drawable.ic_baseline_visibility_off_24);
                    et_password2.setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_CLASS_TEXT);
                    et_password2.setTypeface(Typeface.DEFAULT);
                }
            }
        });
    }

    public void Register(String Account, String psw1, String psw2) {
        if (StringUtils.IsEmpty(Account)) {
            ShowToast("??????????????????");
            return;
        } else if (StringUtils.IsEmpty(psw1)) {
            ShowToast("??????????????????");
            return;
        } else if (StringUtils.IsEmpty(psw2)) {
            ShowToast("??????????????????");
            return;
        }

        post(Account, psw1);

    }

    private void post(String Account, String psw) {
        new Thread(() -> {

            // url??????
            String url = "http://47.107.52.7:88/member/photo/user/register";

            // ?????????
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", appID)
                    .add("appSecret", appSecret)
                    .add("Content-Type", "application/json")
                    .build();

            // ?????????
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("password", psw);
            bodyMap.put("username", Account);
            // ???Map??????????????????????????????????????????
            String body = gson.toJson(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //??????????????????
            Request request = new Request.Builder()
                    .url(url)
                    // ???????????????????????????
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //?????????????????????callback????????????
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * ??????
     */
    private final Callback callback = new Callback() {
        //????????????????????????
        @Override
        public void onFailure(Call call, IOException e) {
            System.out.println("????????????");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 200) {
                String res = response.body().string();
                Gson gson = new Gson();
                LoginResponse loginResponse = gson.fromJson(res, LoginResponse.class);
                if (loginResponse.getCode() == 200) {
                    navgateToWithFlag(LoginActivity.class,
                            Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ShowToastAsyn("???????????????");
                    navgateTo(LoginActivity.class);

                } else {
                    ShowToastAsyn("????????????????????????????????????");
                }

            } else {
                ShowToastAsyn("???????????????");
            }
        }
    };
}