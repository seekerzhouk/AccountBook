package com.seekerzhouk.accountbook.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.database.home.Pillar
import kotlin.math.max

class HistogramView : View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 2F
        linePaint.isAntiAlias = true

        histogramPaint.style = Paint.Style.STROKE
        histogramPaint.isAntiAlias = true
        histogramPaint.color = resources.getColor(R.color.histogram)

        textPaint.color = Color.BLACK
        textPaint.textSize = 30F
        textPaint.isAntiAlias = true
    }

    private val TAG = "HistogramView"


    // 直方图的宽高
    private var mWidth: Float = 0f
    private var mHeight: Float = 0F

    // 画线、画图、写字的paint
    private val linePaint = Paint()
    private val histogramPaint = Paint()
    private val textPaint = Paint()

    // 画线的path
    private val linePath = Path()

    // 横线的数量为4，即把高度分成5份
    private val lineCount = 6

    // 每个柱子的宽度、柱子之间的距离，都设为一样
    private var histogramWidth = 70

    // 每根线之间的距离
    private var perLineOff = 0

    // 月份文字的Y坐标
    private val monthTextY = 50F


    // money的平均值
    private var average: Float = 0F
    private var maxMoney = 0F

    // 一个单位高度表示的money大小
    private var moneyPerY = 0F

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
            Log.i(TAG, "set()" + pillarList.size.toString())
            Log.i(TAG, "set() $pillarList")
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
        setMeasuredDimension(measuredWidth, measuredHeight.toInt())
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
        moneyPerY = (maxMoney / (mHeight * 4 / (lineCount + 1)))

        histogramPaint.strokeWidth = histogramWidth.toFloat()
        canvas.translate(0F, perLineOff * lineCount.toFloat())
        canvas.save()
        // 开始画横线
        linePaint.color = resources.getColor(R.color.histogram_line)
        linePath.rMoveTo(0F, (-perLineOff).toFloat())
        for (i in perLineOff until perLineOff * 5 step perLineOff) {
            linePath.rLineTo(mWidth, 0F)
            linePath.rMoveTo(-mWidth, (-perLineOff).toFloat())
        }
        canvas.drawPath(linePath, linePaint)
        linePath.reset()
        canvas.restore()
        canvas.save()
        canvas.translate(histogramWidth.toFloat(), 0F)
        Log.i(TAG, "onDraw()" + pillarList.size.toString())
        for (element in pillarList) {
            val histogramHeight = element.moneySum / moneyPerY
            val moneyTextY = -histogramHeight - 20 // 金额文字的Y坐标
            canvas.run {
                drawText(element.date, (histogramWidth / 4).toFloat(), monthTextY, textPaint)
                drawText(
                    element.moneySum.toString(),
                    (histogramWidth / 4).toFloat(),
                    moneyTextY,
                    textPaint
                )
                drawLine(
                    (histogramWidth / 2).toFloat(), 0F, (histogramWidth / 2).toFloat(),
                    -histogramHeight, histogramPaint
                )
            }
            canvas.translate((histogramWidth * 2).toFloat(), 0F)
        }
        canvas.restore()
        // 最后才画出X轴,使得X轴不会被柱字覆盖
        linePaint.color = Color.BLACK
        linePath.rLineTo(mWidth, 0F)
        canvas.drawPath(linePath, linePaint)
        Log.i(TAG, "onDraw()")
    }
}