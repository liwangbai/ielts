package com.iyuba.ieltslistening.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.activity.ExerciseActivity;
import com.iyuba.ieltslistening.pojo.ChooseItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuesChooseAdapter extends BaseAdapter{

    private static final String TAG = "QuesChooseAdapter";
    private final Context context;
    private List<ChooseItem> chooseList;
    private final ExerciseActivity activity;
    private final Set<String> userAnswer1;

    public QuesChooseAdapter(Context context, List<ChooseItem> chooseList) {
        this.context = context;
        this.chooseList = chooseList;
        activity = (ExerciseActivity) context;
        userAnswer1 = new HashSet<>();
    }

    @Override
    public int getCount() {
        return chooseList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.ques_choose_item, parent, false);

        LinearLayout optionsLy = convertView.findViewById(R.id.option_ly);
        CheckBox checkBox = convertView.findViewById(R.id.cb_choose_item);
        TextView option = convertView.findViewById(R.id.option);
        TextView optionDesc = convertView.findViewById(R.id.option_desc);

        optionDesc.setText(chooseList.get(position).getContent());
        checkBox.setChecked(chooseList.get(position).isChecked());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "onCheckedChanged: ------> " + isChecked);
            if (isChecked){
                userAnswer1.add(getOption(position));
            }else {
                userAnswer1.remove(getOption(position));
            }
            activity.setUserAnswer1(userAnswer1);
            Log.d(TAG, "getView:  set's value ---------> " + userAnswer1.toString());
        });

        optionsLy.setOnClickListener(v -> checkBox.setChecked(!checkBox.isChecked()));
        String [] options = {"A. ", "B. ", "C. ", "D. ", "E. ", "F. ", "G. ", "H. ", "I. ", "J."};
        if (chooseList.size() < 10) {
            option.setText(options[position]);
        }

        return convertView;
    }

    private String getOption(int index) {
        if (index < 10) {
            String [] options = {"A. ", "B. ", "C. ", "D. ", "E. ", "F. ", "G. ", "H. ", "I. ", "J."};
            return options[index];
        }else
            return "A";
    }

    public void setData(List<ChooseItem> curChooseList) {
        this.chooseList = curChooseList;
        notifyDataSetChanged();
    }


}
