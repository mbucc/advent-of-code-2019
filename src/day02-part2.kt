import java.io.File
import java.lang.Exception
import java.time.Duration
import java.time.Instant


// 3376
fun main() {
    val t0 = Instant.now()
    var invalidOpCodeCount = 0
    for (noun in 0..99) {
        for (verb in 0..99) {
            val xs = File("./src/day02.input")
                    .readText()
                    .trim('\n')
                    .split(",")
                    .map(String::toInt)
                    .toMutableList()
            xs[1] = noun
            xs[2] = verb
            for (i in 0..xs.size step 4) {
                val x : OpCode
                try {
                    x = OpCode.from(xs[i])
                } catch (e : Exception) {
                    invalidOpCodeCount++
                    //println("invalid op code ${xs[i]} at i = $i with (noun, verb) = ($noun, $verb), continuing")
                    break
                }
                when (x) {
                    OpCode.ADD -> add(i, xs, threePositional())
                    OpCode.MULTIPLY -> multiply(i, xs, threePositional())
                    OpCode.HALT -> if (xs[0] == 19690720) {
                        println("Found a win with (noun, verb) = ($noun, $verb)")
                        println("100 * noun + verb = ${100*noun + verb}")
                    }
                    else -> throw IllegalStateException("unexpected opcode $x at position $i")
                }
            }

        }
    }
    val t1 = Instant.now()
    // "took 793 milliseconds"
    println("took ${Duration.between(t0, t1).toMillis()} milliseconds")
    println("${invalidOpCodeCount} noun, verb combinations led to an invalid OpCode")

}
