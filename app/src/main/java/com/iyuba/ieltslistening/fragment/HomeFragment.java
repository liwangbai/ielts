package com.iyuba.ieltslistening.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.adapter.HomeAdapter;
import com.iyuba.ieltslistening.dao.TestPaperDao;
import com.iyuba.ieltslistening.dao.impl.TestPaperDaoImpl;
import com.iyuba.ieltslistening.pojo.TestPaper;
import com.iyuba.ieltslistening.utils.ConstUtil;
import com.iyuba.ieltslistening.utils.SharedPreferencesUtils;
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

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private Context context;
    private List<TestPaper> dataList;
    private TestPaperDao paperDao;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView rv;
    private HomeAdapter adapter;

    public HomeFragment() {}

    public HomeFragment(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    private final Handler handler = new Handler(msg -> {
        int what = msg.what;
        if (what == ConstUtil.REQUEST_FINISH) {
            dataList = new ArrayList<>();
            JSONArray joArr = (JSONArray) msg.obj;
            for (Object obj : joArr) {
                JSONObject jo = JSONObject.parseObject(obj.toString());
                TestPaper temp = new TestPaper(jo.getInteger("downloadState"), jo.getInteger("id"), jo.getBoolean("isDownload"), jo.getBoolean("isFree"), jo.getBoolean("isVip"), jo.getString("name"), jo.getString("productID"), jo.getInteger("progress"), jo.getString("testTime"), jo.getInteger("version"));
                paperDao.addTestPager(temp);
                dataList.add(temp);
                // 2017???????????????????????????????????????; 2022.02.21??????????????????,????????????????????????
                // if (!jo.getString("testTime").contains("2017")) {}
            }
            adapter = new HomeAdapter(getContext(), dataList);
            rv.setAdapter(adapter);
            Log.d(TAG, "list????????????: " + dataList.size());
            SharedPreferencesUtils.setBoolean(requireContext(), SharedPreferencesUtils.APP_INFO, "localExist", true);
            return true;
        }else if (what == ConstUtil.NET_ERROR) {
            if (smartRefreshLayout.isRefreshing())
                smartRefreshLayout.finishRefresh();
            ToastUtil.showToast(getContext(), "??????????????????????????????");
            return true;
        }else if (what == ConstUtil.REFRESH_FINISH) {
            List<Integer> ids = new ArrayList<>();
            for (TestPaper item : dataList) {
                ids.add(item.getId());
            }
            JSONArray joArr = (JSONArray) msg.obj;
            if (joArr.size() > dataList.size()) {
                for (Object obj : joArr) {
                    JSONObject jo = JSONObject.parseObject(obj.toString());
                    if (!ids.contains(jo.getInteger("id"))) {
                        TestPaper temp = new TestPaper(jo.getInteger("downloadState"), jo.getInteger("id"), jo.getBoolean("isDownload"), jo.getBoolean("isFree"), jo.getBoolean("isVip"), jo.getString("name"), jo.getString("productID"), jo.getInteger("progress"), jo.getString("testTime"), jo.getInteger("version"));
                        paperDao.addTestPager(temp);
                        dataList.add(0, temp);
                    }
                }
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
            smartRefreshLayout.finishRefresh();
            return true;
        }
        return false;
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        paperDao = new TestPaperDaoImpl(getContext(), "paper_list");
        initSmartRefresh(view);
        rv = view.findViewById(R.id.rv_test_paper);
        rv.setLayoutManager(new LinearLayoutManager(context));
        // ??????????????????????????????????????????????????????????????????
        if (SharedPreferencesUtils.getBoolean(requireContext(), SharedPreferencesUtils.APP_INFO, "localExist")) {
            dataList = paperDao.findAllTestPaper();
            HomeAdapter adapter = new HomeAdapter(context, dataList);
            rv.setAdapter(adapter);
        }else {
            requestList();
        }
        return view;
    }

    // ?????????????????????????????????
    private void initSmartRefresh(View view) {
        smartRefreshLayout = view.findViewById(R.id.smart_ly);
        smartRefreshLayout.setRefreshHeader(new ClassicsHeader(requireContext()));
        // smartRefreshLayout.setRefreshFooter(new ClassicsFooter(context));
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> refresh());
        // smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {});
    }

    // ??????????????????
    private void requestList() {
        String url = "http://m.iyuba.cn/ncet/getIeltsTestList.jsp";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: ????????????");
                e.printStackTrace();
                Message msg = new Message();
                msg.what = ConstUtil.NET_ERROR;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jo = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
                    String dataStr = jo.getString("data");
                    JSONArray joArr = JSONObject.parseArray(dataStr);

                    Message msg = new Message();
                    msg.what = ConstUtil.REQUEST_FINISH;
                    msg.obj = joArr;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    // ??????????????????
    private void refresh() {
        String url = "http://m.iyuba.cn/ncet/getIeltsTestList.jsp";
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
                    String dataStr = jo.getString("data");
                    JSONArray joArr = JSONObject.parseArray(dataStr);

                    Message msg = new Message();
                    msg.what = ConstUtil.REFRESH_FINISH;
                    msg.obj = joArr;
                    handler.sendMessage(msg);
                }
            }
        });
    }

}
