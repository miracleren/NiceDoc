package com.miracleren;

import java.net.URLDecoder;
import java.util.*;

/**
 * @author： lee
 * @email：miracleren@gmail.com
 * @date：2023/6/5
 */
public class TestTemplate {
    static String path = Main.class.getClassLoader().getResource("Template").getPath() + "/";

    /**
     * 测试示例模板生成word
     */
    public static void buildTestDocx() {
        //测试示例模板生成word
        NiceDoc docx = new NiceDoc(path + "test.docx");

        Map<String, Object> labels = new HashMap<>();
        //值标签
        labels.put("startTime", "1881年9月25日");
        labels.put("endTime", "1936年10月19日");
        labels.put("title", "精选作品目录");
        labels.put("press", "鲁迅同学出版社");

        //枚举标签
        labels.put("likeBook", 2);
        //布尔标签
        labels.put("isQ", true);
        //等于
        labels.put("isNew", 2);
        //多选二进制值
        labels.put("look", 3);
        //if语句
        labels.put("showContent", 2);
        //日期格式标签
        labels.put("printDate", new Date());

        labels.put("fileReceiveBy", "陈先生");
        labels.put("fileRelation", 2);
        labels.put("fileDate", new Date());

        //添加头像
        labels.put("headImg", path + "head.png");

        docx.pushLabels(labels);

        //表格
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


        //生成文档
        docx.save(path, UUID.randomUUID() + ".docx");
    }

    /**
     * 测试示例模板生成xlsx
     */
    public static void buildTestXlsx() {
        //测试示例模板生成word
        NiceExcel excel = new NiceExcel(path + "test.xlsx");

        Map<String, Object> labels = new HashMap<>();
        //值标签
        labels.put("date", "2023年1月1日");
        labels.put("title", "精选作品统计");
        //枚举标签
        labels.put("likeBook", 2);
        //多选二进制值
        labels.put("lookType", 3);
        //if语句
        labels.put("showBanner", 1);
        //日期格式标签
        labels.put("printDate", new Date());

        excel.pushLabels(labels);


        //表格
        List<Map<String, Object>> books = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            Map<String, Object> book = new HashMap<>();
            book.put("name", "汉文学史纲要" + i);
            book.put("time", 1900 + i + "年");
            book.put("intro", "简明扼要的介绍，本书是一本好书，推荐" + i + "星");
            book.put("byName", "作者" + i + "号");
            book.put("pages", i * 100);
            books.add(book);
        }
        excel.pushTable("books", books);


        //生成文档
        excel.save(path, UUID.randomUUID() + ".xlsx");
    }
}
