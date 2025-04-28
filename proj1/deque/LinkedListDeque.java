package deque;

public class LinkedListDeque<T> {
    private int size;
    /** Create sentinel node */
    private Node sentinel;
    private class Node {
        private T item;
        private Node next;
        private Node prev;

        public Node (T i, Node n, Node p) {
            item = i;
            next = n;
            prev = p;
        }
    }
    /** Create an empty LinkedListDeque */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }
    public void addFirst(T item) {
        sentinel.next = new Node(item, sentinel.next, sentinel.prev);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
    }
    public void addLast(T item) {
        sentinel.prev = new Node(item, sentinel.next, sentinel.prev);
        sentinel.prev.prev.next = sentinel.prev;
        size += 1;
    }
    /** Return if size equals to zero */
    public boolean isEmpty() {
        return size == 0;
    }
    /** Return the int size of the deque */
    public int size() {
        return size;
    }
    /** Print deque for size times */
    public void printDeque() {
        printSizeTimes(size);
    }
    /** Helper function for function printDeque */
    private void printSizeTimes(int times) {
        Node p = sentinel;
        while (times != 0) {
            System.out.print(p.next.item);
            System.out.print(' ');
            p = p.next;
            times -= 1;
        }
    }


}
