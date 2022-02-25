package com.iyuba.ieltslistening.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.fragment.HomeFragment;
import com.iyuba.ieltslistening.fragment.MeFragment;
import com.iyuba.ieltslistening.utils.SharedPreferencesUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private long touchTime = 0;  // 在首页按退出的时间
    private TextView tvHome;
    private TextView tvMe;
    private HomeFragment homeFragment;
    private MeFragment meFragment;
    private FragmentManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBySP();
        bindView();
        tvHome.performClick();
    }

    // 从SharedPreferences中获取初始化需要的数据
    private void initBySP() {
        manager = getSupportFragmentManager();
        // APP是否初始化过
        boolean initAPP = SharedPreferencesUtils.getBoolean(this, SharedPreferencesUtils.APP_INFO, "initAPP");
        if (!initAPP) {
            initDB();
            startDialog();
        }
    }

    // 第一次打开，初始化数据库
    private void initDB() {

    }

    // 绑定各组件
    private void bindView() {
        tvHome = findViewById(R.id.tab_home);
        tvMe = findViewById(R.id.tab_my);

        tvHome.setOnClickListener(this);
        tvMe.setOnClickListener(this);
    }

    // 第一次打开弹出隐私政策授权框
    private void startDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_initmate);
            window.setGravity(Gravity.CENTER);

            TextView tvContent = window.findViewById(R.id.tv_content);
            TextView tvCancel = window.findViewById(R.id.tv_cancel);
            TextView tvAgree = window.findViewById(R.id.tv_agree);
            String str = "    感谢您对本公司的支持!本公司非常重视您的个人信息和隐私保护。" +
                    "为了更好地保障您的个人权益，在您使用我们的产品前，" +
                    "请务必审慎阅读《隐私政策》和《用户协议》内的所有条款，" +
                    "尤其是:\n" +
                    " 1.我们对您的个人信息的收集/保存/使用/对外提供/保护等规则条款，以及您的用户权利等条款;\n" +
                    " 2.约定我们的限制责任、免责条款;\n" +
                    " 3.其他以颜色或加粗进行标识的重要条款。\n" +
                    "您点击“同意并继续”的行为即表示您已阅读完毕并同意以上协议的全部内容。" +
                    "如您同意以上协议内容，请点击“同意”，开始使用我们的产品和服务!";

            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(str);
            final int start = str.indexOf("《");//第一个出现的位置
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Uri uri2 = Uri.parse("https://ai.iyuba.cn/api/protocolpri.jsp?apptype=雅思听力&company=1");
                    Intent intent2 = new Intent();
                    intent2.setAction("android.intent.action.VIEW");
                    intent2.setData(uri2);
                    startActivity(intent2);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.primary));
                    ds.setUnderlineText(false);
                }
            }, start, start + 6, 0);

            int end = str.lastIndexOf("《");
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Uri uri = Uri.parse("https://ai.iyuba.cn/api/protocoluse.jsp?apptype=雅思听力&company=1");
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(uri);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(getResources().getColor(R.color.primary));
                    ds.setUnderlineText(false);
                }
            }, end, end + 6, 0);

            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
            tvContent.setText(ssb, TextView.BufferType.SPANNABLE);

            // 不同意用户协议和隐私政策
            tvCancel.setOnClickListener(v -> {
                SharedPreferencesUtils.setBoolean(this, SharedPreferencesUtils.APP_INFO, "initAPP", false);
                alertDialog.cancel();
                finish();
            });

            // 同意用户协议和隐私政策
            tvAgree.setOnClickListener(v -> {
                // 初始化MobSDK
                // MobSDK.submitPolicyGrantResult(true, null);
                // 初始化友盟SDK (需用户同意隐私政策后调用)
                // UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
                SharedPreferencesUtils.setBoolean(this, SharedPreferencesUtils.APP_INFO, "initAPP", true);
                // 请求相关权限
                requestPermission();
                alertDialog.cancel();
            });
        }
    }

    // 请求权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // 请求相关权限
    private void requestPermission() {
        ArrayList<String> permissionsList = new ArrayList<>();

        String[] permissions = {//在这里加入你要使用的权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入这里代表没有权限.
                }
            }
            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(strings), 0);
            }
        }
    }

    // 重写回退方法，两秒内连续退两次才能退出，防止误退
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        long waitTime = 2000;
        if((currentTime - touchTime) >= waitTime) {
            //让Toast的显示时间和等待时间相同
            Toast.makeText(this, "再按一次退出", (int) waitTime).show();
            touchTime = currentTime;
        }else {
            // onKillProcess(this);  // Mob的注销方法
            System.exit(0);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fTransaction = manager.beginTransaction();
        setSelected();
        hideAllFragment(fTransaction);
        int id = v.getId();
        if (id == R.id.tab_home) {
            this.setTitle("雅思听力");
            tvHome.setSelected(true);
            if(homeFragment == null){
                homeFragment = new HomeFragment(this);
                fTransaction.add(R.id.ly_main, homeFragment);
            }else{
                fTransaction.show(homeFragment);
            }
        }else {
            this.setTitle("我的");
            tvMe.setSelected(true);
            if(meFragment == null){
                meFragment = new MeFragment();
                fTransaction.add(R.id.ly_main, meFragment);
            }else{
                fTransaction.show(meFragment);
            }
        }
        fTransaction.commit();
    }

    // 把底部tab_bar全部设置为未选中
    private void setSelected(){
        tvHome.setSelected(false);
        tvMe.setSelected(false);
    }

    //隐藏所有Fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(homeFragment != null)fragmentTransaction.hide(homeFragment);
        if(meFragment != null)fragmentTransaction.hide(meFragment);
    }


}