package com.casic.bonus.service.util;

import java.text.DecimalFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 随机码生成工具类
 */
public class RandomCodeUtil {
    public static final String NUMBER_UPLETTER_CHAR = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 根据券码长度生成优惠券码
     *
     * @param length
     * @return
     */
    public static String generateCouponCode(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(NUMBER_UPLETTER_CHAR.charAt(random.nextInt(NUMBER_UPLETTER_CHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 生成当前大写字母（A-Z）+1的字母
     *
     * @param c
     * @return
     */
    public static String generateUpLetter(char c) {
        if (c < 90) {
            c = (char) (c + 1);
        }
        String s = String.valueOf(c);
        return s;
    }

    /**
     * 生成指定长度的随机数
     *
     * @param length
     * @return
     */
    public static String generateRandomNumber(int length) {
        Double bit = Math.pow(10, length);
        Long num = Math.round(Math.random() * bit);
        String s = String.format("%0" + length + "d", num);
        return s;
    }

    /**
     * 通过时间戳生成优惠券码
     *
     * @param time
     */
    public static String generateCouponCode(Instant time) {
        Long milli = time.toEpochMilli();
        String hexString = Long.toHexString(milli);
        String upperCase = hexString.toUpperCase();
        return upperCase;
    }

    /**
     * 将字符串根据一些特殊符号分割，并将分割后的字符串首字母取出组合成新字符串
     *
     * @param str
     * @param s
     * @return
     */
    public static String generateFirstWordSplitChars(String str, String s) {
        String[] ss = str.split(s);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ss.length; i++) {
            System.out.println(ss[i]);
            sb.append(ss[i].charAt(0));
        }
        return sb.toString();
    }

    /**
     * 将字符串根据一些特殊符号分割,去掉首尾空字符
     *
     * @param str
     * @param s
     * @return
     */
    public static List getWordsSplitChars(String str, String s) {
        String[] ss = str.split(s);
        List<String> strList = new ArrayList();
        for (int i = 0; i < ss.length; i++) {
            strList.add(ss[i].trim());
        }
        return strList;
    }

    /**
     * 含有数字的字符串保留数字
     *
     * @param a
     * @return
     */
    public static Integer getNumberFromChars(String a) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(a);
        String s = m.replaceAll("").trim();
        int num = Integer.parseInt(s);
        return num;
    }

    /**
     * 截取前/后指定位数的字符
     * @param str
     * @param len 截取长度
     * @param isEndChars （true：后{len}位； false：前{len}位）
     * @return
     */
    public static String getSubStringChars(String str, Integer len, boolean isEndChars) {
        String s;
        if (isEndChars) {
            s = str.substring(str.length() - len);
        } else {
            s = str.substring(0, len);
        }

        return s;
    }

    /**
     * 根据当前时间增加指定类型和数量的日期
     * @param date
     * @param type
     * @param num
     * @return
     */
    public static Date addDate(Date date, int type, int num){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(type, num);
        date = calendar.getTime();
        return date;
    }

    /**
     *获取相差的天数
     * @param begin
     * @param end
     * @return
     */
    public static long getBetweenDays(Date begin, Date end){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(begin);
        long timeInMillis1 = calendar.getTimeInMillis();
        calendar.setTime(end);
        long timeInMillis2 = calendar.getTimeInMillis();
        long betweenDays =  (timeInMillis2 - timeInMillis1) / (1000L*3600L*24L);
        return betweenDays;
    }

    /**
     * 通过特殊的大小写生成指定长度的随机码
     * @param length
     * @param numSize
     * @param merchantIntegre
     * @return
     */
    public static HashSet<String> getCharAndNumr(int length, int numSize, HashSet<String> merchantIntegre) {
        //第一层循环用于生成积分券个数，第二层用于组合积分码
        for (int j = 0; j < numSize; j++) {

            String val = new String("");
            Random random = new Random();

            //积分码生成
            for (int i = 0; i < length; i++) {
                // 输出字母还是数字
                String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
                // 字符串
                if ("char".equalsIgnoreCase(charOrNum)) {
                    // 取得大写字母还是小写字母
                    int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                    val += (char) (choice + random.nextInt(26));
                } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                    val += String.valueOf(random.nextInt(10));
                }
            }
            val.toUpperCase();
            merchantIntegre.add(val);
        }

        // 首次循环完成，可能有重复值，生成的字符串总数小于原有，递归循环再次生成。
        while (merchantIntegre.size() < numSize) {
            numSize = numSize - merchantIntegre.size();
            // 继续循环生成
            getCharAndNumr(length, numSize, merchantIntegre);

        }
        return merchantIntegre;
    }

    /**
     * 将价格格式化显示
     * @param price
     * @return
     */
    public static String formatPrice(BigDecimal price){
        DecimalFormat decimalFormat=new DecimalFormat("###.###");
        String s = decimalFormat.format(price);
        return s;
    }
}
