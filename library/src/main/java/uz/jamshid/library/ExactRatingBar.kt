package uz.jamshid.library

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.and
import kotlin.math.abs
import kotlin.math.max


class ExactRatingBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private val paths = ArrayList<Path>()
    private val overLayPath = Path()

    private val lines = ArrayList<Line>()
    private val starPoints = ArrayList<PointF>()

    private var half = 0f
    private var height = 0f
    private var width = 0f
    private var widthOfStar = 0f
    private var starSize = dip(64f)
    private var space = dip(10f)

    private var initialPoint = 0f
    private var seekX = 0f
    private var lastPosX = 0f

    private var numberOfStars = 5
    private var star = 0f
    private var isIndicator = false

    private var fillColor = Color.GREEN
    private var starBorder = 2f

    var onRatingBarChanged: OnRatingBarChanged? = null
    var onRateChanged: ((Float) -> Unit)? = null

    init {
        setUpAttrs(attrs)
        init()
    }

    private fun setUpAttrs(attrs: AttributeSet?){
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.ExactRatingBar, 0, 0)
        star = ta.getFloat(R.styleable.ExactRatingBar_starValue, 1.5f)
        starSize = dip(ta.getInt(R.styleable.ExactRatingBar_starSize, 64).toFloat())
        numberOfStars = ta.getInteger(R.styleable.ExactRatingBar_starCount, 5)
        isIndicator = ta.getBoolean(R.styleable.ExactRatingBar_isIndicator, false)
        fillColor = ta.getColor(R.styleable.ExactRatingBar_starFillColor, Color.GREEN)
        starBorder = ta.getInteger(R.styleable.ExactRatingBar_starBorderWidth, 2).toFloat()

        ta.recycle()
    }

    private fun init(){
        width = 0f
        height = 0f
        paint.style = Paint.Style.STROKE
        paint.color = fillColor
        paint.strokeWidth = starBorder

        half = starSize / 2f

        starPoints.clear()
        lines.clear()
        paths.clear()
        for (i in 0 until numberOfStars){
            setUpLines(i.toFloat(), space * i)
            setUpPoints()
        }

        lines.forEach {
            height = max(it.startY, it.endY)
            width = max(width, it.endX)
        }

        if(star == 0f)
            seekX = 0f

        setStar(star)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        overLayPath.reset()
        overLayPath.moveTo(0f, 0f)
        overLayPath.lineTo(seekX, 0f)
        overLayPath.lineTo(seekX, height)
        overLayPath.lineTo(0f, height)
        overLayPath.close()

        paint.color = fillColor

        paths.forEach {
            canvas?.drawPath(it, paint)
            val p = it.and(overLayPath)
            paint.style = Paint.Style.FILL
            canvas?.drawPath(p, paint)
            paint.style = Paint.Style.STROKE
            canvas?.drawPath(path, paint)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(isIndicator)
            return true

        when(event?.actionMasked){
            MotionEvent.ACTION_DOWN -> {
                initialPoint = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                seekX = event.x - initialPoint + lastPosX
                if(seekX < 0)
                    seekX = 0f
                if(seekX > width)
                    seekX = width
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                lastPosX = seekX
                calculateRatingValue()
            }
        }
        return true
    }

    fun setStar(value: Float){
        star = value

        for(i in 1 .. numberOfStars){
            if(i - 1 <= star && star < i){
                seekX = widthOfStar * star + space * (i - 1)
                lastPosX = seekX
            }
        }
        invalidate()
    }

    fun setStarCount(value: Int){
        numberOfStars = value
        init()
        requestLayout()
    }

    private fun setUpPoints(){
        //To find out intersection point of two nearest lines
        path.reset()
        path.moveTo(lines[0].startX, lines[0].startY)
        findIntersectionOfNearestLines(lines[0], lines[2])
        findIntersectionOfNearestLines(lines[3], lines[0])
        findIntersectionOfNearestLines(lines[1], lines[3])
        findIntersectionOfNearestLines(lines[4], lines[1])
        findIntersectionOfNearestLines(lines[2], lines[4])
        path.close()
        paths.add(Path(path))
    }

    private fun setUpLines(fraction: Float, space: Int){
        if(lines.isEmpty()) {
            lines.add(Line(half * (0f + fraction), half * 0.34f, half * (1.0f + fraction), half * 0.34f))
            lines.add(Line(half * (1.0f + fraction), half * 0.34f, half * (0.18f + fraction), half * 0.95f))
            lines.add(Line(half * (0.18f + fraction), half * 0.95f, half * (0.5f + fraction), half * 0.0f))
            lines.add(Line(half * (0.5f + fraction), half * 0f, half * (0.82f + fraction), half * 0.95f))
            lines.add(Line(half * (0.82f + fraction), half * 0.95f, half *( 0f + fraction), half * 0.34f))
        }else{
            lines[0] = Line(half * (0f + fraction) + space, half * 0.34f, half * (1.0f + fraction) + space, half * 0.34f)
            lines[1] = Line(half * (1.0f + fraction) + space, half * 0.34f, half * (0.18f + fraction) + space, half * 0.95f)
            lines[2] = Line(half * (0.18f + fraction) + space, half * 0.95f, half * (0.5f + fraction) + space, half * 0.0f)
            lines[3] = Line(half * (0.5f + fraction) + space, half * 0f, half * (0.82f + fraction) + space, half * 0.95f)
            lines[4] = Line(half * (0.82f + fraction) + space, half * 0.95f, half * (0f + fraction) + space, half * 0.34f)
        }

        starPoints.add(PointF(lines[0].startX, lines[0].endX))
        widthOfStar = max(widthOfStar, lines[0].endX - lines[0].startX)
        if(fraction == star - 1f && fraction!=0f){
            lastPosX = lines[0].endX
            seekX = lines[0].endX
        }
    }

    private fun findIntersectionOfNearestLines(l1: Line, l2: Line){
        val p = intersectionPoint(l1.startPoint(), l1.endPoint(), l2.startPoint(), l2.endPoint())
        path.lineTo(p.x, p.y)
        path.lineTo(l2.endX, l2.endY)
    }

    private fun intersectionPoint(A: PointF, B: PointF, C: PointF, D: PointF): PointF {
        val a1 = B.y - A.y
        val b1 = A.x - B.x
        val c1 = a1 * A.x + b1 * A.y

        val a2 = D.y - C.y
        val b2 = C.x - D.x
        val c2 = a2 * C.x + b2 * C.y

        val determinant = a1 * b2 - a2 * b1

        return if (determinant == 0f) {
            PointF(-1f, -1f)
        } else {
            val x = (b2 * c1 - b1 * c2) / determinant
            val y = (a1 * c2 - a2 * c1) / determinant
            PointF(x, y)
        }
    }

    private fun calculateRatingValue(){

        var rate = 0f
        for(i in 0 until starPoints.size){
            val w = abs(starPoints[i].y - starPoints[i].x)
            if(starPoints[i].x <= seekX && seekX <= starPoints[i].y + space){
                rate = (seekX - space * i)/w

                if(onRatingBarChanged != null)
                    onRatingBarChanged?.newRate(rate)

                onRateChanged?.invoke(rate)
            }
        }
    }

    fun isIndicator() = isIndicator

    private fun dip(value: Float) = (value * resources.displayMetrics.density).toInt()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val w = MeasureSpec.makeMeasureSpec(width.toInt(), MeasureSpec.EXACTLY)
        val h = MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.EXACTLY)
        setMeasuredDimension(w, h)
    }


    interface OnRatingBarChanged{
        fun newRate(rate: Float)
    }
}