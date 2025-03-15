package JavaCheckers;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import java.io.File;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.event.*;

/**
 * CheckersGame is a graphical animated checkers game swing applet.  A player
 * is asked their name, given a difficulty level (for the opposing AI), and
 * presented with a game board.  
 *
 * <p> Black goes first (per official U.S. rules) by having the user click a 
 * valid piece to move.  Only pieces with valid moves are selectable, and only
 * valid moves are allowed. Then a valid move location must be clicked.
 * The computer then moves (if possible) until one of the players has no more 
 * moves left, at which point a winner is declared and the game ends.
 *
 * <p> Other features include automatic move highlighting, and adjusted speed
 * game animations (based on individual computer speeds).
 *
 * @version 2.0, 02/11/09
 * @author Jason Hamilton
 */
public class CheckersGame implements MouseListener
{
    /**
     * Base directory of the Java application where Main resides.
     */
    String basePath;

    /**
     * Sprite image for red game piece
     */
    Image img_redPiece;

    /**
     * Sprite image for red king game piece
     */
    Image img_redKing;

    /**
     * Sprite image for black game piece
     */
    Image img_blackPiece;

    /**
     * Sprite image for black king game piece
     */
    Image img_blackKing;

    /**
     * Background image for applet
     */
    Image img_background;

    /**
     * Sprite image of the game board.  Squares are 50x50px, total size must be
     * 400x400px.
     */
    Image img_board;

    /**
     * Temp image to smoothly faded out pieces as they are jumped
     */
    BufferedImage img_jumpPiece;

    /**
     * Main window frame for the application
     */
    JFrame jframe;

    /**
     * Button to initialize a new game. Calls GameBoard.newGame().
     */
    JButton btnNewGame;

    /**
     * Button to enable/disable legal move highlighting for the user.
     */
    JButton btnShowHighlight;

    /**
     * Allows the user to forfeit moving first to the computer.
     */
    JButton btnComputerFirst;

    /**
     * Pops up a dialog with the official U.S. checkers rules
     */
    JButton btnShowRules;

    /**
     * Base panel to place everything else on.  Allows for double-buffering.
     */
    RootPanel rootPanel;

    /**
     * North panel of the RootPanel.  Has game option buttons.
     */
    JPanel topPanel;

    /**
     * South panel of the RootPanel.  Has game status messages.
     */
    JPanel bottomPanel;

    /**
     * Left panel of the RootPanel.  Currently just for spacing.
     */
    JPanel leftPanel;

    /**
     * Right panel of the RootPanel.  Currently just for spacing.
     */
    JPanel rightPanel;

    /**
     * Center panel of the RootPanel.  Has the actual gameboard and piece images.
     */
    GameBoardPanel middlePanel;

    /**
     * Displays status messages to the user on the bottom panel.
     */
    JLabel gameStatus;

    /**
     * Stores the players name as entered. Is "Player 1" by default.
     */
    String playerName;

    /**
     * Stores the name of the second (computer) player.  "Computer" by default.
     */
    String compName;

    /**
     * Set to true to enable legal move highlighting for the user, false=disabled
     */
    boolean showHighlight = true;

    /**
     * Used to ensure routines don't hit bad/null values if a game is not
     * initialized.  True if a game is initialized, false if not.
     */
    boolean gameInProgress = false;

    /**
     * Track whether the program is waiting for the user's input.  If false
     * legal move highlights are turned off (so as not to display during
     * animations).
     */
    boolean waitingForInput = false;

    /**
     * Logic backend for the current game. Controls piece locations and enforces
     * game rules.  Also handles AI.
     */
    GameBoard currGame;

    /**
     * Font for displaying messages on screen.
     */
    Font labelFont;

    /**
     * Row currently selected by the user.  Set to -1 if none is selected.
     */
    int selectedRow;

    /**
     * Col currently selected by the user.
     */
    int selectedCol;

    /**
     * Row of the piece being jumped (if any)
     */
    int jumpedRow;

    /**
     * Col of the piece being jumped (if any)
     */
    int jumpedCol;

