package com.example.sin.mobilesafe;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bean.HomeGridviewIteamBeanInfo;
import utils.Constants;
import utils.SharedPreferencesUtils;

/**description：主界面
 * Created by Sin on 2016/9/1.
 */
public class HomeActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView iv_home_logo;
    private ImageView iv_home_setting;
    private GridView gv_home_gridview;
    private List<HomeGridviewIteamBeanInfo> list;


    private final static String[] TITLES = new String[]{"手机防盗", "骚扰拦截", "软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具"};
    private final static String[] DESCS = new String[]{"远程定位手机", "全面拦截骚扰", "管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全"};
    private final static int[] ICONS = new int[]{R.drawable.sjfd, R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj, R.drawable.sjsd, R.drawable.hcql, R.drawable.cygj};
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        iv_home_setting.setOnClickListener(this);
        gv_home_gridview.setOnItemClickListener(this);


    }

    private void initView() {
        iv_home_logo = (ImageView) findViewById(R.id.iv_home_logo);
        iv_home_setting = (ImageView) findViewById(R.id.iv_home_setting);
        gv_home_gridview = (GridView) findViewById(R.id.gv_home_gridview);


        //讲数组中的数据存到集合中
        list = new ArrayList<>();
        for (int i = 0; i < ICONS.length; i++) {
            //模块的对象
            HomeGridviewIteamBeanInfo info = new HomeGridviewIteamBeanInfo();
            info.inconId = ICONS[i];
            info.title = TITLES[i];
            info.desc = DESCS[i];
            list.add(info);
        }
        //设置动画
        setAnimation();
        gv_home_gridview.setAdapter(new MyAdapter());
    }

    //设置动画
    private void setAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(iv_home_logo, "RotationY", 0f, 90f, 180f, 270f, 360f);
        //动画的持续时间
        objectAnimator.setDuration(2000);
        //执行次数
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        //旋转模式
        //RESTART 从头开始，一直是这个方向旋转
        //REVERSE 转完一圈之后，再沿相反的方向旋转一圈，如此往复
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        //开始执行
        objectAnimator.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_home_setting:
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                //手机防盗
                //获取保存的密码，看是否为空，如果为空，就进行初始化密码设置，若不为空，就输入密码进行验证
                String psw = SharedPreferencesUtils.getString(HomeActivity.this, Constants.SETPASSWORD, "");
                if (TextUtils.isEmpty(psw)) {
                    //初始化
                    showSetPassWordDialog();
                } else {
                    //验证密码
                    showEnterPassWordDialog();
                }
                break;
            case 1:
                //骚扰拦截操作
                Intent intent1 = new Intent(HomeActivity.this, CallSmsSafeActivity.class);
                startActivity(intent1);

                break;
            case 2:
                //软件管家操作
                Intent intent2 = new Intent(HomeActivity.this, AppManagerActivity.class);
                startActivity(intent2);

                break;
            case 3:
                //进程管理
                Intent intent3 = new Intent(HomeActivity.this, ProcessManagerActivity.class);
                startActivity(intent3);
                break;
            case 4:
                //流量统计
                Intent intent4 = new Intent(HomeActivity.this, TrafficManagerActivity.class);
                startActivity(intent4);
                break;
            case 5:
                //手机杀毒
                Intent intent5 = new Intent(HomeActivity.this, AntivirusActivity.class);
                startActivity(intent5);

                break;
            case 6:
                //缓存清理
                Intent intent6 = new Intent(HomeActivity.this, ClearCacheActivity.class);
                startActivity(intent6);
                break;
            case 7:
                Intent intent7 = new Intent(HomeActivity.this, CommonToolsActivity.class);
                startActivity(intent7);
                break;
        }
    }

    //密码验证
    private void showEnterPassWordDialog() {
        //复制步骤一
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //将布局文件转化成view对象
        final View view = View.inflate(this, R.layout.dialog_enterpassword, null);

        //复制步骤三
        final EditText et_setpassword_psw = (EditText) view.findViewById(R.id.et_setpassword_psw);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        final Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入的密码
                String psw = et_setpassword_psw.getText().toString().trim();
                //获取保存的密码
                String saved_psw = SharedPreferencesUtils.getString(HomeActivity.this, Constants.SETPASSWORD, "");
                //判断密码是否为空
                if (TextUtils.isEmpty(psw)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    //判断输入的密码是否正确，正确就跳转到手机防盗页面，错误就提醒用户
                    if (psw.equals(saved_psw)) {
                        //跳转到手机防盗页面
                        Toast.makeText(HomeActivity.this, "密码正确", Toast.LENGTH_SHORT).show();
                        enterLostFind();
                        //把对话框隐藏
                        dialog.dismiss();
                    } else {
                        //提醒用户
                        Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //复制步骤二
        builder.setView(view);//添加view
        dialog = builder.create();
        dialog.show();
    }

    //跳转到手机防盗页面
    private void enterLostFind() {
        //判断用户是否是第一次进入手机防盗模块，如果是第一次，就进入引导界面，如果不是，就直接进入手机防盗页面
        //获取判断标志
        boolean isFirstEnter = SharedPreferencesUtils.getBoolean(HomeActivity.this, Constants.ISFIRSTENTER, false);
        if (isFirstEnter) {
            //如果不是第一次进入手机防盗模块，进入手机防盗页面
            Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
            startActivity(intent);
        } else {
            //如果是第一次进入手机防盗模块，进入引导界面
            Intent intent = new Intent(HomeActivity.this, SetUp1Activity.class);
            startActivity(intent);
        }

    }

    //初始化密码设置对话框
    private void showSetPassWordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //将布局文件转化成view对象
        View view = View.inflate(this, R.layout.dialog_setpassword, null);
        //初始化控件
        final EditText et_setpassword_psw = (EditText) view.findViewById(R.id.et_setpassword_psw);
        final EditText et_setpassword_confirm = (EditText) view.findViewById(R.id.et_setpassword_confirm);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.获取输入的密码
                String psw = et_setpassword_psw.getText().toString().trim();
                //2.判断密码是否为空，为空提醒用户
                if (TextUtils.isEmpty(psw)) {//null :没有内存    ""：有内存但是没有内容
                    Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //3.获取确认密码
                String confrim_psw = et_setpassword_confirm.getText().toString().trim();
                //4.判断两次密码是否一致，一致：保存密码，不一致：提醒用户，密码不一致
                if (psw.equals(confrim_psw)) {
                    //保存密码
                    SharedPreferencesUtils.saveString(HomeActivity.this, Constants.SETPASSWORD, psw);
                    Toast.makeText(getApplicationContext(), "设置密码成功", Toast.LENGTH_SHORT).show();
                    //隐藏对话框
                    dialog.dismiss();
                } else {
                    //不一致
                    Toast.makeText(getApplicationContext(), "两次密码不一致", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(view);//添加view
        dialog = builder.create();
        dialog.show();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.home_item, null);
            //找到控件
            ImageView iv_homeitem_icon = (ImageView) view.findViewById(R.id.iv_homeitem_icon);
            TextView tv_homeitem_title = (TextView) view.findViewById(R.id.tv_homeitem_title);
            TextView tv_homeitem_desc = (TextView) view.findViewById(R.id.tv_homeitem_desc);
            //设置显示数据
            //1.获取相应条目的bean对象,设置数据
            HomeGridviewIteamBeanInfo homeGridviewItemBeanInfo = list.get(position);
            //2.设置显示
            iv_homeitem_icon.setImageResource(homeGridviewItemBeanInfo.inconId);
            tv_homeitem_title.setText(homeGridviewItemBeanInfo.title);
            tv_homeitem_desc.setText(homeGridviewItemBeanInfo.desc);
            return view;
        }
    }
}
