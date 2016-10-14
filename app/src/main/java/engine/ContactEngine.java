package engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import bean.ContactsInfo;

/**
 * Created by Sin on 2016/9/12.
 */
public class ContactEngine {
    public static List<ContactsInfo> getAllContacts(Context context) {
        List<ContactsInfo> list = new ArrayList<ContactsInfo>();
        //1.获取内容解析者
        ContentResolver contentResolver = context.getContentResolver();
        //2.获取查询路径
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //3.查询
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        };
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        //4.遍历解析cursor
        if (!cursor.equals(null)) {
            //5.获取相应的数据
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                String contactId = cursor.getString(2);
                //6.保存到bean类中
                ContactsInfo contactsInfo = new ContactsInfo();
                contactsInfo.name = name;
                contactsInfo.number = number;
                contactsInfo.contactId = contactId;
                //7.添加到集合中
                list.add(contactsInfo);
            }
        }
        cursor.close();
        return list;
    }

    //获取联系人的头像
    public static Bitmap getContactsPhoto(Context context, String contactId) {
        //1.获取内容解析者
        ContentResolver contentResolver = context.getContentResolver();
        //2.获取头像地址Uri
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
        //3.获取头像，返回流的信息
        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
