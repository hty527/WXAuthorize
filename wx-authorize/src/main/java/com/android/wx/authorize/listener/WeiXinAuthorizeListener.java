package com.android.wx.authorize.listener;

import org.json.JSONObject;

/**
 * Created by TinyHung@outlook.com
 * 2019/4/26
 * WX Authorize Listener
 */

public interface WeiXinAuthorizeListener {

    /**
     * 授权成功，只有授权成功+获取用户基本信息成功才被回调
     * 基本信息如下
     String openid = jsonObject.getString("openid");
     String nickName = jsonObject.getString("nickname");
     String sex = jsonObject.getString("sex");
     String city = jsonObject.getString("city");
     String province = jsonObject.getString("province");
     String country = jsonObject.getString("country");
     String headimgurl = jsonObject.getString("headimgurl");
     String unionid = jsonObject.getString("unionid");
     * @param jsonObject 包含openID和用户基本信息的Json体
     */
    void onSuccess(JSONObject jsonObject);

    /**
     * 授权失败
     * @param code 详见WXConstant常量定义
     * @param error 描述信息
     */
    void onFailure(int code, String error);
}
