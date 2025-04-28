package deque;

public class LinkedListDeque<T> {
    private int size;
    /** Declaration of sentinel node */
    private Node sentinel;
    private class Node {
        private T item;
        private Node next;
        private Node prev;
        /** Create data structure Node */
        public Node (T i, Node n, Node p) {
            item = i;
            next = n;
            prev = p;
        }
    }
    /** Create an empty LinkedListDeque with sentinel node */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }
    /** Add first item behind sentinel node */
    public void addFirst(T item) {
        sentinel.next = new Node(item, sentinel.next, sentinel);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }
    /** Add last item before sentinel node */
    public void addLast(T item) {
        sentinel.prev = new Node(item, sentinel, sentinel.prev);
        sentinel.prev.prev.next = sentinel.prev;
        size += 1;
    }
    /** Return if size equals to zero */
    public boolean isEmpty() { return size == 0; }
    /** Return the int size of the deque */
    public int size() {
        return size;
    }
    /** Print whole Deque for one loop */
    public void printDeque() {
        Node p = sentinel;
        for (int i = 0; i < this.size; i++) {
            System.out.print(p.next.item + " ");
            p = p.next;
        }
            System.out.println();
    }
    /** Remove the node next to sentinel node */
    public T removeFirst() {
        if (size == 0) {
            return null;
        } else {
            size -= 1;
            T first = sentinel.next.item;
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
            return first;
        }
    }
    /** Remove the node previous to sentinel node */
    public T removeLast() {
        if (size == 0) {
            return null;
        } else {
            size -= 1;
            T last = sentinel.prev.item;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            return last;
        }
    }
    /** Iteration method to get index node */
    public T get(int index) {
        return getHelper(index, size);
    }
    private T getHelper(int index, int length) {
        Node p = sentinel.next;
        while (length > 0) {
            if (index == 0) {
                return p.item;
            }
            length -= 1;
            index -= 1;
            p = p.next;
        }
        return null;
    }
    /** Recursion method to get index node */
    public T getRecursive(int index) {
        return getRecursiveHelper(index, sentinel.next, 0);
    }
    private T getRecursiveHelper (int i, Node pointer, int recursionDepth) {
        if (recursionDepth > size) {
            return null;
        } else if (i == 0) {
            return pointer.item;
        } else {
            return getRecursiveHelper(i - 1, pointer.next, recursionDepth + 1);
        }
    }

}
