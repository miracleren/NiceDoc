package com.miracleren;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

}
