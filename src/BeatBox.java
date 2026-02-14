import java.util.*;
import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static javax.sound.midi.ShortMessage.*;

public class BeatBox {
    // The place to store the check boxes.
    private ArrayList<JCheckBox> checkboxList ;
    private Sequencer sequencer ;
    private Sequence sequence;
    private Track track ;

    
    // The name of the instruments as an array.
    String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat",
        "Open Hi-Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap",
        "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga",
        "Cowbell", "Vibraslap", "Low-mid Tom", "High Agogo",
        "Open Hi Conga"};

        // The key for each instruments
        int[] instruments =  {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};
        
    public static void main(String[] args) {
        // The start
        new BeatBox().buildGUI();
    }

    private void buildGUI() {
        // The Actual frame that will display with its name
        JFrame frame = new JFrame("Cyber BeatBox");
        // So we can close it without runs for ever
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // The Border we will work on so we can add components on it 
        BorderLayout layout = new BorderLayout();
        // The panel background of the frame with the border on it 
        JPanel background = new JPanel(layout);
        // Empty border gives us margin between the edges of the panel.
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        // Button to start
        JButton start = new JButton("Start");
        start.addActionListener(event -> buildTrackAndStart());
        buttonBox.add(start);

        // Button to stop
        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> sequencer.stop());
        buttonBox.add(stop);
        
        // Button to speed-up
        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(e -> changeTempo(1.03f));     
        buttonBox.add(upTempo);

        // Button to speed-down
        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(e -> changeTempo(0.97f));
        buttonBox.add(downTempo);

        // Button to serialize the patten beat
        JButton serializeIt = new JButton("Serialize Pattern");
        serializeIt.addActionListener(e -> writeFile());
        buttonBox.add(serializeIt);

        JButton readIt = new JButton("Open File");
        readIt.addActionListener(e -> readFile());
        buttonBox.add(readIt);

        // Add names for the Boxes 
        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for(String instrumentsName : instrumentNames){
            JLabel instrumentLabel = new JLabel(instrumentsName);
            // Border on each instrument helps them to line up with the check Boxes
            instrumentLabel.setBorder(BorderFactory.createEmptyBorder(4,1,4,1));
            nameBox.add(instrumentLabel);
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        frame.getContentPane().add(background);

        // Layout manager to grid up the components
        GridLayout grid = new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);

        JPanel mainpanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainpanel);

        // Set the check boxes for false and add them to the arraylist and to GUI panel.
        checkboxList = new ArrayList<>();
        for(int i = 0 ; i < 256 ; i++){
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainpanel.add(c);
        }

        setUpMidi();

        frame.setBounds(50, 50, 300, 300);
        frame.pack();
        frame.setVisible(true);
    }


    // Only to setup sequencer , sequence and track
    private void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void buildTrackAndStart() {
        int[] TrackList ;

        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for(int i = 0 ; i < 16 ; i++){
            TrackList = new int[16];

            int key = instruments[i];

            for(int j = 0 ; j < 16 ; j++){
                JCheckBox jc = checkboxList.get(j + 16 * i);
                if(jc.isSelected()){
                    TrackList[j] = key;
                }else{
                    TrackList[j] = 0 ;
                }
            }

            makeTracks(TrackList);
            track.add(makeEvent(CONTROL_CHANGE, 1, 127, 0, 16));
        }
            track.add(makeEvent(PROGRAM_CHANGE,9,1,0,15));

            try {
                sequencer.setSequence(sequence);
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
                sequencer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        
    }

    private void changeTempo(float tempoMultiplier) {
        float tempFactor = sequencer.getTempoFactor();
        sequencer.setTempoFactor(tempFactor * tempoMultiplier);
    }

    private void makeTracks(int[] list) {
        for(int i = 0 ; i < 16 ; i++){
            int key = list[i];

            if (key != 0) {
                track.add(makeEvent(NOTE_ON, 9, key,100 , i));
                track.add(makeEvent(NOTE_OFF, 9, key,100 , i + 1));

            }
        }
    }

    public static MidiEvent makeEvent(int cmd, int chn1, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage msg = new ShortMessage();
            msg.setMessage(cmd, chn1, one, two);
            event = new MidiEvent(msg, tick);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }

    private void writeFile(){
        boolean[] checkboxState = new boolean[256];

        for(int i = 0 ; i < 256 ; i++){
            JCheckBox check = checkboxList.get(i);
            if(check.isSelected()){
                checkboxState[i] = true ;
            }
        }

        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("CheckBox.ser"))) {
            os.writeObject(checkboxState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFile(){
        boolean[] checkboxState = null;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream("CheckBox.ser"))) {
            checkboxState = (boolean[]) is.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0 ; i < 256 ; i++){
            JCheckBox check = checkboxList.get(i);
            check.setSelected(checkboxState[i]);
        }

        sequencer.stop();
        buildTrackAndStart();
    }

   
    
}
