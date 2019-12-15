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
        value: Int) {
    debug("    --> set xs[${xs[i + 3]}] = $value")
    xs[xs[i + 3]] = value
}

fun jumpIfTrue(i: Int, xs: MutableList<Int>, parameterModes: List<ParameterMode>): Int {
    val nextInstructionPointer =
            if (0 != firstParam(i, xs, parameterModes))
                secondParam(i, xs, parameterModes)
            else
                i + parameterModes.size + 1
    debug("$i: jumpIfTrue (x[${getIndexByMode(i+1, xs, parameterModes[0])}]=${firstParam(i, xs, parameterModes)}): jump from $i to $nextInstructionPointer")
    return nextInstructionPointer
}

fun jumpIfFalse(i: Int, xs: MutableList<Int>, parameterModes: List<ParameterMode>): Int {
    val nextInstructionPointer =
            if (0 == firstParam(i, xs, parameterModes))
                secondParam(i, xs, parameterModes)
            else
                i + parameterModes.size + 1
    debug("$i: jumpIfFalse (x[${getIndexByMode(i+1, xs, parameterModes[0])}]=${firstParam(i, xs, parameterModes)}): jump from $i to $nextInstructionPointer")
    return nextInstructionPointer
}

fun lessThan(i: Int, xs: MutableList<Int>, parameterModes: List<ParameterMode>) {
    debug("$i: lessThan")
    if (firstParam(i, xs, parameterModes) < secondParam(i, xs, parameterModes))
        setThirdParam(i, xs, 1)
    else
        setThirdParam(i, xs, 0)
    debug("$i: lessThan (x[${getIndexByMode(i+1, xs, parameterModes[0])}] < x[${getIndexByMode(i+2, xs, parameterModes[1])}] = ${firstParam(i, xs, parameterModes)} < ${secondParam(i, xs, parameterModes)}): ")

}


fun equals(i: Int, xs: MutableList<Int>, parameterModes: List<ParameterMode>) {
    debug("$i: equals")
    if (firstParam(i, xs, parameterModes) == secondParam(i, xs, parameterModes))
        setThirdParam(i, xs, 1)
    else
        setThirdParam(i, xs, 0)
    debug("$i: equals (x[${getIndexByMode(i+1, xs, parameterModes[0])}] == x[${getIndexByMode(i+2, xs, parameterModes[1])}] = ${firstParam(i, xs, parameterModes)} == ${secondParam(i, xs, parameterModes)}): ")
}

fun exec(program: IntCodeProgram, inputs: List<Input>): List<Output> {

    debug("exec with inputs = $inputs")

    var inputIdx = 0

    // toMutableList() makes a copy
    val mutableProgram = program.program.toMutableList()

    var instructionPointer = 0
    val outputs = emptyList<Output>().toMutableList()
    loop@ while (instructionPointer < program.size) {
        val x = OpCodeWithParameters.from(mutableProgram[instructionPointer])
        when (x.opcode) {
            OpCode.ADD -> add(instructionPointer, mutableProgram, x.parameterModes)
            OpCode.MULTIPLY -> multiply(instructionPointer, mutableProgram, x.parameterModes)
            OpCode.INPUT -> readInput(instructionPointer, mutableProgram, inputs[inputIdx++])
            OpCode.OUTPUT -> writeOutput(
                    instructionPointer,
                    mutableProgram,
                    outputs,
                    x.parameterModes[0])
            OpCode.HALT -> break@loop
            OpCode.JUMP_IF_TRUE -> instructionPointer =
                    jumpIfTrue(instructionPointer, mutableProgram, x.parameterModes)
            OpCode.JUMP_IF_FALSE -> instructionPointer =
                    jumpIfFalse(instructionPointer, mutableProgram, x.parameterModes)
            OpCode.LESS_THAN -> lessThan(instructionPointer, mutableProgram, x.parameterModes)
            OpCode.EQUALS -> equals(instructionPointer, mutableProgram, x.parameterModes)
        }

        if (!x.opcode.updatesInstructionPointer) {
            instructionPointer += x.opcode.parameterCount.value + 1
        }
    }

    return outputs.toList()
}


fun main() {

    // -----------------------------------------------------------------------------------------
    // INPUT I/O
    //
    // Way overkill (on purpose).  Learning by following functional design strictly.
    // If there were more than one input, and we can't collect all inputs up front, then we need
    // a double-, triple-, or n-decker sandwich.
    //
    // Note that I'm diverging from pure functional in two ways.  One, parsing the input throws
    // an exceptions.  Second, the program is mutable list of integers.
    //

    val program = readIntCodeProgramFromFileName("./src/day05.input")
    val input = listOf(Input(5))


    // -----------------------------------------------------------------------------------------
    // PURE FUNCTION
    //
    val output = exec(program, input)


    // -----------------------------------------------------------------------------------------
    // OUTPUT I/O
    //
    output.forEach { println(it) }

}
