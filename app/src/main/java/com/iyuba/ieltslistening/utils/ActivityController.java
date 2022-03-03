package com.iyuba.ieltslistening.utils;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// 控制所有Activity的开关
public class ActivityController {

    private static final String TAG = "ActivityController";

    public static List<Activity> activities = new ArrayList<>();

    // 每创建一个Activity，放入List
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    // 每次关闭Activity的时候，将List中的元素移除
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    // 停止所有Activity，退出程序
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing())
                activity.finish();
        }
    }

    /**
     * 关闭指定多个Activity
     * @param num 指定关闭的个数
     */
    public static void finishSome(int num) {
        if (activities.size() >= num) {
            for (int i = 0; i < num; i++) {
                // remove之后,ArrayList的size()并没有改变，所以每移除一个，需要取到再上一个activity处理
                if (!activities.get(activities.size() - 1 -i).isFinishing()) {
                    activities.get(activities.size() - 1 - i).finish();
                    Log.d(TAG, "finishSome: isFinishing --- false" + activities.get(activities.size() - 1 -i).getClass().getSimpleName());
                }else {
                    Log.d(TAG, "finishSome: isFinishing --- true" + activities.get(activities.size() - 1 - i).getClass().getSimpleName());
                }
            }
        }
    }

    // 打印当前应用中Activity列表
    public static void showCurActivity() {
        for (Activity activity : activities) {
            Log.d(TAG, "showCurActivity:  --------------> " + activity.getClass().getSimpleName());
        }
    }

}
