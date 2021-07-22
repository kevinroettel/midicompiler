package midiCompiler

import kotlinx.collections.immutable.persistentHashMapOf
import midiEncoder.getNotes

fun main(){
//    var instructions = midiCompiler.readMidi("src/midiCompiler.main/resources/midi/AlleMeineEntchen.mid")
//    var instructions = midiCompiler.readMidi("src/midiCompiler.main/resources/midi/firstOperation.mid")
//    var instructions = midiCompiler.readMidi("src/midiCompiler.main/resources/midi/secondOperation.mid")
//    var instructions = midiCompiler.readMidi("src/midiCompiler.main/resources/midi/thirdOperation.mid")
//    var instructions = midiCompiler.readMidi("src/midiCompiler.main/resources/midi/fourthOperation.mid")
//    val instructions = readMidi("src/main/resources/midi/fifthTest.mid")
//    val instructions = getNotes("LET F = G \\ F => \\ EEE => IF EEE == 0 THEN 1 ELSE IF EEE == 1 THEN 1 ELSE F ( EEE - 1 ) + F ( EEE - 2 ) IN F 5" )
//    val instructions = getNotes("LET A = \\ G => G + 3 IN LET DD = \\ F => \\ G => F ( F G ) IN DD A 10")
    val instructions = getNotes("LOOP 5 E { IF E == TRUE THEN FALSE ELSE TRUE }")


    test(instructions)
    testParser(instructions)
    testEval2(instructions)

    /*val midiPlayer = MidiPlayer(instructions)
    midiPlayer.playMidi()*/
}

val z = eval(
    persistentHashMapOf(),
    Parser(Lexer(readMidi("src/main/resources/midi/env.mid"))).parseExpr()
)