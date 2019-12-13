package com.jet.cloud.jetsndcxqyapply.common;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String dateFormatYYMMDDHHmmss() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        return date;
    }

    public static String dateFormatYYMMDD() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        return date;
    }

    public static void main(String[] args) throws Exception {
        String date = DateUtils.timestampToDateStr("1551324342000");
        System.out.println(date);
    }

    public static String getDateFormat(Timestamp timestamp) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(timestamp);
        return date;
    }

    //mysql查询出来.0修改格式
    public static String getPayTime(String time) {
        String pay_time = time.replace(".0", "");
        return pay_time;
    }

    //时间戳转时间字符串
    public static String timestampToDateStr(String times) {
        long timestamp = Long.parseLong(times);
        Date date = new Date(timestamp);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(date);
        return format;
    }

}
