package deque;
/* key in LinkedListDeque: view sentinel and every node as node but not kind of tree or list!
* @author Lyrine Yang
* The invariants:
* sentinel Node, first which is sentinel Node's next, the last which is sentinel Node's prev
* */

import java.util.Iterator;
/** iterable data structure, and it is a Deque */
public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    /* Declaration of sentinel node */
    private final Node sentinel;

    /** to return a iterator for LinkedListDeque */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }
    /** nested class to build up LinkedListDeque iterator */
    private class LinkedListDequeIterator implements Iterator<T> {
        private Node currentNode;
        private LinkedListDequeIterator() {
            currentNode = sentinel.next;
        }
        @Override
        public boolean hasNext() {
            return currentNode != sentinel;
        }
        @Override
        public T next() {
            if (!hasNext()) {
                return null;
            }
            T currentItem = currentNode.item;
            currentNode = currentNode.next;
            return currentItem;
        }
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
    /** create data structure based on nested class Node */
    private class Node {
        private final T item;
        private Node next;
        private Node prev;

        /** Create Node include prev and next */
        private Node(T i, Node n, Node p) {
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
    @Override
    public void addFirst(T item) {
        Node newNode = new Node(item, sentinel.next, sentinel);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size += 1;
    }

    /** Add last item before sentinel node */
    @Override
    public void addLast(T item) {
        Node newNode = new Node(item, sentinel, sentinel.prev);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size += 1;
    }

    /** Return the int size of the deque */
    @Override
    public int size() {
        return size;
    }

    /** Print whole Deque for one loop */
    @Override
    public void printDeque() {
        for (T i : this) {
            System.out.print(i + " ");
        }
        System.out.println();
    }
    /** Remove the node next to sentinel node */
    @Override
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
    @Override
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
    @Override
    public T get(int index) {
        Node p = sentinel.next;
        if (index >= size || index < 0) {
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
