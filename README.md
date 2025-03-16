# Java Checkers 2.0
*Written by Jason Hamilton, 2/12/2009*

![Screenshot: Gameplay](/src/main/resources/screenshot2-gameplay.png)

This was originally written in Java 6 as a college Computer Science 
project, where students were to make a personal project of their choosing 
from scratch and document their daily progress and learnings. I have
successfully re-compiled it in Java 23 with no changes to the code
(shockingly) other than the Main class that launches it.

The class project version is a Java Swing application with a focus on
creating a variable-difficulty AI computer opponent.  The AI utilizes
a recursive Minimax algorithm with weighted outcomes to make its moves,
with the difficulty being how many recursive iterations it is allowed
to predict. 

![Screenshot: Difficulty Selection](/src/main/resources/screenshot-difficulty.png)

The final selected move is what the algorithm predicts to be the
most optimal outcome for the computer opponent under the assumption that
the player chooses the worst outcome for the computer on their turn (hence
the alternating minimum/maximum weights as the name Minimax suggests).

For the user, the game includes adherence to the gameplay rules and visual
help indicators that automatically highlight what pieces have legal moves,
and upon selection of a piece, what destinations can be chosen. The rule
enforcement also accounts for exceptions like forced jumps, double jumps
(where after a move if multiple double-jump options are available the
turn does not change to the other player), kings, etc.

![Screenshot: Gameplay 2](/src/main/resources/screenshot-gameplay.png)

The graphics were all made in Gimp, and I have also included the original
source Gimp files in the images folder.
