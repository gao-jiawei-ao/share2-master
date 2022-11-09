package com.example.myapplication3.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.BaseActivity;
import com.example.myapplication3.R;
import com.example.myapplication3.Utils.ResponseBody;
import com.example.myapplication3.entity.PictureEntity;
import com.example.myapplication3.entity.ResultResponse;
import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PictureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<PictureEntity> datas;
    private static final int SAVE_SUCCESS = 0;//保存图片成功
    private static final int SAVE_FAILURE = 1;//保存图片失败
    private static final int SAVE_BEGIN = 2;//开始保存图片
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SAVE_BEGIN:
                    ShowToast("开始保存，请稍后......");
                    break;
                case SAVE_SUCCESS:
                    ShowToast("图片保存成功,请到相册查找");
                    break;
                case SAVE_FAILURE:
                    ShowToast("图片保存失败,请稍后再试...");
                    break;
            }
        }
    };


    public void setDatas(List<PictureEntity> datas) {
        this.datas = datas;
    }

    public PictureAdapter(Context context) {
        this.mContext = context;

    }

    @NonNull
    @Override
    //创建item，展示图片
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_picture, parent, false);
        PictureHolder viewHolder = new PictureHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PictureHolder vh = (PictureHolder) holder;
        PictureEntity PictureEntity = datas.get(position);
        vh.pe = datas.get(position);
        vh.content.setText(vh.pe.getContent());
        vh.username = GetStringFromSP("username");
        vh.tv_username.setText(vh.pe.getUsername());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long times = Long.parseLong(vh.pe.getCreateTime());
        vh.tv_like.setText(String.valueOf(vh.pe.getLikeNum()));
        vh.tv_time.setText(format.format(times));

        boolean islike = vh.pe.isHasLike();   //从后端获取数据，是否点赞的状态
        //请求之后绑定的状态
        if (islike)//设置点赞的效果
        {
            vh.img_like.setImageResource(R.drawable.ic_like_press);
        } else {
            vh.img_like.setImageResource(R.drawable.ic_like);
        }
        vh.islikeflag = islike;
//        vh.username = GetStringFromSP("username");
        vh.Imageid = vh.pe.getId();
        vh.UID = GetStringFromSP("id");
        vh.pUserID = vh.pe.getpUserId();
        vh.likeid = vh.pe.getLikeId();
        vh.collectid = vh.pe.getCollectId();
        vh.ImageCode = vh.pe.getImageCode();
        vh.title = vh.pe.getTitle();
        vh.ImageUrl = vh.pe.getImageUrlList();


        System.out.println(GetStringFromSP("id")+"我在=====这和"+vh.pUserID);
        if (Objects.equals(GetStringFromSP("id"), vh.pUserID)) {
            //删除按钮
            vh.img_delete.setImageResource(R.drawable.ic_delete);
            vh.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // url路径
                    String url = "http://47.107.52.7:88/member/photo/share/delete?shareId=" + vh.Imageid + "&userId=" + vh.UID;

                    // 请求头
                    Headers headers = new Headers.Builder()
                            .add("appId",BaseActivity.appID)
                            .add("appSecret", BaseActivity.appSecret)
                            .add("Accept", "application/json, text/plain, */*")
                            .build();


                    MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

                    //请求组合创建
                    Request request = new Request.Builder()
                            .url(url)
                            // 将请求头加至请求中
                            .headers(headers)
                            .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                            .build();
                    try {
                        OkHttpClient client = new OkHttpClient();
                        //发起请求，传入callback进行回调
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Looper.prepare();
                                ShowToast("连接失败!");
                                Looper.loop();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                if (response.code() == 200) {
                                    String res = response.body().string();
                                    Gson gson = new Gson();
                                    ResultResponse resultResponse = gson.fromJson(res, ResultResponse.class);
                                    if (resultResponse.getCode() == 200) {
                                        Looper.prepare();
                                        ShowToast("删除图片成功!");
                                        Looper.loop();
                                    } else {
                                        Looper.prepare();
                                        ShowToast("删除图片失败!");
                                        Looper.loop();
                                    }

                                }

                            }
                        });
                    } catch (NetworkOnMainThreadException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        Picasso.with(mContext)
                .load(PictureEntity.getImageUrlList())
                .into(vh.image);


    }

    @Override
    public int getItemCount() {
        if (datas != null && datas.size() > 0) {
            return datas.size();   //返回item的值
        } else
            return 0;

    }

    public class PictureHolder extends RecyclerView.ViewHolder {

        private TextView content;
        private TextView tv_username;
        private TextView tv_like;
        private TextView tv_time;
        private String time;
        private TextView tv_share;
        private TextView tv_save;
        private ImageView img_header;
        public ImageView image;
        private PictureEntity pe;
        private ImageView img_like;
        private ImageView img_save;
        private ImageView img_share;
        private ImageView img_delete;
        private boolean islikeflag;
        private String username;
        private String Imageid;
        private String ImageCode;
        private String title;
        private String UID;
        private String pUserID;
        private String ImageUrl;
        private String likeid;
        private String collectid;

        public PictureHolder(@NonNull View view) {
            super(view);
            content = view.findViewById(R.id.content);
            tv_username = view.findViewById(R.id.username);
            tv_like = view.findViewById(R.id.tv_like);
//            tv_save = view.findViewById(R.id.tv_save);
//            tv_share = view.findViewById(R.id.tv_share);
            img_like = view.findViewById(R.id.img_like);
            img_save = view.findViewById(R.id.img_save);
            img_share = view.findViewById(R.id.img_share);
            img_delete = view.findViewById(R.id.img_delete);
            img_header = view.findViewById(R.id.img_header);
            tv_time = view.findViewById(R.id.time);
            image = view.findViewById(R.id.image);


            //点赞按钮
            img_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int likeNum = Integer.parseInt(tv_like.getText().toString().trim());
                    //前端中点击按钮的状态绑定
                    if (islikeflag) {
                        SubLike(likeid);
                        System.out.println(likeid + "*likeid*collectid" + collectid);
                        img_like.setImageResource(R.drawable.ic_like);
                        tv_like.setText(String.valueOf(--likeNum));
                    } else {
                        System.out.println("点赞开始");
                        AddLike(Imageid, UID);
                        System.out.println(Imageid + "*Imageid*UID" + UID);
                        img_like.setImageResource(R.drawable.ic_like_press);
                        tv_like.setText(String.valueOf(++likeNum));
                    }

                    islikeflag = !islikeflag;

                }
            });

            //下载按钮
            img_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    save();
                }

                private void save() {


                    // url路径
                    String url = "http://47.107.52.7:88/member/photo/share/save";

                    // 请求头
                    Headers headers = new Headers.Builder()
                            .add("appId", BaseActivity.appID)
                            .add("appSecret", BaseActivity.appSecret)
                            .add("Accept", "application/json, text/plain, */*")
                            .build();

                    // 请求体
                    // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
                    String content1 = String.valueOf(content);
                    String Imageid1 = String.valueOf(Imageid);
                    String ImageCode1 = String.valueOf(ImageCode);
                    String UID1 = String.valueOf(UID);
                    String title1 = String.valueOf(title);
                    Map<String, Object> bodyMap = new HashMap<>();
                    bodyMap.put("content", content1);
                    bodyMap.put("id", Imageid1);
                    bodyMap.put("imageCode", ImageCode1);
                    bodyMap.put("pUserId", UID1);
                    bodyMap.put("title", title1);
                    // 将Map转换为字符串类型加入请求体中
                    Gson gson = new Gson();
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
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                                if (response.code() == 200) {
                                    String res = response.body().string();
                                    Gson gson = new Gson();
                                    ResultResponse resultResponse = gson.fromJson(res, ResultResponse.class);
                                    if (resultResponse.getCode() == 200) {
                                        System.out.println("开始下载");
                                        System.out.println(resultResponse.getData());
                                        new Thread(new Runnable() {
                                            @RequiresApi(api = Build.VERSION_CODES.R)
                                            @Override
                                            public void run() {
                                                mHandler.obtainMessage(SAVE_BEGIN).sendToTarget();
                                                Bitmap bp = returnBitmap(ImageUrl);  //获取图片下载地址
                                                saveImageToPhotos(mContext, bp);
                                            }
                                        }).start();

                                    } else {
                                        Looper.prepare();
                                        ShowToast("下载图片失败!");
                                        Looper.loop();
                                    }

                                }
                            }
                        });
                    } catch (NetworkOnMainThreadException ex) {
                        ex.printStackTrace();
                    }

                }
            });

            //分享按钮
            img_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    // 获取系统剪贴板
                    ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (ImageUrl != null) {
                        ClipData mClipData = ClipData.newPlainText("Label", ImageUrl);
                        cm.setPrimaryClip(mClipData);
                        ShowToast("已复制链接到剪贴板上!");
                    } else {
                        ShowToast("分享失败!");
                    }

                }
            });



        }

    }

    public void AddLike(String imageid, String UID) {
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/like?shareId=" + imageid + "&userId=" + UID;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", BaseActivity.appID)
                    .add("appSecret", BaseActivity.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();


            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback1);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback callback1 = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<ResponseBody<Object>>() {
            }.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            Gson gson = new Gson();
            ResponseBody<Object> dataResponseBody = gson.fromJson(body, jsonType);
            Log.d("info", dataResponseBody.toString());
        }
    };

    public void SubLike(String likeid) {
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/like/cancel?likeId=" + likeid;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", BaseActivity.appID)
                    .add("appSecret", BaseActivity.appSecret)
                    .add("Accept", "application/json, text/plain, */*")
                    .build();


            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback1);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();

    }


    public void ShowToast(String msg) {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

    }

    protected String GetStringFromSP(String key) {
        SharedPreferences sp = mContext.getSharedPreferences("sp_sjj", MODE_PRIVATE);
        return sp.getString(key, "");
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void saveImageToPhotos(Context context, Bitmap bitmap) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Looper.prepare();
            ShowToast("保存成功");
            Looper.loop();
        }


        String fileName = System.currentTimeMillis() + ".jpg";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        OutputStream outputStream;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.obtainMessage(SAVE_FAILURE).sendToTarget();
            return;
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));
        mHandler.obtainMessage(SAVE_SUCCESS).sendToTarget();

    }

    public static Bitmap returnBitmap(String url) {
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(url);
            HttpURLConnection conn;
            conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}

