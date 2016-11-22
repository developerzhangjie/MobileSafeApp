package bean;

/**Description:创建黑名单对象
 * Created by Sin on 2016/9/19.
 */
public class BlackNumberInfo {
    //拦截电话号码
    public String number;
    //拦截模式
    public int mode;

    @Override
    public String toString() {
        return "BlackNumberInfo{" +
                "mode=" + mode +
                ", number='" + number + '\'' +
                '}';
    }
}
