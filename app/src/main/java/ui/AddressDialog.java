package ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.sin.mobilesafe.R;

/**
 * Created by Sin on 2016/9/28.
 * Description:
 */

public class AddressDialog extends Dialog {

    private ListView lv_adddressdialog_bg;


    public AddressDialog(Context context) {
        super(context, R.style.addressDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_addressdialog);
        lv_adddressdialog_bg = (ListView) findViewById(R.id.lv_adddressdialog_bg);
        //获取当前activity所在的窗口
        Window window = this.getWindow();
        //获取窗口的属性
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        //设置窗口的属性
        window.setAttributes(params);
    }

    public void setadapter(BaseAdapter baseAdapter) {
        lv_adddressdialog_bg.setAdapter(baseAdapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        lv_adddressdialog_bg.setOnItemClickListener(itemClickListener);
    }
}
