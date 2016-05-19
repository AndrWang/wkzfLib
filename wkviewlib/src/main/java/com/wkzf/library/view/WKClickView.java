package com.wkzf.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.system.Os;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.wkzf.library.R;

/**
 * Created by Ted on 2015/11/2.
 * 自定义的点击效果视图，一般用来套在点击效果控件的外层，
 * 也可以自己设置背景颜色充当点击控件，比如替代Button和ImageButton
 * 共支持四种内容样式 矩形（rectangle），圆角（round），圆形或椭圆（oval），两侧半圆形（semicircle）
 */
public class WKClickView extends FrameLayout
    implements View.OnClickListener, View.OnLongClickListener {
  private int type = 0;
  private int normalClickBgId = -1;
  private int rippleClickBgId = -1;

  private View clickView = null;

  private OnClickListener onClickListener;
  private OnLongClickListener onLongClickListener;

  @Override public void onClick(View v) {
    if (null != onClickListener) {
      onClickListener.onClick(this);
    }
  }

  @Override public boolean onLongClick(View v) {
    if (null != onLongClickListener) {
      onLongClickListener.onLongClick(this);
      return true;
    }
    return false;
  }

  @Override public void setOnClickListener(OnClickListener l) {
    super.setOnClickListener(l);
    onClickListener = l;
  }

  @Override public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
    this.onLongClickListener = onLongClickListener;
  }

  @Override public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    if (null != clickView) clickView.setEnabled(enabled);
  }

  public WKClickView(Context context) {
    super(context);
    initViews();
  }

  public WKClickView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initViews();
    initFromAttributes(context, attrs);
  }

  public WKClickView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initFromAttributes(context, attrs);
    initViews();
  }

  private void initFromAttributes(Context context, AttributeSet attrs) {
    if (attrs != null) {
      TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WKClickView);
      type = array.getInt(R.styleable.WKClickView_layout_style, 0);
      normalClickBgId = array.getResourceId(R.styleable.WKClickView_normal_bg, -1);
      rippleClickBgId = array.getResourceId(R.styleable.WKClickView_ripple_bg, -1);
      array.recycle();
    } else {
      type = 0;
      normalClickBgId = -1;
      rippleClickBgId = -1;
    }
  }

  private void initViews() {
    if (null != findViewWithTag("click")) return;
    this.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
  }

  private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener =
      new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override public void onGlobalLayout() {
          if (null == findViewWithTag("click")) {
            makeClickView();
          }
          FrameLayout.LayoutParams layoutParams =
              new FrameLayout.LayoutParams(getWidth(), getHeight());
          clickView.setLayoutParams(layoutParams);
          clickView.setBackgroundResource(getClickViewResId());
          clickView.bringToFront();
          if (Build.VERSION.SDK_INT > 15) {
            WKClickView.this.getViewTreeObserver()
                .removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
          } else {
            WKClickView.this.getViewTreeObserver()
                .removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
          }
        }
      };

  private void makeClickView() {
    clickView = new View(getContext());
    clickView.setOnClickListener(WKClickView.this);
    clickView.setOnLongClickListener(WKClickView.this);
    clickView.setTag("click");
    WKClickView.this.addView(clickView);
  }

  private int getClickViewResId() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (rippleClickBgId > 0) return rippleClickBgId;
    } else {
      if (normalClickBgId > 0) return normalClickBgId;
    }
    if (type == 0) {
      return R.drawable.wk_click_view_rectangle_bg;
    } else if (type == 1) {
      return R.drawable.wk_click_view_oval_bg;
    } else if (type == 2) {
      return R.drawable.wk_click_view_round_bg;
    } else if (type == 3) {
      return R.drawable.wk_click_view_semicircle_bg;
    } else {
      return R.drawable.wk_click_view_rectangle_bg;
    }
  }
}
