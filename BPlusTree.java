import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.ArrayList;

public class BPlusTree<K extends Comparable<K>, T> {

    //private  Node<K,T> root;
    private int m;

    /**
     * constructor of BPlusTree, order of the B plus tree is m
     */

    public BPlusTree(int order) {
        this.m = order;

        // the initial type of B+ tree is a leaf node
        //this.root = new LeafNode(m);
    }

    /*
       get the order of B plus tree from input file
     */
    private static void getTreeOrder(BufferedReader input) {
        try {
            int order = Integer.parseInt(input.readLine().trim());
            System.out.println("Order of the tree is " + order);
            new BPlusTree(order);
            System.out.println("B+ tree is initialized");
        } catch (Exception e) {
            System.err.println("The specified tree order is invalid, use default value 3");
            new BPlusTree(3);
        }
    }

    /*
      get the type of operation to be executed on the B plus tree
     */
    private static int getOperation(String operation) {
        if (operation.contains("Insert")) {
            return 1;
        } else if (operation.contains("Search") && operation.contains(",")){
            return 2;
        } else {
            return 3;
        }
    }

    public static void main(String[] args) throws IOException {
        // check if the input argument is valid
        if (args.length != 0) {
            System.err.println("Invalid input, please enter:java treesearch file_name");
        } else {
            // get the input file name
            Scanner scanner = new Scanner(System.in);
            String inputFile = scanner.nextLine();
            System.out.println("Start reading from:" + inputFile);

            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            try {
                input = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile + ".txt")));

            } catch (FileNotFoundException e) {
                System.err.println("The specified input file is not found");
            }

            // get the order of the B plus tree
            getTreeOrder(input);

            // execute the operations from input file and
            BufferedWriter outputFile = new BufferedWriter(new FileWriter(new File("out_put.txt")));
            do {
                String newLine = input.readLine().trim();
                switch(getOperation(newLine)) {
                    case 1: // insert operation
                        System.out.println("Got an insert operation");
                        break;
                    case 2: // search by key range operation
                        System.out.println("Got a search range operation");
                        break;
                    case 3: // search by key operation
                        System.out.println("Got a search key operation");
                        break;    
                }
                System.out.println("operations performed: " + newLine);
            } while (input.ready());
        }

    }
}