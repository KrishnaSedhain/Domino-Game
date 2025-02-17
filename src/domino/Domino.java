package domino;

/**
 * Represents a single domino tile with two numbered sides.
 *
 * @author Krishna Sedhain
 */
public class Domino {
    private int leftNumDots;
    private int rightNumDots;

    /**
     * Constructs a domino with specified dot counts on each side.
     *
     * @param leftNumDots  Number of dots on the left side.
     * @param rightNumDots Number of dots on the right side.
     */
    public Domino(int leftNumDots, int rightNumDots) {
        this.leftNumDots = leftNumDots;
        this.rightNumDots = rightNumDots;
    }

    /**
     * Rotates the domino by swapping the left and right dot values.
     */
    public void rotateDomino() {
        int temp = this.leftNumDots;
        this.leftNumDots = this.rightNumDots;
        this.rightNumDots = temp;
    }

    /**
     * Retrieves the number of dots on the left side of the domino.
     *
     * @return The number of dots on the left side.
     */
    public int getLeftNumDots() {
        return leftNumDots;
    }

    /**
     * Retrieves the number of dots on the right side of the domino.
     *
     * @return The number of dots on the right side.
     */
    public int getRightNumDots() {
        return rightNumDots;
    }

    /**
     * Returns a string representation of the domino in the format [left right].
     *
     * @return A string representing the domino's dot values.
     */
    @Override
    public String toString() {
        return "[" + leftNumDots + " " + rightNumDots + "]";
    }

}
