package UnionFindSet;

public interface UnionFindSet {
    boolean connected(int v1, int v2);
    void union(int v1, int v2);
}
