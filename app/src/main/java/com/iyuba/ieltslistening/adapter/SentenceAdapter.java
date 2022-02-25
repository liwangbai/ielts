package com.iyuba.ieltslistening.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.pojo.Sentence;

import java.util.List;

public class SentenceAdapter extends RecyclerView.Adapter<SentenceAdapter.ViewHolder> {

    private static final String TAG = "SectionsAdapter";
    private final Context context;
    private List<Sentence> dataList;
    private int showMode;  // 展示模式，1 - 中英； 2 - 英文； 3 - 中文；

    public SentenceAdapter(Context context, List<Sentence> dataList, int showMode) {
        this.context = context;
        this.dataList = dataList;
        this.showMode = showMode;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setShowMode(int showMode) {
        this.showMode = showMode;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataList (List<Sentence> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_sentence_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + dataList.size());
        Sentence sentence = dataList.get(position);
        holder.tvSen.setText(sentence.getSentence());
        holder.tvSenCn.setText(sentence.getSentenceCn());
        if (sentence.isPlaying()) {
            holder.tvSen.setTextColor(context.getResources().getColor(R.color.primary));
            holder.tvSenCn.setTextColor(context.getResources().getColor(R.color.primary));
        }else {
            holder.tvSen.setTextColor(context.getResources().getColor(R.color.black));
            holder.tvSenCn.setTextColor(context.getResources().getColor(R.color.deep_gray));
        }
        if (showMode == 1) {
            holder.tvSen.setVisibility(View.VISIBLE);
            holder.tvSenCn.setVisibility(View.VISIBLE);
        }else if (showMode == 2) {
            holder.tvSen.setVisibility(View.VISIBLE);
            holder.tvSenCn.setVisibility(View.GONE);
        }else {
            holder.tvSen.setVisibility(View.GONE);
            holder.tvSenCn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (dataList != null)
            return dataList.size();
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSen;
        TextView tvSenCn;
        public ViewHolder(@NonNull View view) {
            super(view);
            tvSen = view.findViewById(R.id.sen_en);
            tvSenCn = view.findViewById(R.id.sen_cn);
        }
    }


}
