package com.surendramaran.yolov8tflite

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.util.LinkedList
import kotlin.math.max

class  OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results = listOf<BoundingBox>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        results = listOf()
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = ContextCompat.getColor(context!!, R.color.bounding_box_color)
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // Стиль уголков-видоискателей
        boxPaint.color = Color.parseColor("#00E676") // Изумрудно-зеленый
        boxPaint.strokeWidth = 10f
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeCap = Paint.Cap.ROUND
        boxPaint.isAntiAlias = true

        // Стиль фона плашки
        textBackgroundPaint.color = Color.parseColor("#CC121212") // Полупрозрачный черный
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.isAntiAlias = true

        // Стиль текста
        textPaint.color = Color.WHITE
        textPaint.textSize = 46f
        textPaint.isFakeBoldText = true
        textPaint.isAntiAlias = true

        // Индикатор-точка на плашке
        val dotPaint = Paint().apply {
            color = Color.parseColor("#00E676")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        results.forEach {
            val left = it.x1 * width
            val top = it.y1 * height
            val right = it.x2 * width
            val bottom = it.y2 * height

            val boxWidth = right - left
            val boxHeight = bottom - top
            val cornerLen = minOf(boxWidth, boxHeight, 350f) * 0.22f

            // 1. Рисуем 4 уголка-видоискателя
            // Левый верхний
            canvas.drawLine(left, top, left + cornerLen, top, boxPaint)
            canvas.drawLine(left, top, left, top + cornerLen, boxPaint)

            // Правый верхний
            canvas.drawLine(right - cornerLen, top, right, top, boxPaint)
            canvas.drawLine(right, top, right, top + cornerLen, boxPaint)

            // Левый нижний
            canvas.drawLine(left, bottom - cornerLen, left, bottom, boxPaint)
            canvas.drawLine(left, bottom, left + cornerLen, bottom, boxPaint)

            // Правый нижний
            canvas.drawLine(right - cornerLen, bottom, right, bottom, boxPaint)
            canvas.drawLine(right, bottom - cornerLen, right, bottom, boxPaint)

            // 2. Красивое название и процент уверенности
            val prettyName = when (it.clsName) {
                "1000" -> "1000 ГМИ"
                "5" -> "5 ГМИ"
                else -> it.clsName
            }
            val confidencePercent = (it.cnf * 100).toInt()
            val drawableText = "$prettyName • $confidencePercent%"

            textPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height()

            val paddingH = 26f
            val paddingV = 16f
            val badgeHeight = textHeight + paddingV * 2
            val badgeWidth = textWidth + paddingH * 2 + 12f

            // Защита от выхода плашки за верхний или правый край экрана
            val badgeTop = if (top - badgeHeight - 12f > 0f) {
                top - badgeHeight - 12f
            } else {
                top + 12f
            }
            val badgeLeft = left.coerceAtMost((width - badgeWidth).coerceAtLeast(0f))
            val badgeBottom = badgeTop + badgeHeight

            // 3. Скругленная плашка-бейдж
            val badgeRect = android.graphics.RectF(
                badgeLeft,
                badgeTop,
                badgeLeft + badgeWidth,
                badgeBottom
            )
            canvas.drawRoundRect(badgeRect, 16f, 16f, textBackgroundPaint)

            // Зеленая точка-индикатор слева
            canvas.drawCircle(badgeLeft + 18f, badgeTop + badgeHeight / 2f, 6f, dotPaint)

            // 4. Текст номинала
            canvas.drawText(
                drawableText,
                badgeLeft + paddingH + 8f,
                badgeBottom - paddingV,
                textPaint
            )
        }
    }

    fun setResults(boundingBoxes: List<BoundingBox>) {
        results = boundingBoxes
        invalidate()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}