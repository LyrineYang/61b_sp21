package UnionFindSet;

/**
 * @author Lyrine Yang
 */
public class UnionFind implements UnionFindSet{
    private final int[] parents;
    private final int totalSize;

    /* Creates a UnionFindSet.UnionFind data structure holding N items. Initially, all
       items are in disjoint sets. */
    /** Creates a UnionFindSet, holding N items in different disjoint sets which parent equals minus one */
    public UnionFind(int N) {
        totalSize = N;
        parents = new int[N];
        for (int i = 0; i < N; i += 1) {
            parents[i] = -1;
        }
    }

    /** Returns the size of the set V belongs to. */
    public int sizeOf(int v) {
        return Math.abs(parents[find(v)]);
    }

    /** Returns the parent of V. If V is the root of a tree, returns the
       negative size of the tree for which V is the root. */
    public int parent(int v) {
        return parents[v];
    }

    /* Returns true if nodes/vertices V1 and V2 are connected. */
    public boolean connected(int v1, int v2) {
        return find(v1) == find(v2);
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. If invalid items are passed into this
       function, throw an IllegalArgumentException. */
    public int find(int v) {
        if (v < 0 || v >= totalSize) {
            throw new IllegalArgumentException("Invalid items");
        }
        if (parent(v) < 0) {
            return v;
        }
        int ultimateRoot = find(parent(v));
        parents[v] = ultimateRoot;
        return ultimateRoot;
    }

    /* Connects two items V1 and V2 together by connecting their respective
       sets. V1 and V2 can be any element, and a union-by-size heuristic is
       used. If the sizes of the sets are equal, tie break by connecting V1's
       root to V2's root. Union-ing an item with itself or items that are
       already connected should not change the structure. */
    public void union(int v1, int v2) {
        int rootV1 = find(v1);
        int rootV2 = find(v2);
        if (rootV1 == rootV2) {
            return;
        }
        if (-parent(rootV1) > -parent(rootV2)) {
            parents[rootV1] += parent(rootV2);
            parents[rootV2] = rootV1;
        } else {
            parents[rootV2] += parent(rootV1);
            parents[rootV1] = rootV2;
        }
    }

}