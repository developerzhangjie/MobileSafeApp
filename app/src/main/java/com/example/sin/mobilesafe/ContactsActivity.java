package com.example.sin.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import bean.ContactsInfo;
import engine.ContactEngine;

/**
 * Description:获取联系人作为安全号码
 * Created by Sin on 2016/9/12.
 */
public class ContactsActivity extends Activity {
    private ListView lv_contact_contacts;
    private List<ContactsInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        initView();
    }

    private void initView() {
        //获取系统所有的联系人
        list = ContactEngine.getAllContacts(this);
        lv_contact_contacts = (ListView) findViewById(R.id.lv_contact_contacts);
        lv_contact_contacts.setAdapter(new MyAdapter());
        lv_contact_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1.获取数据
                Intent data = new Intent();
                data.putExtra("number", list.get(position).number);
                setResult(Activity.RESULT_OK, data);
                //2.销毁活动
                finish();
            }
        });
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

        //设置显示条目的样式
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //2.复用缓存
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(ContactsActivity.this, R.layout.contact_item, null);
                //3.创建一个空盒子
                viewHolder = new ViewHolder();
                //4.将控件放到盒子中
                viewHolder.iv_contact_icon = (ImageView) view.findViewById(R.id.iv_contact_icon);
                viewHolder.tv_contact_name = (TextView) view.findViewById(R.id.tv_contact_name);
                viewHolder.tv_contact_number = (TextView) view.findViewById(R.id.tv_contact_number);
                //5.将盒子绑定到view对象中
                view.setTag(viewHolder);
            } else {
                view = convertView;//获取复用的view对象
                viewHolder = (ViewHolder) view.getTag();
            }
            //设置显示数据
            //获取联系人的bean类
            ContactsInfo contactsInfo = list.get(position);
            viewHolder.tv_contact_name.setText(contactsInfo.name);
            viewHolder.tv_contact_number.setText(contactsInfo.number);
            //获取联系人的头像
            Bitmap bitmap = ContactEngine.getContactsPhoto(getApplicationContext(), contactsInfo.contactId);
            if (bitmap != null) {
                viewHolder.iv_contact_icon.setImageBitmap(bitmap);
            } else {
                viewHolder.iv_contact_icon.setImageResource(R.drawable.ic_contact);
            }
            return view;
        }
    }

    //1.创建盒子
    static class ViewHolder {
        ImageView iv_contact_icon;
        TextView tv_contact_name;
        TextView tv_contact_number;
    }
}
