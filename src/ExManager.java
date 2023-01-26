import java.util.*;
import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ExManager {
    private String path;
    private int num_of_nodes;
    private ArrayList<Node> nodes;
    protected ArrayList<Integer> doneNodes;

    public ExManager(String path) {
        this.path = path;
        this.nodes = new ArrayList<>();
        this.doneNodes = new ArrayList<>();
    }

    public Node get_node(int id) {
//        Enumeration<Integer> e = this.nodes.keys();
//        while (e.hasMoreElements()) {
//            int key = e.nextElement();
//            if (this.nodes.get(key).getId() == id) {
//                return this.nodes.get(key);
//            }
//        }
//        return null;
        return this.nodes.get(id - 1);
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
            Node node = new Node(node_id, this.num_of_nodes, this.doneNodes);
            for (int i = 1; i < data.length; i += 4) {
                int neighbour_id = Integer.parseInt(data[i]);
                Double weight = Double.parseDouble(data[i + 1]);
                int port1 = Integer.parseInt(data[i + 2]);
                int port2 = Integer.parseInt(data[i + 3]);
                node.addNeighbour(neighbour_id, weight, port1, port2);
            }

            node.getNeighboursMatrix();
            // add the node to the nodes list
            this.nodes.add(node);
        }
    }

    public void start() {
        List<Callable<Void>> callables = new ArrayList<>();
        for (Runnable r : this.nodes)
            callables.add(toCallable(r));
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        ExecutorService(callables, executorService);
    }

    static void ExecutorService(List<Callable<Void>> callables, ExecutorService executorService) {
        try {
            executorService.invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private Callable<Void> toCallable(final Runnable runnable) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                runnable.run();
                return null;
            }
        };
    }

    public void terminate() {
//        for (Thread t : this.activeNodes) {
//            t.interrupt();
//        }
        return;
    }
}
