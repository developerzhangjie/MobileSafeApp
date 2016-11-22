package com.example.sin.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * description:所有引导页的父类
 * Created by Sin on 2016/9/10.
 */
public abstract class SetUpBaseActivity extends Activity {
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetector(this, new MyOnGestureListener());
    }

    //注册给TouchEvent监听器，把activity中的TouchEvent事件交给Activity处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        //e1:按下的事件，保存有按下的坐标
        //e2:抬起的事件，保存有抬起的坐标
        //velocity:滑动的速度
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int startX = (int) e1.getRawX();
            int endX = (int) e2.getRawX();
            /*int startY = (int) e1.getRawY();
            int endY = (int) e2.getRawY();
            这一段的作用是消除因斜滑造成的误操作，用户体验不好；但是由于太敏感，体验更不好，所以注释掉
            if (Math.abs(startY - endY) > 100) {
                Toast.makeText(SetUpBaseActivity.this, "方向不对", Toast.LENGTH_SHORT).show();
                return true;
            }*/
            if ((startX - endX) > 100) {
                doNext();
            }
            if ((endX - startX) > 100) {
                doPrevious();
            }
            return true;
        }
    }

    //下一步的点击事件,这个在button的xml中onClick绑定了
    public void next(View view) {
        doNext();
    }

    //上一步的点击事件,这个在button的xml中onClick绑定了
    public void previous(View view) {
        doPrevious();
    }

    //下一步的具体操作
    private void doNext() {
        if (next_activity()) {
            return;
        }
        finish();
        //创建动画，滑动效果
        overridePendingTransition(R.anim.setup_next_enter, R.anim.setup_next_exit);
    }

    //上一步的具体操作
    private void doPrevious() {
        if (previous_activity()) {
            return;
        }
        finish();
        //创建动画，滑动效果
        overridePendingTransition(R.anim.setup_previous_enter, R.anim.setup_previous_exit);
    }


    //获取上一步的activity
    protected abstract boolean previous_activity();

    //获取下一步的activity
    protected abstract boolean next_activity();
}
