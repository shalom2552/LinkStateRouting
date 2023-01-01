import java.util.*;

public class Node extends Thread {
    private int id;
    private int num_of_nodes;
    private HashMap<Integer, Pair<Double, ArrayList<Integer>>> neighbours;


    public Node(int id, int num_of_nodes){
        this.id = id;
        this.num_of_nodes = num_of_nodes;
        this.neighbours = new HashMap<>();
    }

    public void add_neighbour(Integer node_id, Double weight, int port1, int port2){
        // neighbour : (node_id, weight, port1, port2)

        ArrayList<Integer> ports = new ArrayList<>(2);
        ports.add(port1);
        ports.add(port2);

        Pair<Double, ArrayList<Integer>> data = new Pair<>(weight, ports);

        // add to adjacency list
        this.neighbours.put(node_id, data);
    }

    public Node get_node(int id){
//        Thread thread = new Node(0);
//        thread.start();
        return new Node(0, 0);
    }

    public HashMap<Integer, Pair<Double, ArrayList<Integer>>> get_neighbours(){
        return this.neighbours;
    }

    public void print_graph(){
        System.out.println("Print graph");
    }

}
