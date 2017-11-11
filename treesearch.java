import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import javafx.util.Pair;

public class treesearch {

    // initialize the B+ tree with input order
    public static BPlusTree initTree(int order) {
        BPlusTree tree = null;
        if (order >= 4) {
            tree = new BPlusTree(order);
        } else {
            System.err.println("The specified tree order is invalid, use default order 4");
            tree = new BPlusTree(4);
        }
        return tree;
    }
    // get the type of operation to be executed on the B plus tree
    public static int getOperationType(String operation) {
        if (operation.contains("Insert")) {
            return 1;
        } else if (operation.contains("Search") && operation.contains(",")){
            return 2;
        } else {
            return 3;
        }
    }

    public static double parseInsertKey(String line) {
        String keyStr = line.substring(line.indexOf('(') + 1, line.indexOf(',')).trim();
        return Double.parseDouble(keyStr);
    }

    public static String parseInsertValue(String line) {
        return line.substring(line.indexOf("Value") + 5, line.indexOf(')')).trim();
    }

    // for debug purpose
    private static void printTree(Node root) {
        if (root == null) {
            System.out.println("Hitting a null, return from here");
            return;
        }
        Queue<Node> queue = new LinkedList<Node>();
        queue.offer(root);

        int level = 0;
        while (!queue.isEmpty()) {
            level++;
            System.out.println("At level: " + level);

            int qSize = queue.size();
            for (int j = 0; j < qSize; j++) {
                Node node = queue.poll();

                if (!node.isLeafNode) {
                    for (Node child : ((IndexNode)node).children) {
                        //System.out.println("Pushing onto queue index node with key: " + child.keys.get(0));
                        queue.offer(child);
                    }
                } else {
                    LeafNode leaf = (LeafNode) node;
                    int size = leaf.keys.size();
                    for (int i = 0; i < size; i++) {
                        Double keyToPrint = leaf.keys.get(i);
                        for (String val : leaf.values.get(i)) {
                            System.out.println("Leaf node with key: " + keyToPrint);
                            System.out.println("Leaf node with value: " + val);
                        }
                    }
                }
            }

        }
    }

    public static void printSearch(List<String> searchRst, BufferedWriter output) {
        try {
            if (searchRst == null) {
                System.out.println("Null");
                output.write("Null");
            } else {
                String result = "";
                for(String val : searchRst) {
                    result += "Value" + val + ", ";
                }
                result = result.trim();
                result = result.substring(0, result.length() - 1);
                System.out.println(result);
                output.write(result);
                output.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Writing to output file error");
        } 
    }

    public static void printRange(List<Pair<Double, String>> list, BufferedWriter output) {
        String result = "";
            try {
            for (Pair<Double, String> pair : list) {
                result += "(" + pair.getKey() + ",Value" + pair.getValue() + "), ";
            }
            result = result.trim();
            result = result.substring(0, result.length() - 1);
            System.out.println(result);
            output.write(result);
            output.newLine();
        } catch (IOException ex){
            ex.printStackTrace();
            System.err.println("Writing to output file error");            
        }
    }

    public static void main(String[] args) throws IOException {
        // check if the input argument is valid
        if (args.length != 1) {
            System.err.println("Invalid input, please enter:java treesearch file_name");
        } else {
            String inputFile = args[0];
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            try {
                input = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile + ".txt")));

            } catch (FileNotFoundException e) {
                System.err.println("The specified input file is not found, exiting now...");
                System.exit(-1);
            }

            // get the order of the B plus tree and use it to initialize tree
            int order = Integer.parseInt(input.readLine().trim());
            //BPlusTree tree = new BPlusTree(order);
            BPlusTree tree = initTree(order);

            // execute the operations from input file and
            BufferedWriter outputFile = new BufferedWriter(new FileWriter(new File("out_put.txt")));

            do {
                String newLine = input.readLine().trim();
                switch(getOperationType(newLine)) {
                    case 1: // insert operation
                        double insertionKey = parseInsertKey(newLine);
                        String insertionValue = parseInsertValue(newLine);
                        tree.insertion(insertionKey, insertionValue);
                        break;
                    case 2: // search by key range operation
                        double low = Double.parseDouble(newLine.substring(newLine.indexOf('(') + 1, newLine.indexOf(',')).trim());
                        double high = Double.parseDouble(newLine.substring(newLine.indexOf(',') + 1, newLine.indexOf(')')).trim());
                        List<Pair<Double, String>> rangeRst = tree.searchRange(low, high);
                        printRange(rangeRst, outputFile);
                        break;
                    case 3: // search by key operation
                        double searchingKey = Double.parseDouble(newLine.substring(newLine.indexOf('(') + 1, newLine.indexOf(')')).trim());                        
                        List<String> searchRst = tree.search(searchingKey);
                        printSearch(searchRst, outputFile);
                        break;
                }
            } while (input.ready());
            outputFile.close();
        }
    }
}
