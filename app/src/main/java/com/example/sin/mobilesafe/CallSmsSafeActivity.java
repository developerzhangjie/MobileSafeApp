package com.example.sin.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import bean.BlackNumberInfo;
import db.BlackNumberConstant;
import db.dao.BlackNumberDao;

/**
 * Description:骚扰拦截界面
 * Created by Sin on 2016/9/16.
 */
public class CallSmsSafeActivity extends Activity implements View.OnClickListener {

    private ImageView iv_callsms_add;
    private ListView lv_callsms_contant;
    private ImageView iv_callsms_empty;
    private LinearLayout ll_callsmsafe_loading;
    private BlackNumberDao blackNumberDao;
    private List<BlackNumberInfo> list;
    private Context mContext;
    private final int REQUEST_CODE_ADD = 100;
    private final int REQUEST_CODE_UPDADE = 101;
    private final int MAX_NUMBER = 20;
    private int startIndex = 0;
    private MyAdapter mMyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callsmssafe);
        mContext = this;
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        iv_callsms_add = (ImageView) findViewById(R.id.iv_callsms_add);
        lv_callsms_contant = (ListView) findViewById(R.id.lv_callsms_contant);
        iv_callsms_empty = (ImageView) findViewById(R.id.iv_callsms_empty);
        ll_callsmsafe_loading = (LinearLayout) findViewById(R.id.ll_callsmsafe_loading);
        blackNumberDao = new BlackNumberDao(this);
        //加载数据
        fillData();
        iv_callsms_add.setOnClickListener(this);
        //listView条目的点击事件，进行更新
        lv_callsms_contant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CallSmsSafeActivity.this, BlackNumberAddAndEditActivity.class);
                intent.setAction("update");//设置intent表示的动作
                intent.putExtra("number", list.get(position).number);
                intent.putExtra("mode", list.get(position).mode);
                intent.putExtra("position", position);
                startActivityForResult(intent, REQUEST_CODE_UPDADE);
            }
        });

        lv_callsms_contant.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //整个滚动事件结束
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //获取屏幕中最后一条可见条目的位置
                    int position = lv_callsms_contant.getLastVisiblePosition();
                    if (position == list.size() - 1) {
                        //加载数据
                        startIndex += MAX_NUMBER;
                        fillData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * 查询数据是耗时操作，需要放在子线程中执行
     * 必须是先查询，再显示数据，反过来，会报空指针异常（还没查询到数据，就显示，是null）
     */
    private void fillData() {
        new Thread() {
            @Override
            public void run() {
                //list = blackNumberDao.queryAllBlackNumber();
                if (list == null) {
                    list = blackNumberDao.queryPartBlackNumber(MAX_NUMBER, startIndex);
                } else {
                    list.addAll(blackNumberDao.queryPartBlackNumber(MAX_NUMBER, startIndex));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMyAdapter == null) {
                            mMyAdapter = new MyAdapter();
                            lv_callsms_contant.setEmptyView(iv_callsms_empty);
                        } else {
                            //为什么要刷新？为了滑动不会错乱
                            mMyAdapter.notifyDataSetChanged();
                        }
                        lv_callsms_contant.setAdapter(mMyAdapter);
                        //数据加载完成后隐藏进度条
                        ll_callsmsafe_loading.setVisibility(View.GONE);
                    }
                });
            }
        }.start();
    }

    //添加黑名单
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_callsms_add:
                Intent intent = new Intent(CallSmsSafeActivity.this, BlackNumberAddAndEditActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (data != null) {
                        String number = data.getStringExtra("number");
                        int mode = data.getIntExtra("mode", -1);
                        BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                        blackNumberInfo.number = number;
                        blackNumberInfo.mode = mode;
                        list.add(blackNumberInfo);
                        //刷新列表
                        mMyAdapter.notifyDataSetChanged();
                        Log.d("TAG", blackNumberInfo.number);
                    }
                    break;
                default:
                    break;
            }
        } else if (requestCode == REQUEST_CODE_UPDADE) {
            switch (requestCode) {
                case Activity.RESULT_OK:
                    if (data != null) {
                        int mode = data.getIntExtra("mode", -1);
                        int position = data.getIntExtra("position", -1);
                        list.get(position).mode = mode;
                        mMyAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                //这个地方用getApplicationContext()不能将内容显示出来,用CallSmsSafeActivity.this可以，为什么？
                view = View.inflate(CallSmsSafeActivity.this, R.layout.calsssmssafe_item, null);
                viewHolder = new ViewHolder();
                //将view.findViewById放在盒子里
                viewHolder.tv_callsmssafe_number = (TextView) view.findViewById(R.id.tv_callsmssafe_number);
                viewHolder.tv_callsmssafe_mode = (TextView) view.findViewById(R.id.tv_callsmssafe_mode);
                viewHolder.iv_callsms_delete = (ImageView) view.findViewById(R.id.iv_callsms_delete);
                //将盒子绑定view对象
                view.setTag(viewHolder);
            } else {
                view = convertView;
                //从复用的view对象中将盒子取出来
                viewHolder = (ViewHolder) view.getTag();
            }
            //使用盒子中的控件
            final BlackNumberInfo blackNumberInfo = list.get(position);
            viewHolder.tv_callsmssafe_number.setText(blackNumberInfo.number);
            //获取出拦截模式，根据拦截模式显示相应的文字
            int mode = blackNumberInfo.mode;
            switch (mode) {
                case BlackNumberConstant.BLACKNUMBER_CALL:
                    viewHolder.tv_callsmssafe_mode.setText("电话拦截");
                    break;
                case BlackNumberConstant.BACKNUMBER_SMS:
                    viewHolder.tv_callsmssafe_mode.setText("短信拦截");
                    break;
                case BlackNumberConstant.BALCKNUMBER_ALL:
                    viewHolder.tv_callsmssafe_mode.setText("全部拦截");
                    break;
            }
            viewHolder.iv_callsms_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("是否确认删除");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //先从数据库中删除
                            boolean isDelete = blackNumberDao.deleteBlackNumber(blackNumberInfo.number);
                            if (isDelete) {
                                //从list中将数据删除
                                list.remove(position);
                                //刷新列表
                                mMyAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
            });
            return view;
        }
    }

    //1.创建存放控件的盒子
    static class ViewHolder {
        TextView tv_callsmssafe_number;
        TextView tv_callsmssafe_mode;
        ImageView iv_callsms_delete;
    }
}

