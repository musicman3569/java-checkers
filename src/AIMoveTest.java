package JavaCheckers;

/**
 * AIMoveTest is used by doRecursiveAI in class GameBoard to return move
 * results through the recursive calls.  The information includes locations
 * for the piece being moved, the target move location, and the score the
 * move gets from it's ending scenario.  These can then be compared by the AI
 * routine to determine the best move.
 * 
 * @author Jason Hamilton
 */
public class AIMoveTest 
{
   /**
    * Board locations of the piece to be moved and it's target location
    */
   CheckersMove move;
   
   /**
    * Best board score found from branching move tests
    */
   double score;
   
   /**
    * Constructor. Sets whether the piece is a computer piece or user piece.
    * 
    * @param isEnemy true if computer piece, false if user piece
    */
   AIMoveTest(boolean isEnemy)
   {
      move = new CheckersMove(0, 0, 0, 0);
      setDefaultScore(isEnemy);
   }

   /**
    * Sets a default score value to assist in AI decisions.  This assumes
    * that the maxLevels in doRecursivAI hasn't been reached, which means a
    * a win is more desirable (200 points instead of the 100 assigned to 
    * a win by getBoardScore when maxLevels has been reached) if it is sooner.
    * 
    * <p> Conversely, a loss is less desirable (-200 points instead of the -100
    * assigned to a loss by getBoardScore when at maxLevels) if it is sooner.
    *  
    * @param isEnemy Boolean value indicating if the default score is for the computer (enemy) player.
    */
   public void setDefaultScore(boolean isEnemy)
   {
      if (isEnemy)
      {
         score = -200;
      }
      else
      {
         score = 200;
      }
   }
   
   /**
    * Sets all the member variables to new values.
    * 
    * @param newMove new move instance with the piece's source/target locations
    * @param newScore new board score resulting from the given move
    */
   public void setAll(CheckersMove newMove, double newScore)
   {
      move.fromRow = newMove.fromRow;
      move.fromCol = newMove.fromCol;
      move.toRow = newMove.toRow;
      move.toCol = newMove.toCol;
      score = newScore;
   }
   
   /**
    * Returns whether the move member variable contains a valid move
    * by testing to see if the defaults have been updated with a new value.
    * Assumes that the move values are legal if they have been modified.
    * 
    * @return true if the source/target locations are not the same, false if not
    */
   public boolean hasMove()
   {
      return (move.toRow != move.fromRow);
   }
}
