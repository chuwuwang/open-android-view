package com.cat.view.customize.text;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.cat.view.R;

public class ScrollNumberView extends View {

    private int mWidth;
    private int mHeight;
    private Paint mPaint;

    private int mNum;
    private String mNumOld;
    private String mNumNew;

    private int mScrollingStart;
    private float mFraction = 0;
    private boolean mScrolling = false;
    private DIRECTION mDirection = DIRECTION.UP;

    private int mDuration = 600;
    private ValueAnimator mAnimator;

    private enum DIRECTION {
        UP, DOWN
    }

    public ScrollNumberView(Context context) {
        super(context);
        init(null);
    }

    public ScrollNumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        this.mWidth = w;
        this.mHeight = h;
    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);

        if (attrs != null) {
            TypedArray t = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ScrollNumberView, 0, 0);
            try {
                float textSize = dip2px(getContext(), 12);
                textSize = t.getDimension(R.styleable.ScrollNumberView_scrollNumber_textSize, textSize);
                int textColor = t.getColor(R.styleable.ScrollNumberView_scrollNumber_textColor, Color.BLACK);
                mPaint.setColor(textColor);
                mPaint.setTextSize(textSize);
                mNum = t.getInt(R.styleable.ScrollNumberView_scrollNumber_number, 0);
                mNumOld = String.valueOf(mNum);
                mDuration = t.getInt(R.styleable.ScrollNumberView_scrollNumber_duration, 600);
            } finally {
                t.recycle();
            }
        }

        // 每个 [0, 1, 2, 3, ...] 的 measureText 宽度是不一样的
        // 从 mScrollingStart 开始进行翻转

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(mDuration);
        mAnimator.addUpdateListener(
            animation -> {
                mFraction = (float) animation.getAnimatedValue();
                invalidate();
            }
        );
        mAnimator.addListener(
            new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    mScrolling = false;
                    mNumOld = mNumNew;
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    mScrolling = true;
                }

            }
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float abs = Math.abs(fontMetrics.ascent - fontMetrics.descent);
        float startY = mHeight / 2 + abs / 2;
        float offsetX = 0f;

        // old number
        float alpha = 1 - mFraction;
        float offset = mPaint.getFontSpacing() * mFraction;
        int oldNumberLen = mNumOld.length();
        float[] oldWidth = new float[oldNumberLen];
        mPaint.getTextWidths(mNumOld, oldWidth);
        for (int i = 0; i < mNumOld.length(); i++) {
            if (mScrolling) {
                if (i < mScrollingStart) {
                    mPaint.setAlpha(255);
                    canvas.drawText(mNumOld, i, i + 1, offsetX, startY, mPaint);
                    offsetX += oldWidth[i];
                    continue;
                }
                int newAlpha = (int) (255 * alpha);
                if (mDirection == DIRECTION.UP) {
                    mPaint.setAlpha(newAlpha);
                    // 偏移位置修改
                    canvas.drawText(mNumOld, i, i + 1, offsetX, startY - offset, mPaint);
                } else {
                    mPaint.setAlpha(newAlpha);
                    canvas.drawText(mNumOld, i, i + 1, offsetX, startY + offset, mPaint);
                }
                offsetX += oldWidth[i];
            } else {
                mPaint.setAlpha(255);
                canvas.drawText(mNumOld, i, i + 1, offsetX, startY, mPaint);
                offsetX += oldWidth[i];
            }
        }

        // new number
        offsetX = 0f;
        alpha = mFraction;
        offset = mPaint.getFontSpacing() * (1 - mFraction);
        if (mScrolling) {
            int newNumberLen = mNumNew.length();
            float[] newWidth = new float[newNumberLen];
            mPaint.getTextWidths(mNumNew, newWidth);
            for (int i = 0; i < mNumNew.length(); i++) {
                // mScrollingStart 之前的数字不用显示
                if (i < mScrollingStart) {
                    offsetX += newWidth[i];
                    continue;
                }
                int newAlpha = (int) (255 * alpha);
                mPaint.setAlpha(newAlpha);
                if (mDirection == DIRECTION.UP) {
                    canvas.drawText(mNumNew, i, i + 1, offsetX, startY + offset, mPaint);
                } else {
                    canvas.drawText(mNumNew, i, i + 1, offsetX, startY - offset, mPaint);
                }
                offsetX += newWidth[i];
            }
        }
    }

    public void setNumber(int number) {
        this.mNum = number;
        if (number < 0) return;
        mNumOld = String.valueOf(mNum);
        invalidate();
    }

    public void increment() {
        if (mNum == Integer.MAX_VALUE) {
            return;
        }
        mScrollingStart = 0;
        mNumNew = String.valueOf(++mNum);
        for (int i = 0; i < mNumOld.length(); i++) {
            boolean bool = mNumOld.charAt(i) != mNumNew.charAt(i);
            if (bool) {
                mScrollingStart = i;
                break;
            }
        }
        mDirection = DIRECTION.UP;
        mAnimator.start();
    }

    public void decrement() {
        if (mNum - 1 < 0) {
            return;
        }
        mNumNew = String.valueOf(--mNum);
        mScrollingStart = 0;
        for (int i = 0; i < mNumNew.length(); i++) {
            boolean bool = mNumNew.charAt(i) != mNumOld.charAt(i);
            if (bool) {
                mScrollingStart = i;
                break;
            }
        }
        mDirection = DIRECTION.DOWN;
        mAnimator.start();
    }

    private int dip2px(Context context, float dipValue) {
        float destiny = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * destiny + 0.5f);
    }

}