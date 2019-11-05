package com.vvechirko.listcontentholder

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable2
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
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

    private val listItem = Color.parseColor("#AAAAAAAA")
    private val gradientCenter = ColorUtils.setAlphaComponent(Color.WHITE, 102)
    private val gradientEdge = Color.TRANSPARENT

    private var drawableState: DrawableState

    private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val shaderColors = intArrayOf(gradientEdge, gradientCenter, gradientEdge)
    private val shaderPositions = floatArrayOf(0f, 0.5f, 1f)

    var autoStart = true
    private val animator: ValueAnimator = ValueAnimator.ofFloat(-1f, 2f).apply {
        duration = 1500
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener(UpdateListener())
        addListener(AnimatorListener())
    }

    private constructor(state: DrawableState) {
        drawableState = state

        if (autoStart) {
            start()
        }
    }

    constructor(context: Context, resId: Int) {
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
        Log.d(
            "myLogs", "widthPixels $widthPixels, heightPixels $heightPixels\n" +
                    "measuredWidth ${view.measuredWidth}, measuredHeight ${view.measuredHeight}"
        )
        drawableState = DrawableState(view)

        if (autoStart) {
            start()
        }
    }

    private fun updateShader(v: Float) {
        val left = intrinsicWidth * v
        val right = left + intrinsicWidth
        gradientPaint.shader = LinearGradient(
            left, 0f, right, 0f, shaderColors, shaderPositions, Shader.TileMode.CLAMP
        )
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        drawableState.view.draw(canvas)
        canvas.drawRect(
            0f, 0f, intrinsicWidth.toFloat(), intrinsicHeight.toFloat(), gradientPaint
        )
    }

    override fun setAlpha(alpha: Int) {
        // Not supported
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // Not supported
    }

    override fun getIntrinsicWidth(): Int {
        return drawableState.view.width
    }

    override fun getIntrinsicHeight(): Int {
        return drawableState.view.height
    }

    override fun getConstantState(): ConstantState {
        return drawableState
    }

    override fun getChangingConfigurations(): Int {
        return super.getChangingConfigurations() or drawableState.changingConfigurations
    }

    // Animator listeners

    private inner class UpdateListener : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(it: ValueAnimator) {
            updateShader(it.animatedValue as Float)
        }
    }

    private inner class AnimatorListener : Animator.AnimatorListener {
        private val handler = Handler(Looper.getMainLooper())

        override fun onAnimationRepeat(animation: Animator?) {

        }

        override fun onAnimationEnd(animation: Animator?) {
            handler.post {
                animationCallbacks.forEach {
                    it.onAnimationEnd(this@PlaceHolderDrawable)
                }
            }
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
            handler.post {
                animationCallbacks.forEach {
                    it.onAnimationEnd(this@PlaceHolderDrawable)
                }
            }
        }
    }

    // Animatable overrides
    override fun isRunning(): Boolean {
        return animator.isRunning
    }

    override fun start() {
//        updateShader(-1f)
        animator.start()
    }

    override fun stop() {
        animator.pause()
    }

    // Animatable2 overrides
    private val animationCallbacks = mutableListOf<Animatable2.AnimationCallback>()

    override fun registerAnimationCallback(callback: Animatable2.AnimationCallback) {
        if (!animationCallbacks.contains(callback)) {
            animationCallbacks.add(callback)
        }
    }

    override fun unregisterAnimationCallback(callback: Animatable2.AnimationCallback): Boolean {
        return animationCallbacks.remove(callback)
    }

    override fun clearAnimationCallbacks() {
        animationCallbacks.clear()
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