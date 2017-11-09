import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.List;

/*
    As specified in the project description, we can assume the key is of double type, and value of String type.
    Order of this B plus tree is specified from input file.
    Operations on this tree includes:
    0. initialization, which initialize the tree with specified order;
    1. insertion, which does not return anything;
    2. search by key, which returns all the values with this key in any order(return null if there is no such key);
    3. search by key range, which returns all key-value pairs within this key range(sorted by key in ascending order);
 */
public class BPlusTree {

    private static  Node root;
    private static int m;

    public BPlusTree(int order) {
        this.m = order;

        // initial type of B+ tree is a leaf node
        this.root = null;
    }

    public static int treeOrder() {
        return m;
    }

    public static Node treeRoot() {
        return root;
    }


//    // initialize tree with the order from input file
//    private static BPlusTree InitializeTree(BufferedReader input) {
//        BPlusTree tree;
//        try {
//            int order = Integer.parseInt(input.readLine().trim());
//            //System.out.println("Order of the tree is " + order);
//            tree = new BPlusTree(order);
//            //System.out.println("B+ tree is initialized");
//        } catch (Exception e) {
//            System.err.println("The specified tree order is invalid, use default value 3");
//            tree = new BPlusTree(3);
//        }
//        return tree;
//    }




    /*
      insert a key-value pair into the BPlusTree
      if sub-procedure returns null, then there is no overflow and child splitting
      if sub-procedure returns non-null, a new index node should be created
     */
    public void insertion(Double key, String value) {
        LeafNode newLeaf = new LeafNode(key, value);
        Entry<Double, Node> entry = new AbstractMap.SimpleEntry<Double, Node>(key, newLeaf);

        // insert into an empty tree, this leaf node becomes a new root
        if(root == null || root.keys.size() == 0) {
            root = entry.getValue();
        }

        // initially newChildEntry is null, and stays as null on return unless child is split
        //Entry<Double, Node> newChildEntry = insertionHelper(root, entry, null);
        Entry<Double, Node> newChildEntry = insertionHelper(root, entry);

        // insertion does not split leaf node
        if(newChildEntry == null) {
            return;
        } else {
            // splitting take place
            IndexNode newRoot = new IndexNode(newChildEntry.getKey(), root, newChildEntry.getValue());
            root = newRoot;
            return;
        }
    }

    private Entry<Double, Node> insertionHelper(Node node, Entry<Double, Node> entry) {
//    private static Entry<Double, Node> insertionHelper(Node node, Entry<Double, Node> entry,
//            Entry<Double, Node> newChildEntry) {
        Entry<Double, Node> newChildEntry = null;
        if(!node.isLeafNode) {
            IndexNode curr = (IndexNode) node;
            int i = 0;

            // iterate node's keys to find i such that ith of keys <= entry's key value < (i+1)th of keys
            while(i < curr.keys.size()) {
                if(entry.getKey().compareTo(curr.keys.get(i)) < 0) {
                    break;
                }
                i++;
            }

            // recursively call the helper method until it hits a leaf node, then insert entry
            newChildEntry = insertionHelper((Node)curr.children.get(i), entry);

            // the case that insertion does not split node
            if(newChildEntry == null) {
                return null;
            } else {
                // the case that child node splits, need to insert newChildEntry in node
                int j = 0;
                while (j < curr.keys.size()) {
                    if(newChildEntry.getKey().compareTo(curr.keys.get(j)) < 0) {
                        break;
                    }
                    j++;
                }
                curr.insertSorted(newChildEntry, j);

                // after insertion the newChildEntry, need to check overflow status
                if(curr.keys.size() < m) {
                    // no overflow, return to the caller for this recursion
                    return null;
                }
                else{
                    newChildEntry = splitIndexNode(curr);

                    // root was split, use newChildEntry to create a new root and grow the tree height by 1
                    if(curr == root) {
                        // Create new node and make tree's root-node pointer point to newRoot
                        IndexNode newRoot = new IndexNode(newChildEntry.getKey(), curr,
                                newChildEntry.getValue());
                        root = newRoot;
                        return null;
                    }
                    return newChildEntry;
                }
            }
        } else {
            LeafNode leaf = (LeafNode)node;
            LeafNode newLeaf = (LeafNode)entry.getValue();

            leaf.insertSorted(entry.getKey(), newLeaf.values.get(0).get(0));

            // the case that there is extra space for newLeaf
            if(leaf.keys.size() < m) {
                return null;
            }
            else {
                // leaf is overflow after insertion
                newChildEntry = splitLeafNode(leaf);
                if(leaf == root) {
                    IndexNode newRoot = new IndexNode(newChildEntry.getKey(), leaf,
                            newChildEntry.getValue());
                    root = newRoot;
                    return null;
                }
                return newChildEntry;
            }
        }
    }

     // split a leaf node and return an entry consisting of splitting key and new leaf node
    public static Entry<Double, Node> splitLeafNode(LeafNode leaf) {
        ArrayList<Double> newKeys = new ArrayList<>();
        List<List<String>> newValues = new ArrayList<>();

        // m/2 entries move to brand new node, leaf node has max of m + 1 children
        for(int i = m / 2; i <= m + 1; i++) {
            newKeys.add(leaf.keys.get(i));
            List<String> newBucket = leaf.values.get(i);
            newValues.add(newBucket);
        }

        // remove the the above entries from previous node
        for(int i = m / 2; i <= m; i++) {
            leaf.keys.remove(leaf.keys.size()-1);
            leaf.values.remove(leaf.values.size()-1);
        }

        Double splitKey = newKeys.get(0);
        LeafNode rightNode = new LeafNode(newKeys, newValues);

        // add the new leaf node into doubly linked list
        LeafNode temp = leaf.nextSibling;
        leaf.nextSibling = rightNode;
        rightNode.nextSibling = temp;
        temp.preSibling = rightNode;
        rightNode.preSibling = leaf;

        Entry<Double, Node> newChildEntry = new AbstractMap.SimpleEntry<Double, Node>(splitKey, rightNode);
        return newChildEntry;
    }

    // split index node
    public static Entry<Double, Node> splitIndexNode(IndexNode node) {
        ArrayList<Double> newKeys = new ArrayList<>();
        ArrayList<Node> newChildren = new ArrayList<>();

        // push the middle key up a level
        int splitIndex = (int)Math.ceil(m / 2) - 1;
        Double splitKey = node.keys.remove(splitIndex);

        // insert the leftmost child of new index node
        newChildren.add(node.children.remove(splitIndex + 1));

        // store the remaining right half of key and children into a new index node
        while(node.keys.size() > splitIndex) {
            newKeys.add(node.keys.remove(splitIndex));
            newChildren.add(node.children.remove(splitIndex + 1));
        }

        IndexNode rightNode = new IndexNode(newKeys, newChildren);
        Entry<Double, Node> newChildEntry = new AbstractMap.SimpleEntry<Double, Node>(splitKey, rightNode);
        return newChildEntry;
    }
}
