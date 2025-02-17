package domino;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Represents the game board for a domino game, managing available and played dominos.
 * @author Krishna Sedhain
 */
public class Board {
    private ArrayList<Domino> availableDice;
    private Deque<Domino> playedDomino;
    // Represents the number of unique domino values (e.g., if maxDots is 6, dominoRange will be 7 for values 0-6)
    private int dominoRange;

    /**
     * Default constructor that initializes the board with a traditional domino set (0-6).
     */
    public Board() {
        this(6); // Default max dots is 6.
    }

    /**
     * Constructs a Board with the specified maximum number of dots on one side of a domino.
     * For example, if maxDots is 6, the domino values will range from 0 to 6.
     *
     * @param maxDots the maximum number of dots on one side of a domino.
     */
    public Board(int maxDots) {
        if (maxDots < 0) {
            throw new IllegalArgumentException("Maximum dots must be non-negative.");
        }
        // dominoRange is one more than maxDots since values range from 0 to maxDots.
        this.dominoRange = maxDots + 1;
        initializeBoard();
    }

    /**
     * Initializes the board by generating a domino set and shuffling them.
     */
    private void initializeBoard() {
        availableDice = new ArrayList<>();
        playedDomino = new LinkedList<>();
        // Generate domino set for values 0 to (dominoRange - 1)
        for (int i = 0; i < dominoRange; i++) {
            for (int j = i; j < dominoRange; j++) {
                Domino dice = new Domino(i, j);
                availableDice.add(dice);
            }
        }
        Collections.shuffle(availableDice);
    }

    /**
     * Retrieves the list of available dominos in the boneyard.
     *
     * @return List of available dominos, or null if empty.
     */
    public ArrayList<Domino> getAvailableDomino() {
        if (this.availableDice.isEmpty()) {
            return null;
        }
        return availableDice;
    }

    /**
     * Draws a domino from the boneyard.
     *
     * @return The drawn domino, or null if the boneyard is empty.
     */
    public Domino drawFromBoneyard() {
        if (availableDice.isEmpty()) {
            System.out.println("Boneyard is empty");
            return null;
        }
        // Remove and return the first domino.
        return availableDice.remove(0);
    }

    /**
     * Places a domino on the left end of the played dominos.
     *
     * @param dice The domino to place on the left.
     */
    public void placeOnLeft(Domino dice) {
        playedDomino.addFirst(dice);
    }

    /**
     * Places a domino on the right end of the played dominos.
     *
     * @param dice The domino to place on the right.
     */
    public void placeOnRight(Domino dice) {
        playedDomino.addLast(dice);
    }

    /**
     * Retrieves the deque of played dominos.
     *
     * @return Deque containing the played dominos.
     */
    public Deque<Domino> getPlayedDomino() {
        return playedDomino;
    }

    /**
     * Retrieves the first domino placed on the board.
     *
     * @return The first played domino.
     */
    public Domino getFirstPlayedDomino() {
        return playedDomino.getFirst();
    }

    /**
     * Retrieves the total number of dominos remaining in the boneyard.
     *
     * @return The number of dominos remaining.
     */
    public int getBoneyardSize() {
        return availableDice.size();
    }

    /**
     * Retrieves the domino range which is (maxDots + 1).
     * This represents the number of unique domino values.
     *
     * @return The domino range.
     */
    public int getDominoRange() {
        return dominoRange;
    }

}
