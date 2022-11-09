package com.example.myapplication3.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication3.LoginActivity;
import com.example.myapplication3.MyLike;
import com.example.myapplication3.MySpace;
import com.example.myapplication3.R;
import com.example.myapplication3.upload.PictureActivity;


public class MyFragment extends BaseFragment {

    private RelativeLayout upload;
    private RelativeLayout logout;
    private RelativeLayout my_space;
    private RelativeLayout my_like;
    private String username;
    private String UID;
    private TextView et_username;
    private TextView et_UID;
    private TextView account;

    public static MyFragment newInstance() {
        return new MyFragment();
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView() {
        upload = mRootView.findViewById(R.id.rl_upload);
        logout=mRootView.findViewById(R.id.rl_logout);
        my_space = mRootView.findViewById(R.id.rl_my);
        my_like = mRootView.findViewById(R.id.rl_like);
        et_username=mRootView.findViewById(R.id.username);
        username=GetStringFromSP("username");
        UID=GetStringFromSP("id");
        et_UID = mRootView.findViewById(R.id.UID);
        account = mRootView.findViewById(R.id.account);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {

        upload.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            navgateTo(PictureActivity.class);
                                        }
                                    }
        );
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveStringFromSP();
                navgateToWithFlag(LoginActivity.class,
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        });
        my_space.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            navgateTo(MySpace.class);
                                        }
                                    }
        );
        my_like.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            navgateTo(MyLike.class);
                                        }
                                    }
        );
        et_username.setText(username);
        account.setText(username + "的主页");
        et_UID.setText("UID:"+UID);
    }
}