package bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Sin on 2016/9/29.
 * Description:
 */

public class AppInfo {
    //包名
    public String packageName;
    //软件名称
    public String name;
    //软件的图标
    public Drawable icon;
    //是否保存到sd
    public boolean isSD;
    //软件占用的大小
    public long memorySize;
    //表示是用户程序还是系统程序
    public boolean isSystem;
    //获取程序的uid
    public int uid;

    public AppInfo(Drawable icon, boolean isSD, boolean isSystem, long memorySize, String name, String packageName, int uid) {
        this.icon = icon;
        this.isSD = isSD;
        this.isSystem = isSystem;
        this.memorySize = memorySize;
        this.name = name;
        this.packageName = packageName;
        this.uid = uid;
    }
}
