package com.example.myapplication3.Utils;

import com.alibaba.fastjson.JSONObject;

public class StringUtils {
    public static boolean IsEmpty(String str)
    {
        if(str==null||str.length()<=0)
        {
            return true;
        }
        else {
            return false;
        }
    }

    public static String getValue(String res,String myKey) {
        JSONObject jsonobject = JSONObject.parseObject(res);
        JSONObject key = jsonobject.getJSONObject("data");
        String value = key.getString(myKey);
        return value;
    }

    public static void inputStr(String s1,String s2) {
        System.out.println(s1+"是:"+s2);
    }
}
