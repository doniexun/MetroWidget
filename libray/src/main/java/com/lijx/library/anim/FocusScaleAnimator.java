package com.lijx.library.anim;

import android.animation.TimeAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public class FocusScaleAnimator implements TimeAnimator.TimeListener {
    private View mView;
    private int mDuration;
    private float mScaleDiff;
    private float mFocusLevel = 0f;
    private float mFocusLevelStart;
    private float mFocusLevelDelta;
    private TimeAnimator mAnimator = new TimeAnimator();
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    public FocusScaleAnimator(View view, float scale, int duration) {
        mView = view;
        mDuration = duration;
        mScaleDiff = scale - 1f;
        mAnimator.setTimeListener(this);
    }

    public void animateFocus(boolean select, boolean immediate) {
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

    public FocusScaleAnimator delay(long delayTime) {
        mAnimator.setStartDelay(delayTime);
        return this;
    }
    void endAnimation() {
        mAnimator.end();
    }

    void setFocusLevel(float level) {
        mFocusLevel = level;
        float scale = 1f + mScaleDiff * level;
        mView.setScaleX(scale);
        mView.setScaleY(scale);
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
