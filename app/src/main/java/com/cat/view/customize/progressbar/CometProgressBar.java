package com.cat.view.customize.progressbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.cat.view.R;

import java.util.Locale;

/**
 * 模仿彗星形状的 ProgressBar
 */
public class CometProgressBar extends View {

    private static final String TAG = "CometProgressBar";

    // 默认有5层圆圈
    private static final int DEFAULT_COUNT = 5;
    private static final int DEFAULT_ANGLE_OFFSET = 3;
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final float DEFAULT_STROKE_WIDTH = 2;

    private Paint mPaint;

    private int mCx;
    private int mCY;
    private int mCount;
    private int mToColor;
    private int mFromColor;
    private int mAngleOffset;
    private float mRadius;
    private float mStrokeWidth = 6;

    private ValueAnimator mValueAnimator;
    private OnUpdateListener mUpdateListener;

    public CometProgressBar(Context context) {
        super(context);
        init(null);
    }

    public CometProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mCount = DEFAULT_COUNT;
        mToColor = Color.WHITE;
        mFromColor = Color.BLACK;
        mAngleOffset = DEFAULT_ANGLE_OFFSET;
        if (attrs != null) {
            TypedArray t = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressBar, 0, 0);
            try {
                int strokeWidth = dip2px(DEFAULT_STROKE_WIDTH);
                mCount = t.getInt(R.styleable.ProgressBar_progressbar_count, DEFAULT_COUNT);
                mToColor = t.getColor(R.styleable.ProgressBar_progressbar_toColor, Color.WHITE);
                mFromColor = t.getColor(R.styleable.ProgressBar_progressbar_fromColor, Color.BLACK);
                mRadius = t.getDimension(R.styleable.ProgressBar_progressbar_radius, 0);
                mStrokeWidth = t.getDimension(R.styleable.ProgressBar_progressbar_strokeWidth, strokeWidth);
                mAngleOffset = t.getInt(R.styleable.ProgressBar_progressbar_angleOffset, DEFAULT_ANGLE_OFFSET);
            } finally {
                t.recycle();
            }
        }

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        getViewTreeObserver().addOnPreDrawListener(
            new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mCx = getWidth() / 2;
                    mCY = getHeight() / 2;
                    // 扫描是顺时针的
                    SweepGradient shader = new SweepGradient(mCx, mCY, mToColor, mFromColor);
                    mPaint.setShader(shader);
                    float t = mCx > mCY ? mCY - mStrokeWidth : mCx - mStrokeWidth;
                    if (mRadius == 0 || mRadius > t) {
                        mRadius = t;
                    }
                    String msg = String.format(Locale.US, "onPreDraw width: %d, height: %d, radius: %f", getWidth(), getHeight(), mRadius);
                    Log.i(TAG, msg);
                    getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }

            }
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        int offset = 0;
        for (int i = 0; i < mCount; i++) {
            canvas.rotate(offset, mCx, mCY);
            float t = mRadius + mStrokeWidth * i;
            Path path = new Path();
            path.addArc(mCx - t, mCY - t, mCx + t, mCY + t, 0, -350);
            canvas.drawPath(path, mPaint);
            offset -= mAngleOffset;
        }
        canvas.restore();
    }

    public void setFromColor(int color) {
        this.mFromColor = color;
        SweepGradient sweep = new SweepGradient(mCx, mCY, mToColor, mFromColor);
        mPaint.setShader(sweep);
        if (getParent() != null) invalidate();
    }

    public void setToColor(int color) {
        this.mToColor = color;
        SweepGradient sweep = new SweepGradient(mCx, mCY, mToColor, mFromColor);
        mPaint.setShader(sweep);
        if (getParent() != null) invalidate();
    }

    public void setRadius(float radius) {
        this.mRadius = radius;
        if (getParent() != null) invalidate();
    }

    public float getRadius() {
        return this.mRadius;
    }

    public void start() {
        if (mValueAnimator == null) {
            LinearInterpolator interpolator = new LinearInterpolator();
            mValueAnimator = ObjectAnimator.ofFloat(this, "rotation", 0, 360).setDuration(2000);
            mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mValueAnimator.setInterpolator(interpolator);
            mValueAnimator.addUpdateListener(
                animation -> {

                    float value = (float) animation.getAnimatedValue();
                    if (mUpdateListener != null) {
                        mUpdateListener.update(value);
                    }

                }
            );
            mValueAnimator.addListener(
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mUpdateListener != null) mUpdateListener.end();
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (mUpdateListener != null) mUpdateListener.start();
                    }

                }
            );
        }
        mValueAnimator.start();
    }

    public void end() {
        boolean isRunning = mValueAnimator != null && mValueAnimator.isRunning();
        if (isRunning) mValueAnimator.end();
    }

    public void setUpdateListener(OnUpdateListener listener) {
        this.mUpdateListener = listener;
    }

    public interface OnUpdateListener {

        void update(float value);

        void start();

        void end();

    }

    private int dip2px(float dipValue) {
        float destiny = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * destiny + 0.5f);
    }

}