package com.seekerzhouk.accountbook.ui.customize

import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.home.Sector
import java.text.NumberFormat
import kotlin.math.*

class PieView : View {
    private val tag = PieView::class.java.name

    constructor(context: Context) : this(context, null)

    constructor(
        context: Context, attrs: AttributeSet?
    ) : super(context, attrs) {
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true

        linePaint.color = Color.BLACK
        linePaint.style = Paint.Style.STROKE
        linePaint.isAntiAlias = true

        textPaint.color = Color.BLACK
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.isAntiAlias = true
        textPaint.textSize = 28F

        numberFormat.maximumFractionDigits = 2
    }

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

    // 初始绘制角度,弧度制
    private var startAngle = 0F
        set(value) {
            field = value
            invalidate()
        }

    // 数据
    var sectorList: List<Sector> = ArrayList()
        @RequiresApi(Build.VERSION_CODES.M)
        set(value) {
            field = value
            initData(field)
            invalidate()
        }

    // 总宽高
    private var totalWidth: Int = 0
    private var totalHeight: Int = 0

    // 画笔
    private val mPaint = Paint()
    private val linePaint = Paint()
    private val textPaint: TextPaint = TextPaint()

    // 路径
    private val path = Path()

    // 饼状图绘制区域
    private val rectF: RectF = RectF()

    // 圆心在半径方向上的偏移量
    private val offR: Float = 6F

    private var pieR: Float = 0F

    // 辅助线的长度
    private val aLength: Float = 80F

    // 百分数工具
    private val numberFormat: NumberFormat = NumberFormat.getPercentInstance()

    private var clickPosition = -1

    private var listener: OnSectorClickListener? = null

    fun setOnSectorClickListener(l: OnSectorClickListener) {
        this.listener = l
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        totalWidth = w
        totalHeight = h
        pieR = (min(totalWidth, totalHeight) / 2 * 0.5).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = measuredWidth
        var height = measuredWidth
        width = resolveSize(width, widthMeasureSpec)
        height = resolveSize(height, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private var isActionDown = false
    private var clickX = 0F
    private var clickY = 0F
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
        listener?.apply {
            if (clickPosition == -1) {
                return super.performClick()
            } else {
                onCLick(clickPosition)
                clickPosition = -1
            }
        }
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var currentStartAngle: Float = startAngle
        rectF.set(-pieR, -pieR, pieR, pieR)
        canvas?.translate((totalWidth / 2).toFloat(), totalHeight * 0.55.toFloat())
        for (i in sectorList.indices) {
            if (sectorList[i].moneySum == 0F) {
                continue
            }
            // 第一段引线的角度
            val lineAngle = currentStartAngle + sectorList[i].angle / 2
            // 第一段引线从圆弧开始，下面是第一段引线和圆弧的交点
            val x1 = (pieR + offR) * cos(lineAngle)
            val y1 = (pieR + offR) * sin(lineAngle)
            // 一二段引线的交点，第二段引线的起点是第一段引线的终点
            val x2 = (pieR + aLength) * cos(lineAngle)
            val y2 = (pieR + aLength) * sin(lineAngle)
            // 第二段引线终点
            val x3: Float = if (x1 > 0) (x2 + aLength) else (x2 - aLength)
            val y3: Float = y2
            // 文字的起点
            val textX: Float = if (x1 > 0) (x2 - 20) else (x3 - 30)
            val textY: Float = if (y3 > 0) y3 else (y3 - 40)
            // 画扇形
            canvas?.save()
            var clickAngle = atan2(clickY, clickX)
            if (clickAngle < 0) {
                clickAngle = (clickAngle + Math.PI * 2).toFloat()
            }
            if (isActionDown
                && clickAngle > currentStartAngle
                && clickAngle < (currentStartAngle + sectorList[i].angle)
                && hypot(clickX, clickY) <= pieR + offR
            ) {
                clickPosition = i
            } else {
                canvas?.translate(
                    offR * cos(lineAngle),
                    offR * sin(lineAngle)
                )
            }

            mPaint.color = sectorList[i].color
            canvas?.drawArc(
                rectF,
                Math.toDegrees(currentStartAngle.toDouble()).toFloat(),
                Math.toDegrees(sectorList[i].angle.toDouble()).toFloat(),
                true,
                mPaint
            ) // 画饼状图
            canvas?.restore()

            // 画线和文字
            canvas?.save()
            path.reset()
            path.moveTo(x1, y1)
            path.lineTo(x2, y2)
            path.lineTo(x3, y3)
            canvas?.drawPath(path, linePaint)
            val string =
                sectorList[i].consumptionType + "(" + numberFormat.format(sectorList[i].percentage) + ")"
            val staticLayout: StaticLayout = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                StaticLayout(
                    string, textPaint, 600, Layout.Alignment.ALIGN_NORMAL,
                    1F, 0F, true
                )
            } else {
                StaticLayout.Builder.obtain(string, 0, string.length, textPaint, 600)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0F, 1F)
                    .setIncludePad(true)
                    .build()
            }
            canvas?.translate(textX, textY)
            staticLayout.draw(canvas)
            canvas?.restore()

            // 当前所处角度
            currentStartAngle += sectorList[i].angle
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

    interface OnSectorClickListener {
        fun onCLick(i: Int)
    }
}