import java.io.File
import kotlin.system.exitProcess


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
        x.f(i, xs)
        //println("$x  : ${xs}")
    }
}
