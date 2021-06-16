import java.io.*;
import javax.sound.midi.*;

public class Test {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public static void main(String[] args) throws Exception {
        Sequence sequence = MidiSystem.getSequence(new File("D:\\projects\\midicompiler\\src\\Keys.mid"));

        int trackNumber = 0;
        for (Track track : sequence.getTracks()) {
            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();

            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();

                if (message instanceof ShortMessage) {

                    ShortMessage sm = (ShortMessage) message;
                    if (sm.getCommand() == NOTE_ON && sm.getData2() != 0) {
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        System.out.println("Note on, " + noteName + octave + " key=" + key);
                    } else if (sm.getCommand() == NOTE_OFF && sm.getData2() != 0) {
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        System.out.println("Note: " + noteName + octave + " key=" + key);
                    }
                }
            }

            System.out.println();
        }

    }
}