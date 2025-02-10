package domino;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

public class Board {
    private ArrayList<Domino> availableDice;
    private Deque<Domino> playedDice;
    private int numberOfDices = 7;

    public Board() {
        initializeBoard();
    }

    private void initializeBoard() {
        availableDice = new ArrayList<>();
        playedDice = new LinkedList<>();
        for (int i = 0; i < numberOfDices; i++) {
            for (int j = i; j < numberOfDices; j++) {
                Domino dice = new Domino(i, j);
                availableDice.add(dice);
            }
        }
        Collections.shuffle(availableDice);
    }

    public Domino pickADiceFromBoneyard() {
        if (availableDice.isEmpty()) {
            System.out.println("Boneyard is empty");
            return null;
        }
        return availableDice.remove(0);
    }


    public void addToBoardRight(Domino dice) {
        playedDice.addLast(dice);
    }


}

