import kotlinx.collections.immutable.persistentHashMapOf

fun main(){
//    var instructions = readMidi("src/main/resources/midi/AlleMeineEntchen.mid")
//    var instructions = readMidi("src/main/resources/midi/firstOperation.mid")
//    var instructions = readMidi("src/main/resources/midi/secondOperation.mid")
//    var instructions = readMidi("src/main/resources/midi/thirdOperation.mid")
//    var instructions = readMidi("src/main/resources/midi/fourthOperation.mid")
    var instructions = readMidi("src/main/resources/midi/fifthTest.mid")


//    println(instructions)

    test(instructions)
    testParser(instructions)
    testEval(instructions)
}

val z = eval(
    persistentHashMapOf(),
    Parser(Lexer(readMidi("src/main/resources/midi/env.mid"))).parseExpr()
)