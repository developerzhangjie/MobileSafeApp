package com.example.sin.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import db.dao.CommonNumberDao;

/**
 * Created by Sin on 2016/9/29.
 * Description:
 */

public class CommonNumberActivity extends Activity {

    private ExpandableListView elv_commonNumber_commonNumbers;
    private List<CommonNumberDao.GroupInfo> groups;
    private int currentExpandGroup = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonnumber);
        initView();
    }

    private void initView() {
        elv_commonNumber_commonNumbers = (ExpandableListView) findViewById(R.id.elv_commonnumber_commonnumbers);
        new Thread() {
            @Override
            public void run() {
                //获取数据
                groups = CommonNumberDao.getGroups(CommonNumberActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        elv_commonNumber_commonNumbers.setAdapter(new MyAdapter());
                    }
                });
            }
        }.start();

        //设置组的点击事件
        elv_commonNumber_commonNumbers.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //=-1表示之前没打开过
                if (currentExpandGroup == -1) {
                    //打开自己
                    elv_commonNumber_commonNumbers.expandGroup(groupPosition);
                    currentExpandGroup = groupPosition;
                    //将打开的条目置顶
                    elv_commonNumber_commonNumbers.setSelectedGroup(groupPosition);
                } else {
                    //关闭组，打开其他组
                    if (currentExpandGroup == groupPosition) {
                        //打开的是自己，点击自己，关闭自己
                        elv_commonNumber_commonNumbers.collapseGroup(groupPosition);
                        currentExpandGroup = -1;
                    } else {
                        //打开的是自己，点击其他组，打开其他组，置顶，关闭自己
                        elv_commonNumber_commonNumbers.expandGroup(groupPosition);
                        elv_commonNumber_commonNumbers.collapseGroup(currentExpandGroup);
                        //将打开的条目置顶
                        elv_commonNumber_commonNumbers.setSelectedGroup(groupPosition);
                        currentExpandGroup = groupPosition;
                    }
                }
                return true;
            }
        });

        //设置孩子的点击事件
        elv_commonNumber_commonNumbers.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //获取号码
                String number = groups.get(groupPosition).child.get(childPosition).number;
                Intent intent = new Intent();
                //打电话
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
                //执行完成
                return true;
            }
        });
    }


    private class MyAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return groups.get(groupPosition).child.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return groups.get(groupPosition).child.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        // 判断id是否稳定,如果你返回id,返回false,没有返回id,返回true
        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(CommonNumberActivity.this);
            textView.setText(groups.get(groupPosition).name);
            textView.setTextSize(20);
            textView.setTextColor(Color.BLACK);
            textView.setBackgroundColor(Color.parseColor("#33000000"));
            textView.setPadding(8, 8, 8, 8);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView textView = new TextView(CommonNumberActivity.this);
            textView.setText(groups.get(groupPosition).child.get(childPosition).name
                    + "\n"
                    + groups.get(groupPosition).child.get(childPosition).number);
            textView.setTextSize(18);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(8, 8, 8, 8);
            return textView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
