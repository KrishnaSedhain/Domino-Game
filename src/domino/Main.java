package domino;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * The Main class manages the command-line version of the Domino game.
 * It handles game initialization, player turns, domino distribution,
 * and determining the winner.
 *
 * <p>The game supports a variable domino set size based on the maximum number
 * of dots specified (default is 6). It supports both human and computer players.</p>
 */
public class Main {

    /** The game board containing the domino set and the played dominoes. */
    private final Board board;

    /** The human player. */
    private final Player human = new Player(Players.Human);

    /** The computer player. */
    private final Player computer = new Player(Players.Computer);

    /** Tracks whose turn it is. */
    private Players currentPlayer = Players.Human;

    /** Scanner for reading human input from the console. */
    private final Scanner scanner = new Scanner(System.in);

    /** Flag to indicate if the game has ended. */
    private boolean gameOver = false;

    /**
     * Constructs a new Main game instance with a specified maximum number of dots.
     * The maximum dots are used to build the domino set.
     *
     * @param maxDots the maximum number of dots for any side of a domino
     */
    public Main(int maxDots) {
        // Pass the maxDots parameter to the Board so that it builds the domino set accordingly.
        this.board = new Board(maxDots);
    }

    /**
     * Default constructor. Creates a game instance with the default maximum dots value of 6.
     */
    public Main() {
        this(6);
    }

