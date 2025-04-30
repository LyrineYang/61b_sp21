package deque; // 确保这个包名和你的 ArrayDeque 类所在的包匹配

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Performs some basic array deque tests.
 */
public class ArrayDequeTest {

    @Test
    /** Adds a few things to the deque, checking isEmpty() and size() are correct,
     * finally printing the results. */
    public void addIsEmptySizeTest() {
        System.out.println("Running add/isEmpty/Size test.");

        ArrayDeque<String> ad1 = new ArrayDeque<String>();

        // A newly initialized deque should be empty
        assertTrue("A newly initialized ArrayDeque should be empty", ad1.isEmpty());

        ad1.addFirst("front");
        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, ad1.size());
        assertFalse("ad1 should now contain 1 item", ad1.isEmpty());

        ad1.addLast("middle");
        assertEquals(2, ad1.size());

        ad1.addLast("back");
        assertEquals(3, ad1.size());

        System.out.println("Printing out deque: ");
        ad1.printDeque(); // Assuming your printDeque() works for debugging visualization
    }

    @Test
    /** Adds an item, then removes an item, and ensures that deque is empty afterwards. */
    public void addRemoveTest() {
        System.out.println("Running add/remove test.");

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());

        ad1.removeFirst();
        // should be empty
        assertTrue("ad1 should be empty after removal", ad1.isEmpty());

        // Test removing last
        ad1.addLast(20);
        assertFalse("ad1 should contain 1 item", ad1.isEmpty());
        ad1.removeLast();
        assertTrue("ad1 should be empty after removeLast", ad1.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {
        System.out.println("Running remove from empty test.");

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        ad1.addFirst(3); // Add one item

        // Remove enough times to make it empty and test removing from empty
        ad1.removeLast();  // Size becomes 0
        ad1.removeFirst(); // Try removing from empty
        ad1.removeLast();  // Try removing from empty again
        ad1.removeFirst(); // Try removing from empty again

        int size = ad1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size); // Size should still be 0
    }

    @Test
    /* Check if you can create ArrayDeques with different parameterized types*/
    public void multipleParamTest() {
        System.out.println("Running multiple parameter type test.");

        ArrayDeque<String>  ad1 = new ArrayDeque<String>();
        ArrayDeque<Double>  ad2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<Boolean>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        // Retrieving items to ensure type compatibility
        String s = ad1.removeFirst();
        assertEquals("string", s);

        double d = ad2.removeFirst();
        assertEquals(3.14159, d, 0.00001); // Use delta for double comparison

        boolean b = ad3.removeFirst();
        assertTrue(b);
    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {
        System.out.println("Running empty null return test.");

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();

        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, ad1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, ad1.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct.
     * This test implicitly checks the resize operation (growing). */
    public void bigArrayDequeGrowTest() {
        System.out.println("Running big ArrayDeque grow test.");

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        int N = 100000; // Use a large number, adjust if too slow/too much memory
        for (int i = 0; i < N; i++) {
            ad1.addLast(i);
        }
        assertEquals("Size should be N after adding N elements", N, ad1.size());

        // Loop to remove elements from the front and check order
        for (int i = 0; i < N; i++) {
            assertEquals("removeFirst() should return correct item " + i, Integer.valueOf(i), ad1.removeFirst());
        }
        assertEquals("Size should be 0 after removing all elements", 0, ad1.size());
        assertTrue("Deque should be empty after removing all elements", ad1.isEmpty());
    }

    @Test
    /* Add large number of elements, then remove enough to trigger shrinking (if implemented).
       Checks basic functionality after potential shrinks. */
    public void bigArrayDequeShrinkTest() {
        System.out.println("Running big ArrayDeque shrink test.");

        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        int N_add = 10000;     // Number of elements to add (must be >= 16 for shrink check)
        int N_remove = 9990; // Number of elements to remove (should trigger shrink if N_add/4 > N_add - N_remove)

        // Add elements to force growth
        for (int i = 0; i < N_add; i++) {
            ad1.addLast(i);
        }
        assertEquals(N_add, ad1.size());

        // Remove elements (likely triggering shrink)
        for (int i = 0; i < N_remove; i++) {
             ad1.removeFirst();
        }
        int expectedSize = N_add - N_remove;
        assertEquals("Size should be correct after removing elements", expectedSize, ad1.size());

        // Check if remaining elements are correct using get()
        for (int i = 0; i < expectedSize; i++) {
            // The remaining elements should start from N_remove
            assertEquals("get() should return correct item after shrink", Integer.valueOf(N_remove + i), ad1.get(i));
        }

         // Check if removing remaining elements works
        for (int i = 0; i < expectedSize; i++) {
            assertEquals("removeFirst() should work after shrink", Integer.valueOf(N_remove + i), ad1.removeFirst());
        }
        assertEquals(0, ad1.size());
        assertTrue(ad1.isEmpty());
    }


    @Test
    /* Tests the get method, including edge cases and wrap-around scenarios. */
    public void getTest() {
        System.out.println("Running get test.");
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        // Test get on empty deque - should throw IndexOutOfBoundsException
        try {
            ad1.get(0);
            fail("get(0) on empty deque should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected behavior
        }

        // Add items (less than initial capacity 8)
        for (int i = 0; i < 5; i++) {
            ad1.addLast(i * 10); // 0, 10, 20, 30, 40
        }
        assertEquals(Integer.valueOf(0), ad1.get(0));   // Get first
        assertEquals(Integer.valueOf(40), ad1.get(4));  // Get last
        assertEquals(Integer.valueOf(20), ad1.get(2));  // Get middle

        // Test get out of bounds
        try {
            ad1.get(-1);
            fail("get(-1) should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) { /* Expected */ }
        try {
            ad1.get(5); // size is 5, index 5 is out of bounds
            fail("get(5) should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) { /* Expected */ }

        // Test get after wrap-around
        // Current state (conceptual): [ _, 0, 10, 20, 30, 40, _, _ ] nextFirst=0, nextLast=6
        ad1.removeFirst(); // remove 0 -> [ _, _, 10, 20, 30, 40, _, _ ] nextFirst=1, nextLast=6, size=4
        ad1.removeFirst(); // remove 10 -> [ _, _, _, 20, 30, 40, _, _ ] nextFirst=2, nextLast=6, size=3
        ad1.addFirst(5);  // add 5 -> [ _, _, 5, 20, 30, 40, _, _ ] nextFirst=1, nextLast=6, size=4
        ad1.addFirst(-5); // add -5 -> [ _, -5, 5, 20, 30, 40, _, _ ] nextFirst=0, nextLast=6, size=5
        ad1.addLast(50);  // add 50 -> [ _, -5, 5, 20, 30, 40, 50, _ ] nextFirst=0, nextLast=7, size=6
        ad1.addLast(60);  // add 60 -> [ 60, -5, 5, 20, 30, 40, 50, _ ] nextFirst=0, nextLast=0, size=7 (Wrap!) WRONG nextLast should be 0
        //Let's retrace addLast(60): items[nextLast=7]=60. nextLast=(7+1)%8=0. size=7. nextFirst=0. Correct.
        ad1.addFirst(-15); // add -15 -> [ 60, -5, 5, 20, 30, 40, 50, -15 ] nextFirst=7, nextLast=0, size=8 (Full!) WRONG nextFirst should be 7
        //Let's retrace addFirst(-15): items[nextFirst=0]=-15. nextFirst=(0-1+8)%8=7. size=8. nextLast=0. Correct.

        // State: Full Array [ 60, -5, 5, 20, 30, 40, 50, -15 ], size=8, nextFirst=7, nextLast=0
        // Logical order: -15, 60, -5, 5, 20, 30, 40, 50
        assertEquals("Get after wrap (index 0)", Integer.valueOf(-15), ad1.get(0)); // first element (-15 at physical index 7 + 1)%8 = 0 -> wrong. physical index = (nextFirst+1+index)%length = (7+1+0)%8=0 -> WRONG. should be items[7]???
        // Let's rethink get index calculation: physicalIndex = (first_element_physical_index + logical_index) % length
        // first_element_physical_index = (nextFirst + 1) % length = (7 + 1) % 8 = 0.
        // get(0) -> physical index = (0 + 0) % 8 = 0. items[0] is 60. Expected -15. Calculation is wrong?

        // Let's re-evaluate get(int index) logic in ArrayDeque.java
        // public Item get(int index) {
        //    if (index < 0 || index >= size) { throw new IndexOutOfBoundsException(...) }
        //    int firstIndex = getFirstIndex(); // physical index of first element
        //    return items[(firstIndex + index) % items.length]; // CORRECT FORMULA
        // }
        // Let's re-apply:
        // State: [ 60, -5, 5, 20, 30, 40, 50, -15 ], size=8, nextFirst=7, nextLast=0
        // firstIndex = (nextFirst + 1) % length = (7 + 1) % 8 = 0.
        // get(0): physical index = (0 + 0) % 8 = 0. Should return items[0] = 60. -> Logical element 0 is 60? No, it's -15.
        // Where did the trace go wrong?
        // Start: Empty. nextFirst=0, nextLast=1.
        // addLast(0..40): items=[_,0,10,20,30,40,_,_], size=5, nextFirst=0, nextLast=6. Logical: 0,10,20,30,40.
        // removeFirst(): removes 0. firstIndex=(0+1)%8=1. item=items[1]=0. nextFirst=1. size=4. items=[_,_,10,20,30,40,_,_]. Logical: 10,20,30,40.
        // removeFirst(): removes 10. firstIndex=(1+1)%8=2. item=items[2]=10. nextFirst=2. size=3. items=[_,_,_,20,30,40,_,_]. Logical: 20,30,40.
        // addFirst(5): items[nextFirst=2]=5. nextFirst=(2-1+8)%8=1. size=4. items=[_,_,5,20,30,40,_,_]. Logical: 5,20,30,40.
        // addFirst(-5): items[nextFirst=1]=-5. nextFirst=(1-1+8)%8=0. size=5. items=[_,-5,5,20,30,40,_,_]. Logical: -5,5,20,30,40.
        // addLast(50): items[nextLast=6]=50. nextLast=(6+1)%8=7. size=6. items=[_,-5,5,20,30,40,50,_]. Logical: -5,5,20,30,40,50.
        // addLast(60): items[nextLast=7]=60. nextLast=(7+1)%8=0. size=7. items=[_,-5,5,20,30,40,50,60]. Logical: -5,5,20,30,40,50,60. nextFirst=0, nextLast=0.
        // addFirst(-15): items[nextFirst=0]=-15. nextFirst=(0-1+8)%8=7. size=8. items=[-15,-5,5,20,30,40,50,60]. Logical: -15,-5,5,20,30,40,50,60. nextFirst=7, nextLast=0. Array full.
        // State: items=[-15,-5,5,20,30,40,50,60], size=8, nextFirst=7, nextLast=0.
        // Now, let's test get():
        // firstIndex = (nextFirst + 1) % length = (7 + 1) % 8 = 0.
        // get(0): physical index = (0 + 0) % 8 = 0. items[0] is -15. Correct.
        // get(1): physical index = (0 + 1) % 8 = 1. items[1] is -5. Correct.
        // get(7): physical index = (0 + 7) % 8 = 7. items[7] is 60. Correct.

        // My previous manual trace of the array contents was wrong. The get logic seems correct based on the formula.

        // Back to the test case:
        assertEquals("Get after wrap (index 0)", Integer.valueOf(-15), ad1.get(0));
        assertEquals("Get after wrap (index 3)", Integer.valueOf(20), ad1.get(3));
        assertEquals("Get after wrap (index 7)", Integer.valueOf(60), ad1.get(7)); // Last element

    }

    // You might want to add more specific tests for resizing behavior
    // or edge cases involving wrap-around if needed.

}