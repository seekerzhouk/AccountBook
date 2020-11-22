package com.seekerzhouk.accountbook.ui.customize

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withTranslation
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.home.Pillar
import kotlin.math.max

class HistogramView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 直方图的宽高
    private var mWidth: Float = 0f
    private var mHeight: Float = 0F

    // 每个柱子的宽度、柱子之间的距离，都设为一样
    private var histogramWidth = 70

    // 画线、画图、写字的paint
    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2F
        isAntiAlias = true
    }
    private val histogramPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeWidth = histogramWidth.toFloat()
        color = resources.getColor(R.color.histogram)
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 30F
        isAntiAlias = true
    }

    // 画横线的path
    private val linePath = Path()

    // 横线的数量为6，即把高度分成7份。X轴一条、浅黄色线4条，剩余的不画。
    private val lineCount = 6

    // 每根线之间的距离
    private var perLineOff = 0

    // 柱子的高度
    private var histogramHeight = 0F

    // 文字、柱子的X坐标
    private var commonX = (histogramWidth / 2).toFloat()

    // moneySum文字Y坐标
    private var moneyTextY = 0F

    // 月份文字的Y坐标
    private val monthTextY = 50F

    // money的平均值
    private var average: Float = 0F

    // money的最大值
    private var maxMoney = 0F

    // 一个单位高度表示的money大小
    private var moneyPerY = 0F

    // 点击位置
    private var clickX = 0F
    private var clickY =0F

    // 点击的柱子，-1 代表未点击到柱子
    private var clickPosition = -1

    // 点击监听器
    private lateinit var listener: (Int) -> Unit

    // 月份-消费
    var pillarList: List<Pillar> = ArrayList()
        set(value) {
            field = value
            var sum = 0F
            for (element in pillarList) {
                sum += element.moneySum
                maxMoney = max(maxMoney, element.moneySum)
            }
            average = sum / pillarList.size
            invalidate()
        }

    fun setOnHistogramClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN ->{
                clickX = event.x
                clickY = event.y - perLineOff * lineCount
                invalidate()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                clickX = 0F
                clickY = 0F
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                clickX = 0F
                clickY = 0F
                invalidate()
                performClick()
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }

    override fun performClick(): Boolean {
        if (clickPosition != -1) {
            listener(clickPosition)
            clickPosition = -1
        }
        return super.performClick()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var measuredWidth = histogramWidth * 25
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
        if (pillarList.isEmpty()) {
            return
        }
        perLineOff = (mHeight / (lineCount + 1)).toInt()
        moneyPerY = (maxMoney / (perLineOff * (lineCount - 2)))

        canvas.withTranslation(0F, perLineOff * lineCount.toFloat()) {
            drawYellowLine(canvas)
            drawTextAndLine(canvas)
            drawXLine(canvas)
        }
    }

    /**
     * 画出浅黄色横线
     */
    private fun drawYellowLine(canvas: Canvas) {
        linePath.reset()
        linePaint.color = resources.getColor(R.color.histogram_line)
        linePath.rMoveTo(0F, (-perLineOff).toFloat())
        for (i in perLineOff until perLineOff * 5 step perLineOff) {
            linePath.rLineTo(mWidth, 0F)
            linePath.rMoveTo(-mWidth, (-perLineOff).toFloat())
        }
        canvas.drawPath(linePath, linePaint)
    }

    /**
     * 画出文字、柱子
     */
    private fun drawTextAndLine(canvas: Canvas) {
        for (i in pillarList.indices) {
            histogramHeight = pillarList[i].moneySum / moneyPerY
            commonX = (histogramWidth * 1.5F) + (histogramWidth * 2 * i).toFloat()
            moneyTextY = -histogramHeight - 20
            canvas.drawText(
                pillarList[i].month,
                commonX - textPaint.measureText(pillarList[i].month) / 2,
                monthTextY,
                textPaint
            )
            if (pillarList[i].moneySum > 0) {
                if (clickX >= commonX - histogramWidth / 2 && clickX <= commonX + histogramWidth / 2 && clickY >= -histogramHeight && clickY <= 0){
                    clickPosition = i
                    moneyTextY -= 10
                    histogramHeight += 10
                    histogramPaint.strokeWidth += 5
                }
                canvas.drawText(
                    String.format("%.2f",pillarList[i].moneySum),
                    commonX - textPaint.measureText(String.format("%.2f",pillarList[i].moneySum)) / 2,
                    moneyTextY,
                    textPaint
                )
                canvas.drawLine(
                    commonX, 0F, commonX,
                    -histogramHeight, histogramPaint
                )
                histogramPaint.strokeWidth = histogramWidth.toFloat()
            }
        }
    }

    /**
     * 画X轴
     */
    private fun drawXLine(canvas: Canvas) {
        linePath.reset()
        linePaint.color = Color.BLACK
        linePath.rLineTo(mWidth, 0F)
        canvas.drawPath(linePath, linePaint)
    }

}