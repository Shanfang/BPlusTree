import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.AbstractMap;
import java.util.Map;

public class LeafNode extends Node {
//public class LeafNode {
    // use a list of list so that each key can store multiple values
    protected List<List<String>> values;
    protected LeafNode nextSibling = null;
    protected LeafNode preSibling = null;

    // this constructor is used to create a brand new leaf node
    public LeafNode(Double firstKey, String firstValue) {
        isLeafNode = true;
        keys = new ArrayList<Double>();
        keys.add(firstKey);
        values = new ArrayList<>();
        List<String> bucket = new ArrayList<>();
        bucket.add(firstValue);
        values.add(bucket);
    }

    // this constructor is used to create a leaf node to store right half when a node is split
    public LeafNode(List<Double> newKeys, List<List<String>> newValues) {
        isLeafNode = true;
        keys = new ArrayList<Double>(newKeys);
        values = new ArrayList<>(newValues);
        // DO REMEMBER TO CREATE A LIST for bucket, otherwise null pointer issue in insertSorted
    }

    /*
     insert key/value into this node so that it still remains sorted
     */
    public void insertSorted(Double key, String value) {
        List<String> bucket = new ArrayList<>();
        bucket.add(value);

        // when the inserting key is the smallest
        if (key.compareTo(keys.get(0)) < 0) {
            keys.add(0, key);
            values.add(0, bucket);
        } else if (key.compareTo(keys.get(keys.size() - 1)) > 0) {
            //append to key list and values list
            keys.add(key);
            values.add(bucket);
        } else{
            ListIterator<Double> iterator = keys.listIterator();
            while (iterator.hasNext()) {
                Double compKey = iterator.next();
                int position = iterator.previousIndex();
                // insert into an existing key, append value to existing bucket
                if (compKey.compareTo(key) == 0) {
                    System.out.println("Before adding new value, values under key: " + key);
                    for (String str : values.get(position)) {
                        System.out.println(str);
                    }
                    values.get(position).add(value);

                    System.out.println("After adding new value, values under key: " + key);
                    for (String str : values.get(position)) {
                        System.out.println(str);
                    }
                    break;
                }
                // insert new key and new bucket
                if (compKey.compareTo(key) > 0) {
                    keys.add(position, key);
                    values.add(position, bucket);
                    break;
                }
            }

        }
    }

}
