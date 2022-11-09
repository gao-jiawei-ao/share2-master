package com.example.myapplication3.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dueeeke.videoplayer.player.VideoViewManager;


public abstract class BaseFragment extends Fragment {
    protected View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRootView==null)
        {
            mRootView=inflater.inflate(initLayout(),container,false);
            initView();
        }

        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    protected abstract int initLayout();
    protected abstract void initView();
    protected abstract void initData();
    public void ShowToast(String msg)
    {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
    public void navgateTo(Class cls)
    {
        Intent it=new Intent(getActivity(),cls);
        startActivity(it);
    }
    public void navgateToWithBundle(Class cls,Bundle bundle)
    {
        Intent it=new Intent(getActivity(),cls);
        it.putExtras(bundle);
        startActivity(it);
    }
    public void navgateToWithFlag(Class cls,int flags)
    {
        Intent it=new Intent(getActivity(),cls);
        it.setFlags(flags);
        startActivity(it);
    }
    public void ShowToastAsyn(String msg)
    {
        Looper.prepare();
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
    protected void SaveToSP(String key,String val)
    {
        SharedPreferences sp= getActivity().getSharedPreferences("sp_sjj", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,val);
        editor.commit();
    }
    protected String GetStringFromSP(String key)
    {
        SharedPreferences sp= getActivity().getSharedPreferences("sp_sjj", MODE_PRIVATE);
        return sp.getString(key,"");
    }
    protected  void RemoveStringFromSP()
    {
        SharedPreferences sp= getActivity().getSharedPreferences("sp_sjj", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.clear();
        editor.commit();
    }
    protected VideoViewManager getVideoViewManager() {
        return VideoViewManager.instance();
    }
}