    /**
     * Stores the current level of opacity for the piece being jumped.
     */
    float jumpOpacity;

    /**
     * Stores all currently available legal moves for either player
     */
    CheckersMove[] legalMoves;

    /**
     * Initializes the class member variables to prepare the applet for screen
     * display.  Calls methods to configure/add buttons and panels, set up
     * event listeners for the mouse, and then display the finished applet.
     */
    public void init() throws IOException {
        jframe = new JFrame("Java Checkers");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.getContentPane().setBackground(Color.gray);
        jframe.getContentPane().resize(600, 600);

        File f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        basePath = f.getParent();

        // initialize instance variables
        currGame = new GameBoard();
        playerName = null;
        compName = "Computer";
        rootPanel = new RootPanel(new BorderLayout(), this);
        topPanel = new JPanel(new FlowLayout());
        bottomPanel = new JPanel(new BorderLayout());
        rightPanel = new JPanel();
        leftPanel = new JPanel();
        middlePanel = new GameBoardPanel(this);
        btnNewGame = new JButton("New Game");
        btnShowHighlight = new JButton("Highlight Moves ON");
        btnComputerFirst = new JButton("Let Computer Move First");
        btnShowRules = new JButton("Show Rules");
        labelFont = new Font("Sans", Font.BOLD, 20);
        gameStatus = new JLabel("Click 'New Game' to start a new game.",
                JLabel.CENTER);
        gameStatus.setForeground(Color.WHITE);
        gameStatus.setBackground(Color.DARK_GRAY);
        gameStatus.setPreferredSize(new Dimension(600, 25));
        gameStatus.setOpaque(true);
        middlePanel.resetAnimMove();

        // used to track clicks on game squares
        jframe.getContentPane().addMouseListener(this);

        loadImages();
        initializeButtons();
        initializePanels();

        waitingForInput = true;      // indicate it's the user's turn
        rootPanel.paintImmediately(0, 0, 600, 600);
        jframe.pack();
        jframe.setVisible(true);
    }

    /**
     * Loads game images and sprites from the images folder, which should
     * always be located in the code base folder of the applet.
     */
    public void loadImages() {
        img_redPiece = getImage("red.png");
        img_redKing = getImage("redKing.png");
        img_blackPiece = getImage("black.png");
        img_blackKing = getImage("blackKing.png");
        img_background = getImage("background.png");
        img_board = getImage("board.png");
    }

    /**
     * Loads an image from the /resources folder into an Image instance
     * and handles any errors.
     *
     * @param filename Name of the resource image file to load.
     * @return File object containing the loaded image file data.
     */
    private Image getImage(String filename) {
        File imageFile;

        try {
            imageFile = new File(
                getClass().getResource("/" + filename).getPath()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Image image;

        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return image;
    }

    /**
     * Configures behavior for mouse clicks on the game buttons, including
     * adding mouse action listeners.
     */
    public void initializeButtons()
    {
        // "New Game" button handler.
        btnNewGame.addActionListener(new ActionNewGame(this));

        // "Highlight Moves ON/OFF" button handler.
        btnShowHighlight.addActionListener(new ActionShowHighlight(this));

        // "Show Rules" button handler.
        btnShowRules.addActionListener(new ActionShowRules(this));

        // "Let Computer Move First" button handler.
        btnComputerFirst.addActionListener(new ActionComputerFirst(this));
    }

    /**
     * Set default size for the panels, and set the opacities to false so that
     * they don't draw over the background image.  The game board is 400x400px
     * with a 100px wide border all the way around (600x600px total). The
     * calling HTML page should always configure the applet to this size,
     * as the game is fixed in resolution.
     */
    public void initializePanels()
    {
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new Dimension(600, 100));
        topPanel.add(btnNewGame);
        topPanel.add(btnShowHighlight);
        topPanel.add(btnComputerFirst);
        topPanel.add(btnShowRules);
        btnComputerFirst.setVisible(false);

        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(600, 25));
        bottomPanel.add(BorderLayout.CENTER, gameStatus);

        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(100, 400));
        leftPanel.setPreferredSize(new Dimension(100, 400));
        leftPanel.setOpaque(false);

