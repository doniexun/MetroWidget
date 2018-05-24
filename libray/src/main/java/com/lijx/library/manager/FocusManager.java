package com.lijx.library.manager;

import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class FocusManager {

    //动画的延迟时间
    public static final int DURATION_MS = 250;

    //3种滚动状态
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;

    public static final boolean DEFAULT_USE_ANIMATION = false;
    public static final float DEFAULT_SCALE = 1.0f;
    //控制焦点和焦点框的放大倍数，1.0f的时候表示不放大
    protected float mScale;
    //是否使用动画，这个值只控制是否使用平移动画，放大动画由mScale控制，1.0f表示没有放大动画
    protected boolean mUseAnim;
    //当前的焦点view
    public View mCurrentFocusView;
    //上一个焦点view
    public View mOldFocusView;
    //当前焦点view的注册的父节点
    private ViewGroup mCurrentFocusParent;
    //是否滚动
    protected boolean isScrolling = false;
    //focuswidget的margin值，调整focusWidget从视觉上看能刚好套在焦点view上面
    Rect mMarginRect;

    public FocusManager(float scale, Rect marginRect) {
        mScale = scale;

        if (marginRect == null) {
            marginRect = new Rect(0, 0, 0, 0);
        }
        mMarginRect = marginRect;
    }

    /**
     *  监听注册的viewgroup内的焦点变化情况
     */
    public void onGlobalFocusChanged(List<ViewGroup> attachParents, View metroWidget, View oldFocus, View newFocus){

    }
    /**
     *  监听注册的viewgroup内的滚动情况
     */
    public void onScrollChanged(List<ViewGroup> attachParents, View metroWidget, int state){

    }

    /**
     * 检测焦点view是否完全可见
     */
    public boolean checkFocusViewBounds(ViewGroup parent, View focusview) {
        boolean isCover;
        Rect parentRect = new Rect();
        parent.getGlobalVisibleRect(parentRect);
        Rect focusWidgetRect = new Rect();
        isCover = focusview.getGlobalVisibleRect(focusWidgetRect);
        if (isCover && parentRect.contains(focusWidgetRect)) {
            return (focusWidgetRect.width() >= focusview.getMeasuredWidth() && focusWidgetRect.width() <= focusview.getMeasuredWidth() * mScale)
                    && (focusWidgetRect.height() >= focusview.getMeasuredHeight() && focusWidgetRect.height() <= focusview.getMeasuredHeight() * mScale);
        }
        return false;
    }

    /**
     * 查找焦点是否是注册过的viewgroup的child
     * 如果是返回注册过的viewgroup
     * 如果否返回空
     */
    protected ViewGroup getAttachedParent(List<ViewGroup> attachParents, View view) {
        if (attachParents != null && attachParents.size() != 0) {
            ViewGroup nextParent = (ViewGroup) view.getParent();
            if (attachParents.contains(nextParent)) {
                return nextParent;
            }
            if (nextParent != view.getRootView()) {
                return getAttachedParent(attachParents, nextParent);
            }
        }
        return null;
    }

    /**
     * 检测attach的视图树上是否有能滚动的view
     * 如果有就返回能滚动的这个节点的viewgroup
     * 如果没有返回空
     * 这里只判断了TV项目开发中最常用的两种ViewPager和RecycleView
     * 如果有需要可自行添加
     */
    public ViewGroup checkAttachViewOrParentCanScroll(ViewGroup attachView) {
        if (attachView instanceof ViewPager || attachView instanceof RecyclerView) {
            return attachView;
        }
        ViewGroup parent = (ViewGroup) attachView.getParent();
        if (parent != attachView.getRootView()) {
            return checkAttachViewOrParentCanScroll(parent);
        }
        return null;
    }

    /**
     *  获取view在屏幕上的xy值
     */
    public int[] getLocation(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location;
    }

    public void setCurrentFocusParent(ViewGroup currentFocusParent) {
        mCurrentFocusParent = currentFocusParent;
    }

    public ViewGroup getCurrentFocusParent() {
        return mCurrentFocusParent;
    }
}