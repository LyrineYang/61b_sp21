package deque;

import java.util.Comparator;
/* @author Lyrine Yang */
public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> itemComparator;
    public MaxArrayDeque(Comparator<T> c) {
        super();
        if (c == null) {
            return;
        }
        itemComparator = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T max = get(0);
        for (int i = 1; i < size(); i++) {
            if (itemComparator.compare(get(i), max) > 0) {
                max = get(i);
            }
        }
        return max;
    }
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T max = get(0);
        for (int i = 1; i < size(); i++) {
            if (c.compare(get(i), max) > 0) {
                max = get(i);
            }
        }
        return max;
    }
}

