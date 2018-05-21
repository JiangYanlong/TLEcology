package weaver.interfaces.jiangyl.kq;

import weaver.conn.RecordSet;
import weaver.general.BaseBean;
import weaver.general.Util;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

public class TllzAction extends BaseBean implements Action {

    @Override
    public String execute(RequestInfo request) {
        Property[] properties = request.getMainTableInfo().getProperty();// 获取表单主字段信息
        // 主表数据字段
        String date2 = ""; // 周后一个工作日
        String resource1 = ""; //人员id
        for (int i = 0; i < properties.length; i++) {
            String name = properties[i].getName();// 主字段名称
            String value = Util.null2String(properties[i].getValue());// 主字段对应的值
            if ("date2".equals(name)) {
                date2 = value;
            }
            if ("resource1".equals(name)) {
                resource1 = value;
            }
        }
        insertlz(resource1,date2);
        return SUCCESS;
    }

    /**
     * 插入离职记录
     *
     * @param list
     * @param absencetype1
     */
    public void insertlz(String hrmid,String date){
        RecordSet rs = new RecordSet();
        String tableName = getPropValue("TLConn", "kqhztable");
        String sql = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " +
                "values('"
                + hrmid + "','" + date + "','17','" + ""
                + "','上午','" + "" + "','0','0','0','0','0','0','0','0','0','0','0','0','221','" + hrmid
                + "','0')";
        rs.execute(sql);
        String sql1 = "insert into " + tableName + " (hrmid,signdate,kqlx,signtime,signlx,cardid,sjhj,bjhj,cjhj,njhj,hjhj,txhj,jdjhj,srjhj,sangjhj,pcjhj,brjhj,tqjhj,formmodeid,modedatacreater,modedatacreatertype) " +
                "values('"
                + hrmid + "','" + date + "','17','" + ""
                + "','下午','" + "" + "','0','0','0','0','0','0','0','0','0','0','0','0','221','" + hrmid
                + "','0')";
        rs.execute(sql1);
    }
}
