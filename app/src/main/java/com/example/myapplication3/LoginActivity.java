package com.example.myapplication3;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

public class LoginActivity extends BaseActivity {
    private Button login;
    private EditText et_username;
    private EditText et_password;
    private Button register;
    private String username;
    private String password;
    private CheckBox remember;
    private ImageView pwd_switch;
    private Boolean bpwd_switch = false;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private final Gson gson = new Gson();

    @Override
    protected int initLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        login = findViewById(R.id.login);
        et_username = findViewById(R.id.username);
        et_password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        pwd_switch = findViewById(R.id.pwd_switch);
        remember = findViewById(R.id.remember);
    }

    @Override
    protected void initData() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et_username.getText().toString().trim();
                password = et_password.getText().toString().trim();
                Login(username,password);
            }
        });
        register.setOnClickListener(view -> {
            navgateTo(RegisterActivity.class);
        });

        //密码可见性切换
        pwd_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bpwd_switch = !bpwd_switch;
                if (bpwd_switch) {
                    pwd_switch.setImageResource(
                            R.drawable.ic_baseline_visibility_24);
                    et_password.setInputType(
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    pwd_switch.setImageResource(
                            R.drawable.ic_baseline_visibility_off_24);
                    et_password.setInputType(
                            InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                    InputType.TYPE_CLASS_TEXT);
                    et_password.setTypeface(Typeface.DEFAULT);
                }
            }
        });

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isremember = pref.getBoolean("remember", false);
        if (isremember) {
            et_username.setText(pref.getString("username", ""));
            et_password.setText(pref.getString("password", ""));
            remember.setChecked(true);
        }
    }

    public void Login(String username, String password) {
        if (StringUtils.IsEmpty(username)) {
            ShowToast("请输入账号！");
            return;
        } else if (StringUtils.IsEmpty(password)) {
            ShowToast("请输入密码！");
            return;
        }
        post(username,password);
    }

    private void post(String username, String password) {
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/user/login?password=" + password + "&username=" + username;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", appID)
                    .add("appSecret", appSecret)
                    .add("Content-Type", "application/json")
                    .build();


            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("password", password);
            bodyMap.put("username", username);
            // 将Map转换为字符串类型加入请求体中
            String body = gson.toJson(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();

            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback callback = new Callback() {
        //请求错误回调方法
        @Override
        public void onFailure(Call call, IOException e) {
            System.out.println("连接失败");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() == 200) {   //返回状态码正确才有效
                String res = response.body().string();
                Gson gson = new Gson();
                LoginResponse loginResponse = gson.fromJson(res, LoginResponse.class);//解析json
                if (loginResponse.getCode() == 200) {
                    String id = StringUtils.getValue(res,"id");
                    SaveToSP("username", username);
                    SaveToSP("id",id);
                    navgateTo(HomeActivity.class);
                    ShowToastAsyn("登陆成功！");
                    finish();
                } else {
                    ShowToastAsyn("账号或者密码错误！");
                }

            }
        }
    };
}