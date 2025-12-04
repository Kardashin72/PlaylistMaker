package com.practicum.playlistmaker.player.presentation.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.use
import androidx.core.graphics.drawable.DrawableCompat
import com.practicum.playlistmaker.R
import com.google.android.material.R as MaterialR

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val playDrawable: Drawable
    private val pauseDrawable: Drawable

    private val contentBounds = Rect()
    private val contentBoundsF = RectF()

    private var isPlayingInternal: Boolean = false

    init {
        var tmpPlay = requireNotNull(context.getDrawable(R.drawable.play_button))
        var tmpPause = requireNotNull(context.getDrawable(R.drawable.pause_button))
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.PlaybackButtonView).use { a ->
                a.getDrawable(R.styleable.PlaybackButtonView_playIcon)?.let { tmpPlay = it }
                a.getDrawable(R.styleable.PlaybackButtonView_pauseIcon)?.let { tmpPause = it }
            }
        }
        playDrawable = tmpPlay
        pauseDrawable = tmpPause
        applyThemeTint()
        isClickable = true
        isFocusable = true
    }

    private fun applyThemeTint() {
        val typedValue = TypedValue()
        val resolved = context.theme.resolveAttribute(MaterialR.attr.colorOnPrimary, typedValue, true)
        if (resolved) {
            val tintColor = typedValue.data
            val tintList = ColorStateList.valueOf(tintColor)
            DrawableCompat.setTintList(DrawableCompat.wrap(playDrawable).mutate(), tintList)
            DrawableCompat.setTintList(DrawableCompat.wrap(pauseDrawable).mutate(), tintList)
        }
    }

    fun setPlaying(isPlaying: Boolean) {
        if (isPlayingInternal != isPlaying) {
            isPlayingInternal = isPlaying
            invalidate()
        }
    }

    fun toggle() {
        isPlayingInternal = !isPlayingInternal
        invalidate()
    }

    fun isPlaying(): Boolean = isPlayingInternal

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP -> {
                toggle()
                performClick()
                return true
            }
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_MOVE -> {
                // consume to receive ACTION_UP
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val availableWidth = w - paddingLeft - paddingRight
        val availableHeight = h - paddingTop - paddingBottom
        val side = minOf(availableWidth, availableHeight)

        val left = paddingLeft + (availableWidth - side) / 2
        val top = paddingTop + (availableHeight - side) / 2
        val right = left + side
        val bottom = top + side

        contentBounds.set(left, top, right, bottom)
        contentBoundsF.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        playDrawable.bounds = contentBounds
        pauseDrawable.bounds = contentBounds
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isPlayingInternal) {
            pauseDrawable.draw(canvas)
        } else {
            playDrawable.draw(canvas)
        }
    }
}


