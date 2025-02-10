package domino;
import java.util.ArrayList;


enum Players {
    Human,
    Computer;
}
public class Player {
    private ArrayList<Domino> tray = new ArrayList<>();
    private Players currPlayer;


    public Player(Players currPlayer){
        this.currPlayer = currPlayer;
    }


    public void addDiceToPlayerTray(Domino dice){
        tray.add(dice);
    }


    public void removedDiceFromPlayerTray(int index){
        this.tray.remove(index);
    }

    public ArrayList<Domino> getTray() {
        return tray;
    }


    public Domino getDiceFromTray(int index){
        return tray.get(index);
    }
}
