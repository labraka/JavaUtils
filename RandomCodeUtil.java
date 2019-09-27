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
}
