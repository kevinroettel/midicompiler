package midiEncoder

import java.lang.NumberFormatException
import java.io.File
import javax.sound.midi.*

const val NOTE_ON = 0x90
const val NOTE_OFF = 0x80
val NOTE_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

fun getNotes(string : String) : MutableList<String> {
    val notes : MutableList<String> = ArrayList()
    val chars = string.split(" ").toMutableList()

    for (character in chars) {
        val temp = when (character) {
            "(" -> mutableListOf("C1")
            ")" -> mutableListOf("D1")
            "+" -> mutableListOf("E1")
            "-" -> mutableListOf("F1")
            "*" -> mutableListOf("G1")
            "=" -> mutableListOf("A1")
            "==" -> mutableListOf("A1", "A1")
            "=>" -> mutableListOf("A1", "B1")
            "if" -> mutableListOf("C2")
            "then" -> mutableListOf("D2")
            "else" -> mutableListOf("E2")
            "let" -> mutableListOf("G2")
            "in" -> mutableListOf("A2")
            "\\" -> mutableListOf("F2")
            "loop" -> mutableListOf("C3")
            "{" -> mutableListOf("D3")
            "}" -> mutableListOf("E3")
            "<" -> mutableListOf("F3")
            ">" -> mutableListOf("G3")
            "true" -> mutableListOf("C6")
            "false" -> mutableListOf("D6")
            "!" -> mutableListOf("A3")
            ":" -> mutableListOf("B3")
            ";" -> mutableListOf("B2")
            else -> parse(character)
        }

        notes.addAll(temp)
    }

    return notes
}

fun parse(character : String) : List<String> {
    val notes : MutableList<String> = ArrayList()

    try {
        val number = character.toInt()
        notes.addAll(parseNumber(number))
    } catch (e : NumberFormatException) {
        notes.addAll(parseIdent(character))
    }

    return notes
}

fun parseNumber(number : Int) : List<String> {
    val notes : MutableList<String> = ArrayList()

    val numberStrings = number.toString().split("").toMutableList()
    numberStrings.removeAt(0)
    numberStrings.removeAt(numberStrings.size - 1)

    notes.add("C4")
    var digit = numberStrings.size

    for (numberString in numberStrings) {
        val num = numberString.toInt()

        for (i in 1..num) {
            notes.add(when (digit) {
                1 -> "B4"
                2 -> "A4"
                3 -> "G4"
                4 -> "F4"
                5 -> "E4"
                6 -> "D4"
                else -> ""
            })
        }

        digit--
    }

    notes.add("C4")
    return notes
}

fun parseIdent(identifier : String) : List<String> {
    val notes : MutableList<String> = ArrayList()

    val identifierContent = identifier.split("").toMutableList()
    identifierContent.removeAt(0)
    identifierContent.removeAt(identifierContent.size - 1)

    notes.add("C5")
    for (char in identifierContent) {
        notes.add(when (char) {
            "D" -> "D5"
            "E" -> "E5"
            "F" -> "F5"
            "G" -> "G5"
            "A" -> "A5"
            "B" -> "B5"
            else -> ""
        })
    }
    notes.add("C5")

    return notes
}

fun writeToFile(notes : List<String>) {
    try {
        val s = Sequence(Sequence.PPQ, 24)

        val t = s.createTrack()

        val b = byteArrayOf(0xF0.toByte(), 0x7E, 0x7F, 0x09, 0x01, 0xF7.toByte())
        val sm = SysexMessage()
        sm.setMessage(b, 6)
        var me = MidiEvent(sm, 0.toLong())
        t.add(me)

        //****  set tempo (meta event)  ****
        var mt = MetaMessage()
        val bt = byteArrayOf(0x02, 0x00.toByte(), 0x00)
        mt.setMessage(0x51, bt, 3)
        me = MidiEvent(mt, 0.toLong())
        t.add(me)

        //****  set track name (meta event)  ****
        mt = MetaMessage()
        val trackName = "midifile track"
        mt.setMessage(0x03, trackName.toByteArray(), trackName.length)
        me = MidiEvent(mt, 0.toLong())
        t.add(me)

        //****  set omni on  ****
        var mm = ShortMessage()
        mm.setMessage(0xB0, 0x7D, 0x00)
        me = MidiEvent(mm, 0.toLong())
        t.add(me)

        //****  set poly on  ****
        mm = ShortMessage()
        mm.setMessage(0xB0, 0x7F, 0x00)
        me = MidiEvent(mm, 0.toLong())
        t.add(me)

        //****  set instrument to Piano  ****
        mm = ShortMessage()
        mm.setMessage(0xC0, 0x00, 0x00)
        me = MidiEvent(mm, 0.toLong())
        t.add(me)

        for (note in notes) {
            val noteName = note[0]
            val octave : Int = note[1].digitToInt()

            val noteIndex : Int = NOTE_NAMES.indexOf(noteName.toString())
            val newOctave = (octave + 1) * 12
            val key = noteIndex + newOctave

            mm = ShortMessage()
            mm.setMessage(NOTE_ON, key, 0x60)
            me = MidiEvent(mm, 1.toLong())
            t.add(me)
        }

        //****  set end of track (meta event) 19 ticks later  ****
        mt = MetaMessage()
        val bet = byteArrayOf() // empty array
        mt.setMessage(0x2F, bet, 0)
        me = MidiEvent(mt, 140.toLong())
        t.add(me)

        val f = File("newMidiFile.mid")
        MidiSystem.write(s, 1, f)
    } catch (e: Exception) {
        println("Exception caught $e")
    }
}

fun main() {
    val string = "3 + 43"
    val notes : List<String> = getNotes(string)
    println(notes)
}

