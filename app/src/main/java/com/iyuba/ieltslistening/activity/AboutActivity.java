package com.iyuba.ieltslistening.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.utils.MD5Util;
import com.iyuba.ieltslistening.utils.SharedPreferencesUtils;
import com.iyuba.ieltslistening.utils.ToastUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AboutActivity extends BaseActivity {

    private static final String TAG = "AboutActivity";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setMyActionBar();

        findViewById(R.id.log_off).setOnClickListener(v -> {
            if (SharedPreferencesUtils.getBoolean(AboutActivity.this, SharedPreferencesUtils.USER_INFO, "isLogin"))
                showWarning();
            else
                ToastUtil.showToast(this, "您还未登录，请先登录");
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
            backIv.setOnClickListener(v -> AboutActivity.this.finish());
        }
    }

    private void showWarning() {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        final View diaView = getLayoutInflater().inflate(R.layout.dialog_logoff, null);
        builder.setView(diaView);
        builder.setPositiveButton("继续注销", (dialog, which) -> PressPwd());
        builder.setNegativeButton("取消", (dialog, which) -> {});
        builder.show();
    }

    private void PressPwd() {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入密码注销账号");
        final View diaView2 = getLayoutInflater().inflate(R.layout.dialog_logoff2, null);
        final EditText et = diaView2.findViewById(R.id.logout_pwd);
        builder.setView(diaView2);
        builder.setPositiveButton("确认注销", (dialog, which) -> {

            String nickName = SharedPreferencesUtils.getString(this, SharedPreferencesUtils.USER_INFO, "nickname");
            String pressPwd = et.getText().toString();
            clearUser(nickName, pressPwd);
            progressDialog = ProgressDialog.show(AboutActivity.this, "注销中...", "", true);
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
        });
        builder.show();
    }

    public void clearUser(String uname, String pwd) {
        Log.d(TAG, "---------------->uname、pwd: " + uname + "--" + pwd);
        String url = "http://api.iyuba.com.cn/v2/api.iyuba";
        String signStr = "11005" + uname + MD5Util.MD5(pwd) + "iyubaV2";
        String sign = MD5Util.MD5(signStr);
        RequestBody body = null;
        try {
            body = new FormBody.Builder()
                    .add("protocol", "11005")
                    .add("username", URLEncoder.encode(uname, "UTF-8"))
                    .add("password", MD5Util.MD5(pwd))
                    .add("format", "json")
                    .add("sign", sign)
                    .build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // client对象
        OkHttpClient client = new OkHttpClient.Builder().build();
        // 创建请求
        Request request = new Request.Builder()
                .url(url)
                .post(Objects.requireNonNull(body))
                .build();
        // 创建请求任务
        Call task = client.newCall(request);
        // 异步回调
        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> ToastUtil.showToast(AboutActivity.this, "网络错误，请稍后重试。"));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                JSONObject jo = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
                System.out.println("--------->注销结果" + jo.toString());
                if ("101".equals(jo.getString("result"))) {
                    progressDialog.dismiss();
                    SharedPreferencesUtils.setBoolean(AboutActivity.this, SharedPreferencesUtils.USER_INFO, "isLogin", false);
                    AboutActivity.this.finish();
                }else if ("103".equals(jo.getString("result"))){
                    progressDialog.dismiss();
                    runOnUiThread(() -> ToastUtil.showToast(AboutActivity.this, "密码输入错误"));
                }else {
                    runOnUiThread(() -> ToastUtil.showToast(AboutActivity.this, "服务忙，请稍后重试。"));
                }
            }
        });
    }
}