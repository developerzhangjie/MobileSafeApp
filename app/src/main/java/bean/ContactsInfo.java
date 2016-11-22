package bean;

/**Description:电话本中的联系人描述类
 * Created by Sin on 2016/9/12.
 */
public class ContactsInfo {
    //联系人姓名
    public String name;
    //联系人电话
    public String number;
    //联系人id
    public String contactId;

    @Override
    public String toString() {
        return "ContactsInfo{" +
                "contactId='" + contactId + '\'' +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