    /**
     * The main entry point of the application.
     *
     * <p>This method processes an optional command-line argument to set the domino set size
     * (allowed values are 3 to 9). It then creates an instance of the game and starts it.</p>
     *
     * @param args command-line arguments; the first argument can be the maximum number of dots
     */
    public static void main(String[] args) {
        int maxDots = 6; // Default value

        // Process command-line argument if provided.
        if (args.length > 0) {
            try {
                int input = Integer.parseInt(args[0]);
                // Accept numbers 3 to 9. (6 is included as it is the default)
                if (input >= 3 && input <= 9) {
                    maxDots = input;
                } else {
                    System.out.println("Invalid domino set size. Please provide a number between 3 and 9.");
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid argument. Please provide an integer value for the domino set size.");
                System.exit(1);
            }
        }
        Main gameManager = new Main(maxDots);
        gameManager.startGame();
    }

    /**
     * Starts the game by distributing dominoes to players and alternating turns
     * until the game is over.
     */
    public void startGame() {
        distributeDomino(human, computer, board);
        // End the game when the boneyard is empty or when both players have no valid move.
        while (!gameOver) {
            if (currentPlayer == Players.Human) {
                playDiceForHuman();
                checkGameEnd();
            } else {
                playDiceForComputer();
                checkGameEnd();
            }
        }
    }

    /**
     * Checks if the game has ended by verifying whether either player can make a valid play.
     * If both players cannot play, the game ends. The winner is determined by:
     * <ul>
     *     <li>If one player's tray is empty, that player wins.</li>
     *     <li>Otherwise, the total remaining dots in each tray are summed, and the player
     *         with the lower sum wins.</li>
     *     <li>If the sums are equal, the game is declared a tie.</li>
     * </ul>
     */
    private void checkGameEnd() {
        boolean validPlayForHuman = checkIfValidPlayExists(human.getTray(), board);
        boolean validPlayForComputer = checkIfValidPlayExists(computer.getTray(), board);
        if (!validPlayForHuman && !validPlayForComputer) {
            // if the player's tray is empty then they are the winner
            String winnerSelection = " player";
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
                // Otherwise count the scores and show who the winner is.
                int remainingHumanTrayCount = human.getTray().stream()
                        .mapToInt(d -> d.getLeftNumDots() + d.getRightNumDots()).sum();
                int remainingComputerTrayCount = computer.getTray().stream()
                        .mapToInt(d -> d.getLeftNumDots() + d.getRightNumDots()).sum();

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

    /**
     * Checks if there is at least one valid domino that can be played from the provided tray.
     *
     * @param tray  the list of dominoes in a player's tray
     * @param board the game board containing the played dominoes
     * @return {@code true} if a valid play exists; {@code false} otherwise
     */
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

    /**
     * Prints the current playing board to the console in two lines.
     * The dominoes are arranged alternately between the two lines.
     */
    private void printPlayingBoard() {
        String firstLine = "";
        String secondLine = "";
        int lineCounter = 0;
        for (Domino dice : board.getPlayedDomino()) {
            if (lineCounter == 0) {
                firstLine += "[" + dice.getLeftNumDots() + " " + dice.getRightNumDots() + "]";
                lineCounter++;
            } else if (lineCounter == 1) {
                secondLine += "[" + dice.getLeftNumDots() + " " + dice.getRightNumDots() + "]";
                lineCounter--;
            }
        }
        System.out.println(firstLine);
        System.out.println("  " + secondLine);
    }

    /**
     * Executes the computer player's turn.
     * The method displays current game information, makes a play if possible,
     * or draws from the boneyard if no valid play is available.
     */
    private void playDiceForComputer() {
        System.out.println("Computer has " + computer.getTray().size() + " dominos");
        int boneyardSize = board.getAvailableDomino() == null ? 0 : board.getAvailableDomino().size();
        System.out.println("Boneyard contains " + boneyardSize + " dominos");
        printPlayingBoard();
        printHumanTray();
        System.out.println("Computer's Turn");
        checkIfValidPlayForComputer();
        System.out.println("Computer has " + computer.getTray().size() + " dominos");
        System.out.println("Boneyard contains " + boneyardSize + " dominos");
        printPlayingBoard();
    }

    /**
     * Checks for a valid play in the computer's tray and makes the move.
     * If no valid move is available, the computer draws from the boneyard until a playable domino is found.
     */
    public void checkIfValidPlayForComputer() {
        ArrayList<Domino> computerTray = computer.getTray();
        int leftEnd = board.getPlayedDomino().getFirst().getLeftNumDots();
        int rightEnd = board.getPlayedDomino().getLast().getRightNumDots();

        for (int i = 0; i < computerTray.size(); i++) {
            Domino computerDice = computerTray.get(i);
            boolean canPlayLeft = computerDice.getLeftNumDots() == 0 || computerDice.getRightNumDots() == 0 ||
                    computerDice.getLeftNumDots() == leftEnd || computerDice.getRightNumDots() == leftEnd;
            boolean canPlayRight = computerDice.getLeftNumDots() == 0 || computerDice.getRightNumDots() == 0 ||
                    computerDice.getLeftNumDots() == rightEnd || computerDice.getRightNumDots() == rightEnd;

            if (canPlayLeft) {
                if (computerDice.getRightNumDots() != leftEnd && computerDice.getRightNumDots() != 0) {
                    computerDice.rotateDomino();
                }
                board.placeOnLeft(computerDice);
                computerTray.remove(i);
                currentPlayer = Players.Human;
                System.out.println("Computer plays [" + computerDice.getLeftNumDots() + " " +
                        computerDice.getRightNumDots() + "] at left");
                return;
            } else if (canPlayRight) {
                if (computerDice.getLeftNumDots() != rightEnd && computerDice.getLeftNumDots() != 0) {
                    computerDice.rotateDomino();
                }
                board.placeOnRight(computerDice);
                computerTray.remove(i);
                currentPlayer = Players.Human;
                System.out.println("Computer plays [" + computerDice.getLeftNumDots() + " " +
                        computerDice.getRightNumDots() + "] at right");
                return;
            }
        }
        while (board.getAvailableDomino() != null) {
            System.out.println("Computer draws from boneyard");
            Domino diceFromBoneyard = board.drawFromBoneyard();
            computerTray.add(diceFromBoneyard);
            if (doesPickedDiceMatchEitherEnd(diceFromBoneyard, board)) {
                break;
            }
        }
        currentPlayer = Players.Human;
    }

    /**
     * Determines if the domino drawn from the boneyard matches either end of the played domino chain.
     * If a match is found, the domino is played on the appropriate side.
     *
     * @param diceFromBoneyard the domino drawn from the boneyard
     * @param board            the game board with played dominoes
     * @return {@code true} if the drawn domino matches and is played; {@code false} otherwise
     */
    public boolean doesPickedDiceMatchEitherEnd(Domino diceFromBoneyard, Board board) {
        if (board.getPlayedDomino().size() > 0) {
            int leftPlayedDice = board.getPlayedDomino().getFirst().getLeftNumDots();
            int rightPlayedDice = board.getPlayedDomino().getLast().getRightNumDots();
            if (diceFromBoneyard.getLeftNumDots() == leftPlayedDice) {
                diceFromBoneyard.rotateDomino();
                board.placeOnLeft(diceFromBoneyard);
                return true;
            } else if (diceFromBoneyard.getRightNumDots() == rightPlayedDice) {
                diceFromBoneyard.rotateDomino();
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

    /**
     * Executes the human player's turn by allowing the player to either play a domino,
     * draw from the boneyard, or quit the game.
     * The method continues to prompt the player until a valid move is made or the game is quit.
     */
    private void playDiceForHuman() {
        boolean selectedDice = false;
        while (!selectedDice) {
            printHumanTray();
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
                        boolean isValid = checkIfValidPlayForHuman(dominoIndex, leftRight, rotate);
                        if (isValid) {
                            selectedDice = true;
                            if (leftRight.equals("l")) {
                                System.out.println("Playing [" + board.getPlayedDomino().getFirst().getLeftNumDots() + " " +
                                        board.getPlayedDomino().getFirst().getRightNumDots() + "] at left");
                            } else {
                                System.out.println("Playing [" + board.getPlayedDomino().getFirst().getLeftNumDots() + " " +
                                        board.getPlayedDomino().getFirst().getRightNumDots() + "] at right");
                            }
                            currentPlayer = Players.Computer;
                        } else {
                            System.out.println("Invalid play. Please check again!");
                        }
                    }
                }
                case "d" -> {
                    if (!validPlayExists) {
                        Domino diceFromBoneyard = board.drawFromBoneyard();
                        if (diceFromBoneyard != null) {
                            human.getTray().add(diceFromBoneyard);
                            if (checkIfValidPlayExists(human.getTray(), board)) {
                                System.out.println("You drew a playable domino: [" + diceFromBoneyard.getLeftNumDots() +
                                        "," + diceFromBoneyard.getRightNumDots() + "]");
                                System.out.println("You can now play this domino.");
                            } else {
                                System.out.println("The drawn domino doesn't match and will be added to your tray.");
                            }
                        } else {
                            System.out.println("The boneyard is empty, so no domino can be drawn..");
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

    /**
     * Prints the human player's tray (the dominoes they hold) to the console.
     */
    private void printHumanTray() {
        System.out.print("Tray: [");
        int numDice = human.getTray().size();
        for (int i = 0; i < numDice; i++) {
            Domino dice = human.getTray().get(i);
            System.out.print("[" + dice.getLeftNumDots() + " " + dice.getRightNumDots() + "]");
            if (i != numDice - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    /**
     * Checks if the human player's selected domino can be played on the chosen side of the board.
     * If a rotation is requested, the domino is rotated before the move is validated.
     *
     * @param index     the index of the domino in the human player's tray
     * @param leftRight a String indicating the chosen side ("l" for left, "r" for right)
     * @param rotate    a String indicating if rotation is requested ("y" for yes, "n" for no)
     * @return {@code true} if the move is valid and executed; {@code false} otherwise
     */
    private boolean checkIfValidPlayForHuman(int index, String leftRight, String rotate) {
        if (board.getPlayedDomino().isEmpty()) {
            board.getPlayedDomino().add(human.getDominoFromTray(index));
            human.removedDominoFromPlayerTray(index);
            return true;
        }

        Domino dominoToPlay = human.getDominoFromTray(index);
        int leftEnd = board.getPlayedDomino().getFirst().getLeftNumDots();
        int rightEnd = board.getPlayedDomino().getLast().getRightNumDots();

        boolean canPlayLeft = dominoToPlay.getLeftNumDots() == 0 || dominoToPlay.getRightNumDots() == 0 ||
                dominoToPlay.getLeftNumDots() == leftEnd || dominoToPlay.getRightNumDots() == leftEnd;
        boolean canPlayRight = dominoToPlay.getLeftNumDots() == 0 || dominoToPlay.getRightNumDots() == 0 ||
                dominoToPlay.getLeftNumDots() == rightEnd || dominoToPlay.getRightNumDots() == rightEnd;

        if (rotate.equals("y")) {
            dominoToPlay.rotateDomino();
        }

        if (leftRight.equals("l") && canPlayLeft) {
            board.getPlayedDomino().addFirst(dominoToPlay);
            human.removedDominoFromPlayerTray(index);
            return true;
        } else if (leftRight.equals("r") && canPlayRight) {
            board.getPlayedDomino().addLast(dominoToPlay);
            human.removedDominoFromPlayerTray(index);
            return true;
        }

        if (rotate.equals("y")) {
            dominoToPlay.rotateDomino();
        }
        return false;
    }

    /**
     * Distributes dominoes to both the human and computer players.
     * Each player receives the number of dominoes specified by {@code startingDomino}.
     *
     * @param human  the human player
     * @param computer the computer player
     * @param board  the game board from which dominoes are drawn
     */
    public void distributeDomino(Player human, Player computer, Board board) {
        for (Players player : Players.values()) {
            int startingDomino = 7;
            for (int i = 0; i < startingDomino; i++) {
                Domino dice = board.drawFromBoneyard();
                if (dice != null) {
                    if (player.equals(Players.Human)) {
                        human.addDominoToPlayerTray(dice);
                    } else {
                        computer.addDominoToPlayerTray(dice);
                    }
                }
            }
        }
    }

    /**
     * Returns the game board.
     *
     * @return the Board instance used in the game
     */
    public Board getBoard() {
        return board;
    }
}
