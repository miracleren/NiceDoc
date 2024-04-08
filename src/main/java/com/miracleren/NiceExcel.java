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
        String cellValue = cell.toString();
        if (cellValue.isEmpty() || cellValue.contains("col#"))
            return;

        Matcher labels = NiceUtils.getMatchingLabels(cellValue);
        while (labels.find()) {
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
                    }

                }
            } else if (keyName.equals("v-if")) {
                logicLabelsInParagraph(cell, params);
            }
        }

    }


    /**
     * 逻辑语句处理，同一cell内有效
     */
    private void logicLabelsInParagraph(XSSFCell cell, Map<String, Object> params) {
        String cellValue = cell.toString();

        Boolean isShow = true;
        Matcher labels = NiceUtils.getMatchingLabels(cellValue);
        while (labels.find()) {
            String label = labels.group();
            String[] key = label.split("#");

            if (key.length == 2) {
                Integer indexName = key[1].indexOf("=") + 1 + key[1].indexOf("&") + 1;
                String keyName = indexName > 0 ? key[1].substring(0, indexName - 1) : key[1];
                if (params.containsKey(keyName)) {
                    String val = params.get(keyName) == null ? "" : params.get(keyName).toString();
                    //条件判断语句
                    if (key[0].equals("v-if")) {
                        if (key[1].contains("=")) {
                            isShow = val.equals(key[1].substring(indexName));
                        } else if (key[1].contains("&")) {
                            Integer curVal = Integer.valueOf(key[1].substring(indexName));
                            isShow = (Integer.valueOf(val) & curVal) == curVal;
                        } else {
                            isShow = val.equals("true");
                        }

                        if (isShow == false) {
                            if (cellValue.indexOf("{{end-if}}") > cellValue.indexOf(NiceUtils.labelFormat(label)))
                                cellValue = cellValue.replace(cellValue.substring(cellValue.indexOf(NiceUtils.labelFormat(label)), cellValue.indexOf("{{end-if}}")), "");
                            else
                                cellValue = cellValue.replace(cellValue.substring(cellValue.indexOf(NiceUtils.labelFormat(label))), "");
                        } else cellValue = cellValue.replace(NiceUtils.labelFormat(label), "");

                        cell.setCellValue(cellValue);
                    }
                }
            } else if (label.equals("end-if")) {
                cell.setCellValue(cellValue.replace(NiceUtils.labelFormat(label), ""));
            }
        }
    }

    /**
     * 填充表格内容到excel
     * {{tableName:colName}}
     *
     * @param tableName
     * @param list
     */
    public void pushTable(String tableName, List<Map<String, Object>> list) {
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
                    if (cell != null && cell.getStringCellValue().contains(tableName + "/col#")) {
                        System.out.println("find the table by name :" + tableName);

                        // 插入数据空白行，数据往后移
                        sheet.shiftRows(rowNum + 1, sheet.getLastRowNum(), list.size() - 1);

                        //插入表格数据
                        int addNum = 0;
                        for (Map<String, Object> rowData : list) {
                            //拷贝当前行
                            XSSFRow setRow = sheet.getRow(rowNum + addNum);
                            if (list.size() > addNum + 1) {
                                XSSFRow newRow = sheet.createRow(rowNum + addNum + 1);
                                copyRow(setRow, newRow);
                            }

                            //填充当前行内容数据
                            for (int setCellNum = 0; setCellNum <= row.getLastCellNum(); setCellNum++) {
                                XSSFCell setCell = setRow.getCell(setCellNum);
                                if (setCell != null) {
                                    String text = setCell.getStringCellValue();
                                    Matcher labels = NiceUtils.getMatchingLabels(text);
                                    while (labels.find()) {
                                        String label = labels.group();
                                        String[] key = label.split("#");
                                        if (rowData.containsKey(key[key.length - 1])) {
                                            String val = text.replace(NiceUtils.labelFormat(label), rowData.get(key[key.length - 1]).toString());
                                            if (NiceUtils.isNumber(rowData.get(key[key.length - 1])))
                                                setCell.setCellValue(Double.parseDouble(val));
                                            else
                                                setCell.setCellValue(val);
                                        }
                                    }
                                }
                            }
                            addNum++;
                        }
                        return;
                    }
                }
            }
        }
    }

    /**
     * 拷贝行数据
     *
     * @param currentRow
     * @param newRow
     */
    private static void copyRow(XSSFRow currentRow, XSSFRow newRow) {
        newRow.setHeight(currentRow.getHeight());
        for (int i = 0; i < currentRow.getLastCellNum(); i++) {
            XSSFCell oldCell = currentRow.getCell(i);
            XSSFCell newCell = newRow.createCell(i);
            if (oldCell != null) {
                // 复制样式和值
                newCell.setCellStyle(oldCell.getCellStyle());
                switch (oldCell.getCellType()) {
                    case STRING:
                        newCell.setCellValue(oldCell.getStringCellValue());
                        break;
                    case NUMERIC:
                        newCell.setCellValue(oldCell.getNumericCellValue());
                        break;
                    case BOOLEAN:
                        newCell.setCellValue(oldCell.getBooleanCellValue());
                        break;
                    // ...其他类型
                    default:
                        newCell.setCellType(oldCell.getCellType());
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
