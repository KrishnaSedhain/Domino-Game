package domino;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Represents the game board for a domino game, managing available and played dominos.
 */
public class Board {
    private ArrayList<Domino> availableDice;
    private Deque<Domino> playedDomino;
    private int numberOfDices = 7;

    /**
     * Constructs a Board and initializes it with a shuffled set of dominos.
     */
    public Board(){
        initializeBoard();
    }

    /**
     * Initializes the board by generating dominos and shuffling them.
     */
    private void initializeBoard() {
        availableDice = new ArrayList<>();
        playedDomino = new LinkedList<>();
        for (int i = 0; i < numberOfDices; i++) {
            for (int j = i; j < numberOfDices; j++) {
                Domino dice = new Domino(i, j);
                availableDice.add(dice);
            }
        }
        Collections.shuffle(availableDice);
    }

    /**
     * Retrieves the list of available dominos in the boneyard.
     *
     * @return List of available dominos or null if empty.
     */
    public ArrayList<Domino> getAvailableDomino() {
        if (this.availableDice.size() == 0){
            return null;
        }
        return availableDice;
    }

    /**
     * Draws a domino from the boneyard.
     *
     * @return The drawn domino or null if the boneyard is empty.
     */
    public Domino drawFromBoneyard(){
        if (availableDice.size() == 0){
            System.out.println("Boneyard is empty");
            return null;
        }
        Domino dice = availableDice.get(0);
        availableDice.remove(0);
        return dice;
    }

    /**
     * Places a domino on the left end of the played dominos.
     *
     * @param dice The domino to place on the left.
     */
    public void placeOnLeft(Domino dice){
        playedDomino.addFirst(dice);
    }

    /**
     * Places a domino on the right end of the played dominos.
     *
     * @param dice The domino to place on the right.
     */
    public void placeOnRight(Domino dice){
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
    public Domino getFirstPlayedDomino(){
        return playedDomino.getFirst();
    }

    /**
     * Retrieves the total number of dominos in the boneyard.
     *
     * @return The number of dominos in the boneyard.
     */
    public int getBoneyardSize() {
        return numberOfDices;
    }
}
