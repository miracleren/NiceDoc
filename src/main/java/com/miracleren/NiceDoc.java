package com.miracleren;

import com.sun.org.apache.xpath.internal.objects.XObject;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * 基于模板快速生成word文档
 * 目前只支持docx文件
 * <p>
 * by miracleren@gmail.com
 */

public class NiceDoc {
    //private HWPFDocument doc;
    private XWPFDocument docx;

    /**
     * 根据路径初始化word模板
     *
     * @param path
     */
    public NiceDoc(String path) {
        if (!path.endsWith(".docx"))
            System.out.println("无效文档后缀，当前只支持docx格式word文档模板。");

        FileInputStream in;
        try {
            in = new FileInputStream(path);
            docx = new XWPFDocument(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (docx == null)
                docx = new XWPFDocument();
        }
    }


    /**
     * 往模板填充标签值
     * {{labelName}}
     *
     * @param labels 标签值
     * @return
     */
    public void pushLabels(Map<String, Object> labels) {
        //遍历普通段落内容对像，填充标签值
        List<XWPFParagraph> paragraphs = docx.getParagraphs();
        replaceLabelsInParagraphs(paragraphs, labels);

        //遍历表格内容，并填充标签值
        List<XWPFTable> tables = docx.getTables();
        for (XWPFTable table : tables) {
            //表格行
            List<XWPFTableRow> rows = table.getRows();
            for (XWPFTableRow row : rows) {
                //表格单元格
                List<XWPFTableCell> cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    //表格段落
                    List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                    replaceLabelsInParagraphs(cellParagraphs, labels);
                }
            }
        }

        //页眉标签值填充
        List<XWPFHeader> headers = docx.getHeaderList();
        for (XWPFHeader header : headers) {
            List<XWPFParagraph> headerParagraphs = header.getListParagraph();
            replaceLabelsInParagraphs(headerParagraphs, labels);
        }

        //页脚填充
        List<XWPFFooter> footers = docx.getFooterList();
        for (XWPFFooter footer : footers) {
            List<XWPFParagraph> footerParagraphs = footer.getListParagraph();
            replaceLabelsInParagraphs(footerParagraphs, labels);
        }
    }

    /**
     * 往模板填充标签值实体类
     *
     * @param entity
     */
    public void pushLabels(Object entity) {
        pushLabels(NiceUtils.entityToMap(entity));
    }

    /**
     * 填充表格内容到文档
     * {{tableName:colName}}
     *
     * @param tableName
     * @param list
     */
    public void pushTable(String tableName, List<Map<String, Object>> list) {
        List<XWPFTable> tables = docx.getTables();
        for (XWPFTable table : tables) {
            boolean isFind = false;
            XWPFTableRow baseRow = null;

            List<XWPFTableRow> rows = table.getRows();
            int rowCount = rows.size();
            for (int i = 0; i < rowCount; i++) {
                List<XWPFTableCell> cells = rows.get(i).getTableCells();
                for (XWPFTableCell cell : cells) {
                    List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                    for (XWPFParagraph cellParagraph : cellParagraphs) {
                        //查找表格标识名称
                        if (!isFind) {
                            if (cellParagraph.getText().contains(NiceUtils.labelFormat("table:" + tableName))) {
                                isFind = true;
                            } else {
                                isFind = false;
                                break;
                            }
                        }

                        //记录开始数据行
                        if (cellParagraph.getText().contains("{{col:")) {
                            baseRow = rows.get(i);
                            break;
                        }
                    }
                    if (!isFind)
                        break;
                }
                if (!isFind)
                    break;

                //已知数据行，开始填充数据
                if (baseRow != null) {
                    int addRowIndex = 1;
                    for (Map<String, Object> listRow : list) {
                        CTRow ctRow = table.getCTTbl().insertNewTr(i + addRowIndex);
                        XWPFTableRow newRow = new XWPFTableRow(ctRow, table);
                        copyRowAndPushLabels(newRow, baseRow, listRow);
                        //table.addRow(newRow, i + addRowIndex);
                        addRowIndex++;
                    }

                    baseRow = null;
                    table.removeRow(i);
                }
            }
            //删除table标识行
            if (isFind)
                table.removeRow(0);
        }
    }

    /**
     * 拷贝行，并填充相关值
     *
     * @param newRow
     * @param baseRow
     * @param params
     */
    private void copyRowAndPushLabels(XWPFTableRow newRow, XWPFTableRow baseRow, Map<String, Object> params) {
        newRow.getCtRow().setTrPr(baseRow.getCtRow().getTrPr());
        for (XWPFTableCell cell : baseRow.getTableCells()) {
            XWPFTableCell newCell = newRow.addNewTableCell();
            newCell.getCTTc().setTcPr(cell.getCTTc().getTcPr());
            boolean isFirst = true;
            //newCell.setParagraph(cell.getParagraphs().get(0));
            for (XWPFParagraph paragraph : cell.getParagraphs()) {
                XWPFParagraph newParagraph = isFirst ? newCell.getParagraphs().get(0) : newCell.addParagraph();
                isFirst = false;
                newParagraph.getCTP().setPPr(paragraph.getCTP().getPPr());
                for (XWPFRun run : paragraph.getRuns()) {
                    XWPFRun newRun = newParagraph.createRun();
                    newRun.getCTR().setRPr(run.getCTR().getRPr());

                    String text = run.getText(0);
                    if (text == null)
                        continue;

                    Matcher labels = NiceUtils.getMatchingLabels(text);
                    while (labels.find()) {
                        String label = labels.group();
                        String[] key = label.split(":");
                        if (params.containsKey(key[key.length - 1])) {
                            newRun.setText(text.replace(NiceUtils.labelFormat(label), params.get(key[key.length - 1]).toString()), 0);
                        }
                    }
                }
            }

        }
    }

    /**
     * 段落列表填充标签
     *
     * @param paragraphs
     * @param params
     */
    private void replaceLabelsInParagraphs(List<XWPFParagraph> paragraphs, Map<String, Object> params) {
        for (XWPFParagraph paragraph : paragraphs) {
            String text = paragraph.getText();
            if (text == null || text.equals("") || !text.contains("{{"))
                continue;
            else
                replaceLabelsInParagraph(paragraph, params);
        }
    }

    /**
     * 段落填充标签
     *
     * @param paragraph
     * @param params
     */
    private void replaceLabelsInParagraph(XWPFParagraph paragraph, Map<String, Object> params) {
        //遍历文本对象，查找标识标签
        List<XWPFRun> runs = paragraph.getRuns();
        String nowText = "";
        int runCount = 0;
        for (XWPFRun run : runs) {
            //防止文本对象标签被分割
            if (run.getText(0) != null && (run.getText(0).contains("{{") || runCount > 0)) {
                nowText += run.getText(0);
                runCount++;
                System.out.println(nowText);
                Matcher labels = NiceUtils.getMatchingLabels(nowText);
                int labelFindCount = 0;
                while (labels.find()) {
                    labelFindCount++;
                    String label = labels.group();
                    //普通文本标签
                    String[] key = label.split(":");
                    if (params.containsKey(key[key.length - 1])) {
                        if (key.length != 0 && label.startsWith("col:"))
                            run.setText(nowText.replace(NiceUtils.labelFormat(label), params.get(key[key.length - 1]).toString()), 0);
                    }
                }

                if (labelFindCount > 0) {
                    nowText = "";
                    runCount = 0;
                }
            }

        }
    }


    /**
     * 保存word文件到目录下
     *
     * @param path
     * @param name
     */
    public void save(String path, String name) {
        try {
            FileOutputStream outStream = new FileOutputStream(path + name);
            docx.write(outStream);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
