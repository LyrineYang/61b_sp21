package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;
/** @author Google Gemini 2.5 pro */

public class MaxArrayDequeTest {
    /**
     * 测试整数类型的 MaxArrayDeque，使用自然顺序比较器 (Integer::compare)
     */
    @Test
    public void testMaxWithIntegersNaturalOrder() {
        // 创建一个比较器，用于整数的自然排序
        Comparator<Integer> naturalOrderComparator = Integer::compare;
        // 或者可以写作：Comparator<Integer> naturalOrderComparator = (a, b) -> a.compareTo(b);

        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(naturalOrderComparator);

        // 1. 测试空队列
        assertNull("Max of empty deque should be null", mad.max());

        // 2. 添加一个元素
        mad.addLast(10);
        assertEquals("Max of deque with one element [10] should be 10", Integer.valueOf(10), mad.max());

        // 3. 添加更多元素
        mad.addLast(5);
        mad.addLast(20); // 当前最大是 20
        mad.addLast(15);
        // Deque: 10, 5, 20, 15
        assertEquals("Max of deque [10, 5, 20, 15] should be 20", Integer.valueOf(20), mad.max());

        // 4. 添加一个更大的元素到头部
        mad.addFirst(100);
        // Deque: 100, 10, 5, 20, 15
        assertEquals("Max of deque [100, 10, 5, 20, 15] should be 100", Integer.valueOf(100), mad.max());

        // 5. 移除最大元素，测试新的最大值
        mad.removeFirst(); // 移除 100
        // Deque: 10, 5, 20, 15
        assertEquals("Max after removing 100 should be 20", Integer.valueOf(20), mad.max());
    }

    /**
     * 测试字符串类型的 MaxArrayDeque，使用自定义比较器 (按长度比较)
     */
    @Test
    public void testMaxWithStringsByLength() {
        // 创建一个比较器，按字符串长度比较
        Comparator<String> byLengthComparator = Comparator.comparingInt(String::length);

        MaxArrayDeque<String> mad = new MaxArrayDeque<>(byLengthComparator);

        // 1. 测试空队列
        assertNull("Max of empty deque should be null", mad.max());

        // 2. 添加元素
        mad.addLast("short");
        mad.addLast("verylongstring");
        mad.addLast("medium");
        // Deque: "short", "verylongstring", "medium"
        assertEquals("Max by length should be 'verylongstring'", "verylongstring", mad.max());

        // 3. 添加另一个更长的字符串
        mad.addFirst("a");
        mad.addLast("supercalifragilisticexpialidocious");
        assertEquals("Max by length should be 'supercalifragilisticexpialidocious'",
                     "supercalifragilisticexpialidocious", mad.max());
    }

    /**
     * 测试带参数的 max(Comparator<T> c) 方法
     */
    @Test
    public void testMaxWithProvidedComparator() {
        // 创建一个使用自然顺序（升序）的 MaxArrayDeque
        Comparator<Integer> naturalOrderComparator = Integer::compare;
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(naturalOrderComparator);

        mad.addLast(10);
        mad.addLast(5);
        mad.addLast(20);
        mad.addLast(15);
        // Deque: 10, 5, 20, 15
        // 默认比较器找到的最大值是 20
        assertEquals("Default max should be 20", Integer.valueOf(20), mad.max());

        // 现在创建一个反向比较器（降序，所以 "max" 会是最小的数字）
        Comparator<Integer> reverseOrderComparator = (a, b) -> b.compareTo(a);
        // 或者 Comparator<Integer> reverseOrderComparator = Comparator.reverseOrder();

        // 使用这个临时比较器调用 max 方法
        assertEquals("Max with reverse order comparator should be 5",
                     Integer.valueOf(5), mad.max(reverseOrderComparator));

        // 测试空队列的情况
        MaxArrayDeque<Integer> emptyMad = new MaxArrayDeque<>(naturalOrderComparator);
        assertNull("Max of empty deque (with provided comparator) should be null",
                   emptyMad.max(reverseOrderComparator));
    }

    /**
     * 测试当构造函数传入的比较器用于查找最小值时的情况
     * (例如，比较器定义 a < b 时返回正数)
     */
    @Test
    public void testMaxWhenComparatorFindsMin() {
        // 这个比较器认为较小的数字是 "更大" 的
        Comparator<Integer> minIsMaxComparator = (a, b) -> b.compareTo(a);
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(minIsMaxComparator);

        mad.addLast(10);
        mad.addLast(5);  // 根据 minIsMaxComparator, 5 "大于" 10
        mad.addLast(20); // 5 "大于" 20
        mad.addLast(2);  // 2 "大于" 5

        // Deque: 10, 5, 20, 2
        assertEquals("Max (which is min by this comparator) should be 2", Integer.valueOf(2), mad.max());
    }

    /**
     * 测试你的 MaxArrayDeque.java 中 constructor 的 null 检查
     * (如果你的构造函数会因为 null comparator 抛出 IllegalArgumentException)
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullComparator() {
        System.out.println("Running constructor null comparator test.");
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(null);
        // 如果构造函数没有抛出异常，这个测试会失败
    }
}
