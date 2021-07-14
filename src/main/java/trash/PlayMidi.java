package trash;

import javax.sound.midi.*;

public class PlayMidi {
    private static void playAlleMeineEntchen() throws Exception {
        //Partitur {{Tonhoehe, DauerInViertelNoten, AnzahlWdh},...}
        final int DATA[][] = {
            { 60, 1, 1 }, //C
            { 62, 1, 1 }, //D
            { 64, 1, 1 }, //E
            { 65, 1, 1 }, //F
            { 67, 2, 2 }, //G,G
            { 69, 1, 4 }, //A,A,A,A
            { 67, 4, 1 }, //G
            { 69, 1, 4 }, //A,A,A,A
            { 67, 4, 1 }, //G
            { 65, 1, 4 }, //F,F,F,F
            { 64, 2, 2 }, //E,E
            { 62, 1, 4 }, //D,D,D,D
            { 60, 4, 1 }  //C
        };

        Synthesizer synth = MidiSystem.getSynthesizer();
        synth.open();
        Receiver rcvr = synth.getReceiver();

        ShortMessage msg = new ShortMessage();
        for (int i = 0; i < DATA.length; ++i) {
            for (int j = 0; j < DATA[i][2]; ++j) {
                msg.setMessage(ShortMessage.NOTE_ON, 0, DATA[i][0], 64);
                rcvr.send(msg, -1);
                try {
                    Thread.sleep(DATA[i][1] * 400);
                } catch (Exception e) {

                }

                msg.setMessage(ShortMessage.NOTE_OFF, 0, DATA[i][0], 0);
                rcvr.send(msg, -1);
            }
        }
        synth.close();
    }

    public static void main(String[] args) {
        try {
            playAlleMeineEntchen();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
