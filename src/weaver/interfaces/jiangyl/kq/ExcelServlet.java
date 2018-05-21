package weaver.interfaces.jiangyl.kq;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import weaver.hrm.company.DepartmentComInfo;

public class ExcelServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String departmentid = (String) request.getParameter("departmentid");
        String date = (String) request.getParameter("date");
        new weaver.general.BaseBean().writeLog("departmentid:"+departmentid);
        ExportExcel.exec(departmentid,date);
        //String path = request.getSession().getServletContext().getRealPath(str);
        try {
            download("D://"+ new DepartmentComInfo().getDepartmentname(departmentid) + date+"考勤汇总情况确认表.xls", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void download(String path, HttpServletResponse response) {
        try {
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            System.out.print("filename:"+filename);
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.setHeader("Content-disposition", "attachment; filename="+ new String(filename.getBytes("GB2312"),"ISO8859-1"));
            // 设定输出文件头        
            response.setContentType("application/msexcel");
            OutputStream toClient = new BufferedOutputStream(
                    response.getOutputStream());
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}