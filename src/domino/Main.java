package domino;
import java.util.Scanner;

public class Main {

    private final int startingDices = 7;
    private final Board board = new Board();
    private final Player human = new Player(Players.Human);
    private final Player computer = new Player(Players.Computer);
    private Players isPlaying = Players.Human;
    private final Scanner inputScanner = new Scanner(System.in);
    private boolean gameOver = false;

    public static void main(String[] args) {
        new Main().startGame();
    }

    public void startGame() {
        giveDicesToPlayers();
        while (!gameOver) {
            playTurn();
        }
    }

    private void playTurn() {
        switch (isPlaying) {
            case Human -> playDiceForHuman();
            case Computer -> playDiceForComputer();
        }
    }

    private void playDiceForComputer() {
        System.out.println("Computer's turn to play");
        playDice(computer);
        isPlaying = Players.Human;
    }

    private void playDiceForHuman() {
        System.out.println("It's your turn");
        printHumanTray();
        System.out.println("[p] Play Domino  [q] Quit");
        String option = inputScanner.next();

        if ("p".equals(option)) {
            System.out.println("Enter domino index:");
            if (inputScanner.hasNextInt()) {
                int index = inputScanner.nextInt();
                if (isValidIndex(index, human.getTray().size())) {
                    playSelectedDomino(human, index);
                } else {
                    System.out.println("Invalid selection. Please choose a valid index.");
                }
            }
        } else if ("q".equals(option)) {
            System.out.println("Quitting Game");
            gameOver = true;
        }
        isPlaying = Players.Computer;
    }

    private void playDice(Player player) {
        if (!player.getTray().isEmpty()) {
            Domino dice = player.getTray().remove(0);
            board.addToBoardRight(dice);
            System.out.println((player == computer ? "Computer" : "You") + " played " + dice);
        } else {
            System.out.println((player == computer ? "Computer" : "You") + " have no dominos left.");
        }
    }

    private void playSelectedDomino(Player player, int index) {
        Domino dice = player.getTray().remove(index);
        board.addToBoardRight(dice);
        System.out.println("You played " + dice);
    }

    private boolean isValidIndex(int index, int size) {
        return index >= 0 && index < size;
    }

    private void printHumanTray() {
        System.out.println("Your Tray:");
        for (int i = 0; i < human.getTray().size(); i++) {
            Domino dice = human.getTray().get(i);
            System.out.println(i + ": [" + dice.getLeftNumDots() + " " + dice.getRightNumDots() + "]");
        }
    }

    private void giveDicesToPlayers() {
        for (int i = 0; i < startingDices; i++) {
            human.addDiceToPlayerTray(board.pickADiceFromBoneyard());
            computer.addDiceToPlayerTray(board.pickADiceFromBoneyard());
        }
    }
}
