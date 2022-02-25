package com.iyuba.ieltslistening.utils;

public class TimeUtils {

    /**
     * 毫秒转化为MM:ss的形式
     * @param millisecond 传递的毫秒数
     * @return 返回转换后的分分:秒秒格式字符串
     */
    public static String milliSecToMinute(int millisecond) {
        String minuteStr = "00";
        String secStr;
        int second = millisecond / 1000;
        if (second > 60) {
            int minute = second / 60;
            int second2 = second % 60;  // 去除分钟后剩余的秒数
            if (minute < 10)
                minuteStr = "0" + minute;
            else
                minuteStr = String.valueOf(minute);
            if (second2 < 10)
                secStr = "0" + second2;
            else
                secStr = String.valueOf(second2);
        }else {
            if (second < 10)
                secStr = "0" + second;
            else
                secStr = String.valueOf(second);
        }
        return minuteStr + ":" + secStr;
    }
}
