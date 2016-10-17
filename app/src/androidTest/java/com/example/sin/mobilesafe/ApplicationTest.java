package com.example.sin.mobilesafe;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.util.List;

import bean.BlackNumberInfo;
import bean.ContactsInfo;
import db.BlackNumberOpenHelper;
import db.WatchDogOpenHelper;
import db.dao.AntivirusDao;
import db.dao.BlackNumberDao;
import db.dao.WatchDogDao;
import engine.ContactEngine;
import utils.MD5Utils;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

    }

    public void testContacts() {
        List<ContactsInfo> list = ContactEngine.getAllContacts(getContext());
        for (ContactsInfo contactsInfo : list) {
            Log.d("TAG", contactsInfo.toString());
        }
    }

    public void testCreateDB() {
        BlackNumberOpenHelper blackNumberOpenHelper = new BlackNumberOpenHelper(getContext());
        blackNumberOpenHelper.getReadableDatabase();
    }

    public void testAddDB() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(getContext());
        for (int i = 0; i < 1000; i++) {
            blackNumberDao.addBlackNumber("10086" + i, 0);
          /*  blackNumberDao.addBlackNumber("10010", 1);
            blackNumberDao.addBlackNumber("10000", 2);*/
        }
    }

    public void testDeleteDB() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(getContext());
        blackNumberDao.deleteBlackNumber("10010");
    }

    public void testUpdateDB() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(getContext());
        blackNumberDao.updateBlackNumber("10010", 9);
    }

    public void testQueryBlackNumber() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(getContext());
        int mode = blackNumberDao.queryBlackNumber("10086");
        assertEquals(0, mode);

    }

    public void testQueryAllBlackNumber() {
        BlackNumberDao blackNumberDao = new BlackNumberDao(getContext());
        List<BlackNumberInfo> list = blackNumberDao.queryAllBlackNumber();
        for (BlackNumberInfo blackNumberInfo : list) {
            System.out.println(blackNumberInfo.toString());
        }
    }

    public void testCreaateLockAPP() {
        WatchDogOpenHelper watchDogOpenHelper = new WatchDogOpenHelper(getContext());
        watchDogOpenHelper.getReadableDatabase();
        //创建成功
    }

    public void testAddLockApp() {
        WatchDogDao watchDogDao = new WatchDogDao(getContext());
        watchDogDao.addLockAPP("fuckYou");
        //通过
    }

    //获取系统短信测试
   /* public void testGetSMS() {
        SMSEngine.getSMS(getContext());
    }*/

    //测试MD5
    public void testMD5() {
//        String s = Md5Utils.md5("123456");
        String s = MD5Utils.md5("123456");
        System.out.println(s);
    }

    //病毒测试
    public void testAntivirus() {
        boolean b = AntivirusDao.isAntivirus(getContext(), "e10adc3949ba59abbe56e057f20f883e");
        if (b) {
            System.out.println("是病毒");
        } else {
            System.out.println("不是病毒");
        }

    }
}