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

    public void rotateDomino() {
        int temp = this.leftNumDots;
        this.leftNumDots = this.rightNumDots;
        this.rightNumDots = temp;
    }


    public int getRightNumDots() {
        return rightNumDots;
    }
}

