import java.io.File
import kotlin.system.exitProcess

fun threePositional() = listOf(
        ParameterMode.POSITION_MODE,
        ParameterMode.POSITION_MODE,
        ParameterMode.POSITION_MODE)

// 7594646
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
        when (x) {
            OpCode.ADD -> add(i, xs, threePositional())
            OpCode.MULTIPLY -> multiply(i, xs, threePositional())
            else -> throw IllegalStateException("unexpected opcode $x at position $i")
        }
        //println("$x  : ${xs}")
    }
}
