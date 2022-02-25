package com.iyuba.ieltslistening.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import com.iyuba.ieltslistening.activity.ContentActivity;
import com.iyuba.ieltslistening.activity.SectionListActivity;
import com.iyuba.ieltslistening.pojo.Sections;

import java.util.List;

public class SectionsAdapter extends RecyclerView.Adapter<SectionsAdapter.ViewHolder> {

    private static final String TAG = "SectionsAdapter";
    private final Context context;
    private final List<Sections> dataList;
    private Dialog loadingDialog;

    public SectionsAdapter(Context context, List<Sections> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_section_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + dataList.size());
        Sections sections = dataList.get(position);
        holder.ly.setTag(position);
        holder.tvName.setText(sections.getTitleName());
        holder.ly.setOnClickListener(v -> {
            showLoading();
            Intent intent = new Intent(context, ContentActivity.class);
            intent.putExtra("sectionName", dataList.get(position).getTitleName());
            intent.putExtra("sectionId", dataList.get(position).getTitleNum());
            intent.putExtra("titleNum1", dataList.get(position).getTitleNum1());
            intent.putExtra("sound", dataList.get(position).getSound());
            context.startActivity(intent);
        });
    }

    void showLoading() {
        if (loadingDialog == null) {
            View v = View.inflate(context, R.layout.dialog_waiting, null);
            loadingDialog = new Dialog(context, R.style.theme_dialog);
            loadingDialog.setContentView(v);
            loadingDialog.setCancelable(false);
        }
        loadingDialog.show();
    }

    // 提供一个获取的接口，给Activity的resume方法调用
    public Dialog getLoadingDialog() {
        if (loadingDialog != null)
            return loadingDialog;
        return null;
    }

    @Override
    public int getItemCount() {
        if (dataList != null)
            return dataList.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ly;
        TextView tvName;
        public ViewHolder(@NonNull View view) {
            super(view);
            ly = view.findViewById(R.id.section_item_ly);
            tvName = view.findViewById(R.id.section_name);

            ly.setOnClickListener(v -> {
                Intent intent = new Intent(context, SectionListActivity.class);
                context.startActivity(intent);
            });
        }
    }
}
