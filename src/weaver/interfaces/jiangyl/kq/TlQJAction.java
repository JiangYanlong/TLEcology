package weaver.interfaces.jiangyl.kq;

import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.hrm.resource.ResourceComInfo;
import weaver.interfaces.sap.SAPConn;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

import com.sap.mw.jco.JCO;

public class TlQJAction extends BaseBean implements Action {

    private String empid = "";
    private String begindate = "";
    private String enddate = "";
    private String begintime = "";
    private String endtime = "";
    private String tslx = "N";
    private String drms = "A";
    private String qjlb = "";
    private String lx = "";
    private String absencetype1 = "";
    private JCO.Client sapconnection = new SAPConn().getConnection();
    @Override
    public String execute(RequestInfo request) {
        SAPConn SAPConn = new SAPConn();
        Property[] properties = request.getMainTableInfo().getProperty();// 获取表单主字段信息
        for (int i = 0; i < properties.length; i++) {// 主表数据
            String name = properties[i].getName().toUpperCase();//字段名
            String value = Util.null2String(properties[i].getValue());//值
            if (name.equals("RESOURCE1")) {
                empid = getEmpid(value);
            }
            if (name.equals("BEGINDATE")) {
                begindate = value.split("-")[0] + value.split("-")[1] + value.split("-")[2];
            }
            if (name.equals("ENDDATE")) {
                enddate = value.split("-")[0] + value.split("-")[1] + value.split("-")[2];
            }
            if (name.equals("BEGINTIME")) {
                begintime = value.split(":")[0] + value.split(":")[1] + "00";
            }
            if (name.equals("ENDTIME")) {
                endtime = value.split(":")[0] + value.split(":")[1] + "00";
            }
            if (name.equals("QJLB")) {
                qjlb = value;
            }
            if (name.equals("ABSENCETYPE1")) {
                absencetype1 = value;
            }
        }
        if ("0".equals(qjlb)) {
            //调休
            lx = "1000";
        }
        if ("1".equals(qjlb)) {
            //请假
            if ("1".equals(absencetype1)) {
                lx = "1020";
            }
            if ("0".equals(absencetype1)) {
                lx = "1030";
            }
            if ("7".equals(absencetype1)) {
                lx = "1040";
            }
            if ("3".equals(absencetype1)) {
                lx = "1050";
            }
            if ("4".equals(absencetype1)) {
                lx = "1060";
            }
            if ("9".equals(absencetype1)) {
                lx = "1070";
            }
            if ("5".equals(absencetype1)) {
                lx = "1080";
            }
            if ("6".equals(absencetype1)) {
                lx = "1090";
            }
            if ("10".equals(absencetype1)) {
                lx = "1100";
            }
        }
        log("员工编号："+empid);
        log("计划请假开始日期："+begindate);
        log("计划请假结束日期："+enddate);
        log("计划请假开始时间："+begintime);
        log("计划请假结束时间："+endtime);
        log("调试类型："+tslx);
        log("导入模式："+drms);
        log("出勤或缺勤类型："+lx);
        //构造SAP需要的参数结构 开始
        JCO.Function function = SAPConn.excuteBapi("ZIHR018");
        //人员号 
        function.getImportParameterList().setValue(empid, "I_PERNR");
        //开始日期 
        function.getImportParameterList().setValue(begindate, "I_BEGDA");
        //结束日期
        function.getImportParameterList().setValue(enddate, "I_ENDDA");
        //开始时间
        function.getImportParameterList().setValue(begintime, "I_BEGUZ");
        //结束时间
        function.getImportParameterList().setValue(endtime, "I_ENDUZ");
        //调试类型
        function.getImportParameterList().setValue(tslx, "I_MOD");
        //导入模式 S批量 A单个员工
        function.getImportParameterList().setValue(drms, "I_TYPE");
        //出勤或缺勤类型
        function.getImportParameterList().setValue(lx, "I_AWART");
        //构造SAP需要的参数结构 结束
        //执行SAP
        sapconnection.execute(function);
        String O_RTYPE = Util.null2String(function.getExportParameterList().getValue("O_RTYPE"));// sap凭证返回信息
        String O_RTMSG = Util.null2String(function.getExportParameterList().getValue("O_RTMSG"));// sap返回信息 消息类型: S 成功,E 错误,W 警告,I 信息,A 中断
        log("消息类型："+O_RTYPE);
        log("消息文本："+O_RTMSG);
        if (!"S".equals(O_RTYPE)) {
            request.getRequestManager().setMessageid("1111111111");
            request.getRequestManager().setMessagecontent(O_RTMSG);
        }
        return SUCCESS;
    }
    
    /**
     * 获取人员工号
     * 
     * @param hrmid
     * @return
     * @throws Exception
     */
    private String getEmpid(String hrmid) {
        String empid = "";
        try {
            empid = new ResourceComInfo().getLoginID(hrmid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empid;
    }
    
    /**
     * 打印日志
     * 
     * @param o
     */
    public void log(Object o){
        writeLog(o);
    }
}