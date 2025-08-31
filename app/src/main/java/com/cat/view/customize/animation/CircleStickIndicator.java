package com.cat.view.customize.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.cat.view.R;

/**
 * 圆形粘性指示器
 */
public class CircleStickIndicator extends View {

    private int count;
    private int color;
    private float radius;
    private float padding;

    private float currentX;
    private int currentIndex;
    private int selectColor;
    private float selectRadius;
    private boolean isStickEnable;
    private boolean isSwitchFinish = true;

    private Paint paint;
    private Path stickPath;
    private float stickAnimationX;
    private ValueAnimator stickAnimator;
    private final PaintFlagsDrawFilter paintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public CircleStickIndicator(Context context) {
        this(context, null);
    }

    public CircleStickIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleStickIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleStyleable(context, attrs);
        init();
    }

    private void handleStyleable(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Indicator, 0, 0);
        try {
            int padding = dp2px(5);
            int selectRadius = dp2px(6);
            int defaultColor = Color.parseColor("#F6DCC7");
            color = typedArray.getColor(R.styleable.Indicator_indicator_color, defaultColor);
            radius = typedArray.getDimension(R.styleable.Indicator_indicator_radius, padding);
            this.padding = typedArray.getDimension(R.styleable.Indicator_indicator_padding, padding);
            selectColor = typedArray.getColor(R.styleable.Indicator_indicator_selectColor, Color.WHITE);
            isStickEnable = typedArray.getBoolean(R.styleable.Indicator_indicator_stickEnable, true);
            this.selectRadius = typedArray.getDimension(R.styleable.Indicator_indicator_selectRadius, selectRadius);
        } finally {
            typedArray.recycle();
        }
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        stickPath = new Path();
        stickAnimator = new ValueAnimator();
        stickAnimator.setDuration(500);
        stickAnimator.addUpdateListener(
            animation -> {
                stickAnimationX = (float) animation.getAnimatedValue();
                invalidate();
            }
        );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = count - 1;
        int height = (int) selectRadius * 2;
        int width = (int) (size * radius * 2 + size * padding + selectRadius * 2);
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSpecSize = MeasureSpec.getMode(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, height);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(width, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, height);
        } else {
            setMeasuredDimension(width, height);
        }
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(paintFlagsDrawFilter);
        float startX = currentX;
        float selectX = 0;
        for (int i = 0; i < count; i++) {
            if (currentIndex == i) {
                paint.setStrokeWidth(2);
                paint.setColor(selectColor);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(startX + selectRadius, selectRadius, selectRadius, paint);
                selectX = startX + selectRadius;
                startX += selectRadius * 2 + padding;
            } else {
                paint.setColor(color);
                paint.setStrokeWidth(2);
                paint.setStyle(Paint.Style.FILL);
                startX += radius;
                canvas.drawCircle(startX, selectRadius, radius, paint);
                startX += radius + padding;
            }
        }
        if (isStickEnable) {
            if (isSwitchFinish) {
                stickPath.reset();
            } else {
                paint.setColor(selectColor);
                float quadStartX = selectX;
                float quadStartY = getHeight() / 2f - selectRadius;
                stickPath.reset();
                stickPath.moveTo(quadStartX, quadStartY);
                stickPath.quadTo(quadStartX + (stickAnimationX - quadStartX) / 2, selectRadius, stickAnimationX, quadStartY);
                stickPath.lineTo(stickAnimationX, quadStartY + selectRadius * 2);
                stickPath.quadTo(quadStartX + (stickAnimationX - quadStartX) / 2, selectRadius, quadStartX, quadStartY + selectRadius * 2);
                stickPath.close();
                canvas.drawCircle(stickAnimationX, selectRadius, selectRadius, paint);
                canvas.drawPath(stickPath, paint);
            }
        }
    }

    public void setCurrentIndex(int index) {
        if (isStickEnable) {
            invalidateIndex(index);
        } else {
            currentIndex = index;
            invalidate();
        }
    }

    public void setCount(int size) {
        reset();
        count = size;
        requestLayout();
        invalidate();
    }

    private void invalidateIndex(int index) {
        boolean running = stickAnimator.isRunning();
        if (running) {
            stickAnimator.end();
        }
        float startValues = getCurrentIndexX() + selectRadius;
        if (index > currentIndex) {
            stickAnimator.setFloatValues(startValues, startValues + padding + radius * 2);
        } else {
            stickAnimator.setFloatValues(startValues, startValues - padding - radius * 2);
        }
        isSwitchFinish = false;
        stickAnimator.removeAllListeners();
        stickAnimator.addListener(
            new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    isSwitchFinish = true;
                    currentIndex = index;
                    invalidate();
                }

            }
        );
        stickAnimator.start();
    }


    private void reset() {
        count = 0;
        currentX = 0;
        currentIndex = 0;
    }

    private float getCurrentIndexX() {
        if (currentX < 0) {
            float translateCount = -currentX / (radius * 2 + padding);
            return (currentIndex - translateCount) * (radius * 2 + padding);
        }
        return currentIndex * (radius * 2 + padding);
    }

    private int dp2px(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

}