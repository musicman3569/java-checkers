package JavaCheckers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * "Let Computer Move First" button handler.  Allows the user to forfeit
 * the first move to the computer (so that the user doesn't have to go
 * first all the time).
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
     * Main action handler that processes the button event. Skips the waiting
     * for the user's first move, preforms the Computer's move, and then
     * returns to waiting for the player.
     *
     * @param evt the event to be processed
     */
    public void actionPerformed(ActionEvent evt)
    {
        game.changeStatus("Computer moving first. Please wait . . .");
        game.doEnemyMove();
        game.btnComputerFirst.setVisible(false);
        game.rootPanel.paintImmediately(0, 0, 600, 600);
        game.changeStatus("Select a piece to move.");
    }
}
