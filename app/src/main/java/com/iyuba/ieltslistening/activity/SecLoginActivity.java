package com.iyuba.ieltslistening.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.utils.ToastUtil;
import com.mob.secverify.GetTokenCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.VerifyCallback;
import com.mob.secverify.common.exception.VerifyException;
import com.mob.secverify.datatype.VerifyResult;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// 秒验登录界面
public class SecLoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "SecLoginActivity";
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_login);
        setMyActionBar();

        checkBox = findViewById(R.id.cb_obey);
        findViewById(R.id.sec_login_btn).setOnClickListener(this);
        findViewById(R.id.to_pwd_login).setOnClickListener(this);

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
            backIv.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.to_pwd_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else if (id == R.id.bar_back) {
            onBackPressed();
        }else if (id == R.id.sec_login_btn) {
            if (checkBox.isChecked()) {
                doSecLogin();
            }else {
                ToastUtil.showToast(this, "请先阅读并同意使用条款和隐私协议");
            }
        }
    }

    // 调秒验接口
    private void doSecLogin() {
        SecVerify.verify(new VerifyCallback() {
            @Override
            public void onOtherLogin() {
                // 用户点击“其他登录方式”，处理自己的逻辑
            }

            @Override
            public void onUserCanceled() {
                // 用户点击“关闭按钮”或“物理返回键”取消登录，处理自己的逻辑
            }

            @Override
            public void onComplete(VerifyResult verifyResult) {
                // 获取授权码成功，将token信息传给应用服务端，再由应用服务端进行登录验证，此功能需由开发者自行实现
                //这里使用data.getOpToken()把获取到的token搭配服务端API进行验证
                // Log.d(TAG, "onFailure:  --------- 秒验成功 " + verifyResult.getOpToken());
                OkHttpClient client = new OkHttpClient();
                String url = "http://api.iyuba.com.cn/v2/api.iyuba?protocol=10010";
                RequestBody body = new FormBody.Builder()
                        .add("appkey", "1eb7ac760ef35")
                        .add("token", verifyResult.getToken())
                        .add("opToken", verifyResult.getOpToken())
                        .add("operator", verifyResult.getOperator())
                        .add("appId", getResources().getString(R.string.appid))
                        .build();
                Request request = new Request.Builder().post(body).url(url).build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d(TAG, "onFailure:  ----------> " + e.toString());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        JSONObject jo = JSONObject.parseObject(response.body().string());
                        Log.d(TAG, "onResponse: -------秒验 iyuba服务器返回 " + jo.toString());
                    }
                });
            }

            @Override
            public void onFailure(VerifyException e) {
                Log.d(TAG, "onFailure:  --------- 秒验失败 " + e.toString());
                //TODO处理失败的结果
            }
        });
    }

}