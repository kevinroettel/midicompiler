package midiCompiler

import getNotes
import kotlinx.collections.immutable.persistentHashMapOf

fun main(){
//    var instructions = midiCompiler.readMidi("src/midiCompiler.main/resources/midi/AlleMeineEntchen.mid")
//    var instructions = midiCompiler.readMidi("src/midiCompiler.main/resources/midi/firstOperation.mid")
//    var instructions = midiCompiler.readMidi("src/midiCompiler.main/resources/midi/secondOperation.mid")
//    var instructions = midiCompiler.readMidi("src/midiCompiler.main/resources/midi/thirdOperation.mid")
//    var instructions = midiCompiler.readMidi("src/midiCompiler.main/resources/midi/fourthOperation.mid")
//    val instructions = readMidi("src/main/resources/midi/fifthTest.mid")
    val instructions = getNotes("LET ADD = \\ EE => EE + 3 IN LET D = \\ DD => \\ EE => DD ( DD EE ) IN D ADD 10")


//    println(instructions)

    test(instructions)
    testParser(instructions)
    testEval(instructions)
}

val z = eval(
    persistentHashMapOf(),
    Parser(Lexer(readMidi("src/main/resources/midi/env.mid"))).parseExpr()
)