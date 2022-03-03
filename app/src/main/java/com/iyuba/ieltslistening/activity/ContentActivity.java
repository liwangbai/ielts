package com.iyuba.ieltslistening.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.adapter.SentenceAdapter;
import com.iyuba.ieltslistening.pojo.Sentence;
import com.iyuba.ieltslistening.utils.ConstUtil;
import com.iyuba.ieltslistening.utils.TimeUtils;
import com.iyuba.ieltslistening.utils.ToastUtil;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContentActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ContentActivity";
    private long sectionId;
    private String sound;
    private MediaPlayer player;
    private TextView tvEnCn;
    private RecyclerView recyclerView;
    private List<Sentence> dataList;
    private SentenceAdapter adapter;
    private ImageView ivPlay;
    private TextView curTime;
    private IndicatorSeekBar seekBar;
    private Dialog loadingDialog;
    private String quesStr;  // 问题，可转化为JSONArray的字符串，传递给ExerciseActivity解析
    private String explainStr;  // 解析，可转化为JSONArray的字符串，传递给ExerciseActivity解析
    private Timer timer;
    private int curPlayingPosition;  // 当前正在播放的句子index

    private final Handler handler = new Handler(msg -> {
        int what = msg.what;
        if (what == ConstUtil.NET_ERROR) {
            ToastUtil.showToast(ContentActivity.this, "网络错误，请稍后重试");
            if (loadingDialog.isShowing())
                loadingDialog.dismiss();
            return true;
        }else if(what == ConstUtil.REQUEST_FINISH) {
            loadingDialog.dismiss();
            JSONArray jsonArray = (JSONArray) msg.obj;
            dataList = new ArrayList<>();
            for (Object item : jsonArray) {
                JSONObject jo = JSONObject.parseObject(item.toString());
                dataList.add(new Sentence(jo.getInteger("voaid"), jo.getInteger("paraId"), jo.getInteger("idIndex"), jo.getString("sentence"), jo.getString("sentence_cn"), jo.getString("timing"), jo.getString("endTiming"), false));
            }
            dataList.get(0).setPlaying(true);
            adapter = new SentenceAdapter(this, dataList, 1);
            recyclerView.setAdapter(adapter);
            // 更新当前播放句子变色，滚动至当前播放句子
            // 更新进度条
            // 更新进度条左边当前播放时间点
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // 更新当前播放句子变色，滚动至当前播放句子
                    int tempCurPosition = 0;
                    for (int i = 0; i < dataList.size(); i++) {
                        Sentence item = dataList.get(i);
                        if (player != null) {
                            int currentPosition = player.getCurrentPosition();
                            int beginTime = (int) Float.parseFloat(item.getTiming()) * 1000;
                            int endTime = (int) Float.parseFloat(item.getEndTiming()) * 1000;
                            if (beginTime < currentPosition && currentPosition < endTime) {
                                tempCurPosition = i;
                            }
                        }
                    }
                    if (curPlayingPosition != tempCurPosition && tempCurPosition != 0) {
                        curPlayingPosition = tempCurPosition;
                        Message msg = new Message();
                        msg.what = ConstUtil.UPDATE_RECYCLER;
                        msg.arg1 = tempCurPosition;
                        handler.sendMessage(msg);
                    }
                    // 更新进度条
                    if (player != null) {
                        int target = (player.getCurrentPosition() * 1000 / player.getDuration());
                        seekBar.setProgress(target);
                    }
                    // 更新进度条左边当前播放时间点
                    runOnUiThread(() -> curTime.setText(TimeUtils.milliSecToMinute(player != null ? player.getCurrentPosition() : 0)));
                }
            };
            if (timer == null)
                timer = new Timer();
            // timer.schedule(task, 100, 100);
            timer.scheduleAtFixedRate(task, 100, 300);
            return true;
        }else if (what == ConstUtil.UPDATE_RECYCLER) {
            int position = msg.arg1;
            // int beginTime = (int) Float.parseFloat(dataList.get(position).getTiming()) * 1000;
            for (int i = 0; i < dataList.size(); i++) {
                dataList.get(i).setPlaying(i == position);
            }
            // int endTime = (int)Float.parseFloat(dataList.get(position).getEndTiming()) * 1000;
            dataList.get(position).setPlaying(true);
            recyclerView.smoothScrollToPosition(position);
            adapter.setDataList(dataList);
            // Log.d(TAG, position + " === run: currentPosition --- beginTime : " + player.getCurrentPosition() + " --- " + beginTime + " --- " + endTime);
            return true;
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        sectionId = getIntent().getLongExtra("sectionId", 0);
        String titleNum1 = getIntent().getStringExtra("titleNum1");
        sound = "http://staticvip2.iyuba.cn/IELTS/" + titleNum1 + "/" + getIntent().getStringExtra("sound");
        String img = "http://static2.iyuba.cn/IELTS/img/" + titleNum1 + "/" + sectionId + ".png";
        String url = "https://ai.iyuba.cn/api/getIeltsDetail.jsp?titlenum=" + sectionId;
        Log.d(TAG, "onCreate:   ------ " + url + "\n" +sound + "\n" + img);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.actionbar_content);  //绑定自定义的布局：actionbar_layout.xml
            TextView barText = actionBar.getCustomView().findViewById(R.id.bar_text);
            ImageView backIv = actionBar.getCustomView().findViewById(R.id.bar_back);
            TextView exec = actionBar.getCustomView().findViewById(R.id.bar_more);
            barText.setText(getIntent().getStringExtra("sectionName"));
            exec.setOnClickListener(this);
            backIv.setOnClickListener(this);
        }
        bindView();
        requestData();
    }

    private void bindView () {
        recyclerView = findViewById(R.id.rv_sentence);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ivPlay = findViewById(R.id.play_btn);
        curTime = findViewById(R.id.cur_time);
        TextView durationTime = findViewById(R.id.duration_time);
        ImageView ivPre = findViewById(R.id.pre_btn);
        ImageView ivNext = findViewById(R.id.next_btn);
        tvEnCn = findViewById(R.id.lan_btn);
        seekBar = findViewById(R.id.seekbar);
        showLoading();
        player = new MediaPlayer();
        try {
            player.setDataSource(sound);
            player.prepare();
            play();
            // 播放完毕监听
            player.setOnCompletionListener(mp -> {
                player.seekTo(0);
                ivPlay.setImageResource(R.mipmap.play);
                curPlayingPosition = 0;
                Message msg = new Message();
                msg.what = ConstUtil.UPDATE_RECYCLER;
                msg.arg1 = 0;
                handler.sendMessage(msg);
            });
            durationTime.setText(TimeUtils.milliSecToMinute(player.getDuration()));
            seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
                @Override
                public void onSeeking(SeekParams seekParams) {
                    // Log.d(TAG, "onSeeking: " + seekParams.progress);
                }

                @Override
                public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
                    // Log.d(TAG, "onStartTrackingTouch: " + seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                    Log.d(TAG, "onStopTrackingTouch: " + seekBar.getProgress());
                    player.seekTo(player.getDuration() * seekBar.getProgress() / 1000);
                }
            });
            curPlayingPosition = 0;
            // 音频准备好之后，取消加载提示框
            loadingDialog.dismiss();
        } catch (IOException e) {
            Log.d(TAG, "bindView: 设置MediaPlayer音频出错");
            e.printStackTrace();
        }

        ivPlay.setOnClickListener(this);
        ivPre.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        tvEnCn.setOnClickListener(this);
    }

    void showLoading() {
        if (loadingDialog == null) {
            View v = View.inflate(this, R.layout.dialog_waiting, null);
            loadingDialog = new Dialog(this, R.style.theme_dialog);
            loadingDialog.setContentView(v);
            loadingDialog.setCancelable(false);
        }
        loadingDialog.show();
    }

    // 直接请求数据，本地缓存以后在优化
    private void requestData() {
        showLoading();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https://ai.iyuba.cn/api/getIeltsDetail.jsp?titlenum=" + sectionId)
                .build();
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
                    JSONObject jo = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
                    if (jo.getInteger("result") == 1) {
                        JSONArray senArr = JSONObject.parseArray(jo.getString("texts"));
                        quesStr = jo.getString("questions");
                        Log.d(TAG, "onResponse:   quesStr ------ " + quesStr);
                        explainStr = jo.getString("ex_plains");
                        Message msg = new Message();
                        msg.what = ConstUtil.REQUEST_FINISH;
                        msg.obj = senArr;
                        handler.sendMessage(msg);
                    }
                }
            }
        });

    }

    // 播放(或暂停)
    private void play() {
        if (player != null && !player.isPlaying()) {
            player.start();
            ivPlay.setImageResource(R.mipmap.pause);
        }else if (player != null && player.isPlaying()) {
            player.pause();
            ivPlay.setImageResource(R.mipmap.play);
        }
    }

    // 上一句
    private void pre() {
        for (int i = 0; i < dataList.size(); i++) {
            if (player.getCurrentPosition() < (int) Float.parseFloat(dataList.get(0).getEndTiming())) {
                curPlayingPosition = 0;
                player.seekTo(0);
                Message msg = new Message();
                msg.what = ConstUtil.UPDATE_RECYCLER;
                msg.arg1 = 0;
                handler.sendMessage(msg);
            }else
                player.seekTo((int) Float.parseFloat(dataList.get(curPlayingPosition - 1).getTiming()) * 1000);
        }
    }

    // 下一句
    private void next() {
        for (int i = 0; i < dataList.size(); i++) {
            if (curPlayingPosition != dataList.size() - 1)
                player.seekTo((int) Float.parseFloat(dataList.get(curPlayingPosition + 1).getTiming()) * 1000);
        }
    }

    // 切换语言
    private void changeLanguage() {
        String curLan = (String) tvEnCn.getText();
        if ("中英".equals(curLan)) {
            tvEnCn.setText("英文");
            adapter.setShowMode(2);
        }else if ("英文".equals(curLan)) {
            tvEnCn.setText("中文");
            adapter.setShowMode(3);
        }else {
            tvEnCn.setText("中英");
            adapter.setShowMode(1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // play();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bar_back) {
            onBackPressed();
        }else if (id == R.id.bar_more) {
            if ("[]".equals(quesStr)) {
                ToastUtil.showToast(this, "暂无数据");
            }else {
                Intent intent = new Intent(this, ExerciseActivity.class);
                intent.putExtra("question", quesStr);
                intent.putExtra("explain", explainStr);
                startActivity(intent);
            }
        }else if (id == R.id.play_btn) {
            play();
        }else if (id == R.id.pre_btn) {
            pre();
        }else if (id == R.id.next_btn) {
            next();
        }else if (id == R.id.lan_btn) {
            changeLanguage();
        }
    }
}