package com.ruoyi.common.utils;

import jxl.format.UnderlineStyle;
import jxl.write.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.*;
import java.util.*;

/**
 * 导出复杂表头execl
 *
 * @author zhangdke
 * 2019年8月16日
 */
public class PoiUtil {



    /**
     * 新增行
     * @param sheet
     * @param startRow 开始行
     * @param rows 行数
     */
    public  static void insertRow(HSSFSheet sheet, int startRow, int rows,int beginRow) {
        sheet.shiftRows(startRow, sheet.getLastRowNum(), rows, true, false);


        for (int i = 0; i < rows; i++) {
            HSSFRow sourceRow = null;//原始位置
            HSSFRow targetRow = null;//移动后位置
            HSSFCell sourceCell = null;
            HSSFCell targetCell = null;
            sourceRow = sheet.createRow(startRow);
            targetRow = sheet.getRow(beginRow);
            sourceRow.setHeight(targetRow.getHeight());

            for (int m = targetRow.getFirstCellNum(); m < targetRow.getPhysicalNumberOfCells(); m++) {
                    sourceCell = sourceRow.createCell(m);
                    targetCell = targetRow.getCell(m);
                    sourceCell.setCellStyle(targetCell.getCellStyle());
                    sourceCell.setCellType(targetCell.getCellType());
            }
            startRow++;
        }
    }
    private PoiUtil() {
    }

    public static HSSFWorkbook workbook;
    public static HSSFSheet sheet;

    /**
     * 创建行元素
     *
     * @param style  样式
     * @param height 行高
     * @param value  行显示的内容
     * @param row1   起始行
     * @param row2   结束行
     * @param col1   起始列
     * @param col2   结束列
     */
    public void createRow(HSSFCellStyle style, int height, String value, int row1, int row2, int col1, int col2) {
        sheet.addMergedRegion(new CellRangeAddress(row1, row2, col1, col2)); //设置从第row1行合并到第row2行，第col1列合并到col2列
        HSSFRow rows = sheet.createRow(row1); //设置第几行
        rows.setHeight((short) height); //设置行高
        HSSFCell cell = rows.createCell(col1); //设置内容开始的列
        cell.setCellStyle(style); //设置样式
        cell.setCellValue(value); //设置该行的值
    }


    public static void createRow(int height, int row1, int row2, List<Map<String, Object>> paramsMapList, boolean isMerge) {
        if (isMerge) {
            Map<String, Object> paramsMap = paramsMapList.get(0);
            sheet.addMergedRegion(new CellRangeAddress(row1, row2, StringUtils.getObjInt(paramsMap.get("clo1")), StringUtils.getObjInt(paramsMap.get("clo2")))); //设置从第row1行合并到第row2行，第col1列合并到col2列
            HSSFRow rows = sheet.createRow(row1); //设置第几行
            rows.setHeight((short) height); //设置行高
            HSSFCell cell = rows.createCell(StringUtils.getObjInt(paramsMap.get("clo1"))); //设置内容开始的列
            cell.setCellStyle((HSSFCellStyle) paramsMap.get("style")); //设置样式
            cell.setCellValue(StringUtils.getObjStr(paramsMap.get("value"))); //设置该行的值
        } else {
            HSSFRow rows = sheet.createRow(row1); //设置第几行
            rows.setHeight((short) height); //设置行高
            for (Map<String, Object> paramsMap : paramsMapList) {
                HSSFCell cell = rows.createCell(StringUtils.getObjInt(paramsMap.get("clo"))); //设置内容开始的列
                cell.setCellStyle((HSSFCellStyle) paramsMap.get("style")); //设置样式
                cell.setCellValue(StringUtils.getObjStr(paramsMap.get("value"))); //设置该行的值
            }
        }
    }

