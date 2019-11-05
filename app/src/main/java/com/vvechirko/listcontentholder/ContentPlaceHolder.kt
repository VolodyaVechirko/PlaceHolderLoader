package com.vvechirko.listcontentholder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.applyCanvas
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.forEach

class ContentPlaceHolder {

    private val listItem = Color.parseColor("#AAAAAAAA")
    private val bitmap: Bitmap

    constructor(parent: ViewGroup, resId: Int) {
        bitmap = viewBitmap(parent, resId)
    }

    constructor(context: Context, resId: Int) {
        bitmap = viewBitmap(context, resId)
    }

    fun newDrawable(): PlaceHolderDrawable {
        return PlaceHolderDrawable(bitmap)
    }

    private fun viewBitmap(parent: ViewGroup, resId: Int): Bitmap {
        val view = LayoutInflater.from(parent.context)
            .inflate(resId, FrameLayout(parent.context), false)
        (view as ViewGroup).forEach {
            it.background = ColorDrawable(listItem)
        }
        parent.doOnNextLayout {
            it.layoutParams
        }

        throw Exception()
    }

    private fun viewBitmap(context: Context, resId: Int): Bitmap {
        val view = LayoutInflater.from(context).inflate(resId, FrameLayout(context), false)
        //TODO: Bad solution
        val widthPixels = context.resources.displayMetrics.widthPixels
        val heightPixels = context.resources.displayMetrics.heightPixels

        (view as ViewGroup).forEach {
            it.background = ColorDrawable(listItem)
        }

        val widthSpec = View.MeasureSpec.makeMeasureSpec(widthPixels, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(heightPixels, View.MeasureSpec.AT_MOST)
        val lp = view.layoutParams as ViewGroup.LayoutParams
        var horizontalPadding = 0
        var verticalPadding = 0

        if (lp is ViewGroup.MarginLayoutParams) {
            horizontalPadding = lp.leftMargin + lp.rightMargin
            verticalPadding = lp.topMargin + lp.bottomMargin
        }

        view.measure(
            ViewGroup.getChildMeasureSpec(widthSpec, horizontalPadding, lp.width),
            ViewGroup.getChildMeasureSpec(heightSpec, verticalPadding, lp.height)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val b =
            Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        b.applyCanvas { view.draw(this) }
        return b
    }
}