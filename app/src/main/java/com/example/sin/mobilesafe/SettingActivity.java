package com.example.sin.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import service.AddressService;
import service.CallSMSSafeService;
import ui.AddressDialog;
import ui.SettingView;
import utils.Constants;
import utils.ServicUtil;
import utils.SharedPreferencesUtils;

/**
 * Created by Sin on 2016/9/6.
 */
public class SettingActivity extends Activity implements View.OnClickListener {
    private SettingView sv_setting_update;
    private SettingView sv_setting_callsmssafe;
    private SettingView sv_setting_address;
    private SettingView sv_setting_changed_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        sv_setting_update.setOnClickListener(this);
        sv_setting_callsmssafe.setOnClickListener(this);
        sv_setting_address.setOnClickListener(this);
        sv_setting_changed_bg.setOnClickListener(this);
    }

    private void initView() {
        sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
        sv_setting_callsmssafe = (SettingView) findViewById(R.id.sv_setting_callsmssafe);
        sv_setting_address = (SettingView) findViewById(R.id.sv_setting_address);
        sv_setting_changed_bg = (SettingView) findViewById(R.id.sv_setting_changed_bg);
        boolean isToggle = SharedPreferencesUtils.getBoolean(this, Constants.TOGGLE, true);
        if (isToggle) {
            sv_setting_update.setToggle(true);
        } else {
            sv_setting_update.setToggle(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //骚扰拦截回显操作
        if (ServicUtil.isServiceRunning(getApplicationContext(), "service.CallSMSSafeService")) {
            sv_setting_callsmssafe.setToggle(true);
        } else {
            sv_setting_callsmssafe.setToggle(false);
        }

        //归属地回显操作
        if (ServicUtil.isServiceRunning(getApplicationContext(), "service.AddressService")) {
            sv_setting_address.setToggle(true);
        } else {
            sv_setting_address.setToggle(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sv_setting_update:
                /*if (sv_setting_update.getToggle()) {
                    sv_setting_update.setToggle(false);
                } else {
                    sv_setting_update.setToggle(true);
                }*/
                sv_setting_update.toggle();
                SharedPreferencesUtils.saveBoolean(SettingActivity.this, Constants.TOGGLE, sv_setting_update.getToggle());
                break;
            case R.id.sv_setting_callsmssafe:
                Intent intent = new Intent(SettingActivity.this, CallSMSSafeService.class);
                //关闭或者打开服务
                //关闭状态 点击->打开  打开状态 点击 ——>关闭
                if (ServicUtil.isServiceRunning(getApplicationContext(), "service.CallSMSSafeService")) {
                    //运行 点击 关闭
                    stopService(intent);
                    sv_setting_callsmssafe.setToggle(false);
                } else {
                    startService(intent);
                    sv_setting_callsmssafe.setToggle(true);
                }
                break;
            case R.id.sv_setting_address:
                Intent add_intent = new Intent(SettingActivity.this, AddressService.class);
                //关闭或者打开服务
                //关闭状态 点击->打开  打开状态 点击 ——>关闭
                if (ServicUtil.isServiceRunning(getApplicationContext(), "service.AddressService")) {
                    //运行 点击 关闭
                    stopService(add_intent);
                    sv_setting_address.setToggle(false);
                } else {
                    startService(add_intent);
                    sv_setting_address.setToggle(true);
                }
                break;
            case R.id.sv_setting_changed_bg:
                final AddressDialog addressDialog = new AddressDialog(SettingActivity.this);
                addressDialog.show();
                addressDialog.setadapter(new MyAdapter());
                addressDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        addressDialog.dismiss();
                        SharedPreferencesUtils.saveInt(SettingActivity.this, Constants.ADDRESSDIALOG_COLOR_RESID, icons[position]);
                    }
                });
                break;
        }
    }

    private String[] titles = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    private int[] icons = new int[]{R.drawable.addressdialog_normal, R.drawable.addressdialog_orange,
            R.drawable.addressdialog_blue, R.drawable.addressdialog_gray,
            R.drawable.addressdialog_green
    };

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.addressdialog_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_addressdialogitem_bgcolor = (ImageView) view.findViewById(R.id.iv_addressdialogitem_bgcolor);
                viewHolder.tv_addressdialogitem_colorname = (TextView) view.findViewById(R.id.tv_addressdialogitem_colorname);
                viewHolder.iv_addressdialogitem_isselect = (ImageView) view.findViewById(R.id.iv_addressdialogitem_isselect);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tv_addressdialogitem_colorname.setText(titles[position]);
            viewHolder.iv_addressdialogitem_bgcolor.setBackgroundResource(icons[position]);
            //获取保存的颜色resid,去和当前的条目的颜色resid进行比较,一致就显示对号,不一致,不显示
            int resId = SharedPreferencesUtils.getInt(SettingActivity.this, Constants.ADDRESSDIALOG_COLOR_RESID, -1);
            if (resId == icons[position]) {
                Log.d("TAG","显示");
                viewHolder.iv_addressdialogitem_isselect.setVisibility(View.VISIBLE);
            } else {
               Log.d("TAG","不显示");
                viewHolder.iv_addressdialogitem_isselect.setVisibility(View.GONE);
            }
            return view;
        }
    }

    private class ViewHolder {
        ImageView iv_addressdialogitem_bgcolor, iv_addressdialogitem_isselect;
        TextView tv_addressdialogitem_colorname;
    }
}
