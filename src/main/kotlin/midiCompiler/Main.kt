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
//    val instructions = getNotes("let F = G \\ F => \\ EEE => if EEE == 0 then 1 else if EEE == 1 then 1 else F ( EEE - 1 ) + F ( EEE - 2 ) in F 5" )
//    val instructions = getNotes("let A = \\ G => G + 3 in let DD = \\ F => \\ G => F ( F G ) in DD A 10")
    val instructions = getNotes(": AB = 5 ; : BA = 19 ; : DA = 4 ; : E = true ; loop 3 AB { if E then AB + BA else AB + DA }")


    test(instructions)
    testParser(instructions)
    val lambdaCalculus = eval(persistentHashMapOf(), Parser(Lexer(readMidi("src/main/resources/midi/env.mid"))).parseExpr())
    evalMidi(instructions, persistentHashMapOf("G" to lambdaCalculus) )

    val midiPlayer = MidiPlayer(instructions)
    midiPlayer.playMidi()
    midiPlayer.setDataFromMutableListOfString(readMidi("src/main/resources/midi/env.mid"))
    midiPlayer.playMidi()
}