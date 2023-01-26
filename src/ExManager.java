import java.util.*;
import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExManager {
    private final String path;
    private int num_of_nodes;
    private final ArrayList<Node> nodes;
    protected ArrayList<Integer> doneNodes;

    public ExManager(String path) {
        this.path = path;
        this.nodes = new ArrayList<>();
        this.doneNodes = new ArrayList<>();
    }

    public Node get_node(int id) {
        return this.nodes.get(id - 1);
    }

    public int get_num_of_nodes() {
        return this.num_of_nodes;
    }

    /**
     * get two nodes and an updated weight for them and update the edge with the new weight
     * @param id1
     * @param id2
     * @param weight
     */
    public void update_edge(int id1, int id2, double weight) {
        Node node1 = this.get_node(id1);
        Node node2 = this.get_node(id2);
        node1.updateNeighbour(id2, weight);
        node2.updateNeighbour(id1, weight);
    }

    /**
     * rReads line by line of the input and breaks it down into different
     * parts such as ports and the edge weightsead line by line the input file
     * @throws FileNotFoundException
     */
    public void read_txt() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("stop")) {  // end of graph representation
                break;
            }
            String[] data = line.split(" ");
            if (data.length == 1) {  // first line holds num of nodes
                this.num_of_nodes = Integer.parseInt(data[0]);
                continue;
            }
            int node_id = Integer.parseInt(data[0]);
            Node node = new Node(node_id, this.num_of_nodes);
            for (int i = 1; i < data.length; i += 4) {
                int neighbour_id = Integer.parseInt(data[i]);
                Double weight = Double.parseDouble(data[i + 1]);
                int port1 = Integer.parseInt(data[i + 2]);
                int port2 = Integer.parseInt(data[i + 3]);
                node.addNeighbour(neighbour_id, weight, port1, port2);
            }
            node.getNeighboursMatrix();
            this.nodes.add(node);
        }
    }

    /**
     * creates a list of "callables" from a list of "Runnable" objects.
     * then creates a fixed thread pool with a maximum of 100
     * threads using the Executors class.
     */
    public void start() {
        List<Callable<Void>> callables = new ArrayList<>();
        for (Runnable r : this.nodes)
            callables.add(toCallable(r));
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        ExecutorService(callables, executorService);
    }

    /**
     * calls the invokeAll() method on the executorService object, passing in the list of callables.
     * Then the method calls the shutdown() method on the executorService object,
     * which initiates a shutdown of the Executor.
     * Finally, it waits for the termination of the Executor with a timeout of 2 seconds.
     * @param callables
     * @param executorService
     */
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

    /**
     * returns a new "Callable" object,
     * which is an anonymous inner class that implements the "Callable" interface.
     * @param runnable
     * @return
     */
    private Callable<Void> toCallable(final Runnable runnable) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                runnable.run();
                return null;
            }
        };
    }

    /**
     *
     */
    public void terminate() {
        return;
    }
}
