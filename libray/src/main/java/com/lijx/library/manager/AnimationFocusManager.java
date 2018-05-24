package com.lijx.library.manager;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.lijx.library.utils.MetroUtils;

import java.util.List;

public class AnimationFocusManager extends FocusManager {

    public AnimationFocusManager() {
        this(DEFAULT_SCALE);
    }

    public AnimationFocusManager(float scale) {
        this(scale, null);
    }

    public AnimationFocusManager(float scale, Rect rect) {
        super(scale, rect);
        mUseAnim = true;
    }

    @Override
    public void onGlobalFocusChanged(List<ViewGroup> attachParents, View metroWidget, View oldFocus, View newFocus) {
        mCurrentFocusView = newFocus;
        mOldFocusView = oldFocus;
        if (newFocus == null || newFocus == oldFocus) {
            return;
        }
        ViewGroup currentParent = getAttachedParent(attachParents, newFocus);
        if (currentParent != null) {
            setCurrentFocusParent(currentParent);
            metroWidget.setVisibility(View.VISIBLE);
            if (checkFocusViewBounds(currentParent, newFocus)) {
                translationToFocusView(metroWidget, newFocus);
            } else {
                MetroUtils.getOrCreateScaleAnimator(metroWidget, mScale, DURATION_MS).delay(0).animateFocus(false, false);
            }
        } else {
            metroWidget.setVisibility(View.GONE);
        }
        if (oldFocus != null) {
            MetroUtils.getOrCreateScaleAnimator(oldFocus, mScale, DURATION_MS).delay(0).animateFocus(false, false);
        }
    }

    private void translationToFocusView(View focusWidget, View newFocus) {
        int[] focusXY = getLocation(newFocus);
        ViewGroup.MarginLayoutParams mp = MetroUtils.getMarginLayoutParams(focusWidget);
        Rect rect = new Rect();
        rect.left = focusXY[0] - mMarginRect.left - mp.leftMargin;
        rect.top = focusXY[1] - mMarginRect.top - mp.topMargin;
        rect.right = newFocus.getMeasuredWidth() + mMarginRect.left + mMarginRect.right;
        rect.bottom = newFocus.getMeasuredHeight() + mMarginRect.top + mMarginRect.bottom;
        MetroUtils.getOrCreateTranslationAnimator(focusWidget, mScale, rect, DURATION_MS / 2).animateFocus(true, false);
        MetroUtils.getOrCreateScaleAnimator(newFocus, mScale, DURATION_MS / 2).delay(DURATION_MS / 2).animateFocus(true, false);
    }

    @Override
    public void onScrollChanged(List<ViewGroup> attachParents, View metroWidget, int state) {
        switch (state) {
            case SCROLL_STATE_IDLE:
                isScrolling = false;
                if (mCurrentFocusView != null) {
                    if (mOldFocusView != null && getAttachedParent(attachParents, mCurrentFocusView) != getAttachedParent(attachParents, mOldFocusView)) {
                        return;
                    }
                    int[] focusXY = getLocation(mCurrentFocusView);
                    ViewGroup.MarginLayoutParams mp = MetroUtils.getMarginLayoutParams(metroWidget);
                    Rect rect = new Rect();
                    rect.left = focusXY[0] - mMarginRect.left - mp.leftMargin;
                    rect.top = focusXY[1] - mMarginRect.top - mp.topMargin;
                    rect.right = mp.width;
                    rect.bottom = mp.height;
                    MetroUtils.getOrCreateTranslationAnimator(metroWidget, mScale, rect, DURATION_MS / 2).animateFocus(true, false);
                    MetroUtils.getOrCreateScaleAnimator(mCurrentFocusView, mScale, DURATION_MS / 2).delay(DURATION_MS / 2).animateFocus(true, false);
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
