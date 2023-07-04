package pages

import emotion.react.css
import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import react.FC
import react.Props
import react.rawUseEffect
import web.cssom.Border
import web.cssom.Color
import web.cssom.LineStyle
import web.cssom.px
import web.timers.Timeout
import web.timers.clearTimeout
import web.timers.setInterval
import kotlin.math.floor
import kotlin.random.Random
import react.dom.html.ReactHTML as h

external interface TimerDemoProps : Props {
    var height: Int
    var width: Int
}

private val hexDigits = ('0'..'9').toList() + ('A'..'F').toList()

private fun randomColor(): String = buildString {
    append('#')
    repeat(6) {
        append(hexDigits[floor(Random.nextDouble() * 16).toInt()])
    }
}

val TimerDemo = FC<TimerDemoProps> { props ->

    val canvasId = "myCanvas"

    var timer: Timeout? = null

    fun createTimer() {
        timer = setInterval({
            val c1 = document.getElementById(canvasId) as HTMLCanvasElement
            (c1.getContext("2d") as CanvasRenderingContext2D).apply {
                val x = Random.nextDouble() * props.width
                val y = Random.nextDouble() * props.height
                lineWidth = 1.0
                strokeStyle = randomColor()
                lineTo(x, y)
                stroke()
            }
        }, 1000)
    }

    fun deleteTimer() {
        if (null != timer) {
            clearTimeout(timer!!)
            timer = null
        }
    }

    rawUseEffect({
        createTimer()
        ::deleteTimer
    }, arrayOf())

    h.div {
        h.small { +"Timer..." }
    }
    h.div {
        h.canvas {
            id = canvasId
            width = props.width.toDouble()
            height = props.height.toDouble()
            css {
                border = Border(1.px, LineStyle.solid, Color("#000000"))
            }
        }
    }
}
