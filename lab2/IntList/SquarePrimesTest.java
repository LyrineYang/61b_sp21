package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);

        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquarePrimesNoPrimes() {
        IntList lst = IntList.of(4, 6, 8, 9, 10, 12); // 列表中没有素数
        boolean changed = IntListExercises.squarePrimes(lst);

        assertEquals("4 -> 6 -> 8 -> 9 -> 10 -> 12", lst.toString()); // 列表应该保持不变
        assertFalse(changed); // 因为没有素数被修改，所以应该返回 false
    }

    // 测试：列表中所有数字都是素数
    @Test
    public void testSquarePrimesAllPrimes() {
        IntList lst = IntList.of(2, 3, 5, 7, 11); // 所有都是素数
        boolean changed = IntListExercises.squarePrimes(lst);

        // 2*2=4, 3*3=9, 5*5=25, 7*7=49, 11*11=121
        assertEquals("4 -> 9 -> 25 -> 49 -> 121", lst.toString());
        assertTrue(changed); // 因为所有数字都被修改了，所以返回 true
    }


    // 测试：包含数字 1 (假设 1 不被视作素数)
    @Test
    public void testSquarePrimesWithOne() {
        IntList lst = IntList.of(1, 2, 4, 5, 1); // 包含 1，以及素数 2, 5
        boolean changed = IntListExercises.squarePrimes(lst);

        // 1 不变, 2*2=4, 4 不变, 5*5=25, 1 不变
        assertEquals("1 -> 4 -> 4 -> 25 -> 1", lst.toString());
        assertTrue(changed); // 因为 2 和 5 被修改了，返回 true
    }

    // 测试：素数在列表的开头和结尾
    @Test
    public void testSquarePrimesAtEdges() {
        IntList lst = IntList.of(13, 14, 15, 16, 19); // 素数 13, 19
        boolean changed = IntListExercises.squarePrimes(lst);

        // 13*13=169, 19*19=361
        assertEquals("169 -> 14 -> 15 -> 16 -> 361", lst.toString());
        assertTrue(changed); // 因为 13 和 19 被修改了，返回 true
    }

     // 测试：包含重复的素数
    @Test
    public void testSquarePrimesWithDuplicatePrimes() {
        IntList lst = IntList.of(7, 9, 7, 10, 3, 3); // 素数 7, 7, 3, 3
        boolean changed = IntListExercises.squarePrimes(lst);

        // 7*7=49, 9不变, 7*7=49, 10不变, 3*3=9, 3*3=9
        assertEquals("49 -> 9 -> 49 -> 10 -> 9 -> 9", lst.toString());
        assertTrue(changed); // 因为 7 和 3 被修改了，返回 true
    }
}

