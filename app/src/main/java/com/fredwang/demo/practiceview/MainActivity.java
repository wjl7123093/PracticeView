package com.fredwang.demo.practiceview;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.fredwang.demo.practiceview.view.DashboardView;

public class MainActivity extends AppCompatActivity {

    DashboardView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (DashboardView) findViewById(R.id.view);

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "mark", 0, 12);
        animator.setStartDelay(2000);
        animator.setInterpolator(new DecelerateInterpolator());
//        animator.setDuration(3000);
        animator.start();
    }
}
