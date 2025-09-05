package com.cat.view.customize.particle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.cat.view.R;

import java.util.LinkedList;
import java.util.Locale;

/**
 * 彗星, 里包含很多 {@link Dust} , 每个 Dust 代表在某处喷射的点集 {@link Element}
 */
public class Comet extends View {

    private static final String TAG = "CometView";

    private final int DEFAULT_DUST_SIZE = 20;
    private final int DEFAULT_INNER_MARGIN = 10;

    private Paint mPaint;

    private float mCx;
    private float mCY;
    private float mRadius;
    private int mInnerMargin;
    private int mStrokeWidth = 4;

    private float mAnimatedValue = 0;
    private ValueAnimator mValueAnimator;

    private boolean mIsRunning = false;

    private final LinkedList<Dust> mDusts = new LinkedList<>();

    public Comet(Context context) {
        super(context);
        init(null);
    }

    public Comet(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mInnerMargin = DEFAULT_INNER_MARGIN;
        if (attrs != null) {
            TypedArray t = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.CometView, 0, 0);
            try {
                mRadius = t.getDimension(R.styleable.CometView_comet_distance, 0);
            } finally {
                t.recycle();
            }
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        getViewTreeObserver().addOnPreDrawListener(
            new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mCx = getWidth() / 2;
                    mCY = getHeight() / 2;
                    float t = mCx > mCY ? mCx - mStrokeWidth : mCY - mStrokeWidth;
                    t -= dip2px(mInnerMargin);
                    // 对 radius 进行校正, 但通常在配置文件中就应该配置好了
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
        super.onDraw(canvas);
        for (int i = 0; i < mDusts.size(); i++) {
            Dust dust = mDusts.get(i);
            dust.draw(canvas);
        }
        if (mIsRunning && mDusts.size() < DEFAULT_DUST_SIZE) addDust();
        if ( ! mIsRunning && mDusts.size() > 0) invalidate();
    }

    public void start(float angle) {
        mIsRunning = true;
        mAnimatedValue = angle;
        invalidate();
    }

    public void end() {
        mIsRunning = false;
    }

    public void setRadius(float radius) {
        this.mRadius = radius;
    }

    public float getRadius(float mRadius) {
        return this.mRadius;
    }

    public void addDust() {
        Dust dust = new Dust(mAnimatedValue, mCx, mCY, mRadius);
        dust.addAnimatorListener(() -> mDusts.remove(dust));
        mDusts.add(dust);
        dust.fire();
    }

    public int getInnerMargin() {
        return mInnerMargin;
    }

    private int dip2px(float dipValue) {
        float destiny = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * destiny + 0.5f);
    }

}