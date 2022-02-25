package com.iyuba.ieltslistening.utils;

import java.util.Map;
import java.util.Set;

public class ConstUtil {
    public static int NET_ERROR = -1;  // 网络错误
    public static int REQUEST_FINISH = 1;  // 请求成功
    public static int REFRESH_FINISH = 2;  // 刷新成功
    public static int GET_MORE_FINISH = 3;  // 下拉加载成功
    public static int UPDATE_RECYCLER = 4;  // 更新recyclerView中的显示

    public static Map<Integer, Set<String>> answerMap1;
    public static Map<Integer, String> answerMap2;

}
