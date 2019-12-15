import java.lang.IllegalStateException

enum class AmplifierState {
    HALTED, BLOCKED_ON_INPUT, INFINITE_LOOP, NOT_RUNNING, RUNNING;
}

data class AmplifierResult(val state: AmplifierState, val result: Output)


data class Amplifier2(val logic: IntCodeProgram) {

    private var instructionPointer: Int = 0
    private var state: AmplifierState = AmplifierState.NOT_RUNNING

    // Created for day07-part2.
    fun amplify(phase: AmplifierPhaseSetting, input: Input): AmplifierResult {

        if (this.state == AmplifierState.HALTED) {
            throw IllegalStateException("tried to process input $input with a halted amplifier")
        }

        var inputIdx = 0
        val inputs =
                if (this.state == AmplifierState.NOT_RUNNING)
                    listOf(phase.asInput, input)
                else
                    listOf(input)
        this.state = AmplifierState.RUNNING

        val outputs = mutableListOf<Output>()

        loop@ while (this.instructionPointer < this.logic.program.size) {
            val x =
                    OpCodeWithParameters.from(this.logic.program[this.instructionPointer])
            when (x.opcode) {
                OpCode.ADD -> add(
                        this.instructionPointer,
                        this.logic.program,
                        x.parameterModes)
                OpCode.MULTIPLY -> multiply(
                        this.instructionPointer,
                        this.logic.program,
                        x.parameterModes)
                OpCode.INPUT -> {
                    val allInputConsumed = inputIdx == inputs.size
                    if (allInputConsumed) {
                        this.state = AmplifierState.BLOCKED_ON_INPUT
                        // Leave instruction pointer on the read input command.
                        break@loop
                    }
                    readInput(
                            this.instructionPointer,
                            this.logic.program,
                            inputs[inputIdx++])
                }
                OpCode.OUTPUT -> {
                    writeOutput(
                            this.instructionPointer,
                            this.logic.program,
                            outputs,
                            x.parameterModes[0])
                }
                OpCode.HALT -> {
                    this.state = AmplifierState.HALTED
                    break@loop
                }
                OpCode.JUMP_IF_TRUE -> this.instructionPointer =
                        jumpIfTrue(
                                this.instructionPointer,
                                this.logic.program,
                                x.parameterModes)
                OpCode.JUMP_IF_FALSE -> this.instructionPointer =
                        jumpIfFalse(
                                this.instructionPointer,
                                this.logic.program,
                                x.parameterModes)
                OpCode.LESS_THAN -> lessThan(
                        this.instructionPointer,
                        this.logic.program,
                        x.parameterModes)
                OpCode.EQUALS -> equals(
                        this.instructionPointer,
                        this.logic.program,
                        x.parameterModes)
            }

            if (!x.opcode.updatesInstructionPointer) {
                this.instructionPointer += x.opcode.parameterCount.value + 1
            }
        }

        // If we are halted, the last output instruction may have been from the previous amp.
        return if (this.state == AmplifierState.HALTED && outputs.isEmpty()) {
            AmplifierResult(this.state, Output(0, input.value))
        } else {
            AmplifierResult(this.state, outputs[0])
        }

    }
}


fun loopUntilHalt(
        amps: List<Amplifier2>,
        initialInput: Input,
        phases: List<AmplifierPhaseSetting>): AmplifierResult {

    var y = AmplifierResult(AmplifierState.BLOCKED_ON_INPUT, Output(0, 0))
    var input = initialInput

    debug("input = ${input}")
    debug("phases = ${phases}")

    val loopLimit = 10000
    var loopCount = 0

    while (true) {

        for (i in amps.indices) {
            y = amps[i].amplify(phases[i], input)
            debug("")
            input = y.result.asInput
        }

        // We return when last amplifier halts, not the first.
        if (y.state == AmplifierState.HALTED) {
            return y
        }

        loopCount++
        if (loopCount >= loopLimit) {
            return AmplifierResult(AmplifierState.INFINITE_LOOP, y.result)
        }

        debug("\n------- $loopCount")


    }

}

fun main() {

    // I/O
    val logic = readIntCodeProgramFromFileName("./src/day07.input")


    // Functional
    val initialInput = Input(0)

    var maxOutputSignal = Output(0, 0)
    var bestPhaseSettings: List<AmplifierPhaseSetting> = emptyList()
    getPhaseCandidates(getFeedbackPhases())
            .forEach { phaseCandidate ->

                // Reset program for each set of candidate phases.
                val amps = listOf(
                        Amplifier2(logic.clone()),
                        Amplifier2(logic.clone()),
                        Amplifier2(logic.clone()),
                        Amplifier2(logic.clone()),
                        Amplifier2(logic.clone()))

                val y = loopUntilHalt(amps, initialInput, phaseCandidate)
                println("$y for $phaseCandidate")
                if (y.state == AmplifierState.HALTED) {
                    if (y.result > maxOutputSignal) {
                        maxOutputSignal = y.result
                        bestPhaseSettings = phaseCandidate
                    }
                }
            }


    // I/O
    println("maxOutputSignal = $maxOutputSignal for phases $bestPhaseSettings")

}


