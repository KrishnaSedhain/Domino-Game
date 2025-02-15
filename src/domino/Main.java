package domino;

import java.util.Scanner;
import java.util.ArrayList;

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
            playTurn();
            checkGameEnd();
        }
    }

    private void playTurn() {
        if (currentPlayer == Players.Human) {
            humanTurn();
        } else {
            computerTurn();
        }
    }

    private void computerTurn() {
        System.out.println("Computer's turn to play");
        if (hasValidMove(computer.getTray(), board)) {
            playDomino(computer);
        } else {
            drawFromBoneyard(computer);
        }
        currentPlayer = Players.Human;
    }

    private void humanTurn() {
        System.out.println("It's your turn");
        printHumanTray();
        System.out.println("[p] Play Domino  [d] Draw from boneyard  [q] Quit");
        String option = inputScanner.next();

        switch (option) {
            case "p" -> {
                System.out.println("Enter domino index:");
                if (inputScanner.hasNextInt()) {
                    int index = inputScanner.nextInt();
                    if (isValidIndex(index, human.getTray().size())) {
                        playSelectedDomino(human, index);
                    } else {
                        System.out.println("Invalid selection. Please choose a valid index.");
                    }
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
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
        currentPlayer = Players.Computer;
    }

    private void playSelectedDomino(Player player, int index) {
        Domino domino = player.getTray().get(index);
        System.out.println("Play [l]eft or [r]ight?");
        String side = inputScanner.next();
        System.out.println("Rotate domino? [y/n]");
        String rotate = inputScanner.next();

        if (rotate.equalsIgnoreCase("y")) {
            domino.rotateDomino();
        }

        if (isValidMove(domino, side)) {
            if (side.equalsIgnoreCase("l")) {
                board.placeOnLeft(domino);
            } else {
                board.placeOnRight(domino);
            }
            player.getTray().remove(index);
            System.out.println("Played: " + domino);
        } else {
            System.out.println("Invalid move. Try again.");
        }
    }

    private void playDomino(Player player) {
        for (int i = 0; i < player.getTray().size(); i++) {
            Domino domino = player.getTray().get(i);
            if (isValidMove(domino, "l")) {
                board.placeOnLeft(domino);
                player.getTray().remove(i);
                System.out.println("Computer played: " + domino + " on the left");
                return;
            } else if (isValidMove(domino, "r")) {
                board.placeOnRight(domino);
                player.getTray().remove(i);
                System.out.println("Computer played: " + domino + " on the right");
                return;
            }
        }
    }

    private void drawFromBoneyard(Player player) {
        Domino domino = board.drawFromBoneyard();
        if (domino != null) {
            player.getTray().add(domino);
            System.out.println((player == human ? "You" : "Computer") + " drew a domino from the boneyard.");
        } else {
            System.out.println("Boneyard is empty.");
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

    private boolean hasValidMove(ArrayList<Domino> tray, Board board) {
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

    private void checkGameEnd() {
        if (human.getTray().isEmpty()) {
            System.out.println("You win! Your tray is empty.");
            gameOver = true;
        } else if (computer.getTray().isEmpty()) {
            System.out.println("Computer wins! Its tray is empty.");
            gameOver = true;
        } else if (!hasValidMove(human.getTray(), board) &&
                !hasValidMove(computer.getTray(), board)) {
            System.out.println("No valid moves left. Game over!");
            gameOver = true;
        }
    }

    private void printHumanTray() {
        System.out.println("Your Tray:");
        for (int i = 0; i < human.getTray().size(); i++) {
            Domino domino = human.getTray().get(i);
            System.out.println(i + ": [" + domino.getLeftNumDots() + " " + domino.getRightNumDots() + "]");
        }
    }

    private void distributeDominos() {
        for (int i = 0; i < startingDominos; i++) {
            human.addDiceToPlayerTray(board.drawFromBoneyard());
            computer.addDiceToPlayerTray(board.drawFromBoneyard());
        }
    }

    private boolean isValidIndex(int index, int size) {
        return index >= 0 && index < size;
    }
}