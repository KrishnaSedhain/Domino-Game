package domino;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private final int startingDomino = 7;
    private final Board board = new Board();
    private final Player human = new Player(Players.Human);
    private final Player computer = new Player(Players.Computer);
    private Players currentPlayer = Players.Human;
    private final Scanner scanner = new Scanner(System.in);
    private boolean gameOver = false;
    private String winnerSelection = " player";

    public static void main(String[] args) {
        Main gameManager = new Main();
        gameManager.startGame();
    }

    public void startGame() {
        distributeDomino(human, computer, board);
        // end the game when the boneyard is empty or when both players have taken a turn without placing a domino
        while (!gameOver) {
            if (currentPlayer == Players.Human) {
                playDiceForHuman();
                checkGameEnd();
            }
            else {
                playDiceForComputer();
                checkGameEnd();
            }
        }
    }


    private void checkGameEnd() {
        boolean validPlayForHuman = checkIfValidPlayExists(human.getTray(), board);
        boolean validPlayForComputer = checkIfValidPlayExists(computer.getTray(), board);
        if (!validPlayForHuman && !validPlayForComputer) {
            // if the player's tray is empty then they are the winner
            if (human.getTray().isEmpty()) {
                winnerSelection = "human";
                System.out.println("Ending the game");
                System.out.println("The winner is " + winnerSelection);
                gameOver = true;
            } else if (computer.getTray().isEmpty()) {
                winnerSelection = "computer";
                System.out.println("Ending the game");
                System.out.println("The winner is " + winnerSelection);
                gameOver = true;
            } else {
                // otherwise count the scores and show who the winner is.
                int remainingHumanTrayCount = human.getTray().stream().mapToInt(d -> d.getLeftNumDots() + d.getRightNumDots()).sum();
                int remainingComputerTrayCount = computer.getTray().stream().mapToInt(d -> d.getLeftNumDots() + d.getRightNumDots()).sum();

                if (remainingComputerTrayCount > remainingHumanTrayCount) {
                    winnerSelection = "human";
                } else if (remainingComputerTrayCount < remainingHumanTrayCount) {
                    winnerSelection = "computer";
                } else {
                    winnerSelection = "no-one. It's a tie!";
                }
                System.out.println("Ending the game");
                System.out.println("The winner is " + winnerSelection);
                gameOver = true;
            }
        }
    }



    public boolean checkIfValidPlayExists(ArrayList<Domino> tray, Board board) {
        if (tray.isEmpty()) {
            return false; // Can't play if the tray is empty.
        }

        if (board.getPlayedDomino().isEmpty()) {
            return true; // Any domino can be played if the board is empty.
        }

        int leftEnd = board.getPlayedDomino().getFirst().getLeftNumDots();
        int rightEnd = board.getPlayedDomino().getLast().getRightNumDots();

        for (Domino domino : tray) {
            if (domino.getLeftNumDots() == 0 || domino.getRightNumDots() == 0 ||
                    domino.getLeftNumDots() == leftEnd || domino.getRightNumDots() == leftEnd ||
                    domino.getLeftNumDots() == rightEnd || domino.getRightNumDots() == rightEnd) {
                return true; // Found a domino that can be played.
            }
        }

        return false; // No domino in the tray can be played.
    }




    private void printPlayingBoard(){
        String firstLine = "";
        String secondLine = "";
        int lineCounter = 0;
        for (Domino dice: board.getPlayedDomino()){
            if (lineCounter == 0){
                firstLine += "["+ dice.getLeftNumDots()+ " " + dice.getRightNumDots() + "]";
                lineCounter++;
            }
            else if (lineCounter ==1){
                secondLine += "["+ dice.getLeftNumDots()+ " " + dice.getRightNumDots() + "]";
                lineCounter--;
            }
        }
        System.out.println(firstLine);
        System.out.println("  " + secondLine);
    }




    private void playDiceForComputer(){
        System.out.println("Computer has " + computer.getTray().size() + " dominos");
        // only print the boneyard size if the size is not null
        int boneyardSize = board.getAvailableDomino() == null ? 0 :board.getAvailableDomino().size();
        System.out.println("Boneyard contains " + boneyardSize + " dominos");
        printPlayingBoard();
        printHumanTray();
        System.out.println("Computer's Turn");
        checkIfValidPlayForComputer();
        System.out.println("Computer has " + computer.getTray().size() + " dominos");
        System.out.println("Boneyard contains " + boneyardSize + " dominos");
        printPlayingBoard();
    }


    public void checkIfValidPlayForComputer() {
        ArrayList<Domino> computerTray = computer.getTray();
        // check for a match in the left of first dice and right of last dice
        int leftEnd = board.getPlayedDomino().getFirst().getLeftNumDots();
        int rightEnd = board.getPlayedDomino().getLast().getRightNumDots();

        for (int i = 0; i < computerTray.size(); i++) {
            Domino computerDice = computerTray.get(i);
            // Check if the current domino has a wildcard (0) or can match the ends
            boolean canPlayLeft = computerDice.getLeftNumDots() == 0 || computerDice.getRightNumDots() == 0 || computerDice.getLeftNumDots() == leftEnd || computerDice.getRightNumDots() == leftEnd;
            boolean canPlayRight = computerDice.getLeftNumDots() == 0 || computerDice.getRightNumDots() == 0 || computerDice.getLeftNumDots() == rightEnd || computerDice.getRightNumDots() == rightEnd;

            // If the domino can be played on the left end, possibly rotating if needed
            if (canPlayLeft) {
                if (computerDice.getRightNumDots() != leftEnd && computerDice.getRightNumDots() != 0) {
                    computerDice.rotate();
                }
                board.placeOnLeft(computerDice);
                computerTray.remove(i);
                currentPlayer = Players.Human;
                System.out.println("Computer plays [" + computerDice.getLeftNumDots() + " " +
                        computerDice.getRightNumDots() + "] at left");
                return;
            }
            // If the domino can be played on the right end, possibly rotating if needed
            else if (canPlayRight) {
                if (computerDice.getLeftNumDots() != rightEnd && computerDice.getLeftNumDots() != 0) {
                    computerDice.rotate();
                }
                board.placeOnRight(computerDice);
                computerTray.remove(i);
                currentPlayer = Players.Human;
                System.out.println("Computer plays [" + computerDice.getLeftNumDots() + " " +
                        computerDice.getRightNumDots() + "] at right");
                return;
            }
        }
        // If no domino can be played, draw from the boneyard
        while (board.getAvailableDomino() != null) {
            System.out.println("Computer draws from boneyard");
            Domino diceFromBoneyard = board.drawFromBoneyard();
            computerTray.add(diceFromBoneyard);
            if (doesPickedDiceMatchEitherEnd(diceFromBoneyard, board)){
                break;
            }
        }
        // change player to human
        currentPlayer = Players.Human;
    }



    public boolean doesPickedDiceMatchEitherEnd(Domino diceFromBoneyard, Board board){
        if (board.getPlayedDomino().size() > 0) {
            int leftPlayedDice = board.getPlayedDomino().getFirst().getLeftNumDots();
            int rightPlayedDice = board.getPlayedDomino().getLast().getRightNumDots();
            if (diceFromBoneyard.getLeftNumDots() == leftPlayedDice) {
                diceFromBoneyard.rotate();
                board.placeOnLeft(diceFromBoneyard);
                return true;
            } else if (diceFromBoneyard.getRightNumDots() == rightPlayedDice) {
                diceFromBoneyard.rotate();
                board.placeOnRight(diceFromBoneyard);
                return true;
            } else if (diceFromBoneyard.getLeftNumDots() == rightPlayedDice) {
                board.placeOnRight(diceFromBoneyard);
                return true;
            } else if (diceFromBoneyard.getRightNumDots() == leftPlayedDice) {
                board.placeOnLeft(diceFromBoneyard);
                return true;
            }
        }
        return false;
    }

    private void playDiceForHuman() {
        boolean selectedDice = false;
        while (!selectedDice) {
            printHumanTray();

            // Check for valid play before prompting the options.
            boolean validPlayExists = checkIfValidPlayExists(human.getTray(), board);

            System.out.println("Human’s turn\n" +
                    "[p] Play Domino\n" +
                    "[d] Draw from boneyard\n" +
                    "[q] Quit");

            String humanOptions = scanner.next();

            switch (humanOptions) {
                case "p" -> {
                    System.out.println("Which domino?");
                    if (!scanner.hasNextInt()) {
                        System.out.println("Please enter a valid number");
                        return;
                    }
                    int dominoIndex = scanner.nextInt();
                    String leftRight;
                    String rotate;
                    if (human.getTray().size() <= dominoIndex || dominoIndex < 0) {
                        System.out.println("Invalid Domino Index");
                        continue;
                    }
                    System.out.println("Left or Right? (l/r)");
                    leftRight = scanner.next();
                    if (leftRight.equals("l") || leftRight.equals("r")) {
                        System.out.println("Rotate first? (y/n)");
                        rotate = scanner.next();
                        if (!(rotate.equals("y") || rotate.equals("n"))) {
                            System.out.println("Wrong Input!");
                            continue;
                        }
                        // if true, change isPlaying.
                        boolean isValid = checkIfValidPlayForHuman(dominoIndex, leftRight, rotate);
                        if (isValid) {
                            selectedDice = true;
                            // print what index number is going to be played at what location (left or right)
                            if (leftRight.equals("l")) {
                                System.out.println("Playing [" + board.getPlayedDomino().getFirst().getLeftNumDots() + " "
                                        + board.getPlayedDomino().getFirst().getRightNumDots() + "] at left");
                            } else
                                System.out.println("Playing [" + board.getPlayedDomino().getFirst().getLeftNumDots() + " "
                                        + board.getPlayedDomino().getFirst().getRightNumDots() + "] at right");
                            currentPlayer = Players.Computer;
                        }
                        else {
                            System.out.println("Invalid play. Please check again!");
                        }
                    }
                }
                case "d" -> {
                    if (!validPlayExists) {
                        Domino diceFromBoneyard = board.drawFromBoneyard();
                        if (diceFromBoneyard != null) {
                            human.getTray().add(diceFromBoneyard);
                            // Now check if the new domino can be played
                            if (checkIfValidPlayExists(human.getTray(), board)) {
                                System.out.println("You drew a playable domino: [" + diceFromBoneyard.getLeftNumDots() + "," + diceFromBoneyard.getRightNumDots() + "]");
                                System.out.println("You can now play this domino.");
                                // Do not return here; instead, allow the user to play.
                            } else {
                                System.out.println("The drawn domino doesn't match and will be added to your tray.");
                                // If the new domino still doesn't provide a valid play, continue the loop.
                            }
                        } else {
                            System.out.println("The boneyard is empty, so no domino can be drawn..");
                            // If boneyard is empty, handle accordingly (e.g., switch turn, end game, etc.).
                        }
                    } else {
                        System.out.println("You are not allowed to draw since you have a playable move available in your tray..");
                    }
                }
                case "q" -> {
                    System.out.println("Quitting Game");
                    selectedDice = true;
                    gameOver = true;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }


    private void printHumanTray (){
        // print the human's tray
        System.out.print("Tray: [");
        int numDice = human.getTray().size();
        for(int i = 0; i < numDice; i++){
            Domino dice = human.getTray().get(i);
            System.out.print("[" + dice.getLeftNumDots()+ " "+ dice.getRightNumDots() + "]");
            if(i != numDice - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }


    private boolean checkIfValidPlayForHuman(int index, String leftRight, String rotate) {
        if (board.getPlayedDomino().isEmpty()) {
            // If the board is empty, any domino can be played.
            board.getPlayedDomino().add(human.getDominoFromTray(index));
            human.removedDominoFromPlayerTray(index);
            return true;
        }

        Domino dominoToPlay = human.getDominoFromTray(index);
        int leftEnd = board.getPlayedDomino().getFirst().getLeftNumDots();
        int rightEnd = board.getPlayedDomino().getLast().getRightNumDots();

        // Check if the domino or the board ends have a wildcard '0'
        boolean canPlayLeft = dominoToPlay.getLeftNumDots() == 0 || dominoToPlay.getRightNumDots() == 0 ||
                dominoToPlay.getLeftNumDots() == leftEnd || dominoToPlay.getRightNumDots() == leftEnd;
        boolean canPlayRight = dominoToPlay.getLeftNumDots() == 0 || dominoToPlay.getRightNumDots() == 0 ||
                dominoToPlay.getLeftNumDots() == rightEnd || dominoToPlay.getRightNumDots() == rightEnd;

        if (rotate.equals("y")) {
            dominoToPlay.rotate(); // This will swap the left and right numbers of the domino.
        }

        // Attempt to play the domino on the specified side if it's a valid move
        if (leftRight.equals("l") && canPlayLeft) {
            board.getPlayedDomino().addFirst(dominoToPlay);
            human.removedDominoFromPlayerTray(index);
            return true;
        } else if (leftRight.equals("r") && canPlayRight) {
            board.getPlayedDomino().addLast(dominoToPlay);
            human.removedDominoFromPlayerTray(index);
            return true;
        }

        // If the domino was rotated for the check and didn't match, rotate it back
        if (rotate.equals("y")) {
            dominoToPlay.rotate();
        }

        // If we reach here, no valid move was made
        return false;
    }



    public void distributeDomino(Player human, Player computer, Board board) {
        for (Players player : Players.values()) {
            for (int i = 0; i < startingDomino; i++) {
                Domino dice = board.drawFromBoneyard();
                if (dice != null) { // Check that we actually picked a domino
                    if (player.equals(Players.Human)) {
                        human.addDominoToPlayerTray(dice);
                    } else {
                        computer.addDominoToPlayerTray(dice);
                    }
                }
            }
        }
    }


    public Board getBoard() {
        return board;
    }
}
