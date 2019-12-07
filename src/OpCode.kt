// POSITION_MODE means the parameter is a reference (array index); IMMEDIATE_MODE means a value.
enum class ParameterMode() {
    POSITION_MODE, IMMEDIATE_MODE;

    companion object {
        @JvmStatic
        fun from(x: Int) =
                when (x) {
                    0 -> POSITION_MODE
                    1 -> IMMEDIATE_MODE
                    // TODO: Make a total function with https://github.com/michaelbull/kotlin-result
                    else -> throw IllegalArgumentException("invalid ParameterMode $x")
                }

        @JvmStatic
        fun listFrom(input: Int, n: ParameterCount): List<ParameterMode> {
            var shifted = input / 100
            val ys = emptyList<ParameterMode>().toMutableList()
            for (i in 0 until n.value) {
                val y = if (shifted % 10 == 0) POSITION_MODE else IMMEDIATE_MODE
                ys.add(y)
                shifted /= 10
            }
            return ys.toList()
        }
    }
}

data class Parameter(val value: Int, val mode: ParameterMode) {

}

// A ParameterCount is the number of parameters an OpCode takes.
enum class ParameterCount(val value: Int) {
    ZERO(0),
    ONE(1),
    TWO( 2),
    THREE(3)
}

enum class OpCode(val parameterCount: ParameterCount) {
    ADD(ParameterCount.THREE),
    MULTIPLY(ParameterCount.THREE),
    INPUT(ParameterCount.ONE),
    OUTPUT(ParameterCount.ONE),
    JUMP_IF_TRUE(ParameterCount.TWO),
    HALT(ParameterCount.ZERO);

    companion object {
        @JvmStatic
        fun from(x: Int) =
                when (x % 100) {
                    1 -> ADD
                    2 -> MULTIPLY
                    3 -> INPUT
                    4 -> OUTPUT
                    99 -> HALT
                    // TODO: Make a total function with https://github.com/michaelbull/kotlin-result
                    else -> throw IllegalArgumentException("invalid OpCode $x")
                }
    }

}

data class OpCodeWithParameters(val opcode: OpCode, val parameterModes: List<ParameterMode>) {
    init {

        // Parameter counts must match.
        require(parameterModes.size == opcode.parameterCount.value)
        { "wrong parameter count ${parameterModes.size}, opcode $opcode requires ${opcode.parameterCount}" }

        // Output parameters must be positional, not immediate.
        when (opcode) {
            OpCode.ADD,
            OpCode.MULTIPLY -> require(parameterModes[2] == ParameterMode.POSITION_MODE)
            OpCode.INPUT -> require(parameterModes[0] == ParameterMode.POSITION_MODE)
            else -> Unit // No other opcodes write, so OK.
        }
    }

    companion object {
        @JvmStatic
        fun from(x: Int) =
                OpCodeWithParameters(
                        OpCode.from(x),
                        ParameterMode.listFrom(x, OpCode.from(x).parameterCount))

    }
}
