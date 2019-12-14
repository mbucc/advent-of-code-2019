import java.io.File

enum class AmplifierPhaseSetting {
    ZERO, ONE, TWO, THREE, FOUR;

    val asInput: Input
        get() =
            when (this) {
                ZERO -> Input(0)
                ONE -> Input(1)
                TWO -> Input(2)
                THREE -> Input(3)
                FOUR -> Input(4)
            }
}

data class IntCodeProgram(val value: List<Int>) {
    val size: Int
        get() = this.value.size

    operator fun get(index: Int) = this.value[index]

}


data class Amplifier(val phase: AmplifierPhaseSetting, val logic: IntCodeProgram)


fun readIntCodeProgramFromFileName(fileName: String) =
        IntCodeProgram(
                File(fileName)
                        .readText()
                        .trim('\n')
                        .split(",")
                        .map(String::toInt))


fun amplify(a: Amplifier, b: Input): Output {

    val output = exec(a.logic, listOf(a.phase.asInput, b))

    if (output.size != 1) {
        throw IllegalStateException(
                "amps should only produce one output, got $output " +
                "for $a and with input $b")
    }
    return output[0]
}


// Partial application in Kotlin---dependency injection, functional style.
//
// This function takes an IntCodeProgram (the logic for the amplifier) and
// returns a function that converts a phase and input into the amp output.
fun amplifyWithPhase(logic: IntCodeProgram)
        : (AmplifierPhaseSetting, Input) -> Output =
        { phase: AmplifierPhaseSetting, input: Input ->
            amplify(Amplifier(phase, logic), input)
        }


fun amplifierChain(
        amp: (AmplifierPhaseSetting, Input) -> Output,
        firstInput: Input,
        phases: List<AmplifierPhaseSetting>): Output {
    val aout = amp(phases[0], firstInput)
    if (aout != amp(phases[0], firstInput)) {
        throw IllegalStateException("mutable program, code error")
    }
    val bout = amp(phases[1], aout.asInput)
    val cout = amp(phases[2], bout.asInput)
    val dout = amp(phases[3], cout.asInput)
    return amp(phases[4], dout.asInput)

}


// First call:
//    ( [A,B,C], [] )
// Second call:
//    ( [B,C], [A] )
//    ( [A,C], [B] )
//    ( [A,B], [C] )
// Third call:
//    ( [C], [A,B] )
//    ( [B], [A,C] )
//    ( [C], [B,A] )
//    ( [A], [B,C] )
//    ( [B], [C,A] )
//    ( [A], [C,B] )
// Fourth call (hit stop condition):
//    ( [], [A,B,C] )
//    ( [], [A,C,B] )
//    ( [], [B,A,C] )
//    ( [], [B,C,A] )
//    ( [], [C,A,B] )
//    ( [], [C,B,A] )

fun getPhaseCandidates(phases: List<AmplifierPhaseSetting>): List<List<AmplifierPhaseSetting>> {

    if (phases.size == 1) {
        return listOf(phases)
    }

    val head = phases[0]
    val tail = phases.drop(1)
    val accum1 = mutableListOf<List<AmplifierPhaseSetting>>()
    for (perm in getPhaseCandidates(tail)) {
        for (i in 0..perm.size) {
            val newPerm = perm.toMutableList()
            newPerm.add(i, head)
            accum1.add(newPerm)
        }
    }
    return accum1.toList()

}


fun main() {

    // I/O
    val logic = readIntCodeProgramFromFileName("./src/day07.input")
    val firstInput = Input(0)


    // Functions
    val amp = amplifyWithPhase(logic)
    var maxOutputSignal = Output(0, 0)
    var bestPhaseSettings : List<AmplifierPhaseSetting> = emptyList()
    getPhaseCandidates(AmplifierPhaseSetting.values().toList())
            .forEach { phaseCandidate ->
                val eout = amplifierChain(amp, firstInput, phaseCandidate)
                if (eout > maxOutputSignal) {
                    maxOutputSignal = eout
                    bestPhaseSettings = phaseCandidate
                }
            }


    // I/O
    println("maxOutputSignal = $maxOutputSignal for phases $bestPhaseSettings")

}



