package com.cat.view.animation

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import androidx.core.graphics.withSave
import androidx.core.view.setPadding
import com.cat.view.R
import com.cat.view.touch.dp

class MarqueeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet ? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var endColor = 0
    private var startColor = 0
    private var middleColor = 0

    private var duration = 0
    private var borderWidth = 0f
    private var borderRadius = 0f

    init {
        // 设置view圆角
        outlineProvider = object : ViewOutlineProvider() {

            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, borderRadius)
            }

        }
        clipToOutline = true
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MarqueeLayout)
        try {
            endColor = attributes.getColor(R.styleable.MarqueeLayout_marquee_end_color, Color.BLUE)
            startColor = attributes.getColor(R.styleable.MarqueeLayout_marquee_start_color, Color.BLUE)
            middleColor = attributes.getColor(R.styleable.MarqueeLayout_marquee_middle_color, Color.WHITE)
            duration = attributes.getInt(R.styleable.MarqueeLayout_marquee_duration, 3000)
            borderWidth = attributes.getDimension(R.styleable.MarqueeLayout_marquee_border_width, 4.dp)
            borderRadius = attributes.getDimension(R.styleable.MarqueeLayout_marquee_border_radius, 8.dp)
        } finally {
            attributes.recycle()
        }
        val padding = borderWidth.toInt() / 2
        setPadding(padding)
        // setPadding(borderWidth.toInt(), 0, borderWidth.toInt(), 0)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF by lazy {
        val left = 0f + borderWidth / 2f
        val top = 0f + borderWidth / 2f
        val right = left + width - borderWidth
        val bottom = top + height - borderWidth
        RectF(left, top, right, bottom)
    }

    private val gradientStartColor by lazy {
        LinearGradient(width * 1f, height / 2f, width * 1f, height * 1f, intArrayOf(Color.TRANSPARENT, startColor), floatArrayOf(0f, 0.9f), Shader.TileMode.CLAMP)
    }

    private val gradientEndColor by lazy {
        LinearGradient(width / 2f, height / 2f, width / 2f, 0f, intArrayOf(Color.TRANSPARENT, endColor), floatArrayOf(0f, 0.9f), Shader.TileMode.CLAMP)
    }

    private val animator by lazy {
        val animator = ObjectAnimator.ofFloat(this, "currentSpeed", 0f, 360f)
        animator.repeatCount = -1
        animator.repeatMode = ObjectAnimator.RESTART
        animator.interpolator = null
        animator.duration = duration.toLong()
        animator
    }

    private var currentSpeed = 0f
        set(value) {
            field = value
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        animator.start()
    }

    override fun dispatchDraw(canvas: Canvas) {
        val left = width / 2f
        val top = height / 2f
        val right = left + width
        val bottom = top + width
        canvas.withSave {
            canvas.rotate(currentSpeed, width / 2f, height / 2f)
            paint.shader = gradientStartColor
            canvas.drawRect(left, top, right, bottom, paint)
            paint.shader = null
            if (endColor != -1) {
                // 绘制渐变view2
                paint.shader = gradientEndColor
                canvas.drawRect(left, top, -right, -bottom, paint)
                paint.shader = null
            }
        }
        paint.color = middleColor
        canvas.drawRoundRect(rectF, borderRadius, borderRadius, paint)
        super.dispatchDraw(canvas)
    }

}