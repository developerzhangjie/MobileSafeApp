package ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sin.mobilesafe.R;

/**
 * Created by Sin on 2016/9/6.
 */
public class SettingView extends RelativeLayout {

    private TextView tv_setting_title;
    private ImageView iv_setting_islock;
    private View view;
    private boolean isToggle;

    public SettingView(Context context) {
        super(context);
        initView();
    }

    public SettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingView);
        String title = typedArray.getString(R.styleable.SettingView_title);
        int bkg = typedArray.getInt(R.styleable.SettingView_setBackground, 0);
        boolean isShow = typedArray.getBoolean(R.styleable.SettingView_isToggle, true);
        typedArray.recycle();
        tv_setting_title.setText(title);
        iv_setting_islock.setVisibility(isShow ? View.VISIBLE : View.GONE);
        switch (bkg) {
            case 0:
                view.setBackgroundResource(R.drawable.setting_first_selector);
                break;
            case 1:
                view.setBackgroundResource(R.drawable.setting_middle_selector);
                break;
            case 2:
                view.setBackgroundResource(R.drawable.setting_last_selector);
                break;
            default:
                view.setBackgroundResource(R.drawable.setting_first_selector);
                break;
        }

    }

    private void initView() {
        view = View.inflate(getContext(), R.layout.setting_view, null);
        tv_setting_title = (TextView) view.findViewById(R.id.tv_setting_title);
        iv_setting_islock = (ImageView) view.findViewById(R.id.iv_setting_islock);
        this.addView(view);
    }

    public void setToggle(boolean toggle) {
        this.isToggle = toggle;
        if (toggle) {
            iv_setting_islock.setImageResource(R.drawable.on);
        } else {
            iv_setting_islock.setImageResource(R.drawable.off);
        }
    }

    public boolean getToggle() {
        return isToggle;
    }

    public void toggle(){
        setToggle(!isToggle);
    }
    public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


}
