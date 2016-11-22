package com.example.sin.mobilesafe;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Sin on 2016/9/8.
 */
public class SetUp1Activity extends SetUpBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    protected boolean previous_activity() {
        return false;
    }

    @Override
    protected boolean next_activity() {
        Intent intent = new Intent(this, SetUp2Activity.class);
        startActivity(intent);
        return false;
    }
}
