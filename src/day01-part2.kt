import java.io.File


fun addFuelWeight(x: Fuel): Fuel = x + fuelWeight(Fuel(0), x)

fun fuelWeight(accum: Fuel, x: Fuel): Fuel {
    val y = fuel(x.mass())
    if (y.value == 0) {
        return accum
    }
    return fuelWeight(accum + y, y)
}


fun main() {
    val total = File("./src/day01.input")
            .readLines()
            .map { Mass(stoi(it)) }
            .map { fuel(it) }
            .map { addFuelWeight(it) }
            .sumBy { it.value }
    println("total = ${total}")

}
