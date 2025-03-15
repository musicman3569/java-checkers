package JavaCheckers;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;

/**
 * The GameBoardPanel is responsible for displaying and animating the
 * active game.  It is the 400x400px gameboard in the center of the applet.
 * This size must be used for it to work correctly.
 *
 * <p> It also must be the inner class of the CheckersGame class, as it uses
 * logic variables from the CheckersGame class to control animation and
 * sprite locations.
 */
class GameBoardPanel extends JPanel
{
    /**
     * CheckersGame instance that will be displayed on the panel.
     */
    CheckersGame game;

    /**
     * Target time in milliseconds it should take for a piece to animate
     * during a single move.  Used to calculate pixel move size based
     * on machine speed.
     */
    static final double TARGET_SPEED = 300;

    /**
     * A cast from the paintComponent graphics object.  Used for speed
     * efficiency by avoiding calling the new operator each animation cycle
     */
    Graphics2D g2d;

    /**
     * The source/target location of the piece being animated
     */
    CheckersMove animMove;

    /**
     * A temporary array of all the pieces currently on the board and their
     * locations.  Used for efficiency during animations to avoid having
     * to scan the entire board.
     */
    ArrayList<GamePiece> pieces;

    /**
     * Index of the piece being animated in the ArrayList pieces
     */
    int animIndex;

    /**
     * Animation speed multiplier to adjust for speed of different machines.
     */
    double gameSpeed;

    GameBoardPanel(CheckersGame currentGame) {
        game = currentGame;
    }

    /**
     * Runs a test animation of 70 frames to clock how fast the computer
     * can complete it.  The first 10 frames are to "warm the system up" so
     * everything is in memory and ready to go.
     *
     * <p> This is used to set the gameSpeed multiplier so that
     * different speed computers can run the animation sequences at
     * similar speeds.
     */
    public void setGameSpeed()
    {
        long runTime;
        long startTime = 0;

        for (int i = 0; i < 70; i++)
        {
            if (i == 10)
            {
                startTime = System.currentTimeMillis();
            }

            paintImmediately(0, 0, 400, 400);
        }

        runTime = System.currentTimeMillis() - startTime;
        gameSpeed = runTime / TARGET_SPEED;

        //
        // Make sure the gameSpeed is within one move size (50) and above 0.
        // A value of 0 would create an infinite loop, and a value > 50
        // would move the pieces too far.  50 means no in between animation;
        // pieces just appear at their destinations immediately.
        //
        if (gameSpeed < .001)
        {
            gameSpeed = .001;
        }
        if (gameSpeed > 50)
        {
            gameSpeed = 50;
        }

        System.out.println("Run Time: " + runTime);
        System.out.println("GameSpeed: " + gameSpeed);
    }

    /**
     * Repaints the game board panel. Called by animateMove or other
     * methods, using the paintImmediately() method call (which in turn
     * calls this) to ensure the repaint is done in the same order of
     * execution (otherwise, animation errors can occur from bad
     * synchronization).
     *
     * @param g the graphics object for the GameBoardPanel
     */
    public void paintComponent(Graphics g)
    {
        g.drawImage(game.img_board, 0, 0, this);

        if (game.gameInProgress)
        {
            g.setColor(Color.BLUE);

            // draw highlight if a game space is currently selected
            if (game.selectedRow >= 0)
            {
                g.fillRect(50 * game.selectedCol, 50 * game.selectedRow, 50, 50);
            }

            //
            // Highlight legal selection options if enabled, and an animation
            // is not in progress (aka. waiting for user input)
            //
            if (game.legalMoves != null && game.waitingForInput && game.showHighlight)
            {
                if (game.selectedRow >= 0)
                {
                    highlightMoveSquares(g);
                }

                // highlight pieces that can be moved
                for (CheckersMove m : game.legalMoves)
                {
                    g.drawRect(m.fromCol * 50, m.fromRow * 50, 49, 49);
                }
            }
            else if (game.img_jumpPiece != null && !game.waitingForInput)
            {
                // smoothly fade out the piece being jumped
                fadeJumpedPiece(g);
            }

            // paint all the game piece sprites
            drawBoardPieces(g);
        }
    }

    /**
     * Called by paintComponent to smoothly fade out a piece being jumped.
     * It is already determined which piece and where (using img_jumpPiece
     * and jumpedCol/jumpedRow).  Scales the opacity down as the jumping
     * piece passes over it, with the fade duration matching the duration
     * of the jump animation.
     *
     * @param g graphics object passed from paintComponent
     */
    public void fadeJumpedPiece(Graphics g)
    {
        g2d = (Graphics2D) g;
        float[] newRGBA = {1f, 1f, 1f, game.jumpOpacity};
        RescaleOp newOpacity = new RescaleOp(newRGBA, new float[4], null);

        g2d.drawImage(game.img_jumpPiece, newOpacity,
                game.jumpedCol * 50, game.jumpedRow * 50);
    }

