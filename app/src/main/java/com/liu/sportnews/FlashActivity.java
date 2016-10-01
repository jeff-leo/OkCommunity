package com.liu.sportnews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.liu.sportnews.utils.Config;
import com.liu.sportnews.utils.StatusUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlashActivity extends AppCompatActivity {

    @BindView(R.id.flash_image) ImageView mFlashImage;
    @BindView(R.id.flash_bottom) LinearLayout mLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使状态栏透明
        StatusUtil.StatusChange(this);
        setContentView(R.layout.activity_flash);

        ButterKnife.bind(this);

        StartAnimation();
    }

    private void StartAnimation() {

        float translationY = mLinearLayout.getTranslationY();

        ObjectAnimator imageAnimator = ObjectAnimator.ofFloat(mFlashImage, "scaleY", 1f, 1.1f);
        ObjectAnimator bottomAnimator = ObjectAnimator.ofFloat(mFlashImage, "scaleX", 1f, 1.1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mFlashImage, "alpha", 0, 1);

        AnimatorSet set = new AnimatorSet();
        set.play(imageAnimator).with(bottomAnimator).with(alpha);
        set.setStartDelay(500);
        set.setDuration(3000);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(FlashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
