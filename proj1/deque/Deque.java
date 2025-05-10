package deque;

//can not be instantiated because it is an interface
public interface Deque<Item> {
    public void addFirst(Item item);

    /**
     * Add last item before sentinel node
     */
    public void addLast(Item item);

    /**
     * Return if size equals to zero
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Return the int size of the deque
     */
    public int size();

    /**
     * Print whole Deque for one loop
     */
    public void printDeque();

    /**
     * Remove the node next to sentinel node
     */
    public Item removeFirst();

    /**
     * Remove the node previous to sentinel node
     */
    public Item removeLast();

    /**
     * method to get No.Index node
     */
    public Item get(int index);

}
