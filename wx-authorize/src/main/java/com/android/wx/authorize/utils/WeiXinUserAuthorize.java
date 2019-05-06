package com.android.wx.authorize.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.android.wx.authorize.constans.WXConstant;
import com.android.wx.authorize.listener.WeiXinAuthorizeListener;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import org.json.JSONObject;

/**
 * Created by TinyHung@outlook.com
 * 2019/4/26
 * 微信授权
 */

public class WeiXinUserAuthorize {

    private static WeiXinUserAuthorize mInstance;
    private WeiXinAuthorizeListener mListener;
    private IWXAPI mIwxapi;
    private String mAppKey,mAppSecret;

    public static WeiXinUserAuthorize getInstance(){
        if(null==mInstance){
            synchronized (WeiXinUserAuthorize.class){
                mInstance = new WeiXinUserAuthorize();
            }
        }
        return mInstance;
    }

    /**
     * 开始授权
     * @param context 上下文
     * @param appKey 微信平台申请的KEY
     * @param appSecret 微信平台生成的Secret
     * @param startMarket 如果设备未安装微信客户端，是否前往系统应用市场安装微信客户端
     * @param listener 监听器
     */
    public void startAuthorize(Context context,String appKey, String appSecret, boolean startMarket,WeiXinAuthorizeListener listener){
        this.mListener=listener;
        if(TextUtils.isEmpty(appKey)||TextUtils.isEmpty(appSecret)){
            if(null!=mListener){
                mListener.onFailure(WXConstant.RESULT_FAILURE,"APP_ID和APP_SECRET为必传项");
            }
            return;
        }
        this.mAppKey=appKey;
        this.mAppSecret=appSecret;
        mIwxapi = WXAPIFactory.createWXAPI(context, appKey, true);
        mIwxapi.registerApp(appKey);
        if (!mIwxapi.isWXAppInstalled()) {
            //微信未安装
            if(null!=mListener){
                mListener.onFailure(WXConstant.RESULT_FAILURE,"未安装微信客户端");
            }
            if(startMarket){
                Uri uri = Uri.parse("market://details?id=" + WXConstant.WEIXIN_PACKAGE);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } else {
            final SendAuth.Req req = new SendAuth.Req();
            //请求事件
            req.scope= "snsapi_userinfo";
            //随便填
            req.state = "wechat_xinqu";
            boolean sendReq = mIwxapi.sendReq(req);
            if(!sendReq){
                if(null!=mListener){
                    mListener.onFailure(WXConstant.RESULT_FAILURE,"授权失败");
                }
            }
        }
    }

    /**
     * 这个方法在WXEntryActivity中被调用
     * @return app_key
     */
    public String getAppKey() {
        return mAppKey;
    }

    /**
     * 这个方法在WXEntryActivity中被调用
     * @return app_secret
     */
    public String getAppSecret() {
        return mAppSecret;
    }

    /**
     * 这个方法在WXEntryActivity中被调用
     * @param jsonObject 包含access_token 和 openid的json体
     */
    public void success(JSONObject jsonObject) {
        if(null!=mListener){
            mListener.onSuccess(jsonObject);
        }
    }

    /**
     * 这个方法在WXEntryActivity中被调用
     * @param code 错误码
     * @param msg 描述日志
     */
    public void failure(int code, String msg) {
        if(null!=mListener){
            mListener.onFailure(code,msg);
        }
    }
}