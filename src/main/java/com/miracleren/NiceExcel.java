package com.miracleren;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于模板快速生成word文档
 * 目前只支持xlsx文件
 * <p>
 * by miracleren@gmail.com
 */

public class NiceExcel {
    private XSSFWorkbook xlsx;

    /**
     * 根据路径初始化word模板
     *
     * @param path
     */
    public NiceExcel(String path) {
        if (!path.endsWith(".xlsx")) System.out.println("无效文档后缀，当前只支持xlsx格式Excel文档模板。");

        FileInputStream in;
        try {
            in = new FileInputStream(path);
            xlsx = new XSSFWorkbook(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (xlsx == null) xlsx = new XSSFWorkbook();
        }
    }

    /**
     * 往模板填充标签值
     * {{labelName}}
     *
     * @param labels 标签值
     */
    public void pushLabels(Map<String, Object> labels) {

    }
}
