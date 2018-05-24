package com.lijx.library.manager;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.lijx.library.utils.MetroUtils;

import java.util.List;

public class NormalFocusManager extends FocusManager {
    private View mPreValidFocusView;

    public NormalFocusManager() {
        this(DEFAULT_SCALE);
    }

    public NormalFocusManager(float scale) {
        this(scale, null);
    }

    public NormalFocusManager(float scale, Rect rect) {
        super(scale, rect);
        mUseAnim = DEFAULT_USE_ANIMATION;
    }

    @Override
    public void onGlobalFocusChanged(List<ViewGroup> attachParents, View metroWidget, View oldFocus, View newFocus) {
        //如果新的焦点为空或者新焦点等于旧焦点
        mCurrentFocusView = newFocus;
        mOldFocusView = oldFocus;
        if (newFocus == null || newFocus == oldFocus) {
            return;
        }
        setFocusWidgetToFocusView(attachParents, metroWidget, newFocus);
        //只有不在滚动状态的时候才去执行放大效果
        if (!isScrolling) {
            MetroUtils.getOrCreateScaleAnimator(newFocus, mScale, DURATION_MS).animateFocus(true, false);
            MetroUtils.getOrCreateScaleAnimator(metroWidget, mScale, DURATION_MS).animateFocus(true, false);
        }
        if (oldFocus != null) {
            MetroUtils.getOrCreateScaleAnimator(oldFocus, mScale, DURATION_MS).animateFocus(false, false);
        }
    }

    /**
     * 把focusWidget附着在有焦点的view上
     */
    private void setFocusWidgetToFocusView(List<ViewGroup> attachParents, View focusWidget, View focusView) {
        //取消focusWidget的放大效果
        MetroUtils.getOrCreateScaleAnimator(focusWidget, mScale, DURATION_MS).animateFocus(false, true);
        ViewGroup currentParent = getAttachedParent(attachParents, focusView);
        if (currentParent != null) {
            setCurrentFocusParent(currentParent);
            focusWidget.setVisibility(View.VISIBLE);
            int[] focusXY = getLocation(focusView);
            //检测焦点view是否在其parent中完全可见，主要针对RecycleView滚动的情况
            if (!checkFocusViewBounds(currentParent, focusView)) {
                if (mPreValidFocusView != null && checkFocusViewBounds(currentParent, mPreValidFocusView)) {
                    //当Recycleview正在滚动且focusview还不是完全可见的情况，先focuswidget附着在上一个焦点上
                    focusXY = getLocation(mPreValidFocusView);
                    int measuredWidth = mPreValidFocusView.getMeasuredWidth();
                    int measuredHeight = mPreValidFocusView.getMeasuredHeight();
                    float scaleX = mPreValidFocusView.getScaleX();
                    float scaleY = mPreValidFocusView.getScaleY();
                    focusXY[0] = (int) (focusXY[0] + ((measuredWidth * scaleX - measuredWidth) / 2));
                    focusXY[1] = (int) (focusXY[1] + ((measuredHeight * scaleY - measuredHeight) / 2));
                } else {
                    return;
                }
            }
            ViewGroup.MarginLayoutParams mp = MetroUtils.getMarginLayoutParams(focusWidget);
            //设置focusWidget的宽高和左上角的xy值
            mp.width = focusView.getMeasuredWidth() + mMarginRect.left + mMarginRect.right;
            mp.height = focusView.getMeasuredHeight() + mMarginRect.top + mMarginRect.bottom;
            mp.leftMargin = focusXY[0] - mMarginRect.left;
            mp.topMargin = focusXY[1] - mMarginRect.top;
            Log.d("TAG", "left:" + mp.leftMargin + ",top:" + mp.topMargin + ",width:" + mp.width + ",height:" + mp.height
                    + "\nfocusRect:" + focusXY[0] + "," + focusXY[1] + "\nmarginRect:" + mMarginRect.toString());
            focusWidget.setLayoutParams(mp);
            mPreValidFocusView = focusView;
        } else {
            focusWidget.setVisibility(View.GONE);
        }
    }

    @Override
    public void onScrollChanged(List<ViewGroup> attachParents, View metroWidget, int state) {
        switch (state) {
            case SCROLL_STATE_IDLE:
                isScrolling = false;
                //滚动结束后重新设置focusWidget的位置和大小，并且放大新的焦点和focusWidget
                if (mCurrentFocusView != null) {
                    setFocusWidgetToFocusView(attachParents, metroWidget, mCurrentFocusView);
                    MetroUtils.getOrCreateScaleAnimator(mCurrentFocusView, mScale, DURATION_MS).animateFocus(true, false);
                    MetroUtils.getOrCreateScaleAnimator(metroWidget, mScale, DURATION_MS).animateFocus(true, false);
                }
                break;
            case SCROLL_STATE_DRAGGING:
                isScrolling = true;
                break;
            case SCROLL_STATE_SETTLING:
                isScrolling = true;
                break;
        }
    }
}
