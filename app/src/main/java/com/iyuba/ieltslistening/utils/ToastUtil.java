package com.iyuba.ieltslistening.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static Toast mTextToast;

    public static void showToast(Context context, String msg) {
        if (mTextToast == null) {
            mTextToast = new Toast(context);
            mTextToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }else {
            mTextToast.setText(msg);
        }
        mTextToast.show();
    }
}
