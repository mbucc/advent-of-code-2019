import java.io.File
import java.util.Stack

data class Input(val value: Int)
data class Output(val index: Int, val value: Int) {
    operator fun compareTo(x: Output): Int {
        return this.value.compareTo(x.value)
    }

    val asInput: Input
        get() = Input(this.value)
}


fun getInput(): Input = Input(1)


fun readInput(i: Int, xs: MutableList<Int>, input: Input) {
    val idx = xs[i + 1]
    debug("$i: readInput: read ${input.value} and save to index $idx")
    xs[idx] = input.value
}

fun getIndexByMode(i: Int, xs: MutableList<Int>, mode: ParameterMode) =
        when (mode) {
            ParameterMode.IMMEDIATE_MODE -> i
            ParameterMode.POSITION_MODE -> xs[i]
        }

fun getValueByMode(i: Int, xs: MutableList<Int>, mode: ParameterMode): Int =
        xs[getIndexByMode(i, xs, mode)]

fun writeOutput(
        i: Int,
        xs: MutableList<Int>,
        outputs: MutableList<Output>,
        mode: ParameterMode) {
    val value = getValueByMode(i + 1, xs, mode)
    val x = Output(i, value)
    debug("$i: writeOutput: $value from xs[${getIndexByMode(i + 1, xs, mode)}] in $mode")
    outputs.add(x)
}

const val DEBUG_PROGRAM = false
fun debug(msg: String) {
    if (DEBUG_PROGRAM) {
        println(msg)
    }
}

fun add(i: Int, xs: MutableList<Int>, parameterModes: List<ParameterMode>) {
    val a = getValueByMode(i + 1, xs, parameterModes[0])
    val b = getValueByMode(i + 2, xs, parameterModes[1])
    val c = xs[i + 3]
    debug(
            "$i: add: xs[$c]" +
            " = x[${getIndexByMode(i + 1, xs, parameterModes[0])}]" +
            " + x[${getIndexByMode(i + 2, xs, parameterModes[1])}]" +
            " = $a + $b")
    xs[c] = a + b
}

fun multiply(i: Int, xs: MutableList<Int>, parameterModes: List<ParameterMode>) {
    val a = getValueByMode(i + 1, xs, parameterModes[0])
    val b = getValueByMode(i + 2, xs, parameterModes[1])
    val c = xs[i + 3]
    debug("$i: mul: xs[$c]" +
          " = x[${getIndexByMode(i + 1, xs, parameterModes[0])}]" +
          " * x[${getIndexByMode(i + 2, xs, parameterModes[1])}]" +
          " = $a * $b")
    xs[c] = a * b
}

// Make a functional sandwich:
//           I/O           first slice of bread---get the input
//      pure function      no side effects (AKA I/O)
//           I/O           print output
//
fun main() {

    // -----------------------------------------------------------------------------------------
    // INPUT I/O
    //
    // Way overkill (on purpose).  Learning by following functional design strictly.
    // If there were more than one input, and we can't collect all inputs up front, then we need
    // a double-, triple-, or n-decker sandwich.
    //
    // Note that I'm diverging from pure functional in two ways.  One, parsing the input throws
    // an exceptions.  Second, I'm changing the list of integers.
    //
    val inputs = Stack<Input>()
    inputs.push(getInput())

    // Read program from file.
    val xs = File("./src/day05.input")
            .readText()
            .trim('\n')
            .split(",")
            .map(String::toInt)
            .toMutableList()

    // -----------------------------------------------------------------------------------------
    // PURE FUNCTIONS
    //
    var i = 0
    val outputs = emptyList<Output>().toMutableList()
    loop@ while (i < xs.size) {
        val x = OpCodeWithParameters.from(xs[i])
        when (x.opcode) {
            OpCode.ADD -> add(i, xs, x.parameterModes)
            OpCode.MULTIPLY -> multiply(i, xs, x.parameterModes)
            OpCode.INPUT -> readInput(i, xs, inputs.pop()!!)
            OpCode.OUTPUT -> writeOutput(i, xs, outputs, x.parameterModes[0])
            OpCode.HALT -> break@loop
        }
        i += x.opcode.parameterCount.value + 1
    }

    // -----------------------------------------------------------------------------------------
    // OUTPUT I/O
    //
    outputs.forEach { println(it) }

}
