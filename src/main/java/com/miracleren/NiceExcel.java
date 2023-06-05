package com.miracleren;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFTable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

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
        //遍历excel所有sheet
        for (int i = 0; i < xlsx.getNumberOfSheets(); i++) {
            XSSFSheet sheet = xlsx.getSheetAt(i);
            //表格遍历行
            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                for (int cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {
                    XSSFCell cell = row.getCell(cellNum);
                    if (cell == null) {
                        continue;
                    }
                    replaceLabelsInCell(cell, labels);
                }
            }
        }
    }

    /**
     * 段落填充标签
     *
     * @param cell
     * @param params
     */
    private void replaceLabelsInCell(XSSFCell cell, Map<String, Object> params) {
        String cellValue = cell.getStringCellValue();
        if (cellValue.isEmpty())
            return;

        Matcher labels = NiceUtils.getMatchingLabels(cellValue);
        int labelFindCount = 0;
        while (labels.find()) {
            labelFindCount++;
            String label = labels.group();

            String[] key = label.split("#");
            Integer indexName = key[0].indexOf("=") + 1 + key[0].indexOf("&") + 1;
            String keyName = indexName > 0 ? key[0].substring(0, indexName - 1) : key[0];
            //标签书签
            if (params.containsKey(keyName)) {
                //普通文本标签
                Object val = params.get(keyName) == null ? "" : params.get(keyName);
                if (key.length == 1) {
                    cellValue = cellValue.replace(NiceUtils.labelFormat(label), val.toString());
                    cell.setCellValue(cellValue);
                    continue;
                }

                if (key.length == 2) {
                    //日期类型填充
                    if (key[1].startsWith("Date:")) {
                        String textVal = val.equals("") ? val.toString() : new SimpleDateFormat(key[1].replace("Date:", "")).format(val);
                        cellValue = cellValue.replace(NiceUtils.labelFormat(label), textVal);
                        cell.setCellValue(cellValue);
                        continue;
                    }

                    //枚举数组标签
                    if (key[1].startsWith("[") && key[1].endsWith("]")) {
                        String group = key[1].substring(1, key[1].length() - 1);
                        for (String keyVal : group.split(",")) {
                            if (keyVal.indexOf(val + ":") == 0) {
                                cellValue = cellValue.replace(NiceUtils.labelFormat(label), keyVal.replace(val + ":", ""));
                                cell.setCellValue(cellValue);
                            }
                        }
                        continue;
                    }

                    //值判定类型标签
                    String[] bool = key[1].split(":");
                    String trueVal = bool[0];
                    String falseVal = bool.length == 1 ? "" : bool[1];
                    if (bool.length >= 1) {
                        String textVal = "";
                        if (key[0].contains("=")) {
                            textVal = val.toString().equals(key[0].substring(indexName)) ? trueVal : falseVal;
                        } else if (key[0].contains("&")) {
                            Integer curVal = Integer.valueOf(key[0].substring(indexName));
                            textVal = (Integer.valueOf(val.toString()) & curVal) == curVal ? trueVal : falseVal;
                        } else {
                            textVal = val.toString().equals("true") ? trueVal : falseVal;
                        }
                        cellValue = cellValue.replace(NiceUtils.labelFormat(label), textVal);
                        cell.setCellValue(cellValue);
                        continue;
                    }

                }
            }
        }

    }

    /**
     * 保存excel文件到目录下
     *
     * @param path
     * @param name
     */
    public void save(String path, String name) {
        try {
            FileOutputStream outStream = new FileOutputStream(path + name);
            xlsx.write(outStream);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
