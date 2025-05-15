package UnionFindSet;

import java.util.Random;

// 假设您的 UnionFindSet.UnionFind 类定义在此处或在同一包中可访问
// public class UnionFindSet.UnionFind { ... }

public class UnionFindTester {

    public static void main(String[] args) {
        testBasicCorrectness();
        testLargeScalePerformance(100000, 500000); // 10万个元素，50万次混合操作
        testLargeScalePerformance(1000000, 5000000); // 100万个元素，500万次混合操作
        // 您可以尝试更大的 N 和 M 来观察效果，但请注意内存和时间消耗
        // testLargeScalePerformance(10000000, 50000000);
    }

    public static void testBasicCorrectness() {
        System.out.println("--- 开始基础功能测试 ---");
        UnionFind uf = new UnionFind(10); // 创建10个元素的并查集

        // 1. 初始状态检查
        System.out.println("初始时，元素0的集合大小: " + uf.sizeOf(0) + " (预期: 1)");
        assert uf.sizeOf(0) == 1 : "错误：初始大小不为1";
        System.out.println("初始时，元素0和元素1是否连接: " + uf.connected(0, 1) + " (预期: false)");
        assert !uf.connected(0, 1) : "错误：初始不应连接";

        // 2. 基本 union 和 connected 操作
        uf.union(0, 1);
        System.out.println("union(0, 1)后，元素0和元素1是否连接: " + uf.connected(0, 1) + " (预期: true)");
        assert uf.connected(0, 1) : "错误：union后0,1应连接";
        System.out.println("union(0, 1)后，元素0的集合大小: " + uf.sizeOf(0) + " (预期: 2)");
        assert uf.sizeOf(0) == 2 : "错误：union后大小应为2";
        System.out.println("union(0, 1)后，元素1的集合大小: " + uf.sizeOf(1) + " (预期: 2)");
        assert uf.sizeOf(1) == 2 : "错误：union后大小应为2";

        uf.union(2, 3);
        uf.union(0, 2); // 将 {0,1} 和 {2,3} 合并
        System.out.println("union(0, 2)后，元素1和元素3是否连接: " + uf.connected(1, 3) + " (预期: true)");
        assert uf.connected(1, 3) : "错误：union后1,3应连接";
        System.out.println("union(0, 2)后，元素3的集合大小: " + uf.sizeOf(3) + " (预期: 4)");
        assert uf.sizeOf(3) == 4 : "错误：union后大小应为4";

        // 3. 对已连接元素执行 union (不应改变结构)
        int sizeBeforeRedundantUnion = uf.sizeOf(0);
        uf.union(0, 3); // 0和3已经通过之前的union连接了
        System.out.println("对已连接的0和3执行union后，元素0的集合大小: " + uf.sizeOf(0) + " (预期: " + sizeBeforeRedundantUnion + ")");
        assert uf.sizeOf(0) == sizeBeforeRedundantUnion : "错误：对已连接元素union后大小改变";
        System.out.println("对已连接的0和3执行union后，0和3是否连接: " + uf.connected(0, 3) + " (预期: true)");
        assert uf.connected(0, 3) : "错误：对已连接元素union后连接状态改变";


        // 4. 元素与自身执行 union (不应改变结构)
        int sizeBeforeSelfUnion = uf.sizeOf(5);
        uf.union(5, 5);
        System.out.println("元素5与自身union后，元素5的集合大小: " + uf.sizeOf(5) + " (预期: " + sizeBeforeSelfUnion + ")");
        assert uf.sizeOf(5) == sizeBeforeSelfUnion : "错误：与自身union后大小改变";

        // 5. 测试路径压缩效果 (间接)
        // 构造一个可能产生较长链的场景，然后find一个叶子节点，再检查路径上节点的parent
        UnionFind ufPc = new UnionFind(5); // 0, 1, 2, 3, 4
        ufPc.union(0, 1); // {0,1}
        ufPc.union(0, 2); // {0,1,2}
        ufPc.union(0, 3); // {0,1,2,3}
        ufPc.union(0, 4); // {0,1,2,3,4}
        // 此时，如果按大小合并且0是v1, 4是v2, 4的根是0，而0的parent是-5。
        // 如果0是v2, 4是v1, 0的根是4，而4的parent是-5。
        // 假设0成为了最终的根节点 (parent[0] = -5)
        // 并且其他节点可能直接或间接指向0，例如 parent[4] = 0 (如果4是后加入的)
        // 或者 parent[1]=0, parent[2]=0, parent[3]=0, parent[4]=0
        System.out.println("进行路径压缩测试...");
        int rootBeforeFind = -1;
        // 为了演示，我们假设一个可能的父指针情况，实际情况取决于union的tie-breaking
        // 比如形成了 4 -> 3 -> 2 -> 1 -> 0 (0是根) 的链（虽然按大小合并会避免这种情况）
        // 或者更可能是 1->0, 2->0, 3->0, 4->0 的星型结构
        // 路径压缩主要体现在多次find后，中间节点的父指针会直接指向根
        System.out.println("元素4的根 (第一次find会进行路径压缩): " + ufPc.find(4));
        System.out.println("元素3的根 (第一次find会进行路径压缩): " + ufPc.find(3));
        // 此时，如果4和3的路径上有共同的非根祖先，这些祖先现在应该直接指向根了
        // 我们可以检查它们的parent值（但这需要访问parent数组或修改parent方法，不适合黑盒测试）
        // 但我们可以确信，后续对4或3的find会更快。

        // 6. 边界条件和异常测试
        System.out.println("测试非法参数...");
        try {
            uf.find(-1);
            System.err.println("错误：find(-1) 未抛出异常");
        } catch (IllegalArgumentException e) {
            System.out.println("成功：find(-1) 抛出异常: " + e.getMessage());
        }
        try {
            uf.find(10); // uf 的大小是10，有效索引是0-9
            System.err.println("错误：find(10) 未抛出异常");
        } catch (IllegalArgumentException e) {
            System.out.println("成功：find(10) 抛出异常: " + e.getMessage());
        }

        System.out.println("--- 基础功能测试结束 ---");
    }

