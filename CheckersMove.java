/**
 * A CheckersMove contains row/col values for a piece being moved and it's
 * target row/col values.
 * 
 * @author Jason Hamilton
 */
public class CheckersMove 
{
   /**
    * Row where the piece is currently located
    */
   int fromRow;
   
   /**
    * Column where the piece is currently located
    */
   int fromCol;
   
   /**
    * Row where the piece is to be moved
    */
   int toRow;
   
   /**
    * Column where the piece is to be moved
    */
   int toCol;
   
   /**
    * Constructor. Initializes all member variables to argument values.
    * 
    * @param newFromRow Row where the piece is currently located
    * @param newFromCol Column where the piece is currently located
    * @param newToRow Row where the piece is to be moved
    * @param newToCol Column where the piece is to be moved
    */
   CheckersMove(int newFromRow, int newFromCol, int newToRow, int newToCol)
    {
       fromRow = newFromRow;
       fromCol = newFromCol;
       toRow = newToRow;
       toCol = newToCol;
    }
    
    /**
     * Determines whether the move currently stored is a jump by seeing
     * if the fromCol and toCol are 2 spaces apart.
     * 
     * @return true if the move is a jump, false if not
     */
    boolean isJump()
    {
       return (Math.abs(fromCol - toCol) == 2);
    }
}
