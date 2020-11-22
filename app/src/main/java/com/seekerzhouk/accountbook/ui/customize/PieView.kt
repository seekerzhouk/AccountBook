package com.seekerzhouk.accountbook.ui.customize

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.graphics.withTranslation
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.home.Sector
import java.text.NumberFormat
import kotlin.math.*

class PieView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 总宽高
    private var totalWidth: Int = 0
    private var totalHeight: Int = 0

    // 画笔
    private val mPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val linePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val textPaint: TextPaint = TextPaint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 28F
    }

    // 路径
    private val path = Path()

    // 饼状图绘制区域
    private val rectF: RectF = RectF()

    // 初始绘制角度,弧度制。
    private val startAngle = 0F

    // 圆心在半径方向上的偏移量
    private val offR: Float = 6F

    private var pieR: Float = 0F

    // 辅助线的长度
    private val aLength: Float = 80F

    // 百分数工具
    private val numberFormat: NumberFormat = NumberFormat.getPercentInstance().apply {
        maximumFractionDigits = 2
    }

    // 当前绘制到的角度
    private var currentStartAngle = 0F

    // 当前绘制的sector的引线的角度
    private var lineAngle = 0F

    // 第一段引线从圆弧开始，下面是第一段引线和圆弧的交点
    private var x1 = 0F
    private var y1 = 0F

    // 一二段引线的交点，第二段引线的起点是第一段引线的终点
    private var x2 = 0F
    private var y2 = 0F

    // 第二段引线终点
    private var x3 = 0F
    private var y3 = 0F

    // 文字的起点
    private var textX = 0F
    private var textY = 0F

    // 文字内容
    private var sectorText = ""

    // 记录点击事件ACTION_DOWN
    private var isActionDown = false

    // 点击位置坐标
    private var clickX = 0F
    private var clickY = 0F

    // 点击的点处在坐标系中的角度
    private var clickAngle = 0F

    // 点击的sector的位置，-1 代表未点击sector区域
    private var clickPosition = -1

    // 点击监听器
    private lateinit var listener: (Int) -> Unit

    // 颜色表，ARGB颜色
    @RequiresApi(Build.VERSION_CODES.M)
    private val mColors = arrayOf(
        context.getColor(R.color.pie_view_0),
        context.getColor(R.color.pie_view_1),
        context.getColor(R.color.pie_view_2),
        context.getColor(R.color.pie_view_3),
        context.getColor(R.color.pie_view_4),
        context.getColor(R.color.pie_view_5),
        context.getColor(R.color.pie_view_6),
        context.getColor(R.color.pie_view_7),
        context.getColor(R.color.pie_view_8)
    )

    // 数据
    var sectorList: List<Sector> = ArrayList()
        @RequiresApi(Build.VERSION_CODES.M)
        set(value) {
            field = value
            initData(field)
            invalidate()
        }

    fun setOnSectorClickListener(listener: (Int) ->Unit) {
        this.listener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = measuredWidth
        var height = measuredWidth
        width = resolveSize(width, widthMeasureSpec)
        height = resolveSize(height, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        totalWidth = w
        totalHeight = h
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                isActionDown = true
                clickX = event.x - (totalWidth / 2).toFloat()
                clickY = event.y - totalHeight * 0.55.toFloat()
                if (hypot(clickX, clickY) > pieR + offR) {
                    return false
                }
                invalidate()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                isActionDown = false
                clickX = 0F
                clickY = 0F
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                isActionDown = false
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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        currentStartAngle = startAngle
        canvas.withTranslation((totalWidth / 2).toFloat(), totalHeight * 0.55.toFloat()) {
            for (i in sectorList.indices) {
                if (sectorList[i].moneySum == 0F) {
                    continue
                }
                lineAngle = currentStartAngle + sectorList[i].angle / 2
                clickAngle = atan2(clickY, clickX)
                mPaint.color = sectorList[i].color

                if (clickAngle < 0) {
                    clickAngle = (clickAngle + Math.PI * 2).toFloat()
                }
                if (isActionDown
                    && clickAngle > currentStartAngle
                    && clickAngle < (currentStartAngle + sectorList[i].angle)
                    && hypot(clickX, clickY) <= pieR + offR
                ) {
                    drawClickedSector(canvas, i)
                } else {
                    drawNormalSectors(canvas, i)
                }

                // 画线。后面的坐标都是相对于x1、y1的，所以不需要分类计算
                x2 = (pieR + aLength) * cos(lineAngle)
                y2 = (pieR + aLength) * sin(lineAngle)
                x3 = if (x1 > 0) (x2 + aLength) else (x2 - aLength)
                y3 = y2
                path.reset()
                path.moveTo(x1, y1)
                path.lineTo(x2, y2)
                path.lineTo(x3, y3)
                canvas.drawPath(path, linePaint)
                // 画文字
                textX = if (x1 > 0) (x2 - 20) else (x3 - 30)
                textY = if (y3 > 0) y3 + 35 else (y3 - 15)
                sectorText =
                    sectorList[i].consumptionType + "(" + numberFormat.format(sectorList[i].percentage) + ")"
                canvas.drawText(sectorText, textX, textY, textPaint)

                // 当前所处角度
                currentStartAngle += sectorList[i].angle
            }
        }
    }

    private fun drawClickedSector(canvas: Canvas, i: Int) {
        pieR = (min(totalWidth, totalHeight) / 2 * 0.5).toFloat() + 2 * offR
        rectF.set(-pieR, -pieR, pieR, pieR)
        x1 = (pieR) * cos(lineAngle)
        y1 = (pieR) * sin(lineAngle)
        clickPosition = i
        // 画扇形
        canvas.drawArc(
            rectF,
            Math.toDegrees(currentStartAngle.toDouble()).toFloat(),
            Math.toDegrees(sectorList[i].angle.toDouble()).toFloat(),
            true,
            mPaint
        )
    }

    private fun drawNormalSectors(canvas: Canvas, i: Int) {
        pieR = (min(totalWidth, totalHeight) / 2 * 0.5).toFloat()
        rectF.set(-pieR, -pieR, pieR, pieR)
        canvas.withTranslation(
            offR * cos(lineAngle),
            offR * sin(lineAngle)
        ){
            x1 = (pieR + offR) * cos(lineAngle)
            y1 = (pieR + offR) * sin(lineAngle)
            // 画扇形
            canvas.drawArc(
                rectF,
                Math.toDegrees(currentStartAngle.toDouble()).toFloat(),
                Math.toDegrees(sectorList[i].angle.toDouble()).toFloat(),
                true,
                mPaint
            )
        }
    }

    // 对传入的数据进行处理
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initData(mData: List<Sector>) {
        if (mData.isEmpty()) {
            return
        }

        var sumValue = 0.00
        for (i in mData.indices) {
            val sector: Sector = mData[i]
            sumValue += sector.moneySum // 算出总的value
            val j = (i % mColors.size)
            sector.color = mColors[j]
        }

        for (pie in mData) {
            val percentage = pie.moneySum / sumValue
            pie.percentage = percentage.toFloat()
            val angle = percentage * 2 * Math.PI
            pie.angle = angle.toFloat()
        }
    }

}