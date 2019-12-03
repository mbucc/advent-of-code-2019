import java.io.File
import kotlin.system.exitProcess

typealias updater = (Int, MutableList<Int>) -> Unit

fun apply(i: Int, xs: MutableList<Int>, g: (Int, Int) -> Int) {
    val x = xs[i + 1]
    val y = xs[i + 2]
    val z = xs[i + 3]
    xs[z] = g(xs[x], xs[y])
}

fun adder(i: Int, xs: MutableList<Int>) {
    apply(i, xs) { x, y -> x + y }
}

fun multiplier(i: Int, xs: MutableList<Int>) {
    apply(i, xs) { x, y -> x * y }
}

fun noop(i: Int, xs: MutableList<Int>) = Unit

enum class OpCode(val f: updater) {
    ONE(::adder),
    TWO(::multiplier),
    HALT(::noop);

    companion object {
        @JvmStatic
        fun from(x: Int) =
                when (x) {
                    1 -> ONE
                    2 -> TWO
                    99 -> HALT
                    else -> throw IllegalArgumentException("invalid OpCode $x")
                }
    }
}

fun main() {

    val xs = File("./src/day02.input")
            .readText()
            .trim('\n')
            .split(",")
            .map(String::toInt)
            .toMutableList()
    println("start0: $xs")
    // Testing reading comprehension?  Missed this first time through.  ;(
    xs[1] = 12
    xs[2] = 2
    println("start1: $xs")
    for (i in 0..xs.size step 4) {
        val x = OpCode.from(xs[i])
        if (x == OpCode.HALT) {
            println("end   : $xs")
            exitProcess(0)
        }
        x.f(i, xs)
        //println("$x  : ${xs}")
    }
}