        middlePanel.setOpaque(false);
        middlePanel.setPreferredSize(new Dimension(400, 400));

        // rootPanel serves as the background and holds all other panels
        rootPanel.setPreferredSize(new Dimension(600, 600));
        rootPanel.add(BorderLayout.NORTH, topPanel);
        rootPanel.add(BorderLayout.SOUTH, bottomPanel);
        rootPanel.add(BorderLayout.CENTER, middlePanel);
        rootPanel.add(BorderLayout.EAST, leftPanel);
        rootPanel.add(BorderLayout.WEST, rightPanel);
        jframe.getContentPane().add(BorderLayout.CENTER, rootPanel);
    }

    /**
     * Changes the status message at the bottom of the screen and immediately
     * updates (so that 'please wait...' type messages appear before lengthy
     * processes).
     *
     * @param message new String to display on the bottom status line
     */
    public void changeStatus(String message)
    {
        gameStatus.setText(message);
        gameStatus.paintImmediately(0, 0, 600, 25);
    }

    /**
     * Checks if the user has clicked somewhere on the game board.  If a game
     * is in progress, call the routine to check for/do valid moves.
     */
    public void mouseClicked(MouseEvent e)
    {
        int col = (e.getX() - 100) / 50;
        int row = (e.getY() - 100) / 50;

        // make sure the click is on the board and that a game is in progress
        if (col >= 0 && col < 8 && row >= 0 && row < 8 && gameInProgress)
        {
            doClickSquare(row, col);
        }
    }

    /**
     * This is called by mouseClickHandler() when a player clicks on the
     * square in the specified row and col.  It has already been checked
     * that a game is, in fact, in progress. Row 0 is the top row, and col 0
     * is the left column.
     *
     * @param row the game board row where the mouse was clicked (0-7)
     * @param col the game board column where the mouse was clicked (0-7)
     */
    public void doClickSquare(int row, int col)
    {
        boolean newSelection = false;

        //
        // If the player clicked on one of the pieces that the player
        // can move, mark this row and col as selected and return.  This
        // might change a previous selection.  Reset the message, in
        // case it was previously displaying an error message.
        //
        for (int i = 0; i < legalMoves.length; i++)
        {
            if (legalMoves[i].fromRow == row && legalMoves[i].fromCol == col)
            {
                if (btnComputerFirst.isVisible())
                {
                    btnComputerFirst.setVisible(false);
                }

                selectedRow = row;
                selectedCol = col;

                middlePanel.paintImmediately(0, 0, 400, 400);
                changeStatus("Select location to move to.");
                newSelection = true;
            }
        }

        //
        // If no piece has been selected to be moved, the user must first
        // select a piece.  Show an error message and return.
        //
        if (selectedRow < 0)
        {
            gameStatus.setText
                    ("Invalid selection. Select a piece with a valid move.");
        }
        else if (!newSelection)
        {
            //
            // If the user clicked on a square where the selected piece can be
            // legally moved, then make the move and return.
            //
            for (int i = 0; i < legalMoves.length; i++)
            {
                if (legalMoves[i].fromRow == selectedRow &&
                        legalMoves[i].fromCol == selectedCol &&
                        legalMoves[i].toRow == row && legalMoves[i].toCol == col)
                {
                    doMakeMove(legalMoves[i]);
                }
            }
        }
    }

    /**
     * This is called when the current player has chosen the specified
     * move.  Make the move, and then either end or continue the game
     * appropriately.
     *
     * @param move contains original/target locations of the user-selected move
     */
    void doMakeMove(CheckersMove move)
    {
        selectedRow = -1;
        middlePanel.animateMove(move);

        //
        // If the move was a jump, it's possible that the player has another
        // jump.  Check for legal jumps starting from the square that the player
        // just moved to.  If there are any, the player must jump.  The same
        // player continues moving.
        //
        if (move.isJump())
        {
            legalMoves = currGame.getLegalJumpsFrom(GameBoard.PLAYER,
                    move.toRow, move.toCol);
            if (legalMoves != null)
            {
                selectedRow = move.toRow;  // Only one piece can be moved, select it
                selectedCol = move.toCol;
                changeStatus("You must continue jumping...");
            }
        }

        //
        // The current player's turn is ended, so change to the computer player.
        // Get that computer's legal moves.  If the computer has no legal moves,
        // then the game ends.
        //
        if (selectedRow == -1)
        {
            middlePanel.paintImmediately(0, 0, 400, 400);
            legalMoves = currGame.getLegalMoves(GameBoard.ENEMY);
            if (legalMoves == null)
            {
                gameOver(compName + " has no moves.  " + playerName + " wins.");
            }
            else
            {
                changeStatus("Computer's move. Please wait...");
                doEnemyMove();
            }

            legalMoves = currGame.getLegalMoves(GameBoard.PLAYER);
            if (legalMoves == null)
            {
                gameOver(playerName + " has no moves.  " + compName + " wins.");
            }
            if (gameInProgress)
            {
                gameStatus.setText("Select a piece to move.");
            }

            //
            // As a courtesy to the user, if all legal moves use the same piece,
            // select that piece automatically so the user won't have to click on
            // it to select it.
            //
            if (legalMoves != null)
            {
                boolean sameStartSquare = true;
                for (int i = 1; i < legalMoves.length; i++)
                {
                    if (legalMoves[i].fromRow != legalMoves[0].fromRow ||
                            legalMoves[i].fromCol != legalMoves[0].fromCol)
                    {
                        sameStartSquare = false;
                        break;
                    }
                }
                if (sameStartSquare)
                {
                    selectedRow = legalMoves[0].fromRow;
                    selectedCol = legalMoves[0].fromCol;
                    changeStatus("You can only move this piece.");
                }
            }
        }

        // Make sure the board is redrawn in its new state.
        middlePanel.paintImmediately(0, 0, 400, 400);
    }

    /**
     * Calls the routine to perform AI analysis and choose an optimal move
     * for the computer.  Performs the move, and repeats if the move was
     * a jump and more jumps are available (repeat jumps).
     */
    public void doEnemyMove()
    {
        waitingForInput = false;    // used to disable painting user input objects
        AIMoveTest currMove = null; // stores the move chosen by doRecursiveAI
        int currPlayer = GameBoard.ENEMY;

        do
        {
            if (currMove == null)
            {
                currMove = new AIMoveTest(true);
            }
            else
            {
                //
                // Execution gets here during a multiple jump move.
                // This prevents the AI routine from moving the same piece again
                // (it won't do the move if toRow==fromRow), while still setting
                // the previous move as a jump, which is tested by seeing if
                // Math.abs(toCol - fromCol == 2).  Since doRecursiveAI switches
                // players each turn, the currPlayer is changed to compensate
                // for this.
                //
                currMove.move.fromRow = currMove.move.toRow;
                currPlayer = GameBoard.PLAYER;
            }

            currMove = currGame.doRecursiveAI(currGame, currMove.move, 0,
                    currPlayer);

            // only move if a legal move was found
            if (currMove.hasMove())
            {
                middlePanel.animateMove(currMove.move);
            }
        } while (currMove.move.isJump() &&
                (currGame.getLegalJumpsFrom(GameBoard.ENEMY, currMove.move.toRow,
                        currMove.move.toCol) != null));

        waitingForInput = true;    // it's the user's turn again
    }

    /**
     * Called by doMakeMove.  It is assumed that a player has been determined
     * to have lost already.  Outputs the message argument and sets
     * the gameInProgress status to false so that nothing can be done until
     * a new game is initialized or the applet is closed.
     *
     * @param displayMessage message to output in a dialog when the game is over
     */
    void gameOver(String displayMessage)
    {
        changeStatus("Game Over");
        JOptionPane.showMessageDialog(null, displayMessage);
        gameInProgress = false;
        rootPanel.paintImmediately(0, 0, 600, 600);
        changeStatus("Click 'New Game' to start a new game.");
    }

    // needed to 'implement MouseListener', but not needed by the program
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