    /**
     * 创建样式
     *
     * @param fontSize 字体大小
     * @param align    水平位置
     * @param bold     是否加粗
     * @return
     */
    public static HSSFCellStyle getStyle(int fontSize, short align, boolean bold, boolean border, boolean isbgColor) {
        HSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) fontSize);// 字体大小
        font.setBold(bold);

        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); //设置字体
        style.setAlignment(HorizontalAlignment.forInt(align)); // 左右居中2 居右3 默认居左
        style.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中1
        // style.setFillForegroundColor((short) 13);
        if (isbgColor) {
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (border) {
            /*细边框*/
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setLocked(true);
        }
        return style;
    }

    /**
     * 根据数据集生成Excel，并返回Excel文件流
     *
     * @param data      数据集
     * @param sheetName Excel中sheet单元名称
     * @param colKeys   列key,数据集根据该key进行按顺序取值
     * @return
     * @throws IOException
     */
    public static InputStream getExcelFile(List<Map<String, Object>> data, String sheetName, List<String> title, List<String> colKeys) throws IOException {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(sheetName);
        // 创建表头 startRow代表表体开始的行
        int startRow = createHeadCell(title);

        // 创建表体数据
        HSSFCellStyle cellStyle = getStyle(12, HorizontalAlignment.CENTER.getCode(), false, true, false); // 建立新的cell样式
        setCellData(data, cellStyle, startRow, colKeys);

        // //创建表尾
        //  createTailCell(data, data.size() + 4, colWidths, colKeys);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        byte[] ba = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        return bais;
    }

    /**
     * 创建表头
     *
     * @param colKeys 列信息
     */
    public static int createHeadCell(List<String> colKeys) {
        //第四行表头
        HSSFRow row2 = sheet.createRow(0);
        row2.setHeight((short) 0x289);
        HSSFCellStyle cellStyle = getStyle(12, HorizontalAlignment.CENTER.getCode(), false, true, true); // 建立新的cell样式

        for (int i = 0; i < colKeys.size(); i++) {
            Cell tempCell = row2.createCell(i);
            // tempCell.setCellValue(row_three[i]);
            tempCell.setCellValue(colKeys.get(i));
            tempCell.setCellStyle(cellStyle);
            sheet.setColumnWidth(i, 4000);//宽度

        }
        return 1; //数据从哪一行开始渲染表体
    }

    /**
     * 创建表体数据
     *
     * @param data      表体数据
     * @param cellStyle 样式
     * @param startRow  开始行
     * @param colKeys   值对应map的key
     */
    public static void setCellData(List<Map<String, Object>> data, HSSFCellStyle cellStyle, int startRow, List<String> colKeys) {
        // 创建数据
        HSSFRow row = null;
        HSSFCell cell = null;

        if (data != null && data.size() > 0) {
            for (Map<String, Object> rowData : data) {
                row = sheet.createRow(startRow);
                row.setHeight((short) 0x189);
                int j = 0;

                for (int k = 0; k < colKeys.size(); k++) {
                    String colKey = colKeys.get(k);//获取Key值
                    Object colValue = rowData.get(colKey);

                    cell = row.createCell(j);
                    cell.setCellStyle(cellStyle);
                    if (colValue != null) {
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(colValue.toString());
                    }
                    j++;
                }
                startRow++;
            }
        }
    }

    /**
     * 创建表尾
     *
     * @param size
     * @param
     */
    public static void createTailCell(List<Map> data, int size, int colWidths[], String[] colKeys) {
        HSSFRow row2 = sheet.createRow(size);
        row2.setHeight((short) 0x379);
        HSSFCellStyle cellStyle = getStyle(15, HorizontalAlignment.CENTER.getCode(), false, true, false); // 建立新的cell样式
        String[] row_three = {"超时小计:", "", "", "", "", "", "rwsum", "", "", "cjsum", "", "", "jysum", "", "", "jssum", "", "", "fksum", "", "", ""};
        for (int i = 0; i < row_three.length; i++) {
            Cell tempCell = row2.createCell(i);
            if (i == 6 || i == 9 || i == 12 || i == 15 || i == 18) {
                int num = 0;
                int sum = 0;
                for (int j = i; j < i + 3; j++) {
                    for (Map<String, Object> map : data) {
                        num = (int) map.get(colKeys[j]);
                    }
                    sum += num;
                }
                tempCell.setCellValue(sum);
                tempCell.setCellStyle(cellStyle);
                continue;
            }
            tempCell.setCellValue(row_three[i]);
            tempCell.setCellStyle(cellStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(size, size, 0, 5));
        sheet.addMergedRegion(new CellRangeAddress(size, size, 6, 8));
        sheet.addMergedRegion(new CellRangeAddress(size, size, 9, 11));
        sheet.addMergedRegion(new CellRangeAddress(size, size, 12, 14));
        sheet.addMergedRegion(new CellRangeAddress(size, size, 15, 17));
        sheet.addMergedRegion(new CellRangeAddress(size, size, 18, 20));

    }



    // 测试
    public static void main(String[] args) throws IOException {
        PoiUtil excel = new PoiUtil();
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> e = new HashMap<String, Object>();
        e.put("xh", "");
        e.put("xh1", "");
        e.put("xh2", "");
        e.put("unit", "合计");
        e.put("wwdw", "");
        e.put("date", "");
        e.put("swsh", "");
        e.put("swgd", "");
        e.put("swcs", "");
        e.put("cjsh", "");
        e.put("cjgd", "");
        e.put("cjcs", "");
        data.add(e);
        for (int i = 0; i < 10; i++) {
            Map<String, Object> d = new HashMap<String, Object>();
            d.put("xh", i);
            d.put("xh1", i);
            d.put("xh2", i);
            d.put("unit", i);
            d.put("wwdw", i);
            d.put("date", i);
            d.put("swsh", i);
            d.put("swgd", i);
            d.put("swcs", i);
            d.put("cjsh", i);
            d.put("cjgd", i);
            d.put("cjcs", i);
            data.add(d);
        }


        // String[] headNames = {"序号", "", "", "委托单位", "外委单位", "提交工作日期", "审核时长", "规定时长", "超时时长", "审核时长", "规定时长", "超时时长"};
        // String[] keys = {"xh", "xh1", "xh2", "unit", "wwdw", "date", "swsh", "swgd", "swcs", "cjsh", "cjgd", "cjcs"};
        int colWidths[] = {200, 200, 200, 300, 400, 400, 400, 200, 600, 400, 500, 400};

        InputStream input = (excel.getExcelFile(data, "Z03 收入决算表(财决03表)", new ArrayList<>(),new ArrayList<>()));

        File f = new File("d:\\excel6.xls");
        if (f.exists())
            f.delete();
        f.createNewFile();
        FileOutputStream out = new FileOutputStream(f);
        HSSFWorkbook book = new HSSFWorkbook(input);
        book.write(out);
        out.flush();
        out.close();
    }

}