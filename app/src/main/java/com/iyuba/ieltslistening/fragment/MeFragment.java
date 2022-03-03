package com.iyuba.ieltslistening.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.activity.AboutActivity;
import com.iyuba.ieltslistening.activity.ChangeInfoActivity;
import com.iyuba.ieltslistening.activity.SecLoginActivity;
import com.iyuba.ieltslistening.utils.SharedPreferencesUtils;

import cn.smssdk.ui.companent.CircleImageView;


public class MeFragment extends Fragment implements View.OnClickListener{

    private PopupWindow pop;
    private CircleImageView uHead;
    private TextView uName;
    private LinearLayout userinfoLy;
    private TextView tvLogin;

    public MeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        uHead = view.findViewById(R.id.u_head);
        uName = view.findViewById(R.id.u_name);
        userinfoLy = view.findViewById(R.id.userinfo_ly);
        tvLogin = view.findViewById(R.id.tv_login);
        TextView changeInfo = view.findViewById(R.id.change_info);
        LinearLayout lyAbout = view.findViewById(R.id.ly_about);
        LinearLayout lyRule = view.findViewById(R.id.ly_rule);
        LinearLayout lyLogin = view.findViewById(R.id.ly_login);

        changeInfo.setOnClickListener(this);
        lyAbout.setOnClickListener(this);
        lyRule.setOnClickListener(this);
        lyLogin.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ly_about) {
            Intent intent = new Intent(requireContext(), AboutActivity.class);
            requireContext().startActivity(intent);
        }else if (id == R.id.ly_login) {
            if (SharedPreferencesUtils.getBoolean(getContext(), SharedPreferencesUtils.USER_INFO, "isLogin")) {
                SharedPreferencesUtils.setBoolean(getContext(), SharedPreferencesUtils.USER_INFO, "isLogin", false);;
                userinfoLy.setVisibility(View.GONE);
                tvLogin.setText("登录");
            }else {
                Intent intent = new Intent(requireContext(), SecLoginActivity.class);
                requireContext().startActivity(intent);
            }
        }else if (id == R.id.ly_rule){
            showPrivacyPop();
        }else if (id == R.id.change_info) {
            Intent intent = new Intent(getContext(), ChangeInfoActivity.class);
            getActivity().startActivity(intent);
        }
    }

    // 展示隐私pop弹框
    private void showPrivacyPop() {
        View bottomView = View.inflate(getActivity(), R.layout.privacy_pop, null);
        TextView mUse = bottomView.findViewById(R.id.tv_1_pri);
        TextView mPri = bottomView.findViewById(R.id.tv_2_pri);
        TextView mCc = bottomView.findViewById(R.id.tv_cancel_pri);

        pop = new PopupWindow(bottomView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = requireActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        requireActivity().getWindow().setAttributes(lp);
        pop.setOnDismissListener(() -> {
            WindowManager.LayoutParams lp1 = requireActivity().getWindow().getAttributes();
            lp1.alpha = 1f;
            requireActivity().getWindow().setAttributes(lp1);
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(requireActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        @SuppressLint("NonConstantResourceId") View.OnClickListener clickListener = view -> {
            switch (view.getId()) {
                case R.id.tv_1_pri:
                    //使用条款
                    Uri uri = Uri.parse("https://ai.iyuba.cn/api/protocoluse.jsp?apptype=雅思听力&company=1");
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(uri);
                    startActivity(intent);
                    break;
                case R.id.tv_2_pri:
                    //隐私政策
                    Uri uri2 = Uri.parse("https://ai.iyuba.cn/api/protocolpri.jsp?apptype=雅思听力&company=1");
                    Intent intent2 = new Intent();
                    intent2.setAction("android.intent.action.VIEW");
                    intent2.setData(uri2);
                    startActivity(intent2);
                    break;
                case R.id.tv_cancel_pri:
                    //取消
                    closePopupWindow();
                    break;
            }
            closePopupWindow();
        };
        mUse.setOnClickListener(clickListener);
        mPri.setOnClickListener(clickListener);
        mCc.setOnClickListener(clickListener);
    }

    // 关闭Pop弹框
    public void closePopupWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SharedPreferencesUtils.getBoolean(getContext(), SharedPreferencesUtils.USER_INFO,"isLogin")) {
            userinfoLy.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(SharedPreferencesUtils.getString(getContext(), SharedPreferencesUtils.USER_INFO, "head")).into(uHead);
            tvLogin.setText("退出登录");
            uName.setText(SharedPreferencesUtils.getString(getContext(), SharedPreferencesUtils.USER_INFO, "nickname"));
        }else {
            userinfoLy.setVisibility(View.GONE);
            tvLogin.setText("登录");
        }
    }
}
