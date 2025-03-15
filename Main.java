import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        CheckersGame game = new CheckersGame();
        try {
            game.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}