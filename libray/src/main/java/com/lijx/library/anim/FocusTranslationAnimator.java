package com.lijx.library.anim;

import android.animation.TimeAnimator;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.lijx.library.utils.MetroUtils;

public class FocusTranslationAnimator implements TimeAnimator.TimeListener {
    private View mView;
    private int mDuration;
    private float mFocusLevel = 0f;
    private float mFocusLevelStart;
    private float mFocusLevelDelta;
    private TimeAnimator mAnimator = new TimeAnimator();
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private Rect mTranslationRect;
    private int mDefaultWidth;
    private int mDefaultHeight;
    private float mScale;

    public FocusTranslationAnimator(View view, float scale, Rect translationRect, int duration) {
        mView = view;
        mDuration = duration;
        mScale = scale;
        mTranslationRect = translationRect;
        mAnimator.setTimeListener(this);
    }

    public void setTranslationRect(Rect translationRect) {
        mTranslationRect = translationRect;
    }

    public void animateFocus(boolean select, boolean immediate) {
        mFocusLevel = 0f;
        MetroUtils.getOrCreateScaleAnimator(mView, mScale, mDuration).animateFocus(false, true);
        ViewGroup.MarginLayoutParams mp = MetroUtils.getMarginLayoutParams(mView);
        mDefaultWidth = mp.width;
        mDefaultHeight = mp.height;
        endAnimation();
        float end = select ? 1 : 0;
        if (immediate) {
            setFocusLevel(end);
        } else if (mFocusLevel != end) {
            mFocusLevelStart = mFocusLevel;
            mFocusLevelDelta = end - mFocusLevelStart;
            mAnimator.start();
        }
    }

    void endAnimation() {
        mAnimator.end();
    }

    void setFocusLevel(float level) {
        mFocusLevel = level;
        int width = mTranslationRect.right - mDefaultWidth;
        int height = mTranslationRect.bottom - mDefaultHeight;
        float x = mTranslationRect.left * level;
        float y = mTranslationRect.top * level;
        ViewGroup.MarginLayoutParams mp = MetroUtils.getMarginLayoutParams(mView);
        mp.width = mDefaultWidth + ((int) (width * level));
        mp.height = mDefaultHeight + ((int) (height * level));
        if (level == 1.0f) {
            mp.leftMargin += x;
            mp.topMargin += y;
            mView.setLayoutParams(mp);
            mView.setTranslationX(0f);
            mView.setTranslationY(0f);
            MetroUtils.getOrCreateScaleAnimator(mView, mScale, mDuration).animateFocus(true, false);
        } else {
            mView.setLayoutParams(mp);
            mView.setTranslationX(x);
            mView.setTranslationY(y);
        }
    }

    float getFocusLevel() {
        return mFocusLevel;
    }

    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        float fraction;
        if (totalTime >= mDuration) {
            fraction = 1;
            mAnimator.end();
        } else {
            fraction = (float) (totalTime / (double) mDuration);
        }
        if (mInterpolator != null) {
            fraction = mInterpolator.getInterpolation(fraction);
        }
        setFocusLevel(mFocusLevelStart + fraction * mFocusLevelDelta);
    }

}
