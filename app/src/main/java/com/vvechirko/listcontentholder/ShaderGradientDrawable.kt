package com.vvechirko.listcontentholder

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.graphics.*
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import androidx.core.graphics.ColorUtils

@TargetApi(Build.VERSION_CODES.M)
class ShaderGradientDrawable : Drawable, Animatable2 {

    private val gradientCenter = ColorUtils.setAlphaComponent(Color.WHITE, 102)
    private val gradientEdge = Color.TRANSPARENT

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

    constructor() {
        if (autoStart) {
            start()
        }
    }

    private fun updateShader(v: Float) {
        val left = bounds.width() * v
        val right = left + bounds.width()
        gradientPaint.shader = LinearGradient(
            left, 0f, right, 0f, shaderColors, shaderPositions, Shader.TileMode.CLAMP
        )
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawRect(bounds, gradientPaint)
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
                    it.onAnimationEnd(this@ShaderGradientDrawable)
                }
            }
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
            handler.post {
                animationCallbacks.forEach {
                    it.onAnimationEnd(this@ShaderGradientDrawable)
                }
            }
        }
    }

    // Animatable overrides
    override fun isRunning(): Boolean {
        return animator.isRunning
    }

    override fun start() {
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
}