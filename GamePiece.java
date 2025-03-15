/**
 * Game piece is used to store pixel coordinates and piece image type for
 * a checkers game piece.
 * 
 * @author Jason Hamilton
 */
public class GamePiece 
{
   /**
    * The x coordinate of the image
    */
   double x;
   
   /**
    * The y coordinate of the image
    */
   double y;
   
   /**
    * The game piece type of the image as defined in GameBoard.
    * Should be one of the following constants: EMPTY, PLAYER, PLAYER_KING, 
    * ENEMY, ENEMY_KING
    */
   int type;
   
   /**
    * Constructor.  Initializes all member variables by calling set method.
    * 
    * @param newX new x coordinate for piece
    * @param newY new y coordinate for piece
    * @param newType new game piece type for piece
    */
   GamePiece(double newX, double newY, int newType)
   {
      set(newX, newY, newType);
   }
   
   /**
    * Assigns new values to all member variables.
    * 
    * @param newX new x coordinate for piece
    * @param newY new y coordinate for piece
    * @param newType new game piece type for piece
    */
   public void set(double newX, double newY, int newType)
   {
      x = newX;
      y = newY;
      type = newType;
   }
   
   /**
    * Returns an integer version of the x value for painting on screen (since
    * painting must be done to an exact pixel value).
    * 
    * @return int cast of x coordinate
    */
   public int getX()
   {
      return (int) x;
   }
   
   /**
    * Returns an integer version of the y value for painting on screen (since
    * painting must be done to an exact pixel value).
    * 
    * @return int cast of y coordinate
    */
   public int getY()
   {
      return (int) y;
   }
}
