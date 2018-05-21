package weaver.interfaces.jiangyl.kq;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;

import weaver.hrm.company.DepartmentComInfo;

public class ExportExcel {

    public static void exportExcel(String departmentid,
                                   String fromdate2,
                                   String title,
                                   String[] headers,
                                   OutputStream out,
                                   String pattern,
                                   int days) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 8);
        // 生成一个样式
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.WHITE.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);

        // 声明一个画图的顶级管理器
        //        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        //        // 定义注释的大小和位置,详见文档
        //        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
        //        // 设置注释内容
        //        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        //        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        //        comment.setAuthor("leno");

        DepartmentComInfo department = null;
        try {
            department = new DepartmentComInfo();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String departmentName = department.getDepartmentname(departmentid);

        HSSFRow row0 = sheet.createRow(0);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell0 = row0.createCell(i);
            cell0.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell0.setCellStyle(style);
            cell0.setCellValue(departmentName + fromdate2 + "考勤汇总情况确认表");
        }
        sheet.addMergedRegion(new Region(0, (short) (0), 0, (short) (headers.length - 1)));

        // 产生表格标题行
        HSSFRow row = sheet.createRow(1);
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellStyle(style);
            cell.setCellValue(headers[i]);
        }
        TlHzkq tl = new TlHzkq();
        Map<String, TlHzMode> am = tl.getAm(departmentid, fromdate2);
        Map<String, TlHzMode> pm = tl.getPm(departmentid, fromdate2);
        java.util.List<String> holidayList = getHolidayList();
        int count = 2;
        for (Entry<String, TlHzMode> entry : am.entrySet()) {
            String key = entry.getKey();
            TlHzMode value = entry.getValue();
            String name = value.getName();
            String bjhj = value.getBjhj();
            String cjhj = value.getCjhj();
            String hjhj = value.getHjhj();
            String njhj = value.getNjhj();
            String sjhj = value.getSjhj();
            String txhj = value.getTxhj();

            String jdjhj = value.getJdjhj();
            String srjhj = value.getSrjhj();
            String sangjhj = value.getSangjhj();
            String pcjhjs = value.getPcjhj();
            String brjhj = value.getBrjhj();
            String tqjhj = value.getTqjhj();

            Map<String, String> map = value.getMap();

            HSSFRow row1 = sheet.createRow(count);
            HSSFCell cell = row1.createCell((short) 0);
            cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellStyle(style2);
            cell.setCellValue(name);

            for (short i = 1; i <= days; i++) {
                String dateStr = "";
                if (String.valueOf(i).length() == 1) {
                    dateStr = fromdate2 + "-0" + String.valueOf(i);
                } else {
                    dateStr = fromdate2 + "-" + String.valueOf(i);
                }
                if (isWeekend(dateStr) || holidayList.contains(dateStr)) {
                    HSSFCell cell1 = row1.createCell((short) i);
                    cell1.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell1.setCellStyle(style2);
                    cell1.setCellValue("");
                } else {
                    HSSFCell cell1 = row1.createCell((short) i);
                    cell1.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell1.setCellStyle(style2);
                    if (map.containsKey(String.valueOf(i))) {
                        cell1.setCellValue(map.get(String.valueOf(i)));
                    } else {
                        cell1.setCellValue("旷工");
                    }
                }
            }
            int n = 0;
            String[] strs = new String[] { sjhj, bjhj, cjhj, njhj, hjhj, txhj,jdjhj,srjhj,sangjhj,pcjhjs,brjhj,tqjhj, " " };
            for (short j = (short) (headers.length - 13); j < headers.length; j++) {
                HSSFCell cell1 = row1.createCell((short) j);
                cell1.setEncoding(HSSFCell.ENCODING_UTF_16);
                cell1.setCellStyle(style2);
                cell1.setCellValue(strs[n]);
                n++;
            }
            count++;

            if (pm.containsKey(key)) {
                TlHzMode pvalue = pm.get(key);
                String pname = pvalue.getName();
                String pbjhj = pvalue.getBjhj();
                String pcjhj = pvalue.getCjhj();
                String phjhj = pvalue.getHjhj();
                String pnjhj = pvalue.getNjhj();
                String psjhj = pvalue.getSjhj();
                String ptxhj = pvalue.getTxhj();

                String pjdjhj = pvalue.getJdjhj();
                String psrjhj = pvalue.getSrjhj();
                String psangjhj = pvalue.getSangjhj();
                String ppcjhjs = pvalue.getPcjhj();
                String pbrjhj = pvalue.getBrjhj();
                String ptqjhj = pvalue.getTqjhj();

                Map<String, String> pmap = pvalue.getMap();

                HSSFRow row11 = sheet.createRow(count);
                HSSFCell cell1 = row11.createCell((short) 0);
                cell1.setEncoding(HSSFCell.ENCODING_UTF_16);
                cell1.setCellStyle(style2);
                cell1.setCellValue(pname);

                for (short i = 1; i <= days; i++) {
                    String dateStr = "";
                    if (String.valueOf(i).length() == 1) {
                        dateStr = fromdate2 + "-0" + String.valueOf(i);
                    } else {
                        dateStr = fromdate2 + "-" + String.valueOf(i);
                    }
                    if (isWeekend(dateStr) || holidayList.contains(dateStr)) {
                        HSSFCell cell11 = row11.createCell((short) i);
                        cell11.setEncoding(HSSFCell.ENCODING_UTF_16);
                        cell11.setCellStyle(style2);
                        cell11.setCellValue("");
                    } else {
                        HSSFCell cell11 = row11.createCell((short) i);
                        cell11.setEncoding(HSSFCell.ENCODING_UTF_16);
                        cell11.setCellStyle(style2);
                        if (pmap.containsKey(String.valueOf(i))) {
                            cell11.setCellValue(pmap.get(String.valueOf(i)));
                        } else {
                            cell11.setCellValue("旷工");
                        }
                    }
                }
                int n1 = 0;
                String[] strs1 = new String[] { psjhj, pbjhj, pcjhj, pnjhj, phjhj, ptxhj,pjdjhj,psrjhj,psangjhj,ppcjhjs,pbrjhj,ptqjhj, "" };
                for (short j = (short) (headers.length - 13); j < headers.length; j++) {
                    HSSFCell cell11 = row11.createCell((short) j);
                    cell11.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell11.setCellStyle(style2);
                    cell11.setCellValue(strs1[n1]);
                    n1++;
                }
            } else {
                TlHzMode pvalue = new TlHzMode();
                String pbjhj = pvalue.getBjhj();
                String pcjhj = pvalue.getCjhj();
                String phjhj = pvalue.getHjhj();
                String pnjhj = pvalue.getNjhj();
                String psjhj = pvalue.getSjhj();
                String ptxhj = pvalue.getTxhj();
                String pjdjhj = value.getJdjhj();
                String psrjhj = value.getSrjhj();
                String psangjhj = value.getSangjhj();
                String ppcjhjs = value.getPcjhj();
                String pbrjhj = value.getBrjhj();
                String ptqjhj = value.getTqjhj();
                Map<String, String> pmap = pvalue.getMap();

                HSSFRow row11 = sheet.createRow(count);
                HSSFCell cell1 = row11.createCell((short) 0);
                cell1.setEncoding(HSSFCell.ENCODING_UTF_16);
                cell1.setCellStyle(style2);
                cell1.setCellValue(name);

                for (short i = 1; i <= days; i++) {
                    String dateStr = "";
                    if (String.valueOf(i).length() == 1) {
                        dateStr = fromdate2 + "-0" + String.valueOf(i);
                    } else {
                        dateStr = fromdate2 + "-" + String.valueOf(i);
                    }
                    if (isWeekend(dateStr) || holidayList.contains(dateStr)) {
                        HSSFCell cell11 = row11.createCell((short) i);
                        cell11.setEncoding(HSSFCell.ENCODING_UTF_16);
                        cell11.setCellStyle(style2);
                        cell11.setCellValue("");
                    } else {
                        HSSFCell cell11 = row11.createCell((short) i);
                        cell11.setEncoding(HSSFCell.ENCODING_UTF_16);
                        cell11.setCellStyle(style2);
                        if (pmap.containsKey(String.valueOf(i))) {
                            cell11.setCellValue(pmap.get(String.valueOf(i)));
                        } else {
                            cell11.setCellValue("旷工");
                        }
                    }
                }
                int n1 = 0;
                String[] strs1 = new String[] { psjhj, pbjhj, pcjhj, pnjhj, phjhj, ptxhj,pjdjhj,psrjhj,psangjhj,ppcjhjs,pbrjhj,ptqjhj, "" };
                for (short j = (short) (headers.length - 13); j < headers.length; j++) {
                    HSSFCell cell11 = row11.createCell((short) j);
                    cell11.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell11.setCellStyle(style2);
                    cell11.setCellValue(strs1[n1]);
                    n1++;
                }
            }
            count++;
        }
        int total = am.size() + 2;
        for (int k = 2; k < total * 2; k+=2) {
            sheet.addMergedRegion(new Region(k, (short) (0), k + 1, (short) (0)));
            sheet.addMergedRegion(new Region(k, (short) (headers.length - 1), k + 1, (short) (headers.length - 1)));
        }

        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行
     *
     * @param departmentid
     * @param date
     */
    public static void exec(String departmentid,String date) {
        int days = getDaysOfSpecificMonth(date + "-01");
        StringBuffer sb = new StringBuffer("");
        sb.append("姓名,");
        for (int j = 1; j <= days; j++) {
            sb.append(j + ",");
        }
        sb.append("事假合计,");
        sb.append("病假合计,");
        sb.append("产假合计,");
        sb.append("年假合计,");
        sb.append("婚假合计,");
        sb.append("调休合计,");
        sb.append("积分合计,");
        sb.append("生日合计,");
        sb.append("丧假合计,");
        sb.append("陪产合计,");
        sb.append("哺乳合计,");
        sb.append("探亲合计,");
        sb.append("签字确认");

        String[] headers = sb.toString().split(",");
        OutputStream out = null;
        try {
            out = new FileOutputStream("D://"+new DepartmentComInfo().getDepartmentname(departmentid) +  date+"考勤汇总情况确认表.xls");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        exportExcel(departmentid, date, "sheet", headers, out, "yyyy-MM-dd", days);
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断一个日历是不是周末.
     *
     * @param calendar
     *            the calendar
     * @return true, if checks if is weekend
     */
    private static boolean isWeekend(String dateString) {
        java.text.SimpleDateFormat s = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;
        try {
            date = s.parse(dateString);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        java.util.Calendar calendar = new java.util.GregorianCalendar();
        calendar.setTime(date);
        //判断是星期几
        int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);

        if (dayOfWeek == 1 || dayOfWeek == 7) {
            return true;
        }
        return false;
    }

    /**
     * judge date is holiday or not
     *
     * @param date
     * @return
     */
    public static java.util.List<String> getHolidayList() {
        java.util.List<String> list = new java.util.ArrayList<String>();
        weaver.conn.RecordSet rs = new weaver.conn.RecordSet();
        rs.execute("select holidaydate from HrmPubHoliday");
        while (rs.next()) {
            String day = rs.getString("holidaydate");
            list.add(day);
        }
        return list;
    }

    /**
     * 获取一个月多少天数
     *
     * @parameter date
     */
    public static int getDaysOfSpecificMonth(String date) {
        java.text.SimpleDateFormat s = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        try {
            calendar.setTime(s.parse(date));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        calendar.set(calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), 1);
        calendar.roll(java.util.Calendar.DATE, false);
        return calendar.get(java.util.Calendar.DATE);
    }

    public static void main(String[] args) {
        exec("1", "2016-02");
    }
}