package com.example.sin.mobilesafe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import db.BlackNumberConstant;
import db.dao.BlackNumberDao;


/**具体的黑名单界面
 * Created by Sin on 2016/9/23.
 * Description:
 */

public class BlackNumberAddAndEditActivity extends Activity implements View.OnClickListener {
    private EditText et_activity_blacknumber_add_and_edit_number;
    private RadioGroup rg_activity_blacknumber_select_mode;
    private Button bt_activity_blacknumber_add_and_edit_save;
    private Button bt_activity_blacknumber_add_and_edit_cancel;
    private TextView tv_activity_blacknumber_add_and_edit_title;
    private Context mContext;
    private int mCheckedRadioButtonId;
    private BlackNumberDao mBlackNumberDao;
    private String mAction;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber_add_and_edit);
        mContext = this;
        initView();

        bt_activity_blacknumber_add_and_edit_save.setOnClickListener(this);
        bt_activity_blacknumber_add_and_edit_cancel.setOnClickListener(this);
    }

    private void initView() {
        rg_activity_blacknumber_select_mode = (RadioGroup) findViewById(R.id.rg_activity_blacknumber_select_mode);
        et_activity_blacknumber_add_and_edit_number = (EditText) findViewById(R.id.et_activity_blacknumber_add_and_edit_number);
        bt_activity_blacknumber_add_and_edit_save = (Button) findViewById(R.id.bt_activity_blacknumber_add_and_edit_save);
        bt_activity_blacknumber_add_and_edit_cancel = (Button) findViewById(R.id.bt_activity_blacknumber_add_and_edit_cancel);
        tv_activity_blacknumber_add_and_edit_title = (TextView) findViewById(R.id.tv_activity_blacknumber_add_and_edit_title);
        mBlackNumberDao = new BlackNumberDao(mContext);

        //获取CallSmsSafeActivity.class传递过来的数据
        Intent intent = getIntent();
        mAction = intent.getAction();
        if ("update".equals(mAction)) {
            //获取CallSmsSafeActivity中点击的条目传递过来的数据
            String number = intent.getStringExtra("number");
            int mode = intent.getIntExtra("mode", -1);
            mPosition = intent.getIntExtra("position", -1);
            //更改标题和按钮文本
            tv_activity_blacknumber_add_and_edit_title.setText("更新黑名单");
            bt_activity_blacknumber_add_and_edit_save.setText("更新");
            //回显号码操作
            et_activity_blacknumber_add_and_edit_number.setText(number);
            //输入框不可编辑
            et_activity_blacknumber_add_and_edit_number.setEnabled(false);
            //回显拦截模式操作
            int checkedId = -1;
            switch (mode) {
                case BlackNumberConstant.BLACKNUMBER_CALL:
                    checkedId = R.id.rb_activity_blacknumber_add_and_edit_call;
                    break;
                case BlackNumberConstant.BACKNUMBER_SMS:
                    checkedId = R.id.rb_activity_blacknumber_add_and_edit_sms;
                    break;
                case BlackNumberConstant.BALCKNUMBER_ALL:
                    checkedId = R.id.rb_activity_blacknumber_add_and_edit_all;
                    break;
            }
            rg_activity_blacknumber_select_mode.check(checkedId);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //保存按钮的点击事件
            case R.id.bt_activity_blacknumber_add_and_edit_save:
                String mNumber = et_activity_blacknumber_add_and_edit_number.getText().toString().trim();
                int mMode;
                if (TextUtils.isEmpty(mNumber)) {
                    Toast.makeText(mContext, " 请输入号码", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    mCheckedRadioButtonId = rg_activity_blacknumber_select_mode.getCheckedRadioButtonId();
                    //选择拦截模式
                    switch (mCheckedRadioButtonId) {
                        case R.id.rb_activity_blacknumber_add_and_edit_call:
                            mMode = BlackNumberConstant.BLACKNUMBER_CALL;
                            break;
                        case R.id.rb_activity_blacknumber_add_and_edit_sms:
                            mMode = BlackNumberConstant.BACKNUMBER_SMS;
                            break;
                        case R.id.rb_activity_blacknumber_add_and_edit_all:
                            mMode = BlackNumberConstant.BALCKNUMBER_ALL;
                            break;
                        default:
                            Toast.makeText(mContext, " 请选择拦截模式", Toast.LENGTH_SHORT).show();
                            return;
                    }
                    //判断传递过来的动作是添加还是更新操作
                    if ("update".equals(mAction)) {
                        boolean isUpdate = mBlackNumberDao.updateBlackNumber(mNumber, mMode);
                        if (isUpdate) {
                            Intent intent = new Intent();
                            intent.putExtra("mode", mMode);
//                          intent.putExtra("mode", mode); 修改以前是这一行
                            intent.putExtra("position", mPosition);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(mContext, " 系统繁忙，请稍后再试", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        //添加数据到数据库中
                        boolean isAdd = mBlackNumberDao.addBlackNumber(mNumber, mMode);
                        if (isAdd) {
                            //将号码和拦截模式回传给骚扰拦截界面进行显示
                            Intent intent = new Intent();
                            intent.putExtra("number", mNumber);
                            intent.putExtra("mode", mMode);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(mContext, " 系统繁忙，请稍后再试", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            //取消按钮的点击事件
            case R.id.bt_activity_blacknumber_add_and_edit_cancel:
                finish();
                break;
        }
    }
}
