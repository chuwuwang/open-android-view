package com.cat.view.customize.animation

import android.animation.ObjectAnimator
import android.animation.PointFEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.graphics.minus
import com.cat.view.utils.dp
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 可拖拽回弹View, 仿QQ拖拽效果
 */
class DragRestoredView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet ? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var move = false
    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL_AND_STROKE
    }
    private lateinit var objectAnimator: ObjectAnimator
    var bigPointF = PointF(0f, 0f)
        set(value) {
            field = value
            invalidate()
        }
    private val smallPointF by lazy { PointF(width / 2f, height / 2f) }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = resolveSize((MAX_RADIUS * 2).toInt(), widthMeasureSpec)
        val height = resolveSize((MAX_RADIUS * 2).toInt(), heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bigPointF = PointF(width / 2f, height / 2f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.RED
        // 两圆之间的距离
        val d = distance()
        var ratio = d / MAX_RADIUS
        if (ratio > 0.6) {
            ratio = 0.6f
        }
        // 小圆半径
        val smallRadius = SMALL_RADIUS - SMALL_RADIUS * ratio
        // 小圆
        canvas.drawCircle(smallPointF.x, smallPointF.y, smallRadius, paint)
        val contains = bigPointF.contains(smallPointF, MAX_RADIUS)
        if (contains) {
            // 大圆
            canvas.drawCircle(bigPointF.x, bigPointF.y, BIG_RADIUS, paint)
            // 绘制贝塞尔
            drawBezier(canvas, smallRadius, BIG_RADIUS)
        }
    }

    override fun onTouchEvent(event: MotionEvent ? ): Boolean {
        if (event == null) return super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val f = PointF(event.x, event.y)
                move = bigPointF.contains(f, BIG_RADIUS)
            }
            MotionEvent.ACTION_MOVE -> {
                if (move) { // 如果当前点击的位置包含bigPointF, 说明选中了
                    bigPointF.x = event.x
                    bigPointF.y = event.y
                } else {
                    bigPointF.x = width / 2f
                    bigPointF.y = height / 2f
                }
            }
            MotionEvent.ACTION_UP -> { // 如果大圆位置超出拖拽范围, 不回弹
                val contains = bigPointF.contains(smallPointF, MAX_RADIUS)
                if (contains) {
                    bigAnimator().start()
                } else {
                    // 将View移动到原始位置
                    bigPointF.x = width / 2f
                    bigPointF.y = height / 2f
                }
            }
        }
        invalidate()
        return true
    }

    private fun bigAnimator(): ValueAnimator {
        if (this::objectAnimator.isInitialized) {
            return objectAnimator
        }
        val end = PointF(smallPointF.x, smallPointF.y)
        objectAnimator = ObjectAnimator.ofObject(this, "bigPointF", PointFEvaluator(), end)
        objectAnimator.duration = 400
        objectAnimator.interpolator = OvershootInterpolator(3f)
        return objectAnimator
    }

    /**
     * 小圆与大圆之间的距离
     */
    private fun distance(): Float {
        val current = bigPointF - smallPointF
        val x = current.x.toDouble().pow(2.0)
        val y = current.y.toDouble().pow(2.0)
        return sqrt(x + y).toFloat()
    }

    private fun drawBezier(canvas: Canvas, smallRadius: Float, bigRadius: Float) {
        val current = bigPointF - smallPointF
        val xDouble = current.x.toDouble()
        val yDouble = current.y.toDouble()
        // BDF = BAD
        val bdf = atan(yDouble / xDouble)

        val p1X = smallPointF.x + smallRadius * sin(bdf)
        val p1Y = smallPointF.y - smallRadius * cos(bdf)

        val p2X = bigPointF.x + bigRadius * sin(bdf)
        val p2Y = bigPointF.y - bigRadius * cos(bdf)

        // 控制点
        val controlPointX = current.x / 2 + smallPointF.x
        val controlPointY = current.y / 2 + smallPointF.y

        val p3X = smallPointF.x - smallRadius * sin(bdf)
        val p3Y = smallPointF.y + smallRadius * cos(bdf)

        val p4X = bigPointF.x - bigRadius * sin(bdf)
        val p4Y = bigPointF.y + bigRadius * cos(bdf)

        val path = Path()
        val p1XFloat = p1X.toFloat()
        val p1YFloat = p1Y.toFloat()
        val p2XFloat = p2X.toFloat()
        val p2YFloat = p2Y.toFloat()
        path.moveTo(p1XFloat, p1YFloat)
        path.quadTo(controlPointX, controlPointY, p2XFloat, p2YFloat)

        val p4XFloat = p4X.toFloat()
        val p4YFloat = p4Y.toFloat()
        val p3XFloat = p3X.toFloat()
        val p3YFloat = p3Y.toFloat()
        path.lineTo(p4XFloat, p4YFloat)
        path.quadTo(controlPointX, controlPointY, p3XFloat, p3YFloat)
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun PointF.contains(pointF: PointF, padding: Float = 0f): Boolean {
        val isX = this.x <= pointF.x + padding && this.x >= pointF.x - padding
        val isY = this.y <= pointF.y + padding && this.y >= pointF.y - padding
        return isX && isY
    }

    companion object {

        private val BIG_RADIUS = 20.dp          // 大圆半径
        private val MAX_RADIUS = 150.dp         // 最大范围(半径), 超出这个范围大圆不显示
        private val SMALL_RADIUS = BIG_RADIUS   // 小圆半径

    }

}