import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

public class IndexNode extends Node {

    protected ArrayList<Node> children;

    /* constructor of index node when inserting key leads to overflow
       splitting this overflow index node into leftSub and rightSub
       then construct a parent index node with these two children
    */
    public IndexNode(List<Double> newKeys, List<Node> newChildren) {
        isLeafNode = false;
        keys = new ArrayList<Double>(newKeys);
        children = new ArrayList<Node>(newChildren);
    }

    // constructor for the case that the idex node becomes the root
    public IndexNode(Double key, Node leftChild, Node rightChild) {
        isLeafNode = false;
        //System.out.println("Init an index node with key " + key);
        keys = new ArrayList<Double>();
        keys.add(key);
        children = new ArrayList<Node>();
        children.add(leftChild);
        children.add(rightChild);
    }


    /*
     insert the pair into this node at the specified index so that it still
     remains sorted
     */
    public void insertSorted(Pair<Double, Node> pair, int index) {
        Double key = pair.getKey();
        Node child = pair.getValue();
        if (index >= keys.size()) {
            keys.add(key);
            children.add(child);
        } else {
            keys.add(index, key);
            children.add(index + 1, child);
        }
    }

}