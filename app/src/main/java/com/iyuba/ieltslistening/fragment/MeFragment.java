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

import com.iyuba.ieltslistening.R;
import com.iyuba.ieltslistening.activity.AboutActivity;


public class MeFragment extends Fragment implements View.OnClickListener{

    private PopupWindow pop;

    public MeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        LinearLayout lyAbout = view.findViewById(R.id.ly_about);
        LinearLayout lyRule = view.findViewById(R.id.ly_rule);

        lyAbout.setOnClickListener(this);
        lyRule.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ly_about) {
            Intent intent = new Intent(requireContext(), AboutActivity.class);
            requireContext().startActivity(intent);
        }else {
            showPrivacyPop();
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
}
