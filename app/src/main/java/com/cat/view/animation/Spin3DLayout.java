package com.cat.view.animation;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

public class Spin3DLayout extends LinearLayout {

    private float rotateY = 0;
    private boolean isLeftRotate = true;

    private final Matrix matrix = new Matrix();
    private final Camera camera = new Camera();

    public Spin3DLayout(@NonNull Context context) {
        super(context);
    }

    public Spin3DLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Spin3DLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // camera保存状态
        camera.save();
        // camera设置绕Y轴旋转角度
        camera.rotateY(rotateY);
        // 将变换应用到canvas上
        camera.applyToCanvas(canvas);
        camera.getMatrix(matrix);
        if (isLeftRotate) {
            // 设置靠右进行旋转
            matrix.preTranslate(0, -getHeight() >> 1);
            matrix.postTranslate(0, getHeight() >> 1);
        } else {
            // 设置靠左进行旋转
            matrix.preTranslate(-getWidth(), -getHeight() >> 1);
            matrix.postTranslate(getWidth(), getHeight() >> 1);
        }
        canvas.setMatrix(matrix);
        // camera恢复状态
        camera.restore();
        super.dispatchDraw(canvas);
    }

    public void setRotateY(float rotateY) {
        this.rotateY = rotateY;
        invalidate();
    }

    public void setIsLeftRotate(boolean isLeft) {
        this.isLeftRotate = isLeft;
    }

    public static class Spin3DTransformer implements ViewPager2.PageTransformer {

        @Override
        public void transformPage(@NonNull View page, float position) {
            Spin3DLayout constraintLayout = (Spin3DLayout) page;
            int tiltDegree = 34; // 倾斜度
            float rotateY = position * tiltDegree;
            constraintLayout.setRotateY(rotateY);
            if (position > 0) {
                constraintLayout.setIsLeftRotate(true);
            } else {
                constraintLayout.setIsLeftRotate(false);
            }
        }

    }

}