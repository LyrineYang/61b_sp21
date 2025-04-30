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
    private int firstIndex = getFirstIndex();
    private void reSize(int cap) {
        Item[] t = (Item[]) new Object[cap];
        if (firstIndex < nextLast) {
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
        if (nextFirst == 0) {
            nextFirst = items.length - 1;
        } else {
            nextFirst -= 1;
        }
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
        if (firstIndex < nextLast) {
            for (int i = firstIndex; i < nextLast; i++) {
                System.out.print(items[i] + " ");
            }
            System.out.println();
        } else {
            for (int i = firstIndex; i < items.length; i++) {
                System.out.print(items[i] + " ");
            }
            for (int i = 0; i < nextLast; i++) {
                System.out.println(items[i] + " ");
            }
            System.out.println();
        }
    }
    public Item removeFirst() {
        Item first = items[firstIndex];
        if (size == 0) {
            return null;
        }
        items[firstIndex] = null;
        nextFirst = firstIndex;
        size -= 1;
        checkUsageFactor();
        return first;
    }
    public Item removeLast() {
        Item last = items[nextLast - 1];
        if (size == 0) {
            return null;
        }
        items[nextLast - 1] = null;
        nextLast -= 1;
        size -= 1;
        checkUsageFactor();
        return last;
    }
    public Item get(int index) {
        return items[(firstIndex + index) % items.length];
    }
    private void checkUsageFactor() {
        double usageFactor = (double) size / items.length;
        if (usageFactor < 0.25 && size >= 16) {
            reSize(items.length / 2);
        }
    }
}
