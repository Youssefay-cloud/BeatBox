Cyber BeatBox : 
A Java-based MIDI drum machine that allows users to create, play, and adjust real-time percussion loops. 
This project demonstrates the use of the Java Sound API (javax.sound.midi) and Swing GUI components.

Features
16-Instrument Grid: A 16x16 grid of checkboxes representing 16 drum instruments over 16 "ticks" or beats.

Real-time MIDI Sequencing: Uses the Java Sequencer to play back patterns with low latency.

Tempo Control: Dynamic tempo adjustment (Speed up/Slow down) without stopping the music.

Continuous Looping: Automatic looping for seamless beat creation.

 How it Works
The core of the application translates user input (checkboxes) into MIDI events:

1.The Grid: Each row represents a specific MIDI instrument key (e.g., Bass Drum = 35, Closed Hi-Hat = 42).

2.The Event: When "Start" is clicked, the program iterates through the grid. If a box is checked, it creates a NOTE_ON event and a corresponding NOTE_OFF event at that specific "tick."

3.The Channel: Percussion is sent through Channel 9, which is the dedicated MIDI channel for drums.