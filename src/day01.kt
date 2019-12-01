import java.io.File
import kotlin.math.max

data class Mass(val value: Int) {
    init {
        require(value >= 0) { "invalid mass $value" }
    }
}

data class Fuel(val value: Int) {
    init {
        require(value >= 0) { "invalid fuel $value" }
    }
    operator fun plus(x: Fuel): Fuel = Fuel(value + x.value)
    fun mass() : Mass = Mass(value)
}

fun fuel(x : Mass) : Fuel = Fuel(max(0, x.value / 3 - 2))


fun stoi(x : String) : Int {
    val x1 = x.toLong()
    require(x1 <= Integer.MAX_VALUE)
    return x.toInt()
}

fun main() {
    val total = File("./src/day01.input")
        .readLines()
        .map{Mass(stoi(it))}
        .map{fuel(it)}
        .sumBy { it.value }
    println("total = ${total}")

}
