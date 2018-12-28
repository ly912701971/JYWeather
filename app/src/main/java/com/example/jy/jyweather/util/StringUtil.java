package com.example.jy.jyweather.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * <p>
 * Created by Yang on 2017/12/13.
 */
public class StringUtil {

    private static String[] weekdays = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * 用于查看text是否含有数字
     */
    public static boolean hasNumber(String text) {
        String regex = "[0-9]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    /**
     * 返回紫外线强弱等级
     */
    public static String getUvLevel(String indexText) {
        int index = Integer.parseInt(indexText);
        if (index >= 11) {
            return "极强";
        } else if (index >= 8) {
            return "很强";
        } else if (index >= 6) {
            return "强";
        } else if (index >= 3) {
            return "中等";
        }
        return "弱";
    }

    public static String getWeekday(String dateText) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = null;
        try {
            date = format.parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return weekdays[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }
}
