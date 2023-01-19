import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.*;


public class ExManager {
    private String path;
    private int num_of_nodes;
    private Hashtable<Integer, Node> nodes;
    private ArrayList<Thread> activeNodes;

    public ExManager(String path) {
        this.path = path;
        this.nodes = new Hashtable<>();
    }

    public Node get_node(int id) {
        Enumeration<Integer> e = this.nodes.keys();
        while (e.hasMoreElements()) {
            int key = e.nextElement();
            if (this.nodes.get(key).getId() == id) {
                return this.nodes.get(key);
            }
        }
        return null;
    }

    public int get_num_of_nodes() {
        return this.num_of_nodes;
    }

    public void update_edge(int id1, int id2, double weight) {
        Node node1 = this.get_node(id1);
        Node node2 = this.get_node(id2);

        node1.updateNeighbour(id2, weight);
        node2.updateNeighbour(id1, weight);
    }

    public void read_txt() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // end of graph representation
            if (line.contains("stop")) {
                break;
            }

            // split line by space
            String[] data = line.split(" ");

            // first line holds num of nodes
            if (data.length == 1) {
                this.num_of_nodes = Integer.parseInt(data[0]);
                continue;
            }

            // for each node build its adjacency matrix
            int node_id = Integer.parseInt(data[0]);
            Node node = new Node(node_id, this.num_of_nodes);
            for (int i = 1; i < data.length; i += 4) {
                int neighbour_id = Integer.parseInt(data[i]);
                Double weight = Double.parseDouble(data[i + 1]);
                int port1 = Integer.parseInt(data[i + 2]);
                int port2 = Integer.parseInt(data[i + 3]);
                node.add_neighbour(neighbour_id, weight, port1, port2);
            }

            // add the node to the nodes list
            this.nodes.put(node_id, node);
        }
    }

    public void start() {
        // your code here TODO
        Enumeration<Integer> e = this.nodes.keys();
        while (e.hasMoreElements()) {
            int key = e.nextElement();
            Node node = this.nodes.get(key);
            Thread t = new Thread(node);
            t.start();
//            this.activeNodes.add(t);  // TODO
        }

//        this.terminate();


            // TODO run the linkStateRouting for all nodes in the graph

        //

//        System.out.println(get_node(1).get_neighbours());  // TODO

//        Enumeration<Integer> e = this.nodes.keys();
//
//        while (e.hasMoreElements()) {
//            int key = e.nextElement();
//            System.out.println("node :" + this.nodes.get(key).getId());
//        }
        // TODO if there is no change in the matrix then terminate()
    }

    public void terminate() {
        for (Thread t: this.activeNodes) {
            // TODO stop thread t
            this.activeNodes.remove(t);
        }

    }
}
