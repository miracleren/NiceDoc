package com.miracleren;

import org.apache.poi.util.StringUtil;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用方法
 * <p>
 * by miracleren@gmail.com
 */
public class NiceUtils {


    /**
     * {{par}} 参数查找正则
     *
     * @param str 查找串
     * @return 返结果
     */
    public static Matcher getMatchingLabels(String str) {
        Pattern pattern = Pattern.compile("(?<=\\{\\{)(.+?)(?=\\}\\})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    /**
     * 补全label格式
     *
     * @param label
     * @return
     */
    public static String labelFormat(String label) {
        return "{{" + label + "}}";
    }

    /**
     * 实体类转map
     *
     * @param entity
     * @return
     */
    public static Map<String, Object> entityToMap(Object entity) {
        Map<String, Object> map = new HashMap<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            try {
                boolean flag = field.isAccessible();
                field.setAccessible(true);
                Object o = field.get(entity);
                map.put(field.getName(), o);
                field.setAccessible(flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 实体类列表转map列表
     *
     * @param entityList
     * @return
     */
    public static List<Map<String, Object>> listEntityToMap(List<Object> entityList) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Object entity : entityList) {
            list.add(entityToMap(entity));
        }
        return list;
    }

    /**
     * 转sting方法
     *
     * @param object
     * @return
     */
    public static String toString(Object object) {
        return object == null ? "" : object.toString();
    }


    /**
     * 判断对象是否是数值
     *
     * @param object
     * @return
     */
    public static boolean isNumber(Object object) {
        return object instanceof Number;
    }


    /**
     * 遍历查找内容
     *
     * @param map
     * @param value
     * @return
     */
    public static Integer findInMapByValue(Map<Integer, String> map, String value) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey(); // 返回找到值的键
            }
        }
        return null; // 如果未找到，返回null
    }


    /**
     * 将字符串日期转换为Date类型，支持多种日期格式。
     *
     * @param dateString 日期字符串
     * @param formats    可能的日期格式数组
     * @return 解析成功返回Date对象，解析失败抛出异常
     * @throws Exception 如果无法解析日期字符串
     */
    public static Date parseDate(String dateString, String[] formats) throws Exception {
        for (String format : formats) {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC")); // 设置时区，根据需要调整
            try {
                return formatter.parse(dateString);
            } catch (Exception e) {
                // 当前格式不匹配，尝试下一个格式
            }
        }
        // 所有格式都不匹配，抛出异常
        throw new Exception("无法解析日期: " + dateString);
    }

    /**
     * 获取两个符号之间的内容
     *
     * @param text  源字符串
     * @param start 开始符号
     * @param end   结束符号
     * @return 两个符号之间的内容
     */
    public static String getContentBetweenSymbols(String text, String start, String end) {
        String regex = Pattern.quote(start) + "([\\s\\S]*?)" + Pattern.quote(end);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 是否数值
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException e1) {
                return false;
            }
        }
    }

     /**
     * 将Date对象转换为时间戳（毫秒数）
     * @param date Date对象
     * @return 时间戳（毫秒数）
     */
    public static long convertToTimeStamp(Date date) {
        return date.getTime();
    }


}
