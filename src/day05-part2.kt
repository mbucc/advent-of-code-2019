import java.io.File
import java.util.Stack


private fun firstParam(
        i: Int,
        xs: MutableList<Int>,
        parameterModes: List<ParameterMode>) =
        getValueByMode(i + 1, xs, parameterModes[0])

private fun secondParam(
        i: Int,
        xs: MutableList<Int>,
        parameterModes: List<ParameterMode>) =
        getValueByMode(i + 2, xs, parameterModes[1])

private fun setThirdParam(
        i: Int,
        xs: MutableList<Int>,
        value : Int) {
    xs[xs[i + 3]] = value
}

fun jumpIfTrue(i: Int, xs: MutableList<Int>, parameterModes: List<ParameterMode>) =
        if (0 != firstParam(i, xs, parameterModes))
            secondParam(i, xs, parameterModes)
        else
            i + parameterModes.size + 1

fun jumpIfFalse(i: Int, xs: MutableList<Int>, parameterModes: List<ParameterMode>) =
        if (0 == firstParam(i, xs, parameterModes))
            secondParam(i, xs, parameterModes)
        else
            i + parameterModes.size + 1


fun lessThan(i: Int, xs: MutableList<Int>, parameterModes: List<ParameterMode>) =
        if (firstParam(i, xs, parameterModes) < secondParam(i, xs, parameterModes))
            setThirdParam(i, xs, 1)
        else
            setThirdParam(i, xs, 0)


fun equals(i: Int, xs: MutableList<Int>, parameterModes: List<ParameterMode>) =
        if (firstParam(i, xs, parameterModes) == secondParam(i, xs, parameterModes))
            setThirdParam(i, xs, 1)
        else
            setThirdParam(i, xs, 0)


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
    inputs.push(Input(5))

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
            OpCode.JUMP_IF_TRUE -> i = jumpIfTrue(i, xs, x.parameterModes)
            OpCode.JUMP_IF_FALSE -> i = jumpIfFalse(i, xs, x.parameterModes)
            OpCode.LESS_THAN -> lessThan(i, xs, x.parameterModes)
            OpCode.EQUALS -> equals(i, xs, x.parameterModes)
        }

        if (!x.opcode.updatesInstructionPointer) {
            i += x.opcode.parameterCount.value + 1
        }
    }

    // -----------------------------------------------------------------------------------------
    // OUTPUT I/O
    //
    outputs.forEach { println(it) }

}