    /**
     * Called by paintComponent to highlight legal moves available to the
     * currently selected piece.  A piece has already been selected that
     * has legal moves.  It is also already determined that the
     * highlightMove button is set to ON.
     *
     * @param g graphics object passed from paintComponent
     */
    public void highlightMoveSquares(Graphics g)
    {
        g.setColor(new Color(0, 153, 255));

        CheckersMove[] highlightMoves =
                game.currGame.getLegalMoves(game.selectedRow, game.selectedCol);

        for (CheckersMove m : highlightMoves)
        {
            g.drawRect(m.toCol * 50, m.toRow * 50, 49, 49);
        }

        g.setColor(Color.BLUE);
    }

    /**
     * Called by paintComponent to draw all the game piece sprites
     * in their current locations.  Calls getPieceImage to check
     * each piece's type.
     *
     * @param g graphics object passed from paintComponent
     */
    public void drawBoardPieces(Graphics g)
    {
        // draw game pieces on the board, uses king/single image accordingly
        for (GamePiece p : pieces)
        {
            g.drawImage(getPieceImage(p.type), p.getX(), p.getY(), this);
        }
    }

    /**
     * Called by animateMove to scan the board for all pieces and determine
     * their types and positions.  This is stored in the member variable
     * ArrayList pieces for efficiency during animation (to prevent the entire
     * board from having to be scanned each frame).  Piece positions are read
     * from the GameBoard object currGame.
     */
    public void getPiecePositions()
    {
        int x;
        int y;
        pieces = new ArrayList<GamePiece>();

        // draw game pieces on the board, uses king/single image accordingly
        for (int row = 0; row < 8; row++)
        {
            for (int col = row % 2; col < 8; col += 2)
            {
                if (row == animMove.toRow && col == animMove.toCol)
                {
                    y = animMove.fromRow * 50;
                    x = animMove.fromCol * 50;
                    animIndex = pieces.size();
                }
                else
                {
                    y = row * 50;
                    x = col * 50;
                }

                if (game.currGame.pieceAt(row, col) != GameBoard.EMPTY)
                {
                    pieces.add(new GamePiece(x, y, game.currGame.pieceAt(row, col)));
                }
            }
        }

    }

    /**
     * Resets member variable animMove, which is responsible for storing
     * the location of the piece being moved.  Called by animateMove
     * after the animation is complete to reset it to an inactive value.
     * (Inactive is defined as fromRow == toRow && fromCol == toCol).
     * Row 1, col 0 is used since this is not a playable game square.
     */
    public void resetAnimMove()
    {
        animMove = new CheckersMove(1, 0, 1, 0);
    }


    /**
     * Take a game piece type (as defined in the GameBoard constants) and
     * return a corresponding image object (game sprite).  Returns null
     * if a match is not found.
     *
     * @param pieceType constant from GameBoard for piece type
     * @return a game piece Image object corresponding to pieceType
     */
    public Image getPieceImage(int pieceType)
    {
        Image pieceImage = null;

        switch (pieceType)
        {
            case GameBoard.ENEMY:
                pieceImage = game.img_redPiece;
                break;
            case GameBoard.ENEMY_KING:
                pieceImage = game.img_redKing;
                break;
            case GameBoard.PLAYER:
                pieceImage = game.img_blackPiece;
                break;
            case GameBoard.PLAYER_KING:
                pieceImage = game.img_blackKing;
                break;
        }

        return pieceImage;
    }

    /**
     * Animates a game piece between the fromRow/fromCol and toRow/toCol
     * provided by move.  Calculates the pixel interval to move the sprite
     * based on the gameSpeed determined by setGameSpeed().  Calls
     * middlePanel.paintImmediately() to guarantee each successive frame
     * is drawn (and not collapsed to one paint call) in the proper execution
     * order.
     *
     * @param move contains source and target locations for the moved piece
     */
    public void animateMove(CheckersMove move)
    {
        // configure animation origin, destination, and direction
        animMove = new CheckersMove(move.fromRow, move.fromCol,
                move.toRow, move.toCol);
        int xMoveSize = animMove.toCol - animMove.fromCol;
        int yMoveSize = animMove.toRow - animMove.fromRow;
        int moveSize = Math.abs(xMoveSize);
        double moveBound;

        // prepare jumped piece to be faded if needed
        if (move.isJump())
        {
            game.jumpedCol = (move.fromCol + move.toCol) / 2;
            game.jumpedRow = (move.fromRow + move.toRow) / 2;
            game.img_jumpPiece = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            game.img_jumpPiece.getGraphics().drawImage(
                    getPieceImage(game.currGame.pieceAt(game.jumpedRow, game.jumpedCol)),
                    0, 0, null);
            game.jumpOpacity = 1;
        }

        game.currGame.makeMove(move);
        game.waitingForInput = false;
        getPiecePositions();

        // run animation loop
        moveBound = moveSize * 50 - moveSize * gameSpeed;
        for (double i = 0; i < moveBound; i += moveSize * gameSpeed)
        {
            pieces.get(animIndex).x += xMoveSize * gameSpeed;
            pieces.get(animIndex).y += yMoveSize * gameSpeed;
            game.middlePanel.paintImmediately(0, 0, 400, 400);

            if (game.img_jumpPiece != null)
            {
                game.jumpOpacity -= .02 * gameSpeed;
            }
        }

        // reset animation variables
        game.img_jumpPiece = null;
        resetAnimMove();
        getPiecePositions();
        game.middlePanel.paintImmediately(0, 0, 400, 400);
    }
}