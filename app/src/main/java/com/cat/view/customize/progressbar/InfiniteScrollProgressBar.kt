package com.cat.view.customize.progressbar

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.annotation.FloatRange
import com.cat.view.R
import com.cat.view.customize.animation.InfiniteAnimateView
import com.cat.view.utils.dp
import kotlin.math.min
import kotlin.properties.Delegates.observable

/**
 * 无限流动的进度条
 */
class InfiniteScrollProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet ? = null,
    defStyleAttr: Int = 0,
) : InfiniteAnimateView(context, attrs, defStyleAttr) {

    private companion object {

        private const val MAX_ANGLE = 360

        private const val MIN_SWEEP_ANGLE = 10f

        private const val SPIN_DURATION_MS = 2_000L

    }

    private val progressRect = RectF()
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 进度 [0, 1) 进度条长度占比
     0 - 没有进度, 只显示最小的弧度
     0.5 - 显示一半的弧度
     1 - 显示完整的弧度
     进度条的弧度范围 [MIN_SWEEP_ANGLE, 360)
     进度条的弧度 = MIN_SWEEP_ANGLE + progress * (360 - MIN_SWEEP_ANGLE)
     进度条的弧度永远不会等于 360, 因为那样就没有意义了, 那就成了一个闭环
     1 代表进度条已经满了, 但是仍然会显示最小的弧度, 只是这个弧度会不停地旋转
     */
    @FloatRange(from = .0, to = 1.0, toInclusive = false)
    var progress: Float = 0f
        set(value) {
            field = when {
                value < 0f -> 0f
                value > 1f -> 1f
                else -> value
            }
            sweepAngle = convertToSweepAngle(field)
            invalidate()
        }

    // [0, 360)
    private var currentAngle by observable(0f) { _, _, _ -> invalidate() }
    private var sweepAngle by observable(MIN_SWEEP_ANGLE) { _, _, _ -> invalidate() }

    private var padding = 8.0f
    private var backgroundRadius = 0f

    private val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressBar, defStyleAttr, 0)

    init {
        val strokeColor = Color.parseColor("#80FFFFFF")
        val backgroundColor = Color.parseColor("#20000000")
        try {
            progressPaint.color = typedArray.getColor(R.styleable.ProgressBar_progressbar_color, Color.WHITE)
            strokePaint.color = typedArray.getColor(R.styleable.ProgressBar_progressbar_strokeColor, strokeColor)
            backgroundPaint.color = typedArray.getColor(R.styleable.ProgressBar_progressbar_backgroundColor, backgroundColor)
        } finally {
            typedArray.recycle()
        }
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = 2.dp

        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = 4.dp

        backgroundPaint.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val verticalHalf = (measuredHeight - paddingTop - paddingBottom) / 2f
        val horizontalHalf = (measuredWidth - paddingStart - paddingEnd) / 2f

        val progressOffset = padding + progressPaint.strokeWidth / 2f

        // 由于笔画在线的中心, 我们需要为它留出一半的安全空间, 否则它将被截断的界限
        backgroundRadius = min(horizontalHalf, verticalHalf) - strokePaint.strokeWidth / 2f

        val progressRectMinSize = 2 * (min(horizontalHalf, verticalHalf) - progressOffset)

        progressRect.left = (measuredWidth - progressRectMinSize) / 2f
        progressRect.top = (measuredHeight - progressRectMinSize) / 2f
        progressRect.right = (measuredWidth + progressRectMinSize) / 2f
        progressRect.bottom = (measuredHeight + progressRectMinSize) / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // (radius - strokeWidth) - because we don't want to overlap colors (since they by default translucent)
        canvas.drawCircle(progressRect.centerX(), progressRect.centerY(), backgroundRadius - strokePaint.strokeWidth / 2f, backgroundPaint)

        canvas.drawCircle(progressRect.centerX(), progressRect.centerY(), backgroundRadius, strokePaint)

        canvas.drawArc(progressRect, currentAngle, sweepAngle, false, progressPaint)
    }

    override fun createAnimation(): Animator {
        val animator = ValueAnimator.ofFloat(currentAngle, currentAngle + MAX_ANGLE)
        animator.interpolator = LinearInterpolator()
        animator.duration = SPIN_DURATION_MS
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener {
            currentAngle = normalize(it.animatedValue as Float)
        }
        return animator
    }

    /**
     * 将任意角转换至 [0, 360)
     * 比如说 angle = 400.54 => return 40.54
     * angle = 360 => return 0
     */
    private fun normalize(angle: Float): Float {
        val decimal = angle - angle.toInt()
        return (angle.toInt() % MAX_ANGLE) + decimal
    }

    private fun convertToSweepAngle(progress: Float): Float {
        return MIN_SWEEP_ANGLE + progress * (MAX_ANGLE - MIN_SWEEP_ANGLE)
    }

}