import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class IndexNode<K extends Comparable<K>, T> extends Node<K,T> {

    /*
     index node has at least ceil(m/2) children, at most m - 1 children
    except that root is allowed to have at least 2 children
    */
    protected ArrayList<Node<K,T>> children;

    // constructor for an index node, this is used when an index node is initially created
    public IndexNode(List<K> newKeys, List<Node<K,T>> newChildren) {
        isLeafNode = false;
        keys = new ArrayList<K>(newKeys);
        children = new ArrayList<Node<K,T>>(newChildren);
    }

    /* constructor of index node when inserting key leads to overflow
       splitting this overflow index node into leftSub and rightSub
       then construct a parent index node with these two children
    */
    public IndexNode(K key, Node<K,T> leftSub, Node<K,T> rightSub) {
        isLeafNode = false;
        keys = new ArrayList<K>();
        keys.add(key);
        children = new ArrayList<Node<K,T>>();
        children.add(leftSub);
        children.add(rightSub);
    }


    /*
     insert the entry into this node at the specified index so that it still
     remains sorted
     */
    public void insertSorted(Entry<K, Node<K,T>> e, int index) {
        K key = e.getKey();
        Node<K,T> child = e.getValue();
        if (index >= keys.size()) {
            keys.add(key);
            children.add(child);
        } else {
            keys.add(index, key);
            children.add(index+1, child);
        }
    }

}
