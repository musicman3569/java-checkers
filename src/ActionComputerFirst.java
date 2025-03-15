package JavaCheckers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * "Highlight Moves ON/OFF" button handler.  Toggles whether legal moves
 * and move locations are automatically highlighted on screen.
 */
public class ActionComputerFirst implements ActionListener {
    /**
     * CheckersGame instance from which the button action originated.
     */
    CheckersGame game;

    /**
     * Instantiate a Highlight Moves button action for a CheckersGame instance.
     *
     * @param checkersGame CheckersGame instance from which the button action originated.
     */
    ActionComputerFirst(CheckersGame checkersGame) {
        game = checkersGame;
    }

    /**
     * Main action handler that processes the button event. Changes
     * the button text to match the new state and updates
     * the game root panel.
     *
     * @param evt the event to be processed
     */
    public void actionPerformed(ActionEvent evt)
    {
        game.showHighlight = !game.showHighlight;

        if (game.showHighlight) {
            game.btnShowHighlight.setText("Highlight Moves ON ");
        } else {
            game.btnShowHighlight.setText("Highlight Moves OFF");
        }

        game.rootPanel.paintImmediately(0, 0, 600, 600);
    }
}
