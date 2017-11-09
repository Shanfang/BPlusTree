import java.util.ArrayList;

/*
 node has at least ceil(m/2) children, at most m - 1 children
 except that root is allowed to have at least 2 children
*/
public class Node {
    protected boolean isLeafNode;
    protected ArrayList<Double> keys;

    public boolean isOverflowed() {
        return keys.size() >= BPlusTree.treeOrder();
    }

}
