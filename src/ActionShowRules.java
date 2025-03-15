package JavaCheckers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * Displays the U.S. official Checkers rules (at least, all that are
 * applicable to a computer game) in a separate JFrame (window).
 * The help text is loaded from an HTML file in the /images folder
 * to a JScrollPane.
 */
public class ActionShowRules implements ActionListener {
    /**
     * CheckersGame instance from which the button action originated.
     */
    CheckersGame game;

    /**
     * Instantiate a New Game button action for a CheckersGame instance.
     *
     * @param checkersGame CheckersGame instance from which the button action originated.
     */
    ActionShowRules(CheckersGame checkersGame) {
        game = checkersGame;
    }

    /**
     * Main action handler that processes the button event. Opens
     * a pop-up window that displays the official game rules in
     * a scrollable pane.
     *
     * @param evt the event to be processed
     */
    public void actionPerformed(ActionEvent evt)
    {
        JTextPane tp = new JTextPane();
        JScrollPane js = new JScrollPane();
        js.getViewport().add(tp);

        JFrame jf = new JFrame();
        jf.getContentPane().add(js);
        jf.pack();
        jf.setSize(400,500);

        tp.setContentType("text/plain");
        tp.setText(getTextPaneContent());

        jf.setVisible(true);
        game.rootPanel.paintImmediately(0, 0, 600, 600);
    }

    /**
     * Reads the official rules text content from the
     * resource file and handles loading errors.
     *
     * @return String with full official game rules content.
     */
    private String getTextPaneContent() {
        String content;

        try {
            content = new String(
                Objects.requireNonNull(
                    getClass().getResourceAsStream("/checkersRules.txt")
                ).readAllBytes()
            );
        } catch (Exception e) {
            content = "Error Loading checkersRules.txt";
            e.printStackTrace();
        }

        return content;
    }
}
