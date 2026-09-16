import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;


public class PrioritySet<E> extends PriorityQueue<Solution> {
    private final int maxSize;

    public PrioritySet(int maxSize) {
        super(maxSize, Comparator.comparingInt((Solution o) -> o.evaluation).reversed());
        this.maxSize = maxSize;
    }

    @Override
    public boolean offer(Solution e) {
        if(contains(e)){
            return false;
        }
        if (size() < maxSize) {
            super.offer(e);
            return true;
        } else {
            Solution worstElement = removeWorst();
            if (compare(e, worstElement) >= 0) {
                super.offer(e);
                return true;
            } else {
                super.offer(worstElement);
            }
        }
        return false;
    }

    private int compare(Solution e, Solution worstElement) {
        return e.evaluation - worstElement.evaluation;
    }

    private Solution removeWorst() {
        Solution worstElement = peek();
        for (Solution current : this) {
            if (compare(current, worstElement) <= 0) {
                worstElement = current;
            }
        }
        remove(worstElement);
        return worstElement;
    }

    public ArrayList<Solution> getBest(int m) {
        ArrayList<Solution> best = new ArrayList<>();
        PriorityQueue<Solution> copyQueue = new PriorityQueue<>(this);
        for (int i = 0; i < m && !copyQueue.isEmpty(); i++) {
            best.add(copyQueue.poll());
        }
        return best;
    }
}
