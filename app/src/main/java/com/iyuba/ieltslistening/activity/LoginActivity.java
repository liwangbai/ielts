package com.iyuba.ieltslistening.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.utils.ActivityController;
import com.iyuba.ieltslistening.utils.ConstUtil;
import com.iyuba.ieltslistening.utils.MD5Util;
import com.iyuba.ieltslistening.utils.SharedPreferencesUtils;
import com.iyuba.ieltslistening.utils.ToastUtil;
import com.iyuba.ieltslistening.utils.Xml2Json;
import org.dom4j.DocumentException;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private final OkHttpClient client = new OkHttpClient();
    private long clickTime = 0;

    private final Handler handler = new Handler(msg -> {
        int what = msg.what;
        if (what == ConstUtil.NET_ERROR) {
            ToastUtil.showToast(LoginActivity.this, "网络错误，请稍后重试");
            return true;
        }else if (what == ConstUtil.PWD_ERROR) {
            ToastUtil.showToast(LoginActivity.this, "账号或密码错误");
            return true;
        }else if (what == ConstUtil.REQUEST_FINISH) {
            ToastUtil.showToast(LoginActivity.this, "登录成功");
            return true;
        }
        // 返回false表示继续寻找下一个handler处理，true则截断
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: ");
        setMyActionBar();
        EditText account = findViewById(R.id.account);
        EditText password = findViewById(R.id.pwd);

        findViewById(R.id.forget_pwd).setOnClickListener(v -> {
            Uri uri = Uri.parse("http://m.iyuba.cn/m_login/inputPhonefp.jsp");
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(uri);
            startActivity(intent);
        });

        findViewById(R.id.login_btn).setOnClickListener(v -> {
            String accountStr = account.getText().toString();
            String pwdStr = password.getText().toString();
            doLogin(accountStr, pwdStr);
        });
    }

    private void setMyActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.actionbar_content);  //绑定自定义的布局：actionbar_layout.xml
            TextView barText = actionBar.getCustomView().findViewById(R.id.bar_text);
            ImageView backIv = actionBar.getCustomView().findViewById(R.id.bar_back);
            TextView barIndex = actionBar.getCustomView().findViewById(R.id.bar_more);
            barIndex.setText("");
            barText.setText("登录");
            backIv.setOnClickListener(v -> LoginActivity.this.finish());
        }
    }

    private void doLogin(String account, String pwd) {
        if (account.equals("") || pwd.equals("")){
            ToastUtil.showToast(this, "账号或密码不能为空");
        }else{
            if((System.currentTimeMillis() - clickTime) >= 1000) {
                //让Toast的显示时间和等待时间相同
                clickTime = System.currentTimeMillis();
                // 获取调用接口的sign
                StringBuilder str = new StringBuilder();
                String md5_pwd = MD5Util.MD5(pwd);
                str.append("11001").append(account).append(md5_pwd).append("iyubaV2");
                String sign = MD5Util.MD5(str.toString());
                // 拼接接口url
                // 异步调用
                String urlStr = "http://api.iyuba.com.cn/v2/api.iyuba?" +
                        "protocol=11001" +
                        "&username=" + account +
                        "&password=" + md5_pwd +
                        "&x=0&y=0" +
                        "&appid=241" +
                        "&sign=" + sign +
                        "&format=xml";
                Request request = new Request.Builder().url(urlStr).get().build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Message msg = new Message();
                        msg.what = ConstUtil.NET_ERROR;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jo = Xml2Json.xml2Json(response.body().string());
                                if (jo.get("message").toString().equals("success")){
                                    // 登录成功之后，调用20001接口获取用户更多信息
                                    getMoreInfo(jo.get("username").toString(), jo.get("imgSrc").toString(), jo.get("uid").toString(), jo.get("expireTime").toString(), jo.getInteger("Amount"));
                                }else {
                                    Message msg = new Message();
                                    msg.what = ConstUtil.PWD_ERROR;
                                    handler.sendMessage(msg);
                                }
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Message msg = new Message();
                            msg.what = ConstUtil.NET_ERROR;
                            handler.sendMessage(msg);
                        }
                    }
                });
            }
        }
    }

    // 根据用户uid调用20001接口获取更多信息
    public void getMoreInfo(String nickName, String head, String uid, String endTime, int iYuBi) {
        String signStr = "20001" + uid + "iyubaV2";
        String sign = MD5Util.MD5(signStr);
        String url = "http://api.iyuba.com.cn/v2/api.iyuba?" +
                "platform=" + "android" +
                "&format=" + "json" +
                "&protocol=" + "20001" +
                "&id=" + uid +
                "&myid=" + uid +
                "&appid=" + "241" +
                "&sign=" + sign;

        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message msg = new Message();
                msg.what = ConstUtil.NET_ERROR;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jo = JSONObject.parseObject(response.body().string());
                    // 登录成功，保存信息，返回首页
                    saveUserInfo(nickName, head, uid, endTime, iYuBi, jo.getInteger("icoins"), jo.getInteger("vipStatus"));
                }else {
                    Message msg = new Message();
                    msg.what = ConstUtil.NET_ERROR;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    @SuppressLint("ApplySharedPref")
    public void saveUserInfo(String userName, String head, String uid, String endTime, int iYuBi, int jiFen, int vipStatus){

        Message msg = new Message();
        msg.what = ConstUtil.REQUEST_FINISH;
        handler.sendMessage(msg);

        SharedPreferencesUtils.setBoolean(this, SharedPreferencesUtils.USER_INFO, "isLogin", true);
        SharedPreferencesUtils.setBoolean(this, SharedPreferencesUtils.USER_INFO, "isVip", System.currentTimeMillis() <= Long.parseLong(endTime + "000"));
        SharedPreferencesUtils.setString(this, SharedPreferencesUtils.USER_INFO, "nickname", userName);
        SharedPreferencesUtils.setString(this, SharedPreferencesUtils.USER_INFO, "head", head);
        SharedPreferencesUtils.setString(this, SharedPreferencesUtils.USER_INFO, "uid", uid);
        SharedPreferencesUtils.setString(this, SharedPreferencesUtils.USER_INFO, "vip_time", endTime);
        SharedPreferencesUtils.setInt(this, SharedPreferencesUtils.USER_INFO, "iYuBi", iYuBi);
        SharedPreferencesUtils.setInt(this, SharedPreferencesUtils.USER_INFO, "jiFen", jiFen);
        SharedPreferencesUtils.setInt(this, SharedPreferencesUtils.USER_INFO, "vipStatus", vipStatus);

        ActivityController.finishSome(2);
    }

}