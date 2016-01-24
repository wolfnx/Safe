package cn.itcast.lost.bean;

/**
 * Created by wolfnx on 2016/1/24.
 */
public class BlackNumberInfo {
    /**
     * 黑名单电话号码
     */
    public String number;
    /**
     * 拦截模式
     * 1.全部拦截、电话拦截+短信拦截
     * 2.电话拦截
     * 3.短信拦截
     */
    public String mode;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
