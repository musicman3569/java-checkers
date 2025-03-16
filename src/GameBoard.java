package JavaCheckers;

import javax.swing.JOptionPane;
import java.util.Vector;

/**
 * The GameBoard is a pure logic oriented class to be used as a backend for
 * the CheckersGame applet.  The class methods enforce the appropriate
 * rules for moves, and drive AI through the same methods.
 *
 * @author Jason Hamilton
 */
public class GameBoard
{
    // handles moving the pieces and tracking game stats
    private int[][] board; // used to represent 8 x 8 checkerboard

    // Constants for identifying game piece types on the board
    public static final int EMPTY = 0;
    public static final int PLAYER = 1;
    public static final int PLAYER_KING = 2;
    public static final int ENEMY = 3;
    public static final int ENEMY_KING = 4;

    private static final int DEFAULT_LEVEL = 3;
    int maxLevels;

    /**
     * Constructor.  All the initialization is best done each time a new game
     * is called, not when the object is created.  See newGame().
     */
    GameBoard()
    {
        // STUB: nothing should be done until newGame() is called
    }

    /**
     * Initializes a new game.  Called by btnNewGame in the CheckersGame class.
     * Resets all pieces on the board to starting positions, and prompts for the
     * AI difficulty level in a dialog box.
     */
    public void newGame()
    {
        board = new int[8][8];
        Object[] levelOptions = {"Easy", "Medium", "Hard"};
        maxLevels = JOptionPane.showOptionDialog(
            null,
            "Select a difficulty level:",
            "New Game",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            levelOptions,
            levelOptions[2]
        );

        if (maxLevels > -1)
        {
            maxLevels = 3 * maxLevels;
            if (maxLevels < 1)
            {
                maxLevels = 1;
            }
        }
        else
        {
            // use default difficulty setting if the user clicked the "X"
            maxLevels = DEFAULT_LEVEL;
        }

        // add game pieces to outer 3 rows
        for (int row = 0; row < 3; row++)
        {
            for (int col = row % 2; col < 8; col += 2)
            {
                board[row][col] = ENEMY;
                board[7 - row][7 - col] = PLAYER;
            }
        }
    }

    /**
     * Copy constructor.  Called by doRecursiveAI to create new hypothetical
     * game boards as it is testing possible moves.  Copies the board state
     * to the new instance.
     *
     * @param oldBoard GameBoard instance to be copied
     */
    GameBoard(GameBoard oldBoard)
    {
        board = new int[8][8]; // stores what piece is on each space
        maxLevels = oldBoard.maxLevels;

        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                board[x][y] = oldBoard.board[x][y];
            }
        }
    }

    /**
     * Gets the piece type in the board[][] array at the given row/col
     * as one of the following constants:
     *
     * <pre>
     * EMPTY, PLAYER, PLAYER_KING, ENEMY, ENEMY_KING
     * <pre/>
     *
     * @param row row of the game square to check
     * @param col col of the game square to check
     */
    public int pieceAt(int row, int col)
    {
        return board[row][col];
    }

    /**
     * Make the specified move.  It is assumed that move is non-null and that
     * the move it represents is legal.
     *
     * @param move row/col locations of piece to move and location to move to
     */
    public void makeMove(CheckersMove move)
    {
        makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
    }

    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row piece is currently at
     * @param fromCol col piece is currently at
     * @param toRow row piece is moving to
     * @param toCol col piece is moving to
     */
    public void makeMove(int fromRow, int fromCol, int toRow, int toCol)
    {

        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;

        if (Math.abs(fromRow - toRow) == 2)
        {
            // The move is a jump.  Remove the jumped piece from the board.
            int jumpRow = (fromRow + toRow) / 2;  // Row of the jumped piece.
            int jumpCol = (fromCol + toCol) / 2;  // Column of the jumped piece.
            board[jumpRow][jumpCol] = EMPTY;
        }

        if (toRow == 0 && board[toRow][toCol] == PLAYER)
        {
            board[toRow][toCol] = PLAYER_KING;
        }
        if (toRow == 7 && board[toRow][toCol] == ENEMY)
        {
            board[toRow][toCol] = ENEMY_KING;
        }
    }

    /**
     * Return an array containing all the legal CheckersMoves for the specified
     * player on the current board.  If the player has no legal moves, null is
     * returned.  The value of player should be one of the constants PLAYER or
     * ENEMY; if not, null is returned.  If the returned value is non-null, it
     * consists entirely of jump moves or entirely of regular moves, because if
     * the player can jump, only jumps are legal moves.
     *
     * @param player current player whose pieces are being tested
     * @return CheckerMove array of all possible moves for the current player
     */
    public CheckersMove[] getLegalMoves(int player) {

        CheckersMove[] moveArray;

        int playerKing = (player == PLAYER) ? PLAYER_KING : ENEMY_KING;

        // Moves will be stored in this vector
        Vector<CheckersMove> moves = new Vector<CheckersMove>();

        //
        // First, check for any possible jumps.  Look at each square on the board.
        // If that square contains one of the player's pieces, look at a possible
        // jump in each of the four directions from that square.  If there is
        // a legal jump in that direction, put it in the moves vector.
        //
        for (int row = 0; row < 8; row++)
        {
            for (int col = row % 2; col < 8; col += 2)
            {
                if (board[row][col] == player || board[row][col] == playerKing)
                {
                    testSurroundingMoves(player, row, col, 2, moves);
                }
            }
        }

        //
        // If any jump moves were found, then the user must jump, so we don't
        // add any regular moves.  However, if no jumps were found, check for
        // any legal regular moves.  Look at each playable square on the board.
        // If that square contains one of the player's pieces, look at a possible
        // move in each of the four directions from that square.  If there is
        // a legal move in that direction, put it in the moves vector.
        //
        if (moves.isEmpty())
        {
            for (int row = 0; row < 8; row++)
            {
                for (int col = row % 2; col < 8; col += 2)
                {
                    if (board[row][col] == player || board[row][col] == playerKing)
                    {
                        testSurroundingMoves(player, row, col, 1, moves);
                    }
                }
            }
        }

        //
        // If no legal moves have been found, return null.  Otherwise, create
        // an array just big enough to hold all the legal moves, copy the
        // legal moves from the vector into the array, and return the array.
        //
        if (moves.isEmpty())
        {
            moveArray = null;
        }
        else
        {
            moveArray = new CheckersMove[moves.size()];

            for (int i = 0; i < moves.size(); i++)
            {
                moveArray[i] = moves.elementAt(i);
            }
        }

        return moveArray;
    }

    /**
     * Gets the legal move for a given PLAYER piece.  Will not work for
     * computer pieces.  Called for the purpose of highlighting available
     * moves when a given user piece has been selected.
     *
     * @param row row of the selected piece to test
     * @param col col of the selected piece to test
     * @return array of legal moves for the current PLAYER piece
     */
    public CheckersMove[] getLegalMoves(int row, int col)
    {

        CheckersMove[] moveArray;
        Vector<CheckersMove> moves = new Vector<CheckersMove>();  // Moves will be stored in this vector.

        // check for jumps first
        testSurroundingMoves(PLAYER, row, col, 2, moves);

        // check for single moves if there are no jumps
        if (moves.isEmpty())
        {
            testSurroundingMoves(PLAYER, row, col, 1, moves);
        }

        if (moves.isEmpty())
        {
            // return null if no moves are found
            moveArray = null;
        }
        else
        {
            moveArray = new CheckersMove[moves.size()];

            // copy the vector into an array of possible moves
            for (int i = 0; i < moves.size(); i++)
            {
                moveArray[i] = moves.elementAt(i);
            }
        }

        return moveArray;
    }

    /**
     * Test all the squares around a selected piece at (oldRow, oldCol) at the
     * interval given by moveSize.  If moveSize is 1, it checks for moves one
     * square away; if moveSize is 2, it checks for jumps (2 squares away).
     * Legal moves are added to the vector passed in through "moves".
     *
     * @param player identifies which player's piece is being tested
     * @param oldRow row of the game piece being tested
     * @param oldCol col of the game piece being tested
     * @param moveSize distance (in game squares) to check for legal moves
     * @param moves a vector passed in to add legal moves to
     */
    private void testSurroundingMoves (int player, int oldRow, int oldCol,
                                       int moveSize, Vector<CheckersMove> moves)
    {
        //
        // Scan the surrounding moves, starting with the top left, and going
        // down in left-right fashion.
        //
        for (int row = -moveSize; row <= moveSize; row += 2 * moveSize)
        {
            for (int col = -moveSize; col <= moveSize; col += 2 * moveSize)
            {
                if (
                    (
                        (moveSize == 1) &&
                        canMove(player, oldRow, oldCol, oldRow + row, oldCol + col)
                    ) || (
                        (moveSize == 2) &&
                        canJump(player, oldRow, oldCol, oldRow + row, oldCol + col)
                    )
                )
                {
                    moves.add(new CheckersMove(oldRow, oldCol, oldRow + row, oldCol + col));
                }
            }
        }
    }

    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * @param player identifies which player's piece is being tested
     * @param row the row of the piece being tested
     * @param col the row of the piece being tested
     * @return array of possible jump moves for the given piece
     */
    public CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {

        if (player != PLAYER && player != ENEMY) {
            return null;
        }

        int playerKing = (player == PLAYER) ? PLAYER_KING : ENEMY_KING;

        Vector<CheckersMove> moves = new Vector<>();  // The legal jumps will be stored in this vector.

        if (board[row][col] == player || board[row][col] == playerKing) {
            testSurroundingMoves(player, row, col, 2, moves);
        }

        if (moves.isEmpty()) {
            return null;
        } else {
            CheckersMove[] moveArray = new CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++)
                moveArray[i] = (CheckersMove)moves.elementAt(i);
            return moveArray;
        }
    }

    /**
     * This is called by the two previous methods to check whether the
     * player can legally jump from (r1,c1) to (r3,c3).  It is assumed
     * that the player has a piece at (r1,c1), that (r3,c3) is a position
     * that is 2 rows and 2 columns distant from (r1,c1) and that
     * (r2,c2) is the square between (r1,c1) and (r3,c3).
     *
     * @param player identify which player's piece is being tested
     * @param r1 row the moving piece is currently at
     * @param c1 col the moving piece is currently at
     * @param r3 row to test if the piece can move to
     * @param c3 col to test if the piece can move to
     * @return true if the move is a legal jump, false if not
     */
    private boolean canJump(int player, int r1, int c1, int r3, int c3) {

        boolean jumpFound = false;
        int r2 = (r1 + r3) / 2;
        int c2 = (c1 + c3) / 2;

        if (
            (r3 >= 0 && r3 < 8 && c3 >= 0 && c3 < 8) &&
            (board[r3][c3] == EMPTY)
        ) {
            if (player == PLAYER) {
                if (!(board[r1][c1] == PLAYER && r3 > r1) &&
                        (board[r2][c2] == ENEMY || board[r2][c2] == ENEMY_KING))
                {
                    jumpFound = true;
                }
            }
            else {
                if (!(board[r1][c1] == ENEMY && r3 < r1) &&
                        (board[r2][c2] == PLAYER || board[r2][c2] == PLAYER_KING))
                {
                    jumpFound = true;
                }
            }
        }

        return jumpFound;
    }

    /**
     * This is called by the getLegalMoves() method to determine whether
     * the player can legally move from (r1,c1) to (r2,c2).  It is
     * assumed that (r1,r2) contains one of the player's pieces and
     * that (r2,c2) is a neighboring square.
     *
     * @param player identifies which player's piece is being tested
     * @param r1 row the piece is currently at
     * @param c1 col the piece is currently at
     * @param r2 row being tested as the current piece's target location
     * @param c2 col being tested as the current piece's target location
     * @return true if the move is legal, false if it is not
     */
    private boolean canMove(int player, int r1, int c1, int r2, int c2)
    {
        boolean moveFound = false;

        if (r2 >= 0 && r2 < 8 && c2 >= 0 && c2 < 8 && (board[r2][c2] == EMPTY))
        {
            if (player == PLAYER) {
                if (!(board[r1][c1] == PLAYER && r2 > r1))
                {
                    moveFound = true;
                }
            }
            else {
                if (!(board[r1][c1] == ENEMY && r2 < r1))
                {
                    moveFound = true;
                }
            }
        }

        return moveFound;
    }

    /**
     * Recursively tests all possible moves, up to maxLevels deep.  In other
     * words, if maxLevels is 4, all possible moves up to 4 turns in the
     * future will be analyzed.  Series jump moves do not count against the
     * levelsDeep, as they have limited branches and are essentially "1 turn".
     *
     * <p> For each recursive call, a hypothetical testBoard is created to
     * play out possible scenarios an get the next set of legal moves.
     * Each legal moves calls the function again, until an ending score is
     * returned (either from a player losing, or maxLevels being reached).
     *
     * <p> At this point, the board is given a score (ratio of black:red pieces)
     * which is returned.  The legal moves scores are then tested for the
     * best score (according to who's turn it is) and that is returned to
     * the previous recursive call.
     *
     * @param originalBoard GameBoard instance with the starting state for the recursive tests.
     * @param prevMove Previous CheckersMove representing the move that lead to the current game state, if applicable.
     * @param levelsDeep Integer value indicating the current recursion depth, should start at 0 at the root call.
     * @param player One of the enumerated player values indicating with player's turn it is for this move test.
     * @return move with the best score for the current player (with rows/cols)
     */
    public AIMoveTest doRecursiveAI(GameBoard originalBoard,
                                    CheckersMove prevMove, int levelsDeep,
                                    int player)
    {
        AIMoveTest bestScore = new AIMoveTest(player == ENEMY);
        AIMoveTest currScore = new AIMoveTest(player == ENEMY);
        GameBoard testBoard = new GameBoard(originalBoard);
        int nextPlayer = (player == ENEMY) ? PLAYER : ENEMY;

        if (prevMove.fromRow != prevMove.toRow)
        {
            testBoard.makeMove(prevMove);
        }

        CheckersMove[] legalMoves = testBoard.getLegalMoves(player);

        //
        // If the previous move was a jump, the same player might have another
        // jump.  Check for valid jumps, and if any are found, switch back
        // to the same player and set the legal moves to the jumps for the
        // piece that just did the previous jump
        //
        if (prevMove.isJump())
        {
            CheckersMove[] opponentMoves = testBoard.getLegalJumpsFrom(nextPlayer,
                    prevMove.toRow, prevMove.toCol);

            if (opponentMoves != null)
            {
                player = nextPlayer;
                nextPlayer = (player == ENEMY) ? PLAYER : ENEMY;
                legalMoves = opponentMoves;
                bestScore.setDefaultScore(player == ENEMY);
                currScore.setDefaultScore(player == ENEMY);
                levelsDeep--;
            }

            opponentMoves = null;
        }

        //
        // Once the maximum number of turns to look into the future has been
        // reached, get the ratio of red:black pieces and recursively return
        // the score to analyze the different possible ending scenarios
        //
        if (levelsDeep > maxLevels)
        {
            bestScore.score = testBoard.getBoardScore();
        }
        else if (legalMoves != null)
        {
            //
            // With the array of legal moves, recursively check each move
            // and get back it's score.  If the current player is the computer,
            // have it choose the highest ratio of computer:player pieces.  If
            // it's the player, we need to assume he will act in his own best
            // interest and choose the worst ratio for the computer.
            //
            for (CheckersMove currMove : legalMoves)
            {
                currScore = testBoard.doRecursiveAI(testBoard, currMove,
                        levelsDeep + 1, nextPlayer);

                // have enemy choose move with highest score for the computer
                if (player == ENEMY)
                {
                    if (currScore.score > bestScore.score)
                    {
                        bestScore.setAll(currMove, currScore.score);
                    }
                }
                // assume player will choose move with lowest score for the computer
                else
                {
                    if (currScore.score < bestScore.score)
                    {
                        bestScore.setAll(currMove, currScore.score);
                    }
                }
            }


        }

        return bestScore;
    }

    /**
     * Counts the number of black and red pieces, and returns a ratio
     * of red:black pieces.  Called by doRecursiveAI to test end scenarios.
     * In the case of no pieces, the board is given a large score for a win
     * (to make it very desirable), or a very low score for a lose (to make
     * it very undesirable).
     *
     * @return ratio of red/black pieces
     */
    private double getBoardScore()
    {
        int playerCount = 0;
        int enemyCount = 0;
        double scoreRatio;

        // count pieces for each player left on the board
        for (int row = 0; row < 8; row++)
        {
            for (int col = row % 2; col < 8; col += 2)
            {
                switch(board[row][col])
                {
                    case PLAYER_KING:
                        playerCount += 2;
                        break;
                    case PLAYER:
                        playerCount++;
                        break;
                    case ENEMY_KING:
                        enemyCount += 2;
                        break;
                    case ENEMY:
                        enemyCount++;
                        break;
                }
            }
        }

        // avoid division by zero
        if (playerCount > 0)
        {
            scoreRatio = ((double) enemyCount) / ((double) playerCount);
        }
        // Computer (enemy) wins, set to large (desirable) value
        else
        {
            scoreRatio = 100;
        }

        // if player wins, set to small (undesirable) value
        if (enemyCount < 1)
        {
            scoreRatio = -100;
        }

        return scoreRatio;
    }
}
