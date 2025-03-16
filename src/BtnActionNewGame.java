package JavaCheckers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * "New Game" button action.  Asks for the player's name if it's the first
 * time, and for the difficulty level of the AI.  Resets all pieces
 * to their starting positions. Also calls setGameSpeed to determine how
 * fast the computer is and adjust gameSpeed accordingly.
 */
public class BtnActionNewGame implements ActionListener {
    /**
     * CheckersGame instance from which the button action originated.
     */
    CheckersGame game;

    /**
     * Instantiate a New Game button action for a CheckersGame instance.
     *
     * @param checkersGame CheckersGame instance from which the button action originated.
     */
    BtnActionNewGame(CheckersGame checkersGame) {
        game = checkersGame;
    }

    /**
     * Main action handler that processes the button event. Displays
     * the username prompt dialog and initializes the game.
     *
     * @param evt the event to be processed
     */
    public void actionPerformed(ActionEvent evt)
    {

        if (game.playerName == null)
        {
            game.changeStatus("Waiting for user's name to be entered...");
            String title = "Create New Game";
            String message = "Please enter your name:";
            game.playerName = JOptionPane.showInputDialog(null, message, title, 1);
        }

        // set default name if nothing is entered
        if (game.playerName.isBlank())
        {
            game.playerName = "Player 1";
        }

        game.changeStatus("Waiting for user to select difficulty...");
        game.currGame.newGame();

        game.changeStatus("Creating new game. Please wait . . .");
        game.rootPanel.paintImmediately(0, 0, 600, 600);
        game.gameInProgress = true;
        game.legalMoves = game.currGame.getLegalMoves(GameBoard.PLAYER);
        game.selectedRow = -1;    // indicate that no piece is selected yet
        game.middlePanel.getPiecePositions();
        game.rootPanel.paintImmediately(0, 0, 600, 600);
        game.middlePanel.setGameSpeed();
        game.btnComputerFirst.setVisible(true);
        game.changeStatus("Select a piece to move   OR   'Let Computer Move First'");
    }
}
