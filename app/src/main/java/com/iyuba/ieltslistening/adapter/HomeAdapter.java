package com.iyuba.ieltslistening.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.activity.SectionListActivity;
import com.iyuba.ieltslistening.components.MyApplication;
import com.iyuba.ieltslistening.pojo.TestPaper;


import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static final String TAG = "HomeAdapter";
    private final Context context;
    private final List<TestPaper> dataList;

    public HomeAdapter(Context context, List<TestPaper> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_paper_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestPaper testPaper = dataList.get(position);
        // Log.d(TAG, "onBindViewHolder: ----->" + testPaper.toString());

        Log.d(TAG, "onBindViewHolder: ---> setTag");
        holder.tvTime.setText("IELTS_" + testPaper.getTestTime());
        holder.tvName.setText(testPaper.getName());

        Log.d(TAG, "onBindViewHolder: ----> dataItem" + dataList.get(position).toString());
        int paperId = dataList.get(position).getId();
        String testTime = dataList.get(position).getTestTime();

        holder.ly.setOnClickListener(v -> {
            Intent intent = new Intent(MyApplication.getContext(), SectionListActivity.class);
            intent.putExtra("paperId", paperId);
            intent.putExtra("testTime", testTime);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (dataList != null)
            return dataList.size();
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ly;
        TextView tvTime;
        TextView tvName;
        public ViewHolder(@NonNull View view) {
            super(view);
            ly = view.findViewById(R.id.paper_item_ly);
            tvTime = view.findViewById(R.id.paper_time);
            tvName = view.findViewById(R.id.paper_name);
        }
    }
}
