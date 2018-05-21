package weaver.interfaces.jiangyl.kq;

public class TlTimeMode {

	private String am_sbsj;
	private String am_xbsj;
	private String pm_sbsj;
	private String pm_xbsj;
	public String getAm_sbsj() {
		return am_sbsj;
	}
	public void setAm_sbsj(String am_sbsj) {
		this.am_sbsj = am_sbsj;
	}
	public String getAm_xbsj() {
		return am_xbsj;
	}
	public void setAm_xbsj(String am_xbsj) {
		this.am_xbsj = am_xbsj;
	}
	public String getPm_sbsj() {
		return pm_sbsj;
	}
	public void setPm_sbsj(String pm_sbsj) {
		this.pm_sbsj = pm_sbsj;
	}
	public String getPm_xbsj() {
		return pm_xbsj;
	}
	public void setPm_xbsj(String pm_xbsj) {
		this.pm_xbsj = pm_xbsj;
	}
	@Override
	public String toString() {
		return "am_sbsj : " + am_sbsj + " am_xbsj : " + am_xbsj + " pm_sbsj : " + pm_sbsj + " pm_xbsj : " + pm_xbsj;
	}
	
}
