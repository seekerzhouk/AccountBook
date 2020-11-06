package com.seekerzhouk.accountbook.ui.customize

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withTranslation
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.home.Point
import com.seekerzhouk.accountbook.utils.MyLog
import kotlin.math.hypot
import kotlin.math.max

class LineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 画线、写字、画圆点的paint
    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2F
        isAntiAlias = true
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 30F
        isAntiAlias = true
    }
    private val pointPaint = Paint().apply {
        color = resources.getColor(R.color.circle_point)
        style = Paint.Style.FILL_AND_STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10F
        isAntiAlias = true
    }
    private val auxPaint = Paint().apply {
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        color = resources.getColor(R.color.aux_line)
        strokeWidth = 2F
        isAntiAlias = true
    }
    private val rectPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = resources.getColor(R.color.aux_line)
        strokeWidth = 2F
        isAntiAlias = true
    }

    // 画线的path
    private val linePath = Path()

    // 辅助线的path
    private val auxPath = Path()

    // 折线图的宽高
    private var mWidth: Float = 0F
    private var mHeight: Float = 0F

    // 横线的数量为6，即把高度分成7份
    private val lineCount = 6

    // 每个柱子的宽度、柱子之间的距离，都设为一样
    private val histogramWidth = 70

    // 每根线之间的距离
    private var perLineOff = 0

    // 日期文字、圆点、金额文字的x坐标
    private var commonX = 0F

    // 日期文字y坐标,固定50
    private val dayTextY = 50F

    // 圆点y坐标
    private var cPointY = 0F

    // 金额文字Y坐标
    private var moneyTextY = 0F

    // 记录点击的点的坐标
    private var clickX = 0F
    private var clickY = 0F

    // money的平均值
    private var average: Float = 0F
    private var maxMoney = 0F

    // 一个单位高度表示的money大小
    private var moneyPerY = 0F

    // 数据源
    var daysList: List<Point> = ArrayList()
        set(value) {
            field = value
            var sum = 0F
            for (element in daysList) {
                sum += element.moneySum
                maxMoney = max(maxMoney, element.moneySum)
            }
            average = sum / daysList.size
            invalidate()
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var measuredWidth = histogramWidth * 32
        var measuredHeight = histogramWidth * 9
        measuredWidth = resolveSize(measuredWidth, widthMeasureSpec)
        measuredHeight = resolveSize(measuredHeight, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        if (daysList.isEmpty()) {
            return
        }
        perLineOff = (mHeight / (lineCount + 1)).toInt()
        moneyPerY = (maxMoney / (perLineOff * 3))
        moneyTextY = -(perLineOff * 4).toFloat()
        canvas.withTranslation(0F, perLineOff * lineCount.toFloat()) {
            drawYellowLine(this)

            drawTextPointLine(this)

            drawXLine(this)
        }
    }

    /**
     * 画出浅黄色横线
     */
    private fun drawYellowLine(canvas: Canvas) {
        linePath.reset()
        linePaint.color = resources.getColor(R.color.histogram_line)
        linePath.rMoveTo(0F, (-perLineOff).toFloat())
        for (i in perLineOff until perLineOff * 4 step perLineOff) {
            linePath.rLineTo(mWidth, 0F)
            linePath.rMoveTo(-mWidth, (-perLineOff).toFloat())
        }
        canvas.drawPath(linePath, linePaint)
    }

    /**
     * 画出X轴
     */
    private fun drawXLine(canvas: Canvas) {
        linePath.reset()
        linePaint.color = Color.BLACK
        linePath.rLineTo(mWidth, 0F)
        canvas.drawPath(linePath, linePaint)
    }

    /**
     * 画出日期文字、圆点、折线
     */
    private fun drawTextPointLine(canvas: Canvas) {
        linePath.reset()
        linePaint.color = resources.getColor(R.color.line_chart)
        linePath.rMoveTo(histogramWidth.toFloat(), -daysList[0].moneySum / moneyPerY)

        for (i in daysList.indices) {
            commonX += histogramWidth
            cPointY = -(daysList[i].moneySum / moneyPerY)

            linePath.lineTo(commonX, cPointY)

            // 横向居中画文字
            if (i % 2 == 0) {
                canvas.drawText(
                    daysList[i].date,
                    commonX - textPaint.measureText(daysList[i].date) / 2,
                    dayTextY,
                    textPaint
                )
            }

            // 画圆点
            canvas.drawPoint(commonX, cPointY, pointPaint)
            // 画点击的点所对应的money文字
            if (hypot((clickX - commonX), (clickY - cPointY)) <= 20) {
                auxPath.reset()
                auxPath.rMoveTo(commonX, 0F)
                auxPath.rLineTo(0F, moneyTextY)

                canvas.drawPath(auxPath, auxPaint)
                canvas.drawRoundRect(commonX - 50,
                    moneyTextY-40,
                    commonX + 50,
                    moneyTextY+15,
                    10F,
                    10F,rectPaint)
                canvas.drawText(
                    daysList[i].moneySum.toString(),
                    commonX - textPaint.measureText(daysList[i].moneySum.toString()) / 2,
                    moneyTextY,
                    textPaint
                )
            }
        }
        // 画折线
        canvas.drawPath(linePath, linePaint)
        // 画完要重置文字x坐标，以免在重绘的时候发生错乱。
        commonX = 0F
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                clickX = event.x
                clickY = event.y - perLineOff * lineCount
                invalidate()
                performClick()
            }
        }
        return super.onTouchEvent(event)

    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

}