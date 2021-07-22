package midiCompiler

import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage
import java.lang.Exception

class MidiPlayer (){
    private var data : MutableList<Int> = mutableListOf()

    fun setData( newData :MutableList<Int>){
        data = newData
    }

    constructor(dataString : MutableList<String>) : this(){
        setDataFromMutableListOfString(dataString)
    }

    fun playMidi() {
        val synth = MidiSystem.getSynthesizer()
        synth.open()
        val rcvr = synth.receiver
        val msg = ShortMessage()
        for (i in data.indices) {
            msg.setMessage(ShortMessage.NOTE_ON, 0, data[i], 64)
            rcvr.send(msg, -1)
            try {
                Thread.sleep((1 * 400).toLong())
            } catch (e: Exception) {}
            msg.setMessage(ShortMessage.NOTE_OFF, 0, data[i], 0)
            rcvr.send(msg, -1)
        }
        synth.close()
    }

    fun setDataFromMutableListOfString(dataString : MutableList<String>){
        val newData = mutableListOf<Int>()
        for ( note in dataString) {
            val noteName = note[0]
            val octave: Int = note[1].digitToInt()

            val noteIndex: Int = midiEncoder.NOTE_NAMES.indexOf(noteName.toString())
            val newOctave = (octave + 1) * 12
            val key = noteIndex + newOctave
            newData.add(key)
        }
        setData(newData)
    }
}
