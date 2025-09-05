package com.cat.view.customize.particle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.Random;

/**
 * 粒子爆炸效果
 */
public class ParticleExplosion {

    private final static int DEFAULT_DURATION = 400;
    private final static int DEFAULT_ELEMENT_COUNT = 12;

    private final static float DEFAULT_GRAVITY = 6;
    private final static float DEFAULT_WIND_SPEED = 6;
    private final static float DEFAULT_ELEMENT_SIZE = 8;
    private final static float DEFAULT_LAUNCH_SPEED = 18;

    private Paint paint;

    private int count;              // count of element
    private int color;
    private int duration;
    private int windDirection;      //1 or -1
    private int[] colors;

    private float gravity;
    private float windSpeed;
    private float launchSpeed;
    private float elementSize;

    private Location location;

    private float animatorValue;
    private ValueAnimator animator;
    private AnimationEndListener listener;
    private final ArrayList<Element> elements = new ArrayList<>();

    public ParticleExplosion(Location location, int windDirection) {
        this.location = location;
        this.windDirection = windDirection;

        colors = baseColors;
        gravity = DEFAULT_GRAVITY;
        duration = DEFAULT_DURATION;
        count = DEFAULT_ELEMENT_COUNT;
        windSpeed = DEFAULT_WIND_SPEED;
        elementSize = DEFAULT_ELEMENT_SIZE;
        launchSpeed = DEFAULT_LAUNCH_SPEED;
        init();
    }

    private void init() {
        Random random = new Random();
        int size = random.nextInt(colors.length);
        color = colors[size];
        // 给每个火花设定一个随机的方向 0-180
        for (int i = 0; i < count; i++) {
            int angle = random.nextInt(180);
            Element element = new Element(color, Math.toRadians(angle), random.nextFloat() * launchSpeed);
            elements.add(element);
        }
        paint = new Paint();
        paint.setColor(color);
        // BlurMaskFilter maskFilter = new BlurMaskFilter(2, BlurMaskFilter.Blur.NORMAL);
        // mPaint.setMaskFilter(maskFilter);
    }

    public void fire() {
        AccelerateInterpolator interpolator = new AccelerateInterpolator();
        animator = ValueAnimator.ofFloat(1, 0);
        animator.setDuration(duration);
        animator.setInterpolator(interpolator);
        animator.addUpdateListener(
            new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    animatorValue = (float) valueAnimator.getAnimatedValue();
                    // 计算每个火花的位置
                    for (Element element : elements) {
                        double x = element.x + Math.cos(element.direction) * element.speed * animatorValue + windSpeed * windDirection;
                        double y = element.y - Math.sin(element.direction) * element.speed * animatorValue + gravity * (1 - animatorValue);
                        element.x = (float) x;
                        element.y = (float) y;
                    }
                }

            }
        );
        animator.addListener(
            new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onAnimationEnd();
                }

            }
        );
        animator.start();
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void addAnimationEndListener(AnimationEndListener listener) {
        this.listener = listener;
    }

    public void draw(Canvas canvas) {
        int alpha = (int) (225 * animatorValue);
        paint.setAlpha(alpha);
        for (Element element : elements) {
            canvas.drawCircle(location.x + element.x, location.y + element.y, elementSize, paint);
        }
    }

    private static final int[] baseColors = {
            0xFFFF43,
            0x00E500,
            0x44CEF6,
            0xFF0040,
            0xFF00FFB7,
            0x008CFF,
            0xFF5286,
            0x562CFF,
            0x2C9DFF,
            0x00FFFF,
            0x00FF77,
            0x11FF00,
            0xFFB536,
            0xFF4618,
            0xFF334B,
            0x9CFA18,
    };

    public interface AnimationEndListener {

        void onAnimationEnd();

    }

    public static class Location {

        public float x;
        public float y;

        public Location(float x, float y) {
            this.x = x;
            this.y = y;
        }

    }

}