package weaver.interfaces.jiangyl.kq;

public class TlHrmMode {

    private String hrmid;
    //hrm id
    private String objno;
    //date eg:2015-12-02
    private String signdate;
    //kind 
    private String signkind;
    //time eg:2015-12-02 12:00
    private String signtime;
    //card id
    private String cardid;
    private String mutil_date;
    private String status;
    private String door;
    private String lx;
    private String signlx;

    private String yc_signup;
    private String yc_signout;

    private String lastname;
    private String loginid;
    private String sfdk;


    public String getSfdk() {
        return sfdk;
    }
    public void setSfdk(String sfdk) {
        this.sfdk = sfdk;
    }
    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getLoginid() {
        return loginid;
    }
    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }
    public String getYc_signup() {
        return yc_signup;
    }
    public void setYc_signup(String yc_signup) {
        this.yc_signup = yc_signup;
    }
    public String getYc_signout() {
        return yc_signout;
    }
    public void setYc_signout(String yc_signout) {
        this.yc_signout = yc_signout;
    }
    public String getSignlx() {
        return signlx;
    }
    public void setSignlx(String signlx) {
        this.signlx = signlx;
    }
    public String getLx() {
        return lx;
    }
    public void setLx(String lx) {
        this.lx = lx;
    }
    public String getHrmid() {
        return hrmid;
    }
    public void setHrmid(String hrmid) {
        this.hrmid = hrmid;
    }
    public String getDoor() {
        return door;
    }
    public void setDoor(String door) {
        this.door = door;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getMutil_date() {
        return mutil_date;
    }
    public void setMutil_date(String mutil_date) {
        this.mutil_date = mutil_date;
    }
    public String getObjno() {
        return objno;
    }
    public void setObjno(String objno) {
        this.objno = objno;
    }
    public String getSigndate() {
        return signdate;
    }
    public void setSigndate(String signdate) {
        this.signdate = signdate;
    }
    public String getSignkind() {
        return signkind;
    }
    public void setSignkind(String signkind) {
        this.signkind = signkind;
    }
    public String getSigntime() {
        return signtime;
    }
    public void setSigntime(String signtime) {
        this.signtime = signtime;
    }
    public String getCardid() {
        return cardid;
    }
    public void setCardid(String cardid) {
        this.cardid = cardid;
    }
    @Override
    public String toString() {
        return "hrmid : " + hrmid + " objno : " + objno + " signdate : "+signdate + " signkind : "+ signkind + " signtime : "
                + signtime + " cardid : "+ cardid + " mutil_date : " + mutil_date + " status : " + status + " door : " + door + " lx : " + lx
                + " signlx : " + signlx + " yc_signup : " + yc_signup + " yc_signout : " + yc_signout + " lastname : " + lastname + " loginid : " + loginid;
    }
}
