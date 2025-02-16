package domino;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private final int startingDominos = 7;
    private final Board board = new Board();
    private final Player human = new Player(Players.Human);
    private final Player computer = new Player(Players.Computer);
    private Players currentPlayer = Players.Human;
    private final Scanner inputScanner = new Scanner(System.in);
    private boolean gameOver = false;

    public static void main(String[] args) {
        new Main().startGame();
    }

    public void startGame() {
        distributeDominos();
        while (!gameOver) {
            if (currentPlayer == Players.Human) {
                humanTurn();
            } else {
                computerTurn();
            }
            checkGameEnd();
        }
    }


    public boolean doesPickedDiceMatchEitherEnd(Domino diceFromBoneyard, Board board) {
        if (board.getPlayedDominos().size() > 0) {
            int leftPlayedDice = board.getFirstPlayedDomino().getLeftNumDots();
            int rightPlayedDice = board.getLastPlayedDomino().getRightNumDots();
            if (diceFromBoneyard.getLeftNumDots() == leftPlayedDice) {
                diceFromBoneyard.rotateDomino();
                board.placeOnLeft(diceFromBoneyard, Players.Computer);
                return true;
            } else if (diceFromBoneyard.getRightNumDots() == rightPlayedDice) {
                diceFromBoneyard.rotateDomino();
                board.placeOnRight(diceFromBoneyard, Players.Computer);
                return true;
            } else if (diceFromBoneyard.getLeftNumDots() == rightPlayedDice) {
                board.placeOnRight(diceFromBoneyard, Players.Computer);
                return true;
            } else if (diceFromBoneyard.getRightNumDots() == leftPlayedDice) {
                board.placeOnLeft(diceFromBoneyard, Players.Computer);
                return true;
            }
        }
        return false;
    }

    private void humanTurn() {
        boolean moveCompleted = false;
        while (!moveCompleted && !gameOver) {
            printPlayingBoard();
            printHumanTray();
            int boneyardSize = board.getBoneyardSize();
            System.out.println("Boneyard contains " + boneyardSize + " dominos");
            System.out.println("Human’s turn");
            System.out.println("[p] Play Domino   [d] Draw from boneyard   [q] Quit");
            String option = inputScanner.next();

            switch (option) {
                case "p" -> {
                    System.out.println("Which domino? (enter index)");
                    if (inputScanner.hasNextInt()) {
                        int index = inputScanner.nextInt();
                        if (isValidIndex(index, human.getTray().size())) {
                            if (playSelectedDomino(human, index)) {
                                moveCompleted = true;
                            }
                        } else {
                            System.out.println("Invalid Domino Index. Try again.");
                        }
                    } else {
                        System.out.println("Please enter a valid number.");
                        inputScanner.next();
                    }
                }
                case "d" -> {
                    if (!hasValidMove(human.getTray(), board)) {
                        drawFromBoneyard(human);
                    } else {
                        System.out.println("You have a valid move. You cannot draw from the boneyard.");
                    }
                }
                case "q" -> {
                    System.out.println("Quitting Game");
                    gameOver = true;
                    moveCompleted = true;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
        currentPlayer = Players.Computer;
    }

    private void computerTurn() {
        System.out.println("Computer's turn to play");
        int boneyardSize = board.getBoneyardSize();
        System.out.println("Boneyard contains " + boneyardSize + " dominos");
        printPlayingBoard();
        printHumanTray();
        if (hasValidMove(computer.getTray(), board)) {
            playDomino(computer);
        } else {
            while (board.getBoneyardSize() > 0) {
                System.out.println("Computer draws from boneyard");
                Domino drawn = board.drawFromBoneyard();
                computer.getTray().add(drawn);
                if (doesPickedDiceMatchEitherEnd(drawn, board)) {
                    break;
                }
            }
        }
        currentPlayer = Players.Human;
    }

    private boolean playSelectedDomino(Player player, int index) {
        Domino domino = player.getTray().get(index);
        if (board.getPlayedDominos().isEmpty()) {
            board.placeOnLeft(domino, Players.Human); // First domino is always played by the human
            player.getTray().remove(index);
            System.out.println("Playing [" + domino.getLeftNumDots() + " " + domino.getRightNumDots() + "] as the first domino");
            return true;
        }
        System.out.println("Left or Right? (l/r)");
        String side = inputScanner.next();
        System.out.println("Rotate first? (y/n)");
        String rotate = inputScanner.next();
        if (rotate.equalsIgnoreCase("y")) {
            domino.rotateDomino();
        }
        if (isValidMove(domino, side)) {
            if (side.equalsIgnoreCase("l")) {
                board.placeOnLeft(domino, Players.Human);
                System.out.println("Playing [" + domino.getLeftNumDots() + " " + domino.getRightNumDots() + "] at left");
            } else {
                board.placeOnRight(domino, Players.Human);
                System.out.println("Playing [" + domino.getLeftNumDots() + " " + domino.getRightNumDots() + "] at right");
            }
            player.getTray().remove(index);
            return true;
        } else {
            System.out.println("Invalid move. Please try again.");
            if (rotate.equalsIgnoreCase("y")) {
                domino.rotateDomino();
            }
            return false;
        }
    }

    private void playDomino(Player player) {
        Domino bestDomino = null;
        String bestSide = "";
        int bestValue = -1;

        for (Domino domino : player.getTray()) {
            if (isValidMove(domino, "l")) {
                int value = domino.getLeftNumDots() + domino.getRightNumDots();
                if (value > bestValue) {
                    bestValue = value;
                    bestDomino = domino;
                    bestSide = "l";
                }
            }
            if (isValidMove(domino, "r")) {
                int value = domino.getLeftNumDots() + domino.getRightNumDots();
                if (value > bestValue) {
                    bestValue = value;
                    bestDomino = domino;
                    bestSide = "r";
                }
            }
        }

        if (bestDomino != null) {
            if (bestSide.equals("l")) {
                board.placeOnLeft(bestDomino, Players.Computer);
                System.out.println("Computer plays [" + bestDomino.getLeftNumDots() + " " +
                        bestDomino.getRightNumDots() + "] at left");
            } else {
                board.placeOnRight(bestDomino, Players.Computer);
                System.out.println("Computer plays [" + bestDomino.getLeftNumDots() + " " +
                        bestDomino.getRightNumDots() + "] at right");
            }
            player.getTray().remove(bestDomino);
        }
    }

    private void drawFromBoneyard(Player player) {
        Domino domino = board.drawFromBoneyard();
        if (domino != null) {
            player.getTray().add(domino);
            System.out.println((player == human ? "You" : "Computer") + " drew a domino from the boneyard.");
            if (isValidMove(domino, "l") || isValidMove(domino, "r")) {
                System.out.println("You can play the drawn domino.");
                if (player == human) {
                    playSelectedDomino(player, player.getTray().size() - 1);
                } else {
                    playDomino(player);
                }
            }
        } else {
            System.out.println("Boneyard is empty. No domino drawn.");
        }
    }

    private boolean isValidMove(Domino domino, String side) {
        if (board.getPlayedDominos().isEmpty()) {
            return true;
        }
        int leftEnd = board.getFirstPlayedDomino().getLeftNumDots();
        int rightEnd = board.getLastPlayedDomino().getRightNumDots();
        if (side.equalsIgnoreCase("l")) {
            return domino.getLeftNumDots() == leftEnd || domino.getRightNumDots() == leftEnd ||
                    domino.getLeftNumDots() == 0 || domino.getRightNumDots() == 0;
        } else if (side.equalsIgnoreCase("r")) {
            return domino.getLeftNumDots() == rightEnd || domino.getRightNumDots() == rightEnd ||
                    domino.getLeftNumDots() == 0 || domino.getRightNumDots() == 0;
        }
        return false;
    }

    public boolean hasValidMove(ArrayList<Domino> tray, Board board) {
        if (tray.isEmpty()) {
            return false;
        }
        if (board.getPlayedDominos().isEmpty()) {
            return true;
        }
        int leftEnd = board.getFirstPlayedDomino().getLeftNumDots();
        int rightEnd = board.getLastPlayedDomino().getRightNumDots();
        for (Domino domino : tray) {
            if (domino.getLeftNumDots() == leftEnd || domino.getRightNumDots() == leftEnd ||
                    domino.getLeftNumDots() == rightEnd || domino.getRightNumDots() == rightEnd ||
                    domino.getLeftNumDots() == 0 || domino.getRightNumDots() == 0) {
                return true;
            }
        }
        return false;
    }

    private void printHumanTray() {
        System.out.print("Tray: [");
        for (int i = 0; i < human.getTray().size(); i++) {
            Domino domino = human.getTray().get(i);
            System.out.print("[" + domino.getLeftNumDots() + " " + domino.getRightNumDots() + "]");
            if (i != human.getTray().size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    private void printPlayingBoard() {
        String firstLine = "";  // Human's moves (left-aligned)
        String secondLine = ""; // Computer's moves (indented)

        int index = 0;
        for (Domino domino : board.getPlayedDominos()) {
            Players player = board.getPlayedBy().get(index);
            if (player == Players.Human) {
                // Human's move (left-aligned)
                firstLine += "[" + domino.getLeftNumDots() + " " + domino.getRightNumDots() + "]";
            } else {
                // Computer's move (indented)
                secondLine += "[" + domino.getLeftNumDots() + " " + domino.getRightNumDots() + "]";
            }
            index++;
        }

        // Print the board
        System.out.println(firstLine);
        System.out.println("  " + secondLine); // Indent the computer's moves
    }

    private boolean isValidIndex(int index, int size) {
        return index >= 0 && index < size;
    }

    private void checkGameEnd() {
        boolean validHuman = hasValidMove(human.getTray(), board);
        boolean validComputer = hasValidMove(computer.getTray(), board);
        if (!validHuman && !validComputer) {
            if (human.getTray().isEmpty()) {
                System.out.println("Ending the game");
                System.out.println("The winner is human");
                gameOver = true;
            } else if (computer.getTray().isEmpty()) {
                System.out.println("Ending the game");
                System.out.println("The winner is computer");
                gameOver = true;
            } else {
                int humanSum = 0;
                for (Domino d : human.getTray()) {
                    humanSum += d.getLeftNumDots() + d.getRightNumDots();
                }
                int computerSum = 0;
                for (Domino d : computer.getTray()) {
                    computerSum += d.getLeftNumDots() + d.getRightNumDots();
                }
                String winner;
                if (humanSum < computerSum) {
                    winner = "human";
                } else if (computerSum < humanSum) {
                    winner = "computer";
                } else {
                    winner = "no-one. It's a tie!";
                }
                System.out.println("Ending the game");
                System.out.println("The winner is " + winner);
                gameOver = true;
            }
        }
    }

    public void distributeDominos() {
        for (int i = 0; i < startingDominos; i++) {
            Domino d1 = board.drawFromBoneyard();
            if (d1 != null) {
                human.addDiceToPlayerTray(d1);
            }
            Domino d2 = board.drawFromBoneyard();
            if (d2 != null) {
                computer.addDiceToPlayerTray(d2);
            }
        }
    }

    public Board getBoard() {
        return board;
    }
}