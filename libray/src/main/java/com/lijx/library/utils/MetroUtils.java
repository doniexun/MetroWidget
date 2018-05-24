package com.lijx.library.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.lijx.library.R;
import com.lijx.library.anim.FocusScaleAnimator;
import com.lijx.library.anim.FocusTranslationAnimator;


public class MetroUtils {

    public static ViewGroup.MarginLayoutParams getMarginLayoutParams(View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        ViewGroup.MarginLayoutParams mp = null;
        //获取view的margin设置参数
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            mp = (ViewGroup.MarginLayoutParams) lp;
        } else {
            //不存在时创建一个新的参数
            //基于View本身原有的布局参数对象
            mp = new ViewGroup.MarginLayoutParams(lp);
        }
        return mp;
    }

    /**
     *  获取缩放动画类的实例
     */
    public static FocusScaleAnimator getOrCreateScaleAnimator(View view, float scale, int duration) {
        FocusScaleAnimator animator = (FocusScaleAnimator) view.getTag(R.id.focus_scale_animator);
        if (animator == null) {
            animator = new FocusScaleAnimator(view, scale, duration);
            view.setTag(R.id.focus_scale_animator, animator);
        }
        return animator;
    }

    /**
     *  平移动画类的实例
     */
    public static FocusTranslationAnimator getOrCreateTranslationAnimator(View view, float scale, Rect translationRect, int duration) {
        FocusTranslationAnimator animator = (FocusTranslationAnimator) view.getTag(R.id.focus_translation_animator);
        if (animator == null) {
            animator = new FocusTranslationAnimator(view,scale, translationRect, duration);
            view.setTag(R.id.focus_translation_animator, animator);
        }else{
            animator.setTranslationRect(translationRect);
        }
        return animator;
    }
}
