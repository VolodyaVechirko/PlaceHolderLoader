package com.vvechirko.listcontentholder

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable2
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.graphics.ColorUtils
import androidx.core.view.forEach

@TargetApi(Build.VERSION_CODES.M)
class PlaceHolderDrawable : Drawable, Animatable2 {

    val semiWhite = ColorUtils.setAlphaComponent(Color.WHITE, 102)
    var state: DrawableState
    val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(semiWhite, Color.TRANSPARENT, semiWhite, Color.TRANSPARENT)
    )

    private constructor(state: DrawableState) {
        this.state = state
        initGradient()
    }

    constructor(context: Context, resId: Int) {
        val view = LayoutInflater.from(context).inflate(resId, FrameLayout(context), false)
        // Bad solution
        val widthPixels = context.resources.displayMetrics.widthPixels
        val heightPixels = context.resources.displayMetrics.heightPixels

        (view as ViewGroup).forEach {
            it.background = ColorDrawable(Color.GRAY)
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
        Log.d(
            "myLogs", "widthPixels $widthPixels, heightPixels $heightPixels\n" +
                    "measuredWidth ${view.measuredWidth}, measuredHeight ${view.measuredHeight}"
        )
        state = DrawableState(view)
        initGradient()
    }

    var rect = Rect()
    var gradientOffset = 0f

    fun initGradient() {
        val width = intrinsicWidth
        val height = intrinsicHeight
        rect = Rect(0, 0, 3 * width, height)

        // LinearGradient Shader

        gradientPaint.style = Paint.Style.FILL
        val colors = intArrayOf(semiWhite, Color.TRANSPARENT, semiWhite, Color.TRANSPARENT)
        gradientPaint.shader = LinearGradient(
            0f, 0f, width * 3f, 0f, colors, null, Shader.TileMode.REPEAT
        )
        ValueAnimator.ofInt(0, width * 2).apply {
            interpolator = LinearInterpolator()
            duration = 1500
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                gradientOffset = -2f * width + (it.animatedValue as Int).toFloat()
                invalidateSelf()
            }
        }.start()

        // GradientDrawable

//        gradientDrawable.setBounds(0, 0, width, height)
//
//        ValueAnimator.ofInt(0, 2 * width).apply {
//            interpolator = LinearInterpolator()
//            duration = 1500
//            repeatMode = ValueAnimator.RESTART
//            repeatCount = ValueAnimator.INFINITE
//            addUpdateListener {
//                val v = it.animatedValue as Int
//                gradientDrawable.setBounds(-2 * width + v, 0, width + v, height)
//                invalidateSelf()
//            }
//        }.start()
    }

    override fun draw(canvas: Canvas) {
        state.view.draw(canvas)
//        gradientDrawable.draw(canvas)

        canvas.save()
        canvas.translate(gradientOffset, 0f)
        canvas.drawRect(rect, gradientPaint)
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getIntrinsicWidth(): Int {
        return state.view.width
    }

    override fun getIntrinsicHeight(): Int {
        return state.view.height
    }

    override fun getConstantState(): ConstantState {
        return state
    }

    override fun getChangingConfigurations(): Int {
        return super.getChangingConfigurations() or state.changingConfigurations
    }

    // Animatable2 overrides

    override fun isRunning(): Boolean {
        return true
    }

    override fun registerAnimationCallback(callback: Animatable2.AnimationCallback) {

    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun clearAnimationCallbacks() {

    }

    override fun unregisterAnimationCallback(callback: Animatable2.AnimationCallback): Boolean {
        return false
    }

    class DrawableState(val view: View) : ConstantState() {

        override fun newDrawable(): Drawable {
            return PlaceHolderDrawable(this)
        }

        override fun getChangingConfigurations(): Int {
            return 0
        }
    }
}