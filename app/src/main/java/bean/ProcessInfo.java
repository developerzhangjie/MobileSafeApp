package bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Sin on 2016/10/6.
 * Description:
 */

public class ProcessInfo {
    //包名
    public String packageName;
    //软件名称
    public String name;
    //图标
    public Drawable icon;
    //内存空间
    public long memorySize;
    //是否是系统进程
    public boolean isSystem;
    //条目和checkBox是否选中
    public boolean isChecked = false;
}
