package domino;
public class Domino {
    private int leftNumDots;
    private int rightNumDots;

    public Domino(int leftNumDots, int rightNumDots) {
        this.leftNumDots = leftNumDots;
        this.rightNumDots = rightNumDots;
    }

    public int getLeftNumDots() {
        return leftNumDots;
    }


    public int getRightNumDots() {
        return rightNumDots;
    }
}

