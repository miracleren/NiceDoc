package com.miracleren;

import org.apache.poi.hpsf.GUID;
import org.apache.poi.ss.usermodel.DateUtil;

import java.net.URLDecoder;
import java.util.*;

/**
 * 基于Apache POI开的快速模板填充生成word,excel文档工具
 * by miracleren@gmail.com
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("_   _ _          _____             ");
        System.out.println("| \\ | (_)        |  __ \\            ");
        System.out.println("|  \\| |_  ___ ___| |  | | ___   ___ ");
        System.out.println("| . ` | |/ __/ _ \\ |  | |/ _ \\ / __|");
        System.out.println("| |\\  | | (_|  __/ |__| | (_) | (__ ");
        System.out.println("|_| \\_|_|\\___\\___|_____/ \\___/ \\___|");


        //测试模板示例
        String path = Main.class.getClassLoader().getResource("Template").getPath() + "/";
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //String path = "C:/Users/Administrator/Desktop/test.docx";
        NiceDoc docx = new NiceDoc(path + "test.docx");

        Map<String, Object> labels = new HashMap<>();
        labels.put("startTime", "1881年9月25日");
        labels.put("endTime", "1936年10月19日");
        labels.put("title", "精选作品目录");
        labels.put("press", "鲁迅同学出版社");
        docx.pushLabels(labels);

        List<Map<String, Object>> books = new ArrayList<>();
        Map<String, Object> book1 = new HashMap<>();
        book1.put("name", "汉文学史纲要");
        book1.put("time", "1938年，鲁迅全集出版社");
        books.add(book1);
        Map<String, Object> book2 = new HashMap<>();
        book2.put("name", "中国小说史略");
        book2.put("time", "1923年12月，上册；1924年6月，下册");
        books.add(book2);
        docx.pushTable("books", books);


        docx.save(path, UUID.randomUUID() + ".docx");
    }
}
