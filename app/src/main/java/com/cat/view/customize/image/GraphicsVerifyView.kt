package com.cat.view.customize.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.cat.view.R
import java.util.Random

class GraphicsVerifyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet ? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var seekRadius = 48f
    private var seekMarginTop = 0f
    private var seekBorderWidth = 3f
    private var seekBorderColor = Color.parseColor("#DEDEDE")
    private var seekBackgroundColor = Color.parseColor("#FFFFFF")

    private var seekMoveX = 0f // 手指触摸的X轴位置
    private var seekCenterX = 0f // 滑块的中心点位置
    private val seekBackgroundPath = Path() // 滑块背景路径

    private var seekFailureColor = Color.RED
    private var seekDefaultColor = Color.BLUE
    private var seekSuccessColor = Color.GREEN
    private var seekArrowTouchColor = Color.WHITE // 滑块箭头触摸颜色
    private var seekArrowDefaultColor = Color.WHITE // 滑块箭头默认颜色

    private var paint: Paint
    private var bitmap: Bitmap
    private var isTouch = false
    private var threshold = 10f // 阈值
    private var status = STATUS_DEFAULT
    private var defaultDegrees = 0f
    private var currentDegrees = 0f // 当前图片角度

    companion object {

        private const val TAG = "GraphicsVerifyView"

        private const val STATUS_DEFAULT = 0
        private const val STATUS_FAILURE = 1
        private const val STATUS_SUCCESS = 2

    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_achievement_2)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // 随机初始化默认角度(值的范围为-80 ~ -280) , 匹配的时候与滑块旋转的角度相加如果在误差范围内就验证成功
        val randomValue = Random().nextInt(201) + 80f
        defaultDegrees = -randomValue
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 缩放图片大小(宽高都缩放为控件宽度的二分之一)
        val scaleValue = w / 2
        bitmap = Bitmap.createScaledBitmap(bitmap, scaleValue, scaleValue, true)
        seekMarginTop = bitmap.height / 3f
        initSeekBackgroundPath()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawSeekBackground(canvas)
        drawSeek(canvas)
        drawVerifyBitmap(canvas)
    }

    override fun onTouchEvent(event: MotionEvent ? ): Boolean {
        if (event == null) return super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (status == STATUS_DEFAULT) {
                    // 判断触摸点是否在滑块上
                    val rect = RectF(seekMoveX, seekMarginTop + bitmap.height, seekRadius * 2, seekMarginTop + bitmap.height + seekRadius * 2)
                    val contains = rect.contains(event.x, event.y)
                    if (contains) {
                        isTouch = true
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isTouch) {
                    seekMoveX = event.x
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                status = if (currentDegrees <= threshold && currentDegrees >= -threshold) {
                    STATUS_SUCCESS
                } else {
                    STATUS_FAILURE
                }
                Log.e(TAG, "verify status: $status")
                isTouch = false
                invalidate()
            }
            else -> {}
        }
        return isTouch
    }

    private fun initSeekBackgroundPath() {
        val top = seekMarginTop + bitmap.height
        val borderOffset = 0f
        // 通过计算得出滑块背景的路径
        seekBackgroundPath.moveTo(seekRadius + borderOffset, top)
        seekBackgroundPath.addArc(borderOffset, top, seekRadius * 2 + borderOffset, top + seekRadius * 2, -90f, -180f)
        seekBackgroundPath.lineTo(width - seekRadius - borderOffset, top + seekRadius * 2)
        seekBackgroundPath.addArc(width - seekRadius * 2 - borderOffset, top, width - borderOffset, top + seekRadius * 2, 90f, -180f)
        seekBackgroundPath.lineTo(seekRadius + borderOffset, top)
    }

    private fun drawSeekBackground(canvas: Canvas) {
        // 先画填充色
        paint.color = seekBackgroundColor
        paint.style = Paint.Style.FILL
        paint.strokeWidth = seekBorderWidth
        canvas.drawPath(seekBackgroundPath, paint)
        // 然后画边
        paint.color = seekBorderColor
        paint.style = Paint.Style.STROKE
        canvas.drawPath(seekBackgroundPath, paint)
    }

    private fun drawSeek(canvas: Canvas) {
        seekCenterX = when {
            // 处理左边的边界值
            seekMoveX < seekRadius -> seekRadius
            // 处理右边的边界值
            seekMoveX > width - seekRadius -> width - seekRadius
            else -> seekMoveX
        }
        val centerY = seekMarginTop + bitmap.height + seekRadius
        // 画圆
        paint.color = when (status) {
            STATUS_SUCCESS -> seekSuccessColor
            STATUS_FAILURE -> seekFailureColor
            else -> seekDefaultColor
        }
        paint.style = Paint.Style.FILL
        canvas.drawCircle(seekCenterX, centerY, seekRadius - seekBorderWidth, paint)

        // 画圆的边框
        // paint.style = Paint.Style.STROKE
        // paint.color = seekBorderColor
        // paint.strokeWidth = 0f
        // canvas.drawCircle(seekCenterX, centerY, seekRadius - seekBorderWidth, paint)

        // 画滑块中间的两个箭头
        paint.strokeWidth = 2f
        paint.textSize = seekRadius
        paint.textAlign = Paint.Align.CENTER
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = if (isTouch || status == STATUS_SUCCESS || status == STATUS_FAILURE) seekArrowTouchColor else seekArrowDefaultColor
        val fontMetrics = paint.fontMetrics
        // 计算文字高度 ㄍ
        val fontHeight = fontMetrics.bottom - fontMetrics.top
        // 计算文字 baseline, 让文字垂直居中
        val textBaseY = (seekRadius * 2 - fontHeight) / 2
        // 由于找不到向右的箭头就用这个符号翻转
        canvas.save()
        canvas.translate(seekCenterX, centerY)
        canvas.rotate(180f)
        canvas.drawText("ㄍ", 0f, textBaseY, paint)
        canvas.restore()
    }

    private fun drawVerifyBitmap(canvas: Canvas) {
        // 根据滑块移动的距离计算旋转的角度
        currentDegrees = (seekCenterX - seekRadius) / (width - seekRadius * 2) * 360 + defaultDegrees
        canvas.save()
        canvas.translate(width / 2f, bitmap.height / 2f)
        // 根据拖动滑块来调整角度
        canvas.rotate(currentDegrees)
        // 利用混合模式将图片画成圆形
        canvas.drawCircle(0f, 0f, bitmap.height / 2f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(bitmap, -bitmap.height / 2f, -bitmap.height / 2f, paint)
        paint.xfermode = null
        canvas.restore()
    }

}