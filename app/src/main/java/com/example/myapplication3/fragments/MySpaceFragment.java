package com.example.myapplication3.fragments;

import android.content.Intent;
import android.os.NetworkOnMainThreadException;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication3.BaseActivity;
import com.example.myapplication3.LoginActivity;
import com.example.myapplication3.R;
import com.example.myapplication3.adapter.PictureAdapter;
import com.example.myapplication3.entity.PictureEntity;
import com.example.myapplication3.entity.ResultResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


    public class MySpaceFragment extends BaseFragment {
        private RecyclerView recyclerView;
//        private SmartRefreshLayout refreshLayout;
        private PictureAdapter newsAdapter;
        private List<PictureEntity> datas = new ArrayList<>();
        private LinearLayoutManager linearLayoutManager;
        private String username;
        private String id;
        private int pageNum = 1;

        public MySpaceFragment() {

        }

        public static com.example.myapplication3.fragments.MySpaceFragment newInstance() {
            com.example.myapplication3.fragments.MySpaceFragment fragment = new com.example.myapplication3.fragments.MySpaceFragment();
            return fragment;
        }


        @Override
        protected int initLayout() {
            return R.layout.fragment_image;
        }

        @Override
        protected void initView() {
            recyclerView=mRootView.findViewById(R.id.recycleView);
//            refreshLayout=mRootView.findViewById(R.id.refreshLayout);
            username=GetStringFromSP("username");
            id = GetStringFromSP("id");
        }

        @Override
        protected void initData() {
            linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            newsAdapter = new PictureAdapter(getActivity());
            recyclerView.setAdapter(newsAdapter);

            getNewsList(true);
        }


        private void getNewsList(final boolean isRefresh) {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/myself?userId=" + id;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", BaseActivity.appID)
                    .add("appSecret", BaseActivity.appSecret)
                    .build();

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .get()
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("连接失败");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res= response.body().string();
                        getActivity().runOnUiThread(() -> {

                            ResultResponse resresponse = new Gson().fromJson(res, ResultResponse.class);
                            String data =(JSON.parseObject(res).getString("data"));
                            JSONArray records = null;
                            if (data != null) {

                                records = JSON.parseObject(data).getJSONArray("records");
                            }

                            System.out.println("***************************************");
                            System.out.println(res);
                            System.out.println(data);
                            System.out.println(records);
                            System.out.println("***************************************");

                            if(resresponse!=null&&resresponse.getCode()==401)
                            {
                                RemoveStringFromSP();
                                navgateToWithFlag(LoginActivity.class,
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            JSONObject jsonobj = JSON.parseObject(res);
                            if (resresponse != null && resresponse.getCode() == 200) {
                                List<PictureEntity> list = new ArrayList<PictureEntity>();
                                if (records != null) {
                                    for(int i=0;i<records.size();i++) {
                                        PictureEntity pe = new PictureEntity();
                                        pe.setId((String) records.getJSONObject(i).get("id"));
                                        pe.setContent((String) records.getJSONObject(i).get("content"));
                                        pe.setCreateTime((String) records.getJSONObject(i).get("createTime"));
                                        pe.setImageCode((String) records.getJSONObject(i).get("imageCode"));
                                        pe.setImageUrlList((String) records.getJSONObject(i).getJSONArray("imageUrlList").get(0));
                                        if (records.getJSONObject(i).get("likeNum") == null) {
                                            pe.setLikeNum("0");
                                        } else {
                                            System.out.println(records.getJSONObject(i).get("likeNum"));
                                            pe.setLikeNum(String.valueOf(records.getJSONObject(i).get("likeNum")));
                                        }
                                        pe.setTitle((String) records.getJSONObject(i).get("title"));
                                        pe.setpUserId((String) records.getJSONObject(i).get("pUserId"));
                                        pe.setUsername((String) records.getJSONObject(i).get("username"));
                                        pe.setHasLike((Boolean) records.getJSONObject(i).get("hasLike"));
                                        pe.setLikeId((String) records.getJSONObject(i).get("likeId"));
                                        pe.setCollectId((String) records.getJSONObject(i).get("collectId"));
                                        if (pe != null) {
                                            list.add(pe);
                                        }
                                        System.out.println("我是pe"+pe);
                                    }
                                }


                                if (list != null && list.size() > 0) {
                                    if (isRefresh) {
                                        datas = list;
                                    } else {
                                        datas.addAll(list);
                                    }
                                    newsAdapter.setDatas(datas);
                                    newsAdapter.notifyDataSetChanged();

                                }
                                else
                                {
                                    if (isRefresh) {
                                        ShowToast("暂时无数据");
                                    } else {
                                        ShowToast("没有更多数据");
                                    }
                                }
                            } else {
                                ShowToast("暂时无数据");
                            }
                        });
                    }
                });
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }

        }


    }
