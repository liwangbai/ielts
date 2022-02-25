package com.iyuba.ieltslistening.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import com.iyuba.ieltslistening.adapter.SectionsAdapter;
import com.iyuba.ieltslistening.dao.TestPaperDao;
import com.iyuba.ieltslistening.dao.impl.TestPaperDaoImpl;
import com.iyuba.ieltslistening.pojo.Sections;
import com.iyuba.ieltslistening.utils.ConstUtil;
import com.iyuba.ieltslistening.utils.ToastUtil;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SectionListActivity extends AppCompatActivity {

    private static final String TAG = "SectionListActivity";
    private int paperId;
    private int testTime;
    private RecyclerView rv;
    private SmartRefreshLayout ly;
    private List<Sections> dataList;
    private TestPaperDao paperDao;
    private SectionsAdapter adapter;

    private final Handler handler = new Handler(new Handler.Callback() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int what = msg.what;
            if (what == ConstUtil.NET_ERROR) {
                if (ly.isRefreshing())
                    ly.finishRefresh();
                ToastUtil.showToast(SectionListActivity.this, "网络错误，请稍后重试");
                return true;
            }else if (what == ConstUtil.REQUEST_FINISH) {
                dataList = new ArrayList<>();
                JSONArray joArr = (JSONArray) msg.obj;
                for (Object obj : joArr) {
                    JSONObject jo = JSONObject.parseObject(obj.toString());
                    Sections sections = new Sections(jo.getLong("titlenum"), jo.getString("titlenum1"), jo.getString("PartType"), jo.getString("titlename"), jo.getString("sound"));
                    dataList.add(sections);
                    paperDao.addSection(sections, paperId);
                }
                adapter = new SectionsAdapter(SectionListActivity.this, dataList);
                rv.setAdapter(adapter);
                return true;
            }else if (what == ConstUtil.REFRESH_FINISH) {
                JSONArray joArr = (JSONArray) msg.obj;
                if (joArr.size() > dataList.size()) {
                    List<Integer> ids = new ArrayList<>();
                    for (Sections item : dataList) {
                        ids.add((int)item.getTitleNum());
                    }
                    for (Object obj : joArr) {
                        JSONObject jo = JSONObject.parseObject(obj.toString());
                        Sections sections = new Sections(jo.getLong("titlenum"), jo.getString("titlenum1"), jo.getString("PartType"), jo.getString("titlename"), jo.getString("sound"));
                        if (!ids.contains((int)sections.getTitleNum())) {
                            paperDao.addSection(sections, paperId);
                            dataList.add(0, sections);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                ly.finishRefresh();
                return true;
            }
            return false;
        }
    });

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_list);
        bindView();
        paperId = getIntent().getIntExtra("paperId", 0);
        testTime = Integer.parseInt(getIntent().getStringExtra("testTime"));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.actionbar_section_list);  //绑定自定义的布局：actionbar_layout.xml
            TextView barText = actionBar.getCustomView().findViewById(R.id.bar_text);
            ImageView backIv = actionBar.getCustomView().findViewById(R.id.bar_back);
            ImageView moreIv = actionBar.getCustomView().findViewById(R.id.bar_more);
            barText.setText("IELTS_" + testTime);
            moreIv.setVisibility(View.INVISIBLE);
            backIv.setOnClickListener(v -> onBackPressed());

        }
        paperDao = new TestPaperDaoImpl(this, "paper_list");
        if (paperDao.findSectionExistByPaperId(paperId) != null) {
            Log.d(TAG, "onCreate: ---- 本地有数据，去数据库取 paperId -- " + paperId);
            dataList = paperDao.findSectionsByPaperId(paperId);
            adapter = new SectionsAdapter(SectionListActivity.this, dataList);
            rv.setAdapter(adapter);
        }else {
            Log.d(TAG, "onCreate:  ---- 本地没数据，发起请求");
            requestSections();
        }
    }

    private void bindView() {
        rv = findViewById(R.id.rv_sections);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ly = findViewById(R.id.smart_ly2);
        ly.setRefreshHeader(new ClassicsHeader(this));
        ly.setOnRefreshListener(refreshLayout -> refreshSections());
    }

    // 下拉刷新列表数据
    private void refreshSections() {
        String url = "https://ai.iyuba.cn/api/getIeltsTitleList.jsp?titlenumber=" + testTime;
        Log.d(TAG, "requestSections: ----->" + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
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
                    JSONArray joArr = JSONObject.parseArray(jo.getString("data"));
                    Message msg = new Message();
                    msg.what = ConstUtil.REFRESH_FINISH;
                    msg.obj = joArr;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void requestSections() {
        String url = "https://ai.iyuba.cn/api/getIeltsTitleList.jsp?titlenumber=" + testTime;
        Log.d(TAG, "requestSections: ----->" + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
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
                    JSONArray joArr = JSONObject.parseArray(jo.getString("data"));
                    Message msg = new Message();
                    msg.what = ConstUtil.REQUEST_FINISH;
                    msg.obj = joArr;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null){
            Dialog loadingDialog = adapter.getLoadingDialog();
            if (loadingDialog != null)
                loadingDialog.dismiss();
            }
    }
}