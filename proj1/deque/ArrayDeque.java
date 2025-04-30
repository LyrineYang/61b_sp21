package deque;

public class ArrayDeque<Item> {
    private int size;
    private Item[] items;
    private int nextFirst, nextLast;
    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }
    private int getFirstIndex () {
        return (nextFirst + 1) % items.length;
    }
    private int getLastIndex () {
        return (nextLast - 1) % items.length;
    }
    private void reSize(int cap) {
        int firstIndex = getFirstIndex();
        Item[] t = (Item[]) new Object[cap];
        if (getFirstIndex() < nextLast) {
            System.arraycopy(items, firstIndex, t, 0, size);
        } else {
            System.arraycopy(items, firstIndex, t, 0, items.length - firstIndex);
            System.arraycopy(items, 0, t, items.length - firstIndex, nextLast);
        }
        items = t;
        nextLast = size;
        nextFirst = items.length - 1;
    }
    public int size() {
        return size;
    }
    public void addFirst(Item item) {
        if (size == items.length) {
            reSize(2 * size);
        }
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size += 1;
    }
    public void addLast(Item item) {
        if (size == items.length) {
            reSize(2 * size);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size += 1;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public void printDeque() {
        int firstIndex = getFirstIndex();
        int lastIndex = getLastIndex();
        if (firstIndex < lastIndex) {
            for (int i = firstIndex; i <= lastIndex; i++) {
                System.out.print(items[i] + " ");
            }
            System.out.println();
        } else {
            for (int i = firstIndex; i < items.length; i++) {
                System.out.print(items[i] + " ");
            }
            for (int i = 0; i <= lastIndex; i++) {
                System.out.print(items[i] + " ");
            }
            System.out.println();
        }
    }
    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        }
        int firstIndex = getFirstIndex();
        Item first = items[firstIndex];
        items[firstIndex] = null;
        nextFirst = firstIndex;
        size -= 1;
        checkUsageFactor();
        return first;
    }
    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }
        int lastIndex = getLastIndex();
        Item last = items[lastIndex];
        items[lastIndex] = null;
        nextLast = lastIndex;
        size -= 1;
        checkUsageFactor();
        return last;
    }
    public Item get(int index) {
        int firstIndex = getFirstIndex();
        return items[(firstIndex + index) % items.length];
    }
    private void checkUsageFactor() {
        double usageFactor = (double) size / items.length;
        if (usageFactor < 0.25 && items.length >= 16) {
            reSize(items.length / 2);
        }
    }
}
