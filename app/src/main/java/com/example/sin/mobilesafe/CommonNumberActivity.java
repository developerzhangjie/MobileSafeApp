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

    private ExpandableListView elv_commonnumber_commonnumbers;
    private List<CommonNumberDao.GroupInfo> groups;
    private int currentExpandGroup = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonnumber);
        initView();
    }

    private void initView() {
        elv_commonnumber_commonnumbers = (ExpandableListView) findViewById(R.id.elv_commonnumber_commonnumbers);
        new Thread() {
            @Override
            public void run() {
                groups = CommonNumberDao.getGroups(CommonNumberActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        elv_commonnumber_commonnumbers.setAdapter(new MyAdapter());
                    }
                });
            }
        }.start();

        //设置组的点击事件
        elv_commonnumber_commonnumbers.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //=-1表示之前没打开过
                if (currentExpandGroup == -1) {
                    //打开自己
                    elv_commonnumber_commonnumbers.expandGroup(groupPosition);
                    currentExpandGroup = groupPosition;
                    elv_commonnumber_commonnumbers.setSelectedGroup(groupPosition);
                } else {
                    //关闭组，打开其他组
                    //打开的是自己，点击自己，关闭自己
                    //打开的是自己，点击其他组，打开其他组，置顶，关闭自己
                    if (currentExpandGroup == groupPosition) {
                        elv_commonnumber_commonnumbers.collapseGroup(groupPosition);
                        currentExpandGroup = -1;
                    } else {
                        elv_commonnumber_commonnumbers.expandGroup(groupPosition);
                        elv_commonnumber_commonnumbers.collapseGroup(currentExpandGroup);
                        elv_commonnumber_commonnumbers.setSelectedGroup(groupPosition);
                        currentExpandGroup = groupPosition;
                    }
                }
                return true;
            }
        });

        //设置孩子的点击事件
        elv_commonnumber_commonnumbers.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String number = groups.get(groupPosition).child.get(childPosition).number;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
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
