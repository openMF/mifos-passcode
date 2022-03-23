package com.mifos.mobile.passcode

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Created by dilpreet on 15/7/17.
 */
class MifosPassCodeView : View {
    private var emptyCirclePaint: Paint? = null
    private var fillCirclePaint: Paint? = null
    private val PASSWORD_LENGTH = 4
    private var passwordList: MutableList<String>? = null
    private var isPasscodeVisible = false
    private var passCodeListener: PassCodeListener? = null

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    fun setPassCodeListener(passCodeListener: PassCodeListener?) {
        this.passCodeListener = passCodeListener
    }

    private fun init(attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MifosPassCodeView)
        val defaultTextSize = (12 * context.resources.displayMetrics.density).toInt()
        val color = attributes.getColor(R.styleable.MifosPassCodeView_color, Color.WHITE)
        emptyCirclePaint = Paint()
        emptyCirclePaint!!.color = color
        emptyCirclePaint!!.isAntiAlias = true
        emptyCirclePaint!!.style = Paint.Style.STROKE
        emptyCirclePaint!!.strokeWidth = 1f
        fillCirclePaint = Paint()
        fillCirclePaint!!.color = color
        fillCirclePaint!!.isAntiAlias = true
        fillCirclePaint!!.textSize = attributes.getDimensionPixelSize(
            R.styleable.MifosPassCodeView_text_size,
            defaultTextSize
        ).toFloat()
        fillCirclePaint!!.style = Paint.Style.FILL
        attributes.recycle()
        passwordList = ArrayList()
        isPasscodeVisible = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val stackSize = passwordList!!.size
        var xPosition = width / (PASSWORD_LENGTH * 2)
        for (i in 1..PASSWORD_LENGTH) {
            if (stackSize >= i) {
                if (!isPasscodeVisible) {
                    canvas.drawCircle(
                        xPosition.toFloat(),
                        (height / 2).toFloat(),
                        8f,
                        fillCirclePaint!!
                    )
                } else {
                    canvas.drawText(
                        passwordList!![i - 1], xPosition.toFloat(), (height / 2 +
                                height / 8).toFloat(), fillCirclePaint!!
                    )
                }
            } else {
                canvas.drawCircle(
                    xPosition.toFloat(),
                    (height / 2).toFloat(),
                    8f,
                    emptyCirclePaint!!
                )
            }
            xPosition += width / PASSWORD_LENGTH
        }
    }

    fun enterCode(character: String) {
        if (passwordList!!.size < PASSWORD_LENGTH) {
            passwordList!!.add(character)
            invalidate()
        }
        if (passwordList!!.size == PASSWORD_LENGTH && passCodeListener != null) {
            passCodeListener!!.passCodeEntered(passcode)
        }
    }

    val passcode: String
        get() {
            val builder = StringBuilder()
            for (character in passwordList!!) {
                builder.append(character)
            }
            return builder.toString()
        }

    fun clearPasscodeField() {
        passwordList!!.clear()
        invalidate()
    }

    fun backSpace() {
        if (passwordList!!.size > 0) {
            passwordList!!.removeAt(passwordList!!.size - 1)
            invalidate()
        }
    }

    fun revertPassCodeVisibility() {
        isPasscodeVisible = !isPasscodeVisible
        invalidate()
    }

    fun passcodeVisible(): Boolean {
        return isPasscodeVisible
    }

    interface PassCodeListener {
        fun passCodeEntered(passcode: String?)
    }
}