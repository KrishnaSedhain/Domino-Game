package domino;

import java.util.ArrayList;

/**
 * Enum representing the players in the game.
 * It includes two types of players: Human and Computer.
 * @author Krishna Sedhain
 */
enum Players {
    Human,
    Computer;
}

/**
 * The Player class represents a player in the game, either a human or a computer.
 * Each player has a tray that holds their dominos.
 */
public class Player {

    /**
     * The tray containing the dominos held by the player.
     */
    private ArrayList<Domino> tray = new ArrayList<>();

    /**
     * The type of the current player (either Human or Computer).
     */
    private Players currPlayer;

    /**
     * Constructs a Player with a specified player type.
     *
     * @param currPlayer The type of player (Human or Computer).
     */
    public Player(Players currPlayer) {
        this.currPlayer = currPlayer;
    }

    /**
     * Adds a domino to the player's tray.
     *
     * @param dice The domino to be added to the tray.
     */
    public void addDominoToPlayerTray(Domino dice) {
        tray.add(dice);
    }

    /**
     * Removes a domino from the player's tray at the specified index.
     *
     * @param index The index of the domino to be removed.
     */
    public void removedDominoFromPlayerTray(int index) {
        this.tray.remove(index);
    }

    /**
     * Retrieves the player's tray containing their dominos.
     *
     * @return An ArrayList of dominos in the player's tray.
     */
    public ArrayList<Domino> getTray() {
        return tray;
    }

    /**
     * Retrieves a specific domino from the player's tray at the given index.
     *
     * @param index The index of the domino to retrieve.
     * @return The domino at the specified index in the tray.
     */
    public Domino getDominoFromTray(int index) {
        return tray.get(index);
    }
}
