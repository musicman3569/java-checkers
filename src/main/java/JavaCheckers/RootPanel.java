package JavaCheckers;

import javax.swing.*;
import java.awt.*;

/**
 * The RootPanel acts as the main content pane for all other panels.  It
 * must be border layout, and must be 600x600px.
 *
 * This is better than using the root JFrame's getContentPane(), because
 * the JPanel superclass automatically provides double-buffering to eliminate
 * flicker and the paintImmediately() method to do order-safe calls.
 */
public class RootPanel
        extends JPanel
{
    /**
     * CheckersGame instance that will be displayed on the panel.
     */
    CheckersGame game;

    /**
     * Constructs a new RootPanel object, passing the BorderLayout argument
     * b to the superclass constructor.
     *
     * @param b a BorderLayout object to be passed to the super constructor
     */
    RootPanel(BorderLayout b, CheckersGame currGame)
    {
        super(b);
        game = currGame;
    }

    /**
     * Draw the game background image and name labels.
     *
     * @param g the graphics object of the current RootPanel instance
     */
    public void paintComponent(Graphics g)
    {
        int xOffset;         // used to center text

        // draw game board, buttons, and titles
        g.drawImage(game.img_background, 0, 0, this);

        // draw pieces, names, and stats if a game is started
        if (game.gameInProgress)
        {
            // Center the names of both players on the screen
            g.setColor(Color.WHITE);
            g.setFont(game.labelFont);
            xOffset = 300 - (g.getFontMetrics().stringWidth(game.playerName) / 2);
            g.drawString(game.playerName, xOffset, 530);
            xOffset = 300 - (g.getFontMetrics().stringWidth(game.compName) / 2);
            g.drawString(game.compName, xOffset, 85);
        }
    }
}