package com.example.sin.mobilesafe;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bean.AntivirusInfo;
import db.dao.AntivirusDao;
import utils.MD5Utils;


/**
 * Created by Sin on 2016/10/15.
 * Description:
 */

public class AntivirusActivity extends Activity {
    private ArcProgress arc_progress;
    private PackageManager pm;
    private TextView tv_antivirus_packagename;
    private ListView lv_antivirus_applications;
    private List<AntivirusInfo> list;
    private MyAdatper mMyAdatper;
    private int antivirusCount = 0;
    private int progress = 0;
    private RelativeLayout rel_antivirus_result;
    private RelativeLayout rel_antivirus_acrprogress;
    private TextView tv_antivirus_antivirustext;
    private ImageView iv_antivirus_right;
    private ImageView iv_antivirus_left;
    private Button btn_antivirus_scan;
    private LinearLayout ll_antivirus_canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        initView();
        scan();
    }


    private void initView() {
        arc_progress = (ArcProgress) findViewById(R.id.arc_progress);
        tv_antivirus_packagename = (TextView) findViewById(R.id.tv_antivirus_packagename);
        lv_antivirus_applications = (ListView) findViewById(R.id.lv_antivirus_applications);
        rel_antivirus_acrprogress = (RelativeLayout) findViewById(R.id.rel_antivirus_acrprogress);
        rel_antivirus_result = (RelativeLayout) findViewById(R.id.rel_antivirus_result);
        tv_antivirus_antivirustext = (TextView) findViewById(R.id.tv_antivirus_antivirustext);
        iv_antivirus_right = (ImageView) findViewById(R.id.iv_antivirus_right);
        iv_antivirus_left = (ImageView) findViewById(R.id.iv_antivirus_left);
        btn_antivirus_scan = (Button) findViewById(R.id.btn_antivirus_scan);
        ll_antivirus_canvas = (LinearLayout) findViewById(R.id.ll_antivirus_canvas);
        btn_antivirus_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAnimation();

            }
        });

    }

    private void scan() {
        list = new ArrayList<>();
        pm = getPackageManager();
        arc_progress.setMax(100);
        new Thread() {
            @Override
            public void run() {
                super.run();
                final List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
                final int totalSize = packages.size();
                for (final PackageInfo packageInfo : packages) {
                    SystemClock.sleep(20);
                    progress++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arc_progress.setProgress((int) (progress * 100f / totalSize + 0.5f));
                            tv_antivirus_packagename.setText(packageInfo.packageName);
                            //将数据添加到list中
                            Signature[] signatures = packageInfo.signatures;
                            String s = signatures[0].toCharsString();
                            boolean antivirus = AntivirusDao.isAntivirus(AntivirusActivity.this, MD5Utils.md5(s));
                            AntivirusInfo antivirusInfo = new AntivirusInfo();
                            antivirusInfo.packageName = packageInfo.packageName;
                            antivirusInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                            antivirusInfo.icon = packageInfo.applicationInfo.loadIcon(pm);
                            if (antivirus) {
                                antivirusInfo.isAntivirus = true;
                                antivirusCount++;
                            } else {
                                antivirusInfo.isAntivirus = false;
                            }
                            list.add(antivirusInfo);
                            //显示数据
                            if (mMyAdatper == null) {
                                mMyAdatper = new MyAdatper();
                                lv_antivirus_applications.setAdapter(mMyAdatper);
                            } else {
                                mMyAdatper.notifyDataSetChanged();
                            }
                            if (packages.size() == list.size()) {
                                lv_antivirus_applications.setSelection(0);
                                if (antivirusCount > 0) {
                                    rel_antivirus_result.setVisibility(View.VISIBLE);
                                    rel_antivirus_acrprogress.setVisibility(View.GONE);
                                    tv_antivirus_antivirustext.setText("您的手机很不安全");
                                } else {
                                    rel_antivirus_result.setVisibility(View.VISIBLE);
                                    rel_antivirus_acrprogress.setVisibility(View.GONE);
                                    tv_antivirus_antivirustext.setText("您的手机很安全");
                                }
                                //设置左右图片显示
                                ll_antivirus_canvas.setVisibility(View.VISIBLE);
//                              获取进度条的缓存图片
                                rel_antivirus_acrprogress.setDrawingCacheEnabled(true);
                                rel_antivirus_acrprogress.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                                Bitmap bitmap = rel_antivirus_acrprogress.getDrawingCache();
                                //拆分图片
                                Bitmap leftBitmap = getLeftBitmap(bitmap);
                                Bitmap rightBitmap = getRightBitmap(bitmap);
                                iv_antivirus_left.setImageBitmap(leftBitmap);
                                iv_antivirus_right.setImageBitmap(rightBitmap);
                                showAnimation();

                            } else {
                                lv_antivirus_applications.setSelection(mMyAdatper.getCount());
                            }
                        }
                    });
                }
            }
        }.start();
    }

    private void closeAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(iv_antivirus_left, "translationX", -iv_antivirus_left.getMeasuredWidth(), 0),
                ObjectAnimator.ofFloat(iv_antivirus_right, "translationX", iv_antivirus_right.getMeasuredWidth(), 0),
                ObjectAnimator.ofFloat(iv_antivirus_left, "alpha", 0, 1),
                ObjectAnimator.ofFloat(iv_antivirus_right, "alpha", 0, 1),
                ObjectAnimator.ofFloat(rel_antivirus_result, "alpha", 1, 0));
        animatorSet.setDuration(1000);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ll_antivirus_canvas.setVisibility(View.GONE);
                rel_antivirus_acrprogress.setVisibility(View.VISIBLE);
                scan();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();
    }

    private void showAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(iv_antivirus_left, "translationX", 0, -iv_antivirus_left.getMeasuredWidth()),
                ObjectAnimator.ofFloat(iv_antivirus_right, "translationX", 0, iv_antivirus_right.getMeasuredWidth()),
                ObjectAnimator.ofFloat(iv_antivirus_left, "alpha", 1, 0),
                ObjectAnimator.ofFloat(iv_antivirus_right, "alpha", 1, 0),
                ObjectAnimator.ofFloat(rel_antivirus_result, "alpha", 0, 1)
        );
        animatorSet.setDuration(1000);
        animatorSet.start();
    }

    private Bitmap getRightBitmap(Bitmap bitmap) {
        int width = (int) (bitmap.getWidth() / 2 + 0.5f);
        int height = bitmap.getHeight();
        Bitmap canvasbitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(canvasbitmap);
        Paint paint = new Paint();
        Matrix matrix = new Matrix();
        matrix.setTranslate(-width, 0);
        canvas.drawBitmap(bitmap, matrix, paint);
        return canvasbitmap;
    }

    private Bitmap getLeftBitmap(Bitmap bitmap) {
        int width = (int) (bitmap.getWidth() / 2 + 0.5f);
        int height = bitmap.getHeight();
        Bitmap canvasbitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(canvasbitmap);
        Paint paint = new Paint();
        Matrix matrix = new Matrix();
        //matrix.setTranslate(dx, dy)
        canvas.drawBitmap(bitmap, matrix, paint);
        return canvasbitmap;
    }

    private class MyAdatper extends BaseAdapter {
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
        public android.view.View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                view = View.inflate(AntivirusActivity.this, R.layout.antivirus_item, null);
                viewHolder.iv_anitvirus_icon = (ImageView) view.findViewById(R.id.iv_anitvirus_icon);
                viewHolder.tv_anitvirus_title = (TextView) view.findViewById(R.id.tv_anitvirus_title);
                viewHolder.tv_anitvirus_desc = (TextView) view.findViewById(R.id.tv_anitvirus_desc);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            AntivirusInfo antivirusInfo = list.get(position);
            viewHolder.iv_anitvirus_icon.setImageDrawable(antivirusInfo.icon);
            viewHolder.tv_anitvirus_title.setText(antivirusInfo.name);
            if (antivirusInfo.isAntivirus) {
                viewHolder.tv_anitvirus_desc.setText("病毒");
                viewHolder.tv_anitvirus_desc.setTextColor(Color.RED);
            } else {
                viewHolder.tv_anitvirus_desc.setText("安全");
                viewHolder.tv_anitvirus_desc.setTextColor(Color.GREEN);
            }
            return view;
        }
    }

    private class ViewHolder {
        ImageView iv_anitvirus_icon;
        TextView tv_anitvirus_title, tv_anitvirus_desc;
    }
}
