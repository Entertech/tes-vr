package com.entertech.tes.vr.mode.mbct

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class MbctBrainwaveChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D7CEBF")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#60726D")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2F6A67")
        strokeWidth = 5f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A2F6A67")
        style = Paint.Style.FILL
    }

    private val samples = ArrayList<Int>()

    fun submitSamples(newSamples: List<Int>) {
        samples.clear()
        samples.addAll(newSamples)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val contentWidth = width.toFloat()
        val contentHeight = height.toFloat()
        if (contentWidth <= 0f || contentHeight <= 0f) {
            return
        }

        val top = paddingTop.toFloat() + 8f
        val bottom = contentHeight - paddingBottom.toFloat() - 8f
        val left = paddingLeft.toFloat()
        val right = contentWidth - paddingRight.toFloat()
        val centerY = (top + bottom) / 2f

        canvas.drawLine(left, top, right, top, gridPaint)
        canvas.drawLine(left, centerY, right, centerY, axisPaint)
        canvas.drawLine(left, bottom, right, bottom, gridPaint)

        if (samples.isEmpty()) {
            return
        }

        val pointGap = (right - left) / max(samples.size - 1, 1)
        val linePath = Path()
        val fillPath = Path()

        samples.forEachIndexed { index, sample ->
            val normalized =
                (sample - MbctBrainwavePhase.MIN_VALUE).toFloat() /
                    (MbctBrainwavePhase.MAX_VALUE - MbctBrainwavePhase.MIN_VALUE).toFloat()
            val x = left + index * pointGap
            val y = bottom - normalized * (bottom - top)
            if (index == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, bottom)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo(left + (samples.size - 1) * pointGap, bottom)
        fillPath.close()
        canvas.drawPath(fillPath, fillPaint)
        canvas.drawPath(linePath, linePaint)
    }
}
