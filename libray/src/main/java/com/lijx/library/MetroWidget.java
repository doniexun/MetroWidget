package com.lijx.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.lijx.library.manager.AnimationFocusManager;
import com.lijx.library.manager.FocusManager;
import com.lijx.library.manager.NormalFocusManager;

import java.util.ArrayList;
import java.util.List;

public class MetroWidget extends View implements ViewTreeObserver.OnGlobalFocusChangeListener {
    private List<ViewGroup> mAttachParents;
    private ViewGroup mCurrentFocusParents;
    private FocusManager mFocusManager;

    public MetroWidget(Context context) {
        this(context, null);
    }

    public MetroWidget(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MetroWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mAttachParents = new ArrayList<>();
        mCurrentFocusParents = null;
    }

    /**
     *  调用这个方法则在同一个activity中只会有一个焦点框对象
     */
    public static MetroWidget getInstance(Activity activity) {
        View rootView = activity.getWindow().getDecorView();
        View metroView = rootView.findViewById(R.id.metro_widget_view);
        if (metroView != null && metroView instanceof MetroWidget) {
            return (MetroWidget) metroView;
        }
        return new MetroWidget(activity);

    }

    public void setProperties(boolean useAnim, float scale, Rect marginRect) {
        if (useAnim) {
            mFocusManager = new AnimationFocusManager(scale, marginRect);
        } else {
            mFocusManager = new NormalFocusManager(scale, marginRect);
        }
    }

    public void attachTo(ViewGroup parent) {
        if (mFocusManager == null) {
            throw new NullPointerException("you must be call setProperties() method before attachTo() method!");
        }
        if (parent == null) {
            throw new NullPointerException("this parent is null");
        }
        if (this.getParent() == null) {
            ViewGroup rootView = (ViewGroup) parent.getRootView();
            rootView.addView(this);
            this.setId(R.id.metro_widget_view);
        }

        if (!mAttachParents.contains(parent)) {
            mAttachParents.add(parent);
        }


        ViewTreeObserver viewTreeObserver = parent.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalFocusChangeListener(this);
        }
        ViewGroup canScrollView = mFocusManager.checkAttachViewOrParentCanScroll(parent);
        //注册了监听事件一定要记得在onDetach中进行remove
        if (canScrollView != null) {
            if (canScrollView instanceof ViewPager) {
                ViewPager viewPager = (ViewPager) canScrollView;
                viewPager.addOnPageChangeListener(mOnPageChangeListener);
            } else if (canScrollView instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) canScrollView;
                recyclerView.addOnScrollListener(mOnScrollListener);
            }
        }
        this.setVisibility(GONE);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        mFocusManager.onGlobalFocusChanged(mAttachParents, this, oldFocus, newFocus);
    }

    public void onDetach() {
        if (mFocusManager.getCurrentFocusParent() != null) {
            onDetach(mFocusManager.getCurrentFocusParent());
        }
    }

    public void onDetach(ViewGroup attachParent) {
        if (attachParent == null) {
            return;
        }
        ViewTreeObserver viewTreeObserver = attachParent.getViewTreeObserver();
        viewTreeObserver.removeOnGlobalFocusChangeListener(this);
        ViewGroup canScrollView = mFocusManager.checkAttachViewOrParentCanScroll(attachParent);
        if (canScrollView != null) {
            if (canScrollView instanceof ViewPager) {
                ViewPager viewPager = (ViewPager) canScrollView;
                viewPager.removeOnPageChangeListener(mOnPageChangeListener);
            } else if (canScrollView instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView) canScrollView;
                recyclerView.removeOnScrollListener(mOnScrollListener);
            }
        }
        if (mAttachParents.contains(attachParent)) {
            mAttachParents.remove(attachParent);
        }
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mFocusManager.onScrollChanged(mAttachParents, MetroWidget.this, state);
        }
    };

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            mFocusManager.onScrollChanged(mAttachParents, MetroWidget.this, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mFocusManager.onScrollChanged(mAttachParents, MetroWidget.this, recyclerView.getScrollState());
        }
    };
}
