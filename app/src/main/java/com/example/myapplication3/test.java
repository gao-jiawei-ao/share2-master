package com.example.myapplication3;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication3.Utils.StringUtils;
import com.example.myapplication3.entity.LoginResponse;
import com.example.myapplication3.upload.PathHelper;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class test extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE = 1;
    private final Gson gson = new Gson();
    private Intent intent;
    private String username;
    private String id;
    private Button upload;
    String photoPath = "";//要上传的图片路径
    private ImageButton add;
    private ImageButton back;
    public Context mContext;
    public EditText my_content;
    private String pic_content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);
        mContext=this;

        add = findViewById(R.id.add);
        add.setOnClickListener(this);
        username=GetStringFromSP("username");
        id = GetStringFromSP("id");
        upload=findViewById(R.id.upload);
        upload.setOnClickListener(this);
        my_content = findViewById(R.id.content);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.back:
//                startActivity(new Intent(getApplicationContext(), MyFragment.class));
                finish();
                break;

            case R.id.add:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, CHOOSE);
                break;

            case R.id.upload:
                post();
                break;

        }
    }

    private void post() {
        File file;
        pic_content = my_content.getText().toString();
        if(StringUtils.IsEmpty(pic_content))
        {
            ShowToast("请输入标题！");
            return;
        }
        upload.setEnabled(false);
        file = new File(photoPath);
        if (file.exists()) {


            final RequestBody requestBody= RequestBody.create(MediaType.parse("image/jpg"),file);
            // 请求头
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", BaseActivity.appID)
                    .add("appSecret", BaseActivity.appSecret)
                    .add("Content-Type", "multipart/form-data")
                    .build();

            final MultipartBody multipartBody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("fileList",file.getName(),requestBody)
                    .addFormDataPart("username",username)
                    .addFormDataPart("picturename",pic_content)
                    .build();
            OkHttpClient okHttpClient=new OkHttpClient.Builder().build();
            final Request request = new Request.Builder()
                    .url("http://47.107.52.7:88/member/photo/image/upload")//请求的url
                    .headers(headers)
                    .post(multipartBody)
                    .build();
            Call call = okHttpClient.newCall(request);
            //加入队列 异步操作
            call.enqueue(new Callback() {
                //请求错误回调方法
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    System.out.println("上传：连接服务器失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println(response);
                    if(response.code()==200) {
                        String res=response.body().string();
                        Gson gson=new Gson();
                        LoginResponse loginResponse=gson.fromJson(res,LoginResponse.class);
                        System.out.println(loginResponse);
                        System.out.println(res);
                        if(loginResponse.getCode()==200)
                        {
                            runOnUiThread(() -> {
                                String imageCode = StringUtils.getValue(res,"imageCode");
                                System.out.println(imageCode);
                                upload(imageCode);
                                finish();
                            });
                        }
                        else
                        {
                            runOnUiThread(() -> ShowToast("上传失败！code:"+loginResponse.getCode()));
                        }
                    }
                    else
                    {
                        runOnUiThread(() -> ShowToast("上传失败！"));
                        System.out.println(response);
                    }
                }
            });
        }
    }

    private void upload(String imgID) {

        String url = "http://47.107.52.7:88/member/photo/share/add";

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", BaseActivity.appID)
                .add("appSecret", BaseActivity.appSecret)
                .add("Content-Type", "application/json")
                .build();

        // 请求体
        // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("imageCode", imgID);
        bodyMap.put("id", imgID);
        bodyMap.put("pUserId", id);
        bodyMap.put("title", username+"发布");
        bodyMap.put("content", pic_content);
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
        }catch (NetworkOnMainThreadException ex){
            ex.printStackTrace();
        }
    }

    /**
     * 回调
     */
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
            System.out.println("上传：连接服务器失败");
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            System.out.println(response);
            if(response.code()==200) {
                String res=response.body().string();
                Gson gson=new Gson();
                LoginResponse loginResponse=gson.fromJson(res,LoginResponse.class);
                System.out.println(loginResponse);
                System.out.println(res);
                if(loginResponse.getCode()==200)
                {
                    runOnUiThread(() -> {
                        ShowToast("上传成功!");
                        finish();
                    });
                }
                else
                {
                    runOnUiThread(() -> ShowToast("上传失败！code:"+loginResponse.getCode()));
                }
            }
            else
            {
                runOnUiThread(() -> ShowToast("上传失败！"));
                System.out.println(response);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//requestcode用于鉴别哪一次跳转
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            // 选择图片库的图片
            case CHOOSE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();//系统选择图片界面返回的路径
                    photoPath = PathHelper.getRealPathFromUri(test.this, uri);//转换成绝对路径
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);//通过路径获取到图片
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //压缩图片
                    bitmap = scaleBitmap(bitmap, (float) 0.5);
                    //预览图片
                    ImageView image = findViewById(R.id.imageView);
                    image.setImageBitmap(bitmap);
                    upload.setEnabled(true);
                }
                break;
        }
    }

    //压缩图片
    public Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        return newBM;
    }

    protected String GetStringFromSP(String key)
    {
        SharedPreferences sp= getSharedPreferences("sp_sjj", MODE_PRIVATE);
        return sp.getString(key,"");
    }

    public void ShowToast(String msg)
    {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

}