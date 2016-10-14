package ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sin.mobilesafe.R;

/**
 * Created by Sin on 2016/9/29.
 * Description:
 */

public class ProgressBarView extends LinearLayout {

    private TextView tv_appmanaget_title;
    private TextView tv_appmanager_used;
    private TextView tv_appmanager_free;
    private ProgressBar pb_appmanager_porgress;

    public ProgressBarView(Context context) {
        super(context);
        initView();
    }

    public ProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        View view = View.inflate(getContext(), R.layout.progressbarview, this);
        tv_appmanaget_title = (TextView) view.findViewById(R.id.tv_appmanaget_title);
        tv_appmanager_used = (TextView) view.findViewById(R.id.tv_appmanager_used);
        tv_appmanager_free = (TextView) view.findViewById(R.id.tv_appmanager_free);
        pb_appmanager_porgress = (ProgressBar) view.findViewById(R.id.pb_appmanager_porgress);
    }

    //设置title的方法
    public void setTitle(String title) {
        tv_appmanaget_title.setText(title);
    }

    //设置已用内存的方法
    public void setUsed(String used) {
        tv_appmanager_used.setText(used + "已用");
    }

    // 设置空闲内存的方法
    public void setFree(String free) {
        tv_appmanager_free.setText(free + "可用");
    }

    //设置progressbar的进度操作
    public void setProgress(int progress) {
        pb_appmanager_porgress.setProgress(progress);
    }
}
