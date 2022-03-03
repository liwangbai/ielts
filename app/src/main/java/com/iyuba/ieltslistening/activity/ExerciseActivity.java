package com.iyuba.ieltslistening.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.adapter.QuesChooseAdapter;
import com.iyuba.ieltslistening.pojo.ChooseItem;
import com.iyuba.ieltslistening.pojo.Question;
import com.iyuba.ieltslistening.utils.ActivityController;
import com.iyuba.ieltslistening.utils.ConstUtil;
import com.iyuba.ieltslistening.utils.DensityUtil;
import com.iyuba.ieltslistening.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExerciseActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ExerciseActivity";
    private String explainStr;  // 解析json数组的字符串
    private String quesStr;
    private TextView quesIndex;  // 展示当前题目序号
    private int curType;  // 当前是什么题； 1 - 选择题； 2 - 填空题
    private Dialog imgDialog;
    private TextView preBtn;
    private TextView nextBtn;
    // private QuesChooseAdapter adapter;

    private LinearLayout layout1;  // 选择题布局
    private LinearLayout layout2;  // 填空题布局
    private TextView tvQuesIndex1;  // 选择题题目序号
    private TextView tvQuesIndex2;  // 填空题题目序号
    private TextView tvQuesText1;  // 选择题题目
    private TextView tvQuesText2;  // 填空题题目
    private ListView choose_list;  // 选择题选项列表
    private ImageView quesImg;  // 填空题图片
    private EditText userAns;  // 用户答案

    private int curIndex;  // 当前是第几题
    private int totalNum;  // 总共多少题
    private int curPosition;  // 也是表示当前是第几题，不过这个值可以直接用于数组下标取值

    private Map<Integer, Set<String>> answerMap1;
    private Map<Integer, String> answerMap2;

    private List<Question> quesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        // 获取传递参数
        // 题目json数组的字符串
        quesStr = getIntent().getStringExtra("question");
        explainStr = getIntent().getStringExtra("explain");
        setMyActionBar();
        bindView();
        resolveStr(quesStr);
        if (quesList.size() > 0) {
            curIndex = Integer.parseInt(quesList.get(0).getTitleNum().substring(6, 8));
            totalNum = curIndex + quesList.size() - 1;
            loadUI();
        }
        Log.d(TAG, "onCreate:  ----- " + "[]".equals(quesStr));
        ActivityController.showCurActivity();
    }

    private void setMyActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.actionbar_content);  //绑定自定义的布局：actionbar_layout.xml
            TextView barText = actionBar.getCustomView().findViewById(R.id.bar_text);
            ImageView backIv = actionBar.getCustomView().findViewById(R.id.bar_back);
            quesIndex = actionBar.getCustomView().findViewById(R.id.bar_more);
            quesIndex.setText("");
            barText.setText("做题");
            backIv.setOnClickListener(this);
        }
    }

    private void bindView() {
        layout1 = findViewById(R.id.ques_type1);
        layout2 = findViewById(R.id.ques_type2);
        tvQuesIndex1 = findViewById(R.id.ques_index1);
        tvQuesIndex2 = findViewById(R.id.ques_index2);
        tvQuesText1 = findViewById(R.id.ques_text1);
        tvQuesText2 = findViewById(R.id.ques_text2);
        choose_list = findViewById(R.id.choose_list);
        quesImg = findViewById(R.id.ques_img);
        userAns = findViewById(R.id.user_ans);
        preBtn = findViewById(R.id.exe_pre);
        nextBtn = findViewById(R.id.exe_next);

        preBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        quesImg.setOnClickListener(this);
    }

    // 解析json字符串数据
    private void resolveStr(String jsonStr) {
        quesList = new ArrayList<>();
        JSONArray joArr = JSONObject.parseArray(jsonStr);
        if (joArr != null) {
            for (Object obj : joArr) {
                JSONObject jo = JSONObject.parseObject(obj.toString());
                Question tempObj = new Question();
                if (jo.containsKey("titlenum"))
                    tempObj.setTitleNum(jo.getString("titlenum"));
                if (jo.containsKey("answertext"))
                    tempObj.setAnswerText(jo.getString("answertext"));
                if (jo.containsKey("quesimage"))
                    tempObj.setQuesImage(jo.getString("quesimage"));
                if (jo.containsKey("answer"))
                    tempObj.setAnswer(jo.getString("answer"));
                if (jo.containsKey("quesText"))
                    tempObj.setQuesText(jo.getString("quesText"));
                quesList.add(tempObj);
            }
            if (quesList.size() == 1) {
                nextBtn.setText("提交");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadUI() {
        tvQuesIndex1.setText("QUESTION " + curIndex);
        tvQuesIndex2.setText("QUESTION " + curIndex);
        quesIndex.setText(curIndex + "/" + totalNum);
        if (quesList != null && quesList.size() > 0) {
            Question curQues = quesList.get(curPosition);
            if (curQues.getAnswerText() != null && !curQues.getAnswer().equals("")) {
                curType = 1;
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                // 是否有题目，有则展示
                if (curQues.getQuesText() != null && !curQues.getQuesText().equals("")) {
                    tvQuesText1.setVisibility(View.VISIBLE);
                    if (curQues.getAnswer().split("\\+\\+").length > 1)
                        tvQuesText1.setText("(多选题) " + curQues.getQuesText().replaceAll("‘", "'"));
                    else
                        tvQuesText1.setText("(单选题) " + curQues.getQuesText().replaceAll("‘", "'"));

                }else
                    tvQuesText1.setVisibility(View.GONE);
                String[] chooses = curQues.getAnswerText().split("\\+\\+");
                List<ChooseItem> chooseList = new ArrayList<>();
                for (String item : chooses)
                    chooseList.add(new ChooseItem(item, false));
                // 回答过本道题就把答案填充上去
                if (answerMap1 != null && answerMap1.containsKey(curPosition)){
                    Set<String> strings = answerMap1.get(curPosition);
                    List<Integer> tempList = new ArrayList<>();
                    for (String s : strings)
                        tempList.add(getIndexByABC(s));
                    Log.d(TAG, "loadUI: -------> " + strings.toString());
                    for (int i : tempList) {
                        chooseList.get(i - 1).setChecked(true);
                    }
                }
                QuesChooseAdapter adapter = new QuesChooseAdapter(this, chooseList);
                choose_list.setAdapter(adapter);

//                if (adapter == null) {
//                    adapter = new QuesChooseAdapter(this, chooseList);
//                    choose_list.setAdapter(adapter);
//                }else {
//                    for (ChooseItem item : chooseList) {
//                        Log.d(TAG, "loadUI:  ==> " + item.getContent() + " === " + item.isChecked());
//                    }
//                    adapter.setData(chooseList);
//                    adapter.resetQuesList();
//                }
            }else {
                curType = 2;
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                // 回答过本道题就把答案填充上去
                if (answerMap2 != null && answerMap2.containsKey(curPosition))
                    userAns.setText(answerMap2.get(curPosition));
                // 是否有题目，有则展示
                if (curQues.getQuesText() != null && !curQues.getQuesText().equals("")) {
                    tvQuesText2.setVisibility(View.VISIBLE);
                    tvQuesText2.setText(curQues.getQuesText().replaceAll("‘", "'"));
                }else
                    tvQuesText2.setVisibility(View.GONE);
                // 是否有图片，有则展示
                if (curQues.getQuesImage() != null && !curQues.getQuesImage().equals("")) {
                    quesImg.setVisibility(View.VISIBLE);
                    String imgPart1 = curQues.getQuesImage().substring(0, 6);
                    String lastUrl = curQues.getQuesImage();
                    if (lastUrl.contains("-")) {
                        lastUrl = lastUrl.substring(0, lastUrl.indexOf("-"));
                    }
                    String imgUrl = "http://static2.iyuba.cn/IELTS/img/" + imgPart1 + "/" + lastUrl;
                    if (!imgUrl.startsWith(".png", imgUrl.length() - 4)) {
                        imgUrl = imgUrl + ".png";
                    }
                    Log.d(TAG, "loadUI: imgUrl ----------->" + imgUrl);
                    Glide.with(this).load(imgUrl).into(quesImg);
                }else
                    quesImg.setVisibility(View.GONE);
            }
        }
    }

    public void setUserAnswer1(Set<String> answer) {
        if (answerMap1 == null)
            answerMap1 = new HashMap<>();
        answerMap1.put(curPosition, answer);
        Log.d(TAG, "setUserAnswer1:   value -----------> " + answer.toString());
    }

    public void setUserAnswer2(String answer) {
        if (answerMap2 == null)
            answerMap2 = new HashMap<>();
        answerMap2.put(curPosition, answer);
    }

    // 点击图片，展示大图
    private void showBigImg() {
        String imgPart1 = quesList.get(curPosition).getQuesImage().substring(0, 6);
        String lastUrl = quesList.get(curPosition).getQuesImage();
        if (lastUrl.contains("-")) {
            lastUrl = lastUrl.substring(0, lastUrl.indexOf("-"));
        }
        String imgUrl = "http://static2.iyuba.cn/IELTS/img/" + imgPart1 + "/" + lastUrl;
        if (!imgUrl.startsWith(".png", imgUrl.length() - 4)) {
            imgUrl = imgUrl + ".png";
        }
        if (imgDialog == null) {
            View v = View.inflate(this, R.layout.dialog_big_img, null);
            imgDialog = new Dialog(this, R.style.theme_dialog);
            ImageView bigImg = v.findViewById(R.id.big_img);
            Glide.with(this).load(imgUrl).into(bigImg);
            bigImg.setOnClickListener(v1 -> imgDialog.dismiss());
            imgDialog.setContentView(v);
            imgDialog.setCancelable(true);
        }
        imgDialog.setCanceledOnTouchOutside(true);
        imgDialog.show();
    }

    // 上一题
    @SuppressLint("UseCompatLoadingForDrawables")
    private void toPre(){
        String answer = userAns.getText().toString();
        if (!answer.equals(""))
            setUserAnswer2(answer);
        if (curPosition != 0) {
            curPosition -= 1;
            curIndex -= 1;
            loadUI();
            if (curPosition == 0)
                preBtn.setBackground(getDrawable(R.drawable.btn_view_shape));
            else
                preBtn.setBackground(getDrawable(R.drawable.btn_view_shape1));
            if (curPosition == quesList.size() - 1)
                nextBtn.setText("提交");
            else
                nextBtn.setText("下一题");
            preBtn.setPadding(0, DensityUtil.dip2px(this, 10), 0, DensityUtil.dip2px(this, 10));
        }
    }

    // 下一题
    @SuppressLint("UseCompatLoadingForDrawables")
    private void toNext() {
        if (curPosition != quesList.size() - 1) {
            boolean doneFlag = false;
            if (curType == 1) {
                if (answerMap1 != null && answerMap1.containsKey(curPosition))
                    doneFlag = true;
            }else {
                String answer = userAns.getText().toString();
                if (!answer.equals("")) {
                    doneFlag = true;
                }
                setUserAnswer2(answer);
                userAns.setText("");
            }
            if (doneFlag) {
                curPosition += 1;
                curIndex += 1;
                loadUI();
                if (curPosition == 0)
                    preBtn.setBackground(getDrawable(R.drawable.btn_view_shape));
                else
                    preBtn.setBackground(getDrawable(R.drawable.btn_view_shape1));
                preBtn.setPadding(0, DensityUtil.dip2px(this, 10), 0, DensityUtil.dip2px(this, 10));

                if (curPosition == quesList.size() - 1)
                    nextBtn.setText("提交");
                else
                    nextBtn.setText("下一题");
            }else
                ToastUtil.showToast(this, "请先完成本题");
        }else
            submit();
    }

    // 提交
    private void submit() {
        Intent intent = new Intent(this, ExplainActivity.class);
        intent.putExtra("explainStr", explainStr);
        intent.putExtra("quesStr", quesStr);
        if (curType == 1 && answerMap1.containsKey(curPosition)) {
            intent.putExtra("type", 1);
            // intent.putExtra("uAns", answerMap1.toString());
            ConstUtil.answerMap1 = answerMap1;
            startActivity(intent);
        }else if (curType == 2 && !userAns.getText().toString().equals("")) {
            setUserAnswer2(userAns.getText().toString());
            intent.putExtra("type", 2);
            // intent.putExtra("uAns", answerMap2.toString());
            ConstUtil.answerMap2 = answerMap2;
            startActivity(intent);
        }else {
            ToastUtil.showToast(this, "请先完成本题");
        }
    }

    // 把A B C 等等转化为 1 2 3
    private int getIndexByABC(String chooseChar) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 1; i < 27; i++)
            map.put(String.valueOf((char)(i + 64)), i);
        if (map.containsKey(chooseChar.substring(0, 1)))
            return map.get(chooseChar.substring(0, 1));
        else
            return 0;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bar_back)
            onBackPressed();
        else if (id == R.id.ques_img)
            showBigImg();
        else if (id == R.id.exe_pre) {
            toPre();
        }else if (id == R.id.exe_next) {
            toNext();
        }
    }

}
