package com.vvechirko.listcontentholder

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView

class PlaceHolderDecorator : RecyclerView.ItemDecoration() {

    private val listItem = Color.parseColor("#AAAAAAAA")
    private val gradient = ShaderGradientDrawable()

    private val rect = Rect()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = listItem
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.forEach { item ->
            val c = canvas.save()
            parent.getDecoratedBoundsWithMargins(item, rect)
            gradient.bounds = rect
            canvas.translate(rect.left.toFloat(), rect.top.toFloat())
            if (item is ViewGroup) {
                item.forEach {
                    if (it.visibility == View.VISIBLE) {
                        rect.set(it.left, it.top, it.right, it.bottom)
                        canvas.drawRect(rect, paint)
                    }
                }
            }
            canvas.restoreToCount(c)
            gradient.draw(canvas)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(0, 0, 0, 0)
    }

    private fun View.getBoundsWithMarginsInt(outBounds: Rect) {
        val lp = layoutParams as RecyclerView.LayoutParams
        outBounds.set(
            left - lp.leftMargin,
            top - lp.topMargin,
            right + lp.rightMargin,
            bottom + lp.bottomMargin
        )
    }
}