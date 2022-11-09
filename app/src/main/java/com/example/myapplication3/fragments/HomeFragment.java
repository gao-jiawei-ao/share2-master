package com.example.myapplication3.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;

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
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;
    private PictureAdapter newsAdapter;
    private List<PictureEntity> datas = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private String username;
    private String id;
    private int pageNum = 1;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }


    @Override
    protected int initLayout() {
        return R.layout.fragment_image;
    }

    @Override
    protected void initView() {
        recyclerView=mRootView.findViewById(R.id.recycleView);
        refreshLayout=mRootView.findViewById(R.id.refreshLayout);
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
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshLayout.postDelayed(new FinishRunnable(), 5000);
                pageNum = 1;
                getNewsList(true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshLayout.postDelayed(new FinishRunnable(), 5000);
                pageNum++;
                getNewsList(false);
            }
        });
        getNewsList(true);
    }


    private void getNewsList(final boolean isRefresh) {
//        new Thread(() -> {



        OkHttpClient okHttpClient=new OkHttpClient.Builder().build();
        @SuppressLint("DefaultLocale") String url = String.format("http://47.107.52.7:88/member/photo/share?current=%d&size=%d&userId=%s",pageNum,10,id);

        // 请求头
        Headers headers = new Headers.Builder()
                .add("Accept", "application/json, text/plain, */*")
                .add("appId", BaseActivity.appID)
                .add("appSecret", BaseActivity.appSecret)
                .build();

        final Request request = new Request.Builder()
                .url(url)//请求的url
                .headers(headers)
                .get()
                .build();

        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                    String res= response.body().string();
                    getActivity().runOnUiThread(() -> {
                        if (isRefresh) {
                            refreshLayout.finishRefresh();
                        } else {
                            refreshLayout.finishLoadMore();
                        }
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
//        }).start();
    }

    class FinishRunnable implements Runnable
    {
        @Override
        public void run() {
            if (refreshLayout != null)
            {
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
            }
        }
    }
}