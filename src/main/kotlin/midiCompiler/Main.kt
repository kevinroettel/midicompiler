package midiCompiler

import kotlinx.collections.immutable.persistentHashMapOf
import midiEncoder.getNotes

fun main(){
    // Große Zahl
    //val instructions = midiCompiler.readMidi("src/main/resources/midi/AlleMeineEntchen.mid")

    // 112 + 4 => 116
    //val instructions = midiCompiler.readMidi("src/main/resources/midi/firstOperation.mid")

    // (2 + 11) == (12 - 101) => false
    //val instructions = midiCompiler.readMidi("src/main/resources/midi/thirdOperation.mid")

    // \ABE => IF (1011 == 1011) THEN 1011 ELSE 21
    //val instructions = readMidi("src/main/resources/midi/fifthTest.mid")

    // Fibonacci
    //val instructions = getNotes("let F = G \\ F => \\ EEE => if EEE == 0 then 1 else if EEE == 1 then 1 else F ( EEE - 1 ) + F ( EEE - 2 ) in F 5" )

    // Beispiel aus Vorlesung
    //val instructions = getNotes("let A = \\ G => G + 3 in let DD = \\ F => \\ G => F ( F G ) in DD A 10")

    // Größer Kleiner Vergleich und Negation
    //val instructions = getNotes("if 10 > 5 then 1 else 2")

    // Einfacher Loop => 9
    //val instructions = getNotes(": AB = 0 ; loop 3 AB { AB + 3 }")

    // Komplexer Loop => Ergebnis = 62
    // E == True => 5 + 19 + 19 + 19
    // E == False => 5 + 4 + 4 + 4
    //val instructions = getNotes(": AB = 5 ; : BA = 19 ; : DA = 4 ; : E = true ; loop 3 AB { if E then AB + BA else AB + DA }")

    try {
        test(instructions)
        testParser(instructions)
        val lambdaCalculus = eval(persistentHashMapOf(), Parser(Lexer(readMidi("src/main/resources/midi/env.mid"))).parseExpr())
        evalMidi(instructions, persistentHashMapOf("G" to lambdaCalculus) )
    } catch (e : Exception) {
        println(e.message)
    }

    val midiPlayer = MidiPlayer(instructions)
    midiPlayer.playMidi()
}