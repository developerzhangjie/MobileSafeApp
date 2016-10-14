package com.example.sin.mobilesafe;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import db.BlackNumberOpenHelper;
import db.dao.BlackNumberDao;

/**
 * Created by Sin on 2016/9/19.
 */
@RunWith(MockitoJUnitRunner.class)
public class BlackNumberTest {
    @Mock
    Context mMockContext;
    BlackNumberOpenHelper blackNumberOpenHelper;

    @Test
    public void testCreateDB() {
         blackNumberOpenHelper = new BlackNumberOpenHelper(mMockContext);//创建不出数据库
        blackNumberOpenHelper.getWritableDatabase();//获取数据库
    }

    @Test
    public void addBlackNumber() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(mMockContext);
        blackNumberOpenHelper.getWritableDatabase();//获取数据库
        blackNumberDao.addBlackNumber("110", 0);

    }
}
