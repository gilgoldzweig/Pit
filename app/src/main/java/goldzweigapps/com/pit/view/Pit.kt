package goldzweigapps.com.pit.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.support.annotation.UiThread
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

/**
 * Created by gilgoldzweig on 20/12/2017.
 */
class Pit @JvmOverloads constructor(context: Context,
                                    attrs: AttributeSet? = null,
                                    defStyleAttr: Int = 0,
                                    defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {
    //Line default values
    private val DEFAULT_LINE_COLOR = Color.parseColor("#38B9F4")
    private val DEFAULT_LINE_STORKE_WIDTH = 9f

    //Dots default values
    private val DEFAULT_DOT_COLOR = Color.parseColor("#CF1D30")
    private val DEFAULT_DOT_RADIUS = 12
    private val DEFAULT_TOUCH_RADIUS = Math.pow(DEFAULT_DOT_RADIUS.toDouble(), 3.0)


    private var linePaint = Paint()
    private var dotPaint = Paint()

    private var dotList = LinkedList<Point>()


    private var selectedDotPosition: Int? = null

    private val random = Random()

    init {
        linePaint.strokeWidth = DEFAULT_LINE_STORKE_WIDTH
        linePaint.color = DEFAULT_LINE_COLOR
        dotPaint.color = DEFAULT_DOT_COLOR
    }

    var drawn = false
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (drawn) return
        for (i in 0 until 5) {
            val randomXPoint = random.nextInt(measuredWidth)
            val randomYPoint = random.nextInt(measuredHeight)

            dotList.add(Point(randomXPoint, randomYPoint))
        }
        drawn = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //Verifying that the canvas is not null if it is we have nothing to do
        if (canvas == null) return

        /**
         * Calls the specified function [ block ] with the given [receiver: Canvas]
         * as its receiver and returns its result.
         */
        with(canvas) {

            drawOriginLines()
            dotList
                    .forEachIndexed { index, point ->
                        //verifying that the the list has at least one item
                        if (dotList.size <= 1) return

                        //Drawing the lines that connect the dots
                        when (index) {

                            0 ->  //First: draws only the next line
                                drawLine(point, dotList[index + 1])


                            dotList.size - 1 ->  //Last: draws only the previous line
                                drawLine(point, dotList[index - 1])

                        //Else: draws next and previous lines
                            else -> {
                                //drawing both lines
                                drawLine(point, dotList[index + 1])
                                drawLine(point, dotList[index - 1])
                            }
                        }
                    }
            //draws the dots after the lines in order for the dots to be above the lines
            dotList.forEach {
                drawDot(it)
            }
        }
    }

    private fun Canvas.drawLine(from: Point, to: Point) {

        drawLine(from.x.toFloat(), from.y.toFloat(),
                to.x.toFloat(), to.y.toFloat(),
                linePaint)
    }

    private fun Canvas.drawDot(dot: Point) {
        drawCircle(dot.x.toFloat(), dot.y.toFloat(),
                DEFAULT_DOT_RADIUS.toFloat(),
                dotPaint)
    }

    private fun Canvas.drawOriginLines() {
        val centerX = (width / 2)
        val centerY = (height / 2)
        drawLine(Point(centerX, 0), Point(centerX, height))
        drawLine(Point(0, centerY), Point(width, centerY))
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event == null) return super.onTouchEvent(event)

        // Is the event handled
        var handled = false

        //Touch X, Y points
        val xTouch = event.x
        val yTouch = event.y


        when (event.action) {

        //Finger placed on screen
            MotionEvent.ACTION_DOWN -> {

                /** TODO I'm sure there is a more efficient way to find if the touched area is a dot,
                 * TODO but i didn't have time to improve it
                 *
                 * Finding the dot that was touched if none selected dot will be null
                 */
                dotList.forEachIndexed { index, point ->
                    val x = point.x - xTouch
                    val xSquare = x * x

                    val y = point.y - yTouch
                    val ySquare = y * y

                    if (xSquare + ySquare <= DEFAULT_TOUCH_RADIUS) {
                        selectedDotPosition = index
                        return@forEachIndexed
                    }
                }
                invalidate()
                handled = true
            }

        //Finger still touching the screen, drag the dot if its not null
            MotionEvent.ACTION_MOVE -> {
                selectedDotPosition?.let {
                    val newXPoint = if (xTouch > width) width - DEFAULT_DOT_RADIUS else xTouch.toInt()
                    val newYPoint = if (yTouch > width) height - DEFAULT_DOT_RADIUS else yTouch.toInt()
                    val newDot = Point(newXPoint, newYPoint)
                    dotList[it] = newDot
                    selectedDotPosition = it
                    invalidate()
                    handled = true
                }
            }
        //Finger Is not touching the screen clearing the dot
            MotionEvent.ACTION_UP -> {
                selectedDotPosition = null
                handled = true
            }

        //Any other touch event not interesting
            else ->
                return selectedDotPosition != null
        }
        return handled
    }

    inline fun <T> Iterable<T>.firstOrNullIndexed(predicate: (Int, T) -> Boolean): T? {
        var index = 0
        for (element in this) {
            if (predicate(index, element)) return element
            index++
        }
        return null
    }

    /**
     * Add's a new dot to the list of dots
     * @param dot the dot to insert,
     * default value is the radius so it will be seen as full and not from the center
     * @see UiThread Requires to run the function on ui thread or else the invalidate()
     * will throw exception
     */
    @UiThread
    fun insert(dot: Point = Point(DEFAULT_DOT_RADIUS, DEFAULT_DOT_RADIUS)) {
        dotList.add(0, dot)
        invalidate()
    }

}