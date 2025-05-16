package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;
    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left, right;
        private int size;
        public BSTNode(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
            this.left = null;
            this.right = null;
        }
    }
    public BSTMap() {
        root = null;
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        return getHelper(key, root) != null;
    }

    @Override
    public V get(K key) {
        return getHelper(key, root);
    }
    private V getHelper(K key, BSTNode currentNode) {
        if (currentNode == null) {
            return null;
        }
        if (key.compareTo(currentNode.key) == 0) {
            return currentNode.value;
        } else if (key.compareTo(currentNode.key) < 0) {
            return getHelper(key, currentNode.left);
        } else {
            return getHelper(key, currentNode.right);
        }
    }

    @Override
    public int size() {
        return nodeSize(root);
    }
    private int nodeSize(BSTNode node) {
        if (node == null) {
            return 0;
        }
        return node.size;
    }

    @Override
    public void put(K key, V value) {
        root = putHelper(key, value, root);
    }
    private BSTNode putHelper(K key, V value, BSTNode currentNode) {
        if (currentNode == null) {
            return new BSTNode(key, value, 1);
        }
        if (key.compareTo(currentNode.key) == 0) {
            currentNode.value = value;
            return currentNode;
        } else if (key.compareTo(currentNode.key) < 0) {
            currentNode.left = putHelper(key, value, currentNode.left);
            currentNode.size = 1 + nodeSize(currentNode.left) + nodeSize(currentNode.right);
            return currentNode;
        } else {
            currentNode.right = putHelper(key, value, currentNode.right);
            currentNode.size = 1 + nodeSize(currentNode.left) + nodeSize(currentNode.right);
            return currentNode;
        }
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("This operation is unsupported");
    }

    @Override
    public V remove(K key) {
        V value = get(key);
        root = removeHelper(key, root);
        return value;
    }
    private BSTNode removeHelper(K key, BSTNode currentNode) {
        if (currentNode == null) {
            return null;
        }
        if (key.compareTo(currentNode.key) == 0) {
            if (currentNode.left == null && currentNode.right == null) {
                return null;
            } else if (currentNode.left != null && currentNode.right != null) {
                BSTNode maxLeftNode = getMaxNode(currentNode.left);
                currentNode.value = maxLeftNode.value;
                currentNode.key = maxLeftNode.key;
                currentNode.left = removeHelper(maxLeftNode.key, currentNode.left);
                currentNode.size = 1 + nodeSize(currentNode.left) + nodeSize(currentNode.right);
                return currentNode;
            } else if (currentNode.left != null) {
                return currentNode.left;
            } else {
                return currentNode.right;
            }
        } else if (key.compareTo(currentNode.key) < 0) {
            currentNode.left = removeHelper(key, currentNode.left);
            currentNode.size = 1 + nodeSize(currentNode.left) + nodeSize(currentNode.right);
            return currentNode;
        } else {
            currentNode.right = removeHelper(key, currentNode.right);
            currentNode.size = 1 + nodeSize(currentNode.left) + nodeSize(currentNode.right);
            return currentNode;
        }
    }
    private BSTNode getMaxNode(BSTNode currentNode) {
        BSTNode p = currentNode;
        while(p.right != null) {
            p = p.right;
        }
        return p;
    }

    @Override
    public V remove(K key, V value) {
        if (get(key) == value) {
            removeHelper(key, root);
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("This operation is unsupported");
    }
}
