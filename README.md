# **Overview**
# CS-351 PROJECT-2 Domino
This project features two versions of Domino game: a console-based version that is played
through the command line and a JavaFX version that provides a graphical user interface for an 
interactive gameplay experience. The game supports customizable domino set sizes (default is 6, 
but you can choose from 3 to 9).

# Console Version
This version is played in the console, where the user provides inputs to control the game. The main
entry point is Main.java. During each turn, the human player is given three options: p to play domino,
d to draw from boneyard(if no valid moves are available), or q to quit the game.
If the player chooses to play (p), they must specify where to place the domino (left or right) and
whether to rotate it. Incorrect inputs prompt the user to re-enter their choices from the beginning.
Once a move is made, the program checks if it is valid—if not, the user must retry. After the human
player's turn, the computer automatically plays its move, and the turn returns to the human.
If the boneyard is empty but the human player still has valid moves, they can continue playing until
no moves remain. The game ends when either player runs out of dominoes or both become unable to make 
next move. The human is determined by the player with the lowest total number of dots on their 
remaining dominoes.

# GUI version
This version of the game is built using the JavaFX and is played through a graphical interface with
buttons. The main entry point is GUI.java. The boneyard, computer pieces, and human pieces are
displayed at the top of the screen. The user selects a domino from the left menu, chooses whether to 
play it on the left or right, and decides whether to rotate it. Based on these selections,
the chosen domino is placed on the board and removed from the human's tray. If user try to make invalid
move , a pop-up window appears informing the user of the mistake, adn they click "OK" to continue.
If a valid play exists, drawing from the boneyard is blocked; otherwise, the user can draw a domino.
A pop-up notifies the user if the boneyard is empty or when a winner is determined. The game ends when
the user clicks "OK" in the winner notification.

# Known issues
The console version has issues with ending the game properly. Sometimes, it doesn't recognize when the 
game should stop or update the number of dominoes in the computer's tray or boneyard correctly.
In the GUI version I tried to print the dominoes in separate lines, but I was unable to achieve it. It is
being printed on the same line but logic is working fine.

# Resources
https://www.123rf.com/photo_84345380_black-and-white-silhouettes-of-domino-game-pieces.html
Image for the domino were taken from above link.


