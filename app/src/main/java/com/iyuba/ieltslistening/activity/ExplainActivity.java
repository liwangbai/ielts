package com.iyuba.ieltslistening.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.adapter.QuesChooseAdapter2;
import com.iyuba.ieltslistening.pojo.ChooseItem;
import com.iyuba.ieltslistening.pojo.Explain;
import com.iyuba.ieltslistening.pojo.Question;
import com.iyuba.ieltslistening.utils.ConstUtil;
import com.iyuba.ieltslistening.utils.DensityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExplainActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "ExplainActivity";
    private Map<Integer, Set<String>> answerMap1;  // 用户选择题的答案
    private Map<Integer, String> answerMap2;  // 用户填空题的答案
    private List<Explain> explainList;  // 解析对象集合
    private List<Question> quesList;  // 题目对象集合
    private String quesStr;  // 题目的json字符串
    private String explainStr;  // 解析的json字符串
    private int type;  // 当前是什么题； 1 - 选择； 2 - 填空
    private int curIndex;  // 当前是第几题
    private int totalNum;  // 总共多少题
    private int curPosition;  // 也是表示当前是第几题，不过这个值可以直接用于数组下标取值
    private QuesChooseAdapter2 adapter;
    private Dialog imgDialog;

    private TextView barIndex;  // 右上角的第几题序号
    private LinearLayout layout1;  // 选择题布局
    private LinearLayout layout2;  // 填空题布局
    private TextView tvQuesIndex1;  // 选择题题目序号
    private TextView tvQuesIndex2;  // 填空题题目序号
    private TextView tvQuesText1;  // 选择题题目
    private TextView tvQuesText2;  // 填空题题目
    private ListView chooseList;  // 选择题选项列表
    private ImageView quesImg;  // 填空题图片
    private TextView userAns1;  // 选择题用户的答案
    private TextView rightAns1;  // 选择题正确的答案
    private TextView userAns2;  // 填空题用户的答案
    private TextView rightAns2;  // 填空题正确的答案
    private TextView preBtn;  // 上一题
    private TextView nextBtn;  // 下一题
    private TextView showExpBtn;  // 展示解析

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);
        getIntentData();
        bindView();
        setMyActionBar();
        resolveStr(quesStr, 1);
        resolveStr(explainStr, 2);
        loadUI();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        quesStr = intent.getStringExtra("quesStr");
        explainStr = intent.getStringExtra("explainStr");
        type = intent.getIntExtra("type", 1);
        // String uAns = intent.getStringExtra("uAns");
        if (type == 1)
            answerMap1 = ConstUtil.answerMap1;
        else
            answerMap2 = ConstUtil.answerMap2;
    }

    private void bindView() {
        layout1 = findViewById(R.id.ques_type1);
        layout2 = findViewById(R.id.ques_type2);
        tvQuesIndex1 = findViewById(R.id.ques_index1);
        tvQuesIndex2 = findViewById(R.id.ques_index2);
        tvQuesText1 = findViewById(R.id.ques_text1);
        tvQuesText2 = findViewById(R.id.ques_text2);
        quesImg = findViewById(R.id.ques_img);
        chooseList = findViewById(R.id.choose_list);
        userAns1 = findViewById(R.id.u_ans1);
        rightAns1 = findViewById(R.id.ans1);
        userAns2 = findViewById(R.id.u_ans2);
        rightAns2 = findViewById(R.id.ans2);
        preBtn = findViewById(R.id.exe_pre);
        nextBtn = findViewById(R.id.exe_next);
        showExpBtn = findViewById(R.id.show_explain);

        preBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        quesImg.setOnClickListener(this);
        showExpBtn.setOnClickListener(this);
    }

    private void setMyActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //Enable自定义的View
            actionBar.setCustomView(R.layout.actionbar_content);  //绑定自定义的布局：actionbar_layout.xml
            TextView barText = actionBar.getCustomView().findViewById(R.id.bar_text);
            ImageView backIv = actionBar.getCustomView().findViewById(R.id.bar_back);
            barIndex = actionBar.getCustomView().findViewById(R.id.bar_more);
            barIndex.setText("");
            barText.setText("解析");
            backIv.setOnClickListener(this);
        }
    }

    /**
     *
     * 解析json字符串数据
     * @param jsonStr  需要解析的数据
     * @param type  1 -- 试题的数据，包括题目、图片等等； 2 -- 解析内容的数据
     */
    private void resolveStr(String jsonStr, int type) {
        if (type == 1) {
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
            }
            if (quesList.size() > 0) {
                curIndex = Integer.parseInt(quesList.get(0).getTitleNum().substring(6, 8));
                totalNum = curIndex + quesList.size() - 1;
                curPosition = 0;
            }
        }else {
            explainList = new ArrayList<>();
            JSONArray joArr = JSONObject.parseArray(jsonStr);
            if (joArr != null) {
                for (Object obj : joArr) {
                    JSONObject jo = JSONObject.parseObject(obj.toString());
                    Explain tempObj = new Explain();
                    if (jo.containsKey("titleNum"))
                        tempObj.setTitleNum(jo.getInteger("titleNum"));
                    if (jo.containsKey("quesIndex"))
                        tempObj.setQuesIndex(jo.getInteger("quesIndex"));
                    if (jo.containsKey("testType"))
                        tempObj.setTestType(jo.getInteger("testType"));
                    if (jo.containsKey("partType"))
                        tempObj.setPartType(jo.getInteger("partType"));
                    if (jo.containsKey("ex_plain"))
                        tempObj.setExplain(jo.getString("ex_plain"));
                    explainList.add(tempObj);
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadUI() {
        // 题号序号设置
        tvQuesIndex1.setText("QUESTION " + curIndex);
        tvQuesIndex2.setText("QUESTION " + curIndex);
        // 右上角index设置
        barIndex.setText(curIndex + "/" + totalNum);
        // 当前的题目实例
        Question curQues = quesList.get(curPosition);
        if (type == 1) {
            // 隐藏填空题布局
            layout1.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.GONE);
            String[] chooses = curQues.getAnswerText().split("\\+\\+");
            // 是否有题目，有则展示
            if (curQues.getQuesText() != null && !curQues.getQuesText().equals("")) {
                tvQuesText1.setVisibility(View.VISIBLE);
                if (chooses.length > 1)
                    tvQuesText1.setText("(多选题) " + curQues.getQuesText().replaceAll("‘", "'"));
                else
                    tvQuesText1.setText("(单选题) " + curQues.getQuesText().replaceAll("‘", "'"));
            }else
                tvQuesText1.setVisibility(View.GONE);
            // 选项
            List<ChooseItem> tempList = new ArrayList<>();
            for (String item : chooses)
                tempList.add(new ChooseItem(item, false));
            if (answerMap1 != null && answerMap1.containsKey(curPosition)){
                Set<String> strings = answerMap1.get(curPosition);
                List<Integer> integerList = new ArrayList<>();
                for (String s : strings)
                    integerList.add(getIndexByABC(s));
                for (int i : integerList) {
                    tempList.get(i - 1).setChecked(true);
                }
            }
            if (adapter == null) {
                adapter = new QuesChooseAdapter2(this, tempList);
                chooseList.setAdapter(adapter);
            }else {
                adapter.setData(tempList);
            }
            // 正确答案
            String[] tempArr = quesList.get(curPosition).getAnswer().split("\\+\\+");
            int [] rightAnswers = new int[tempArr.length];
            for (int i = 0; i < tempArr.length; i++) {
                rightAnswers[i] = Integer.parseInt(tempArr[i]);
            }
            String rightAnswerStr = getABCByInt(rightAnswers);
            rightAns1.setText("正确答案: " + rightAnswerStr);
            rightAns1.setTextColor(this.getResources().getColor(R.color.right));
            // 用户答案
            Set<String> tempSet = answerMap1.get(curPosition);
            StringBuilder userAnsStr = new StringBuilder("");
            for (String item : tempSet)
                userAnsStr.append(item.charAt(0)).append(" ");
            userAns1.setText("您的答案: " + userAnsStr.toString());

            if (userAnsStr.toString().equals(rightAnswerStr))
                userAns1.setTextColor(this.getResources().getColor(R.color.right));
            else
                userAns1.setTextColor(this.getResources().getColor(R.color.worry));

        }else {
            // 隐藏选择题布局
            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.VISIBLE);
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
            // 正确答案
            rightAns2.setText("正确答案: " + curQues.getAnswer());
            rightAns2.setTextColor(this.getResources().getColor(R.color.right));
            // 用户答案
            userAns2.setText("您的答案: " + answerMap2.get(curPosition));
            String[] tempAnsArr = curQues.getAnswer().split("/");
            boolean flag = false;
            for (String item : tempAnsArr){
                if (item.equals(answerMap2.get(curPosition))) {
                    flag = true;
                    break;
                }else
                    flag = false;
            }
            if (flag)
                userAns2.setTextColor(this.getResources().getColor(R.color.right));
            else
                userAns2.setTextColor(this.getResources().getColor(R.color.worry));
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

    private String getABCByInt(int [] intArr) {
        StringBuilder resStr = new StringBuilder();
        List<String> tempList = new ArrayList();
        for (int i = 0; i < 26 ; i++)
            tempList.add(String.valueOf((char)(i + 64)));
        for (int j : intArr) resStr.append(tempList.get(j)).append(" ");

        return resStr.toString();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void toPre() {
        if (curPosition != 0) {
            curPosition -= 1;
            curIndex -= 1;
            loadUI();
            if (curPosition == 0)
                preBtn.setBackground(getDrawable(R.drawable.btn_view_shape));
            else
                preBtn.setBackground(getDrawable(R.drawable.btn_view_shape1));
            preBtn.setPadding(0, DensityUtil.dip2px(this, 10), 0, DensityUtil.dip2px(this, 10));

            if (curPosition == quesList.size() - 1)
                nextBtn.setBackground(getDrawable(R.drawable.btn_view_shape));
            else
                nextBtn.setBackground(getDrawable(R.drawable.btn_view_shape1));
            nextBtn.setPadding(0, DensityUtil.dip2px(this, 10), 0, DensityUtil.dip2px(this, 10));

        }
    }

    private void toNext() {
        if (curPosition != quesList.size() - 1) {
            curPosition += 1;
            curIndex += 1;
            loadUI();
            if (curPosition == 0)
                preBtn.setBackground(getDrawable(R.drawable.btn_view_shape));
            else
                preBtn.setBackground(getDrawable(R.drawable.btn_view_shape1));
            preBtn.setPadding(0, DensityUtil.dip2px(this, 10), 0, DensityUtil.dip2px(this, 10));

            if (curPosition == quesList.size() - 1)
                nextBtn.setBackground(getDrawable(R.drawable.btn_view_shape));
            else
                nextBtn.setBackground(getDrawable(R.drawable.btn_view_shape1));
            nextBtn.setPadding(0, DensityUtil.dip2px(this, 10), 0, DensityUtil.dip2px(this, 10));
        }
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

    private void showExplain() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_explain);
            window.setGravity(Gravity.CENTER);

            TextView content = window.findViewById(R.id.dialog_content);
            TextView cancel = window.findViewById(R.id.dialog_cancel);

            content.setText(explainList.get(curPosition).getExplain());

            cancel.setOnClickListener(v -> alertDialog.cancel());

        }
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
        }else if (id == R.id.show_explain) {
            showExplain();
        }
    }
}