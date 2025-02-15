package domino;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

public class Board {

    private ArrayList<Domino> boneyard; // Holds available dominos
    private Deque<Domino> playedDominos; // Holds dominos played on the board
    private final int dominoSetSize = 7; // Size of the domino set

    // Constructor initializes the board and shuffles the dominos
    public Board() {
        boneyard = new ArrayList<>();
        playedDominos = new LinkedList<>();
        initializeDominoSet();
    }

    // Initialize the domino set and shuffle it
    private void initializeDominoSet() {
        for (int i = 0; i <= dominoSetSize; i++) {
            for (int j = i; j <= dominoSetSize; j++) {
                boneyard.add(new Domino(i, j));
            }
        }
        Collections.shuffle(boneyard);
    }

    // Pick a domino from the boneyard
    public Domino drawFromBoneyard() {
        if (boneyard.isEmpty()) {
            System.out.println("Boneyard is empty!");
            return null;
        }
        return boneyard.remove(0);
    }

    // Add a domino to the left end of the board
    public void placeOnLeft(Domino domino) {
        playedDominos.addFirst(domino);
    }

    // Add a domino to the right end of the board
    public void placeOnRight(Domino domino) {
        playedDominos.addLast(domino);
    }

    // Get the list of played dominos
    public Deque<Domino> getPlayedDominos() {
        return playedDominos;
    }

    // Get the first played domino
    public Domino getFirstPlayedDomino() {
        return playedDominos.getFirst();
    }

    // Get the last played domino
    public Domino getLastPlayedDomino() {
        return playedDominos.getLast();
    }

    // Check if the boneyard is empty
    public boolean isBoneyardEmpty() {
        return boneyard.isEmpty();
    }

    // Get the number of dominos in the boneyard
    public int getBoneyardSize() {
        return boneyard.size();
    }
}