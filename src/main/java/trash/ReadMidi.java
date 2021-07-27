package trash;

import java.io.*;
import java.util.Arrays;
import javax.sound.midi.*;

public class ReadMidi {
    private static void playMidiFile(String name) throws Exception {
        // Sequencer und Synthesizer initialisieren
        Sequencer sequencer = MidiSystem.getSequencer();
        Transmitter trans = sequencer.getTransmitter();
        Synthesizer synth = MidiSystem.getSynthesizer();
        Receiver rcvr = synth.getReceiver();

        // Beide öffnen und verbinden
        sequencer.open();
        synth.open();
        trans.setReceiver(rcvr);

        // Sequence lesen und abspielen
        Sequence seq = MidiSystem.getSequence(new File(name));
        sequencer.setSequence(seq);
        sequencer.setTempoInBPM(145);
        sequencer.start();
        while (true) {
            try {
                Thread.sleep(100);

            } catch (Exception e) {
                //nothing
            }
            if (!sequencer.isRunning()) {
                break;
            }
        }

        // Sequencer anhalten und Geräte schließen
        sequencer.stop();
        sequencer.close();
        synth.close();
    }

    public static void main(String[] args) {
        String fileName = "D:\\projects\\midicompiler\\src\\AlleMeineShits.mid";

        try {
            playMidiFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
