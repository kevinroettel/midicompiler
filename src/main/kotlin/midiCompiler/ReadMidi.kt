package midiCompiler

import java.io.File
import javax.sound.midi.*

const val NOTE_ON = 0x90
const val NOTE_OFF = 0x80
val NOTE_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

fun readMidi(pathName : String): MutableList<String> {
    val sequence: Sequence = MidiSystem.getSequence(File(pathName))

    val notes = mutableListOf<String>()

    for (track: Track in sequence.tracks) {
        for (i in 0 until track.size() - 1) {
            val event = track.get(i)
            val message = event.message

            if (message is ShortMessage) {
                val sm: ShortMessage = message
                if (sm.command == NOTE_ON && sm.data2 != 0) {
                    val key = sm.data1
                    val octave = (key / 12) - 1
                    val note = key % 12
                    val noteName = NOTE_NAMES[note]
                    notes.add("$noteName$octave")
                } else if (sm.command == NOTE_OFF && sm.data2 != 0) {
                    val key = sm.data1
                    val octave = (key / 12) - 1
                    val note = key % 12
                    val noteName = NOTE_NAMES[note]
                    notes.add("$noteName$octave")
                }
            }
        }
    }

    return notes
}