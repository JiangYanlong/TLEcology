package weaver.interfaces.jiangyl.kq;

import java.util.HashMap;
import java.util.Map;

public class TlHzMode {

    private String name;
    private String status;
    private String sjhj = "0";
    private String bjhj = "0";
    private String cjhj = "0";
    private String njhj = "0";
    private String hjhj = "0";
    private String txhj = "0";
    private String jdjhj = "0";
    private String srjhj = "0";
    private String sangjhj = "0";
    private String pcjhj = "0";
    private String brjhj = "0";
    private String tqjhj = "0";
    private Map<String,String> map = new HashMap<String,String>();


    public Map<String, String> getMap() {
        return map;
    }
    public void setMap(Map<String, String> map) {
        this.map = map;
    }
    public String getJdjhj() {
        return jdjhj;
    }
    public void setJdjhj(String jdjhj) {
        this.jdjhj = jdjhj;
    }
    public String getSrjhj() {
        return srjhj;
    }
    public void setSrjhj(String srjhj) {
        this.srjhj = srjhj;
    }
    public String getSangjhj() {
        return sangjhj;
    }
    public void setSangjhj(String sangjhj) {
        this.sangjhj = sangjhj;
    }
    public String getPcjhj() {
        return pcjhj;
    }
    public void setPcjhj(String pcjhj) {
        this.pcjhj = pcjhj;
    }
    public String getBrjhj() {
        return brjhj;
    }
    public void setBrjhj(String brjhj) {
        this.brjhj = brjhj;
    }
    public String getTqjhj() {
        return tqjhj;
    }
    public void setTqjhj(String tqjhj) {
        this.tqjhj = tqjhj;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getSjhj() {
        return sjhj;
    }
    public void setSjhj(String sjhj) {
        this.sjhj = sjhj;
    }
    public String getBjhj() {
        return bjhj;
    }
    public void setBjhj(String bjhj) {
        this.bjhj = bjhj;
    }
    public String getCjhj() {
        return cjhj;
    }
    public void setCjhj(String cjhj) {
        this.cjhj = cjhj;
    }
    public String getNjhj() {
        return njhj;
    }
    public void setNjhj(String njhj) {
        this.njhj = njhj;
    }
    public String getHjhj() {
        return hjhj;
    }
    public void setHjhj(String hjhj) {
        this.hjhj = hjhj;
    }
    public String getTxhj() {
        return txhj;
    }
    public void setTxhj(String txhj) {
        this.txhj = txhj;
    }
    @Override
    public String toString() {
        return "name : " + name + " status : " + status + " sjhj : " + sjhj + " bjhj : " + bjhj + " cjhj :" + cjhj
                + " bjhj : " + njhj + " hjhj : " + hjhj + " txhj : " + txhj + "jdjhj :" + jdjhj + " srjhj :" + srjhj + " sangjhj :"+ sangjhj+ " pcjhj :" + pcjhj + " brjhj :" + brjhj + " tqjhj :" + tqjhj + " map : " + map.toString();
    }


}
