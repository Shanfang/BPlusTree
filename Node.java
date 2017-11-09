import java.util.ArrayList;

public class Node<K extends Comparable<K>, T> {
    protected boolean isLeafNode;
    protected ArrayList<K> keys;

    public boolean isOverflowed() {
        return keys.size() >= BPlusTree.treeOrder();
    }

    public boolean isUnderflowed() {
        return keys.size() < Math.ceil(BPlusTree.treeOrder() / 2);
    }

}