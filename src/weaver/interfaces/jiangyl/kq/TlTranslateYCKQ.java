package weaver.interfaces.jiangyl.kq;

public class TlTranslateYCKQ {

    public String trunslate(String id) {
        if ("0".equals(id)) {
            return "正常";
        }
        if ("1".equals(id)) {
            return "迟到";
        }
        if ("2".equals(id)) {
            return "早退";
        }
        if ("3".equals(id)) {
            return "旷工";
        }
        return null;
    }
}
