package deque;

/* key in LinkedListDeque: view sentinel and every node as node but not kind of tree or list!
* @author Lyrine Yang
* The invariants:
* sentinel Node, first which is sentinel Node's next, the last which is sentinel Node's prev
* */

public class LinkedListDeque<T> {
    private int size;
    /* Declaration of sentinel node */
    private Node sentinel;

    /** create data structure based on nested class Node */
    private class Node {
        private T item;
        private Node next;
        private Node prev;

        /** Create Node include prev and next */
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
    public boolean isEmpty() {return size == 0;}

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
        Node p = sentinel.next;
        if(index >= size || index < 0) {
            return null;
        }
        while (index != 0) {
            p = p.next;
            index -= 1;
        }
        return p.item;
    }

    /** Recursion method to get index node */
    public T getRecursive(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return getRecursiveHelper(index, sentinel.next);
    }
    private T getRecursiveHelper(int index, Node p) {
        if (index == 0) {
            return p.item;
        }
        return getRecursiveHelper(index - 1, p.next);
    }
}
