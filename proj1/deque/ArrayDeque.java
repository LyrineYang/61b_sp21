package deque;

import java.util.Iterator;

/*View array as current data strcture to archive first and last pointer
* @author Lyrine Yang
* */
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private T[] items;
    private int nextFirst, nextLast;
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Deque<?> otherDeque) {
            if (size() != otherDeque.size()) {
                return false;
            }
            for (int i = 0; i < size(); i += 1) {
                if (!this.get(i).equals(otherDeque.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    /** returns an iterator for arrayDeque */
    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
    private class ArrayDequeIterator implements Iterator<T> {
        private int position;
        private ArrayDequeIterator() {
            position = 0;
        }
        @Override
        public boolean hasNext() {
            return position < size();
        }
        @Override
        public T next() {
            if (!hasNext()) {
                return null;
            }
            T item = get(position);
            position += 1;
            return item;
        }
    }
    private int getFirstIndex() {
        return (nextFirst + 1) % items.length;
    }
    private int getLastIndex() {
        return (nextLast - 1 + items.length) % items.length;
    }
    private void reSize(int cap) {
        int firstIndex = getFirstIndex();
        T[] newArray = (T[]) new Object[cap];
        if (getFirstIndex() < nextLast) {
            System.arraycopy(items, firstIndex, newArray, 0, size);
        } else {
            System.arraycopy(items, firstIndex, newArray, 0, items.length - firstIndex);
            System.arraycopy(items, 0, newArray, items.length - firstIndex, nextLast);
        }
        items = newArray;
        nextLast = size;
        nextFirst = items.length - 1;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            reSize(2 * size);
        }
        items[nextFirst] = item;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size += 1;
    }
    @Override
    public void addLast(T item) {
        if (size == items.length) {
            reSize(2 * size);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;
        size += 1;
    }
    @Override
    public void printDeque() {
        for (T i : this) {
            System.out.print(i + " ");
        }
        System.out.println();
    }
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        int firstIndex = getFirstIndex();
        T first = items[firstIndex];
        items[firstIndex] = null;
        nextFirst = firstIndex;
        size -= 1;
        checkUsageFactor();
        return first;
    }
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        int lastIndex = getLastIndex();
        T last = items[lastIndex];
        items[lastIndex] = null;
        nextLast = lastIndex;
        size -= 1;
        checkUsageFactor();
        return last;
    }
    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        int firstIndex = getFirstIndex();
        return items[(firstIndex + index) % items.length];
    }
    private static final int MIN_RESIZE_LENGTH = 16;
    private void checkUsageFactor() {
        double usageFactor = (double) size / items.length;
        if (usageFactor < 0.25 && items.length >= MIN_RESIZE_LENGTH) {
            reSize(items.length / 2);
        }
    }
}
