import java.io.File
import javax.sound.midi.*;

const val NOTE_ON = 0x90;
const val NOTE_OFF = 0x80;
val NOTE_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

fun main(){
    val sequence : Sequence = MidiSystem.getSequence(File("src/Keys.mid"))

    var trackNumber = 0;
    for(track: Track in sequence.tracks){
        trackNumber++;
        println("Track" + trackNumber + ": size = " + track.size() )
        println()
        for( i in 0 until track.size() - 1){
            val event = track.get(i)
            val message = event.message

            if ( message is ShortMessage){
                val sm : ShortMessage = message
                if( sm.command == NOTE_ON && sm.data2 != 0){
                    val key = sm.data1
                    val octave = ( key / 12 ) -1
                    val note = key % 12;
                    val noteName = NOTE_NAMES[note]
                    println("Note on, $noteName$octave key=$key")
                }
                else if(sm.command == NOTE_OFF && sm.data2 != 0){
                    val key = sm.data1
                    val octave = ( key / 12 ) -1
                    val note = key % 12;
                    val noteName = NOTE_NAMES[note]
                    println("Note off, $noteName$octave key=$key")
                }
            }
        }
        println()

    }
}