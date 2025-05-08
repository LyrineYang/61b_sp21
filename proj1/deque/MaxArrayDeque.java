package deque;

import java.util.Comparator;

public class MaxArrayDeque<Item> extends ArrayDeque<Item>{
    private Comparator<Item> elementComparator;
    public MaxArrayDeque(Comparator<Item> c) {
        super();
        elementComparator = c;
    }
    public Item max() {
        if (size() == 0 || elementComparator == null) {
            return null;
        }
        Item max = get(0);
        for (int i = 0; i < size(); i++) {
            if (elementComparator.compare(get(i), max) > 0) {
                max = get(i);
            }
        }
        return max;
    }
    public Item max(Comparator<Item> c) {
        if (size() == 0) {
            return null;
        }
        Item max = get(0);
        for (int i = 0; i < size(); i++) {
            if (c.compare(get(i), max) > 0) {
                max = get(i);
            }
        }
        return max;
    }
}

