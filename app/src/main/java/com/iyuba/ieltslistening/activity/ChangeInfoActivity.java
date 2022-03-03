package com.iyuba.ieltslistening.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.utils.Bitmap2File;
import com.iyuba.ieltslistening.utils.ConstUtil;
import com.iyuba.ieltslistening.utils.MD5Util;
import com.iyuba.ieltslistening.utils.SharedPreferencesUtils;
import com.iyuba.ieltslistening.utils.ToastUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import cn.smssdk.ui.companent.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ChangeInfoActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "ChangeInfoActivity";
    private static final int TAKE_PHOTO = 1;  // 照相
    private static final int CHOOSE_PHOTO = 2;  // 选相册图片
    private static final int CROP_PHOTO = 3;  // 裁剪图片
    private Dialog dialog;

    private Uri imgUri;  // 拍照照片的uri

    private final OkHttpClient client = new OkHttpClient();
    private CircleImageView curHead;
    private TextView curName;

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            if (what == ConstUtil.NET_ERROR) {
                ToastUtil.showToast(ChangeInfoActivity.this, "网络错误，请稍后重试");
                return true;
            }else if (what == ConstUtil.REQUEST_FINISH) {
                JSONObject jo = (JSONObject) msg.obj;
                if (msg.arg1 == 1) {
                    if (jo.containsKey("result") && "121".equals(jo.get("result"))) {
                        curName.setText(jo.getString("newName"));
                        SharedPreferencesUtils.setString(ChangeInfoActivity.this, SharedPreferencesUtils.USER_INFO, "nickname", jo.getString("newName"));
                        ToastUtil.showToast(ChangeInfoActivity.this, "修改成功");
                    }else
                        ToastUtil.showToast(ChangeInfoActivity.this, "用户名已占用！");
                }else if (msg.arg1 == 2) {
                    if (jo.containsKey("status") && 0 == jo.getInteger("status")) {
                        SharedPreferencesUtils.setString(ChangeInfoActivity.this, SharedPreferencesUtils.USER_INFO, "head", jo.getString("middleUrl"));
                        Bitmap croppedHead = (Bitmap) jo.get("croppedHead");
                        Glide.with(ChangeInfoActivity.this).load(croppedHead).into(curHead);
                    }
                    dialog.dismiss();
                }
                return true;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);

        initActionBar();
        curHead = findViewById(R.id.cur_head);
        Glide.with(this).load(SharedPreferencesUtils.getString(this, SharedPreferencesUtils.USER_INFO, "head")).into(curHead);
        curName = findViewById(R.id.cur_nickname);
        curName.setText(SharedPreferencesUtils.getString(this, SharedPreferencesUtils.USER_INFO, "nickname"));

        findViewById(R.id.ly_change_head).setOnClickListener(this);
        findViewById(R.id.ly_change_name).setOnClickListener(this);
    }

    // 初始化ActionBar
    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.actionbar_content);  //绑定自定义的布局：actionbar_layout.xml
            TextView barText = actionBar.getCustomView().findViewById(R.id.bar_text);
            ImageView backIv = actionBar.getCustomView().findViewById(R.id.bar_back);
            TextView moreIv = actionBar.getCustomView().findViewById(R.id.bar_more);
            moreIv.setVisibility(View.INVISIBLE);
            barText.setText("个人信息");
            backIv.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.bar_back) {
            this.finish();
        }else if (id == R.id.bar_more) {
            Log.d(TAG, "onClick: click more.");
        }else if (id == R.id.ly_change_head) {
            showChangeHeadDialog();
        }else if (id == R.id.ly_change_name) {
            showChangeNameDialog();
        }
    }

    // 展示修改用户名弹窗
    private void showChangeNameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入您想修改的昵称");
        final View diaView = getLayoutInflater().inflate(R.layout.dialog_change_name, null);
        final EditText et_change_name = diaView.findViewById(R.id.et_change_name);
        builder.setView(diaView);
        builder.setPositiveButton("确认", (dialog, which) -> {
            String uid = SharedPreferencesUtils.getString(this, SharedPreferencesUtils.USER_INFO, "uid");
            String newName = et_change_name.getText().toString();
            // 获取sign
            String signStr = "10012" + uid + "iyubaV2";
            String sign = MD5Util.MD5(signStr);
            // 拼接url
            String url = "http://api.iyuba.com.cn/v2/api.iyuba?"
                    + "protocol=10012"
                    + "&username=" + URLEncoder.encode(newName)
                    + "&oldUsername=" + curName.getText().toString()
                    + "&uid=" + uid
                    + "&platform=android"
                    + "&appid=" + this.getResources().getString(R.string.appid)
                    + "&app=" + this.getResources().getString(R.string.app_enname)
                    + "&format=json"
                    + "&sign=" + sign;
            Request request = new Request.Builder().get().url(url).build();
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
                        Message msg = new Message();
                        jo.put("newName", et_change_name.getText().toString());
                        msg.what = ConstUtil.REQUEST_FINISH;
                        msg.arg1 = 1; // 1表示请求修改昵称的请求结果，2代表请求修改头像的
                        msg.obj = jo;
                        handler.sendMessage(msg);
                    }else {
                        Message msg = new Message();
                        msg.what = ConstUtil.NET_ERROR;
                        handler.sendMessage(msg);
                    }
                }
            });
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
        });
        builder.show();
    }

    // 展示修改头像弹窗
    private void showChangeHeadDialog(){
        View bottomView = View.inflate(this, R.layout.change_head, null);
        TextView mAlbum = bottomView.findViewById(R.id.tv_album);
        TextView mCamera = bottomView.findViewById(R.id.tv_camera);
        TextView mCancel = bottomView.findViewById(R.id.tv_cancel);

        PopupWindow pop = new PopupWindow(bottomView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 0.5f;
        this.getWindow().setAttributes(lp);
        pop.setOnDismissListener(() -> {
            WindowManager.LayoutParams lp1 = getWindow().getAttributes();
            lp1.alpha = 1f;
            getWindow().setAttributes(lp1);
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        View.OnClickListener clickListener = view -> {
            int id = view.getId();
            if (id == R.id.tv_album) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }else {
                    choosePicture();
                }
            }else if (id == R.id.tv_camera) {
                takePhoto();
            }
            pop.dismiss();
        };
        mCamera.setOnClickListener(clickListener);
        mAlbum.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }

    // 拉起照相
    private void takePhoto() {
        // 创建文件
        File outputImg = new File(getExternalCacheDir(), "output_img.jpg");
        try {
            if (outputImg.exists())
                outputImg.delete();
            outputImg.createNewFile();
        }catch (IOException e) {
            e.printStackTrace();
        }
        // 获取创建的文件的URI
        if (Build.VERSION.SDK_INT > 24)
            imgUri = FileProvider.getUriForFile(this, "com.iyuba.ieltslistening.fileprovider", outputImg);
        else
            imgUri = Uri.fromFile(outputImg);
        // 启动相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }


    // 调用系统的裁剪功能
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    cropPhoto(imgUri);
                }
                break;
            case CROP_PHOTO:
                Bundle extras = data.getExtras();
                Bitmap croppedHead = extras.getParcelable("data");
                showLoading();
                requestHead(croppedHead);
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri choosePic = data.getData();
                    // Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(choosePic));
                    cropPhoto(choosePic);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                choosePicture();
            }
        }
    }

    // 接口更改头像
    private void requestHead(Bitmap croppedHead){
        if (croppedHead != null) {
            String uid = SharedPreferencesUtils.getString(ChangeInfoActivity.this, SharedPreferencesUtils.USER_INFO, "uid");
            String url = "http://api.iyuba.com.cn/v2/avatar?uid=" + uid;
            RequestBody requestBody = null;
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("path", "head")
                    .addFormDataPart("platform", "android")
                    .addFormDataPart("appid", getResources().getString(R.string.appid))
                    .addFormDataPart("app", getResources().getString(R.string.app_enname))
                    .addFormDataPart("format", "json")
                    .addFormDataPart("head", "user_avatar.jpg", RequestBody.create(MediaType.parse("image/png;charset=utf-8"), Bitmap2File.getFile(croppedHead)))
                    .build();
            Request request = new Request.Builder().post(requestBody).url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Message msg = new Message();
                    msg.what = ConstUtil.NET_ERROR;
                    handler.sendMessage(msg);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Message msg = new Message();
                    if (response.isSuccessful()) {
                        JSONObject jo = JSONObject.parseObject(response.body().string());
                        Log.d(TAG, "onResponse:  ----------_> " + jo.toString());
                        msg.what = ConstUtil.REQUEST_FINISH;
                        msg.arg1 = 2;  // 1表示请求修改昵称的请求结果，2代表请求修改头像的
                        jo.put("croppedHead", croppedHead);
                        msg.obj = jo;
                    }else {
                        Log.d(TAG, "onResponse: =======>" + response.body().string());
                        msg.what = ConstUtil.NET_ERROR;
                    }
                    handler.sendMessage(msg);
                }
            });
        }
    }

    // 选择照片
    private void choosePicture() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    private void showLoading() {
        if (dialog == null) {
            View v = View.inflate(this, R.layout.dialog_waiting, null);
            TextView tvContent = v.findViewById(R.id.waiting_content);
            tvContent.setText("头像上传中");
            dialog = new Dialog(this, R.style.theme_dialog);
            dialog.setContentView(v);
            dialog.setCancelable(false);
        }
        dialog.show();
    }
}