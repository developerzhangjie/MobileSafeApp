package com.example.sin.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import service.ProtectedService;
import utils.Constants;
import utils.PackageUtil;
import utils.ServicUtil;
import utils.SharedPreferencesUtils;

public class SplashActivity extends Activity {
    private final String TAG = "splash";
    String versionName;
    private String apkurl;
    private String desc;
    private String responeseCode;
    private ProgressDialog progressDialog;
    private final int INSTALL_REQUESTCODE = 100;
    private Handler handler = new Handler();
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            update();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
        TextView splash = (TextView) findViewById(R.id.tv_splash);
        versionName = PackageUtil.getVersionName(this);
        splash.setText("版本:" + versionName);
        //连接服务器，查看是否有最新版本
//        update();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //这里面操作主线程
                if (SharedPreferencesUtils.getBoolean(SplashActivity.this, Constants.TOGGLE, true)) {
                    update();
                } else {
                    enterHome();
                }
            }
        }, 2000);
//        handler.sendEmptyMessageDelayed(0, 2000);
        //拷贝数据库
        copyDB("address.db");
        copyDB("commonnum.db");
        //开启前台进程服务,首先判断服务有没有开启，没开启，就开启
        if (!ServicUtil.isServiceRunning(this, "service.ProtectedService")) {
            startService(new Intent(this, ProtectedService.class));
        }
    }

    /**
     * 拷贝数据库
     */
    private void copyDB(String name) {
        File file = new File(getFilesDir(), name);
        //判断文件是否存在,存在不去拷贝
        if (!file.exists()) {
            //1.获取assets管理者
            AssetManager assetManager = getAssets();
            InputStream in = null;
            FileOutputStream out = null;
            try {
                //2.读取数据库
                in = assetManager.open(name);
                //getCacheDir() : 获取缓存目录
                //getFilesDir() : 获取文件的目录
                out = new FileOutputStream(file);
                //3.读写操作
                byte[] b = new byte[1024];
                int len = -1;
                while ((len = in.read(b)) != -1) {
                    out.write(b, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //4.关流
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
            }
        }
    }

    //连接服务器，查看是否有最新版本
    private void update() {
        //需要联网，1.这是耗时操作，要在子线程中执行；可以使用第三方类库
        //2.需要加联网权限

        //连接超时时间
        HttpUtils httpUtils = new HttpUtils(2000);
        //请求方法 url 回调
        String CONNECTIONURL = "http://192.168.1.133:8080/update.html";
        httpUtils.send(HttpRequest.HttpMethod.GET, CONNECTIONURL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //1.获取服务器返回的数据 code 新版下载地址 apkurl 新版说明 desc
                String result = responseInfo.result;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    responeseCode = jsonObject.getString("code");
                    apkurl = jsonObject.getString("apkurl");
                    desc = jsonObject.getString("desc");
                    Log.d(TAG, result);
                    Log.d(TAG, "服务器返回的数据：" + responeseCode + "  下载地址:" + apkurl + "  说明:" + desc);
                    //2.查看是否有最新版本
                    //没有最新版本
                    if (responeseCode.equals(versionName)) {
                        Log.d(TAG, "没有可更新，已是最新版本");
                        //进入主界面
                        enterHome();
                    } else {
                        //有最新版本
                        Log.d(TAG, "有可更新版本");
                        //弹出对话框，让用户进行选择是否升级
                        showUpdateDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                enterHome();
                Log.d(TAG, "连接失败");
            }
        });
    }

    //弹出对话框，让用户进行选择是否升级
    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置标题
        builder.setTitle("发现新版本" + responeseCode);
        //设置信息
        builder.setMessage(desc);
        //设置图标
        builder.setIcon(R.drawable.ic_launcher);
        //监听对话框消失的操作
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            //当对话框消失的时候执行
            @Override
            public void onCancel(DialogInterface dialog) {
                //进入主界面
                enterHome();
            }
        });
        //设置是否升级按钮
        //立即升级按钮
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载新版apk
                downloadApk();
            }
        });
        //暂不升级按钮
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //暂不升级
                //销毁对话框
                dialog.dismiss();
                //进入主界面
                enterHome();

            }
        });
        builder.show();
    }

    private void downloadApk() {
        //设置下载进度条
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(progressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        //需要联网 在子线程中操作
        HttpUtils http = new HttpUtils();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            http.download(apkurl, "sdcard/app-debug.apk", new RequestCallBack() {

                @Override
                public void onSuccess(ResponseInfo responseInfo) {
                    Log.d(TAG, "下载成功");
                    //销毁下载进度对话框
                    progressDialog.dismiss();
                    //安装新版本APK
                    installerApk();
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.d(TAG, "下载失败");
                    //销毁下载进度对话框
                    progressDialog.dismiss();
                    //安装失败，进入主界面
                    enterHome();
                }

                /*total 最大进度
                * current 当前进度
                *isUploading 是否上传 */
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    //设置最大进度
                    progressDialog.setMax((int) total);
                    //设置当前进度
                    progressDialog.setProgress((int) current);
                }
            });
        } else {
            Toast.makeText(SplashActivity.this, "sd卡异常", Toast.LENGTH_SHORT).show();
        }

    }

    //安装新版本APK
    private void installerApk() {
        /*
        * <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="content" />
                <data android:scheme="file" />
                <data android:mimeType="application/vnd.android.package-archive" />
                */
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(new File("sdcard/app-debug.apk")), "application/vnd.android.package-archive");
        startActivityForResult(intent, INSTALL_REQUESTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //安装完成，进入主界面
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    //进入主界面
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //通常情况下，按返回键，不是进入splash，而是直接退出程序
        finish();
    }
}


