package com.android.wx.authorize.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import com.android.wx.authorize.constans.WXConstant;
import com.android.wx.authorize.utils.OkHttpUtils;
import com.android.wx.authorize.utils.WeiXinUserAuthorize;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TinyHung@outlook.com
 * 2019/4/26
 * WX Authorize Intent
 */

public class WeiXinEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final String TAG = "WeiXinEntryActivity";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if(null!=getSupportActionBar()){
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //将收到的intent传递给handleIntent方法，处理结果
        IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, WeiXinUserAuthorize.getInstance().getAppKey(), false);
        iwxapi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG,"onReq:"+baseReq.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.d(TAG,"baseResp:"+baseResp.toString()+",baseResp.errCode:"+baseResp.errCode);
        //授权回调
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) baseResp).code;
                //获取用户信息
                getAccessToken(code);
                break;
            //用户拒绝授权
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                WeiXinUserAuthorize.getInstance().failure(WXConstant.RESULT_REJECT,baseResp.errStr);
                break;
            //用户取消
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finish();
                WeiXinUserAuthorize.getInstance().failure(WXConstant.RESULT_CANCEL,baseResp.errStr);
                break;
            default:
                finish();
                WeiXinUserAuthorize.getInstance().failure(WXConstant.RESULT_UNKNOWN,baseResp.errStr);
                break;
        }
    }

    /**
     * 获取AccessToken
     * @param code
     */
    private void getAccessToken(String code) {
        //获取授权
        StringBuffer loginUrl = new StringBuffer();
        loginUrl.append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=")
                .append(WeiXinUserAuthorize.getInstance().getAppKey())
                .append("&secret=")
                .append(WeiXinUserAuthorize.getInstance().getAppSecret())
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");

        OkHttpUtils.ResultCallback<String> resultCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                /**
                 * 正确的返回数据示例
                 {
                 "access_token": "xxxxxxxxxx",
                 "expires_in": 7200,
                 "openid": "xxxxxxxxxx",
                 "refresh_token": "20_l-sMqgHbXQM65vSi4I5RDJZtiJpbnujfJHB45iYzFAp44bFxzPusfkPiKfnObXuIIiS-0U8U-ONvunyxAY-WLnazWaCvF4bhYk-_K5A9Llg",
                 "scope": "snsapi_userinfo",
                 "unionid": "xxxxxxxxxx"
                 }
                 */
                if(!TextUtils.isEmpty(response)){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        getUserInfo(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        finish();
                        WeiXinUserAuthorize.getInstance().failure(-1,"授权失败,微信返回数据解析错误"+e.getMessage());
                    } catch (RuntimeException e){
                        e.printStackTrace();
                        finish();
                        WeiXinUserAuthorize.getInstance().failure(-1,"授权失败,微信返回数据解析错误"+e.getMessage());
                    }
                }else{
                    finish();
                    WeiXinUserAuthorize.getInstance().failure(-1,"授权失败,微信返回数据为空");
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                finish();
                WeiXinUserAuthorize.getInstance().failure(-1,"授权失败,获取access_token失败"+e.getMessage());
            }
        };
        OkHttpUtils.get(loginUrl.toString(), resultCallback);
    }

    /**
     * 获取用户信息
     * @param jsonObject
     */
    private void getUserInfo(JSONObject jsonObject) {
        try {
            String accessToken = jsonObject.getString("access_token");
            String openId = jsonObject.getString("openid");
            //获取个人信息
            String getUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId;
            OkHttpUtils.ResultCallback<String> reCallback = new OkHttpUtils.ResultCallback<String>() {
                @Override
                public void onSuccess(String responses) {
                    if(!TextUtils.isEmpty(responses)){
                        try {
                            JSONObject jsonObject = new JSONObject(responses);
                            //
                            /**
                             * 获取用户信息参考
                             String openid = jsonObject.getString("openid");
                             String nickName = jsonObject.getString("nickname");
                             String sex = jsonObject.getString("sex");
                             String city = jsonObject.getString("city");
                             String province = jsonObject.getString("province");
                             String country = jsonObject.getString("country");
                             String headimgurl = jsonObject.getString("headimgurl");
                             String unionid = jsonObject.getString("unionid");
                             */
                            finish();
                            WeiXinUserAuthorize.getInstance().success(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            finish();
                            WeiXinUserAuthorize.getInstance().failure(-1,"解析用户信息失败,"+e.getMessage());
                        }catch (RuntimeException e){
                            e.printStackTrace();
                            finish();
                            WeiXinUserAuthorize.getInstance().failure(-1,"解析用户信息失败,"+e.getMessage());
                        }
                    }else{
                        finish();
                        WeiXinUserAuthorize.getInstance().failure(-1,"获取用户信息为空");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                    WeiXinUserAuthorize.getInstance().failure(-1,"获取用户信息失败,"+e.getMessage());
                    finish();
                }
            };
            OkHttpUtils.get(getUserInfo, reCallback);
        } catch (JSONException e) {
            e.printStackTrace();
            finish();
            WeiXinUserAuthorize.getInstance().failure(-1,"获取access_token失败,"+e.getMessage());
        }catch (RuntimeException e){
            e.printStackTrace();
            finish();
            WeiXinUserAuthorize.getInstance().failure(-1,"获取access_token失败,"+e.getMessage());
        }
    }
}