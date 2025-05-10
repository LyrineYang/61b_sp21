package deque;

//can not be instantiated because it is an interface
public interface Deque<T> {

    void addFirst(T item);

    /**
     * Add last item before sentinel node
     */
    void addLast(T item);

    /**
     * Return if size equals to zero
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Return the int size of the deque
     */
    int size();

    /**
     * Print whole Deque for one loop
     */
    void printDeque();

    /**
     * Remove the node next to sentinel node
     */
    T removeFirst();

    /**
     * Remove the node previous to sentinel node
     */
    T removeLast();

    /**
     * method to get No.Index node
     */
    T get(int index);

}