    public static void testLargeScalePerformance(int N, int M_operations) {
        System.out.println("\n--- 开始大规模操作性能演示 ---");
        System.out.printf("元素数量 (N) = %,d; 操作数量 (M) = %,d%n", N, M_operations);

        UnionFind uf = new UnionFind(N);
        Random random = new Random(); // 使用固定的种子可以使测试可重复，不指定则每次不同

        long startTime = System.nanoTime();

        for (int i = 0; i < M_operations; i++) {
            int u = random.nextInt(N);
            int v = random.nextInt(N);

            // 以一定概率执行 union 或 connected 操作
            // 这里我们让 union 和 connected 的机会均等
            if (random.nextBoolean()) {
                uf.union(u, v);
            } else {
                uf.connected(u, v);
            }
        }

        long endTime = System.nanoTime();
        long durationNs = endTime - startTime;
        double durationMs = durationNs / 1_000_000.0; // 转换为毫秒
        double avgTimePerOpNs = (double) durationNs / M_operations; // 平均每次操作的纳秒数

        System.out.printf("执行 %,d 次混合操作总耗时: %.3f ms%n", M_operations, durationMs);
        System.out.printf("平均每次操作耗时: %.3f ns%n", avgTimePerOpNs);

        // 教育性说明
        System.out.println("观察上面这个“平均每次操作耗时”。");
        System.out.println("即使元素数量 N 和操作数量 M 非常大，由于路径压缩和按大小合并的强大优化，");
        System.out.println("这个平均时间也应该非常小 (通常是纳秒级别，远小于 N 或 logN 对应的纳秒数)，");
        System.out.println("表现出接近常数时间的特性。这就是并查集在处理动态连通性问题时高效的原因。");
        System.out.println("您可以尝试修改 N 和 M 的值，观察平均耗时的变化。");
        System.out.println("--- 大规模操作性能演示结束 ---");
    }
}