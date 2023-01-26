import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Node implements Runnable {
    protected final int id;
    protected ArrayList<Neighbour> neighbours;
    protected Double[][] neighboursMatrix;
    private final int numOfNodes;
    protected boolean done;
    private Map<Integer, Double> weightVector;

    /**
     *
     * @param id of the node
     * @param num_of_nodes in the graph
     */
    public Node(int id, int num_of_nodes) {
        this.id = id;
        this.done = false;
        this.numOfNodes = num_of_nodes;
        this.neighbours = new ArrayList<>();
        this.neighboursMatrix = new Double[num_of_nodes][num_of_nodes];
    }

    /**
     * implement run from runnable
     * manage the message queue
     * creates Thread for each neighbour
     */
    @Override
    public void run() {
        MessagesQueue messagesQueue = new MessagesQueue();
        this.weightVector = getWeightVector();
        Thread server = new Thread(() -> { serverThread(messagesQueue);});
        Thread client = new Thread(() -> { clientThread(messagesQueue);});
        try {
            server.start();
            TimeUnit.SECONDS.sleep(2);
            client.start();
            client.join();
            server.join();
        } catch (InterruptedException e) { e.printStackTrace();
        } finally {
            setNeighboursMatrix(messagesQueue);
            this.done = true;
        }
    }

    /**
     *
     * @param messagesQueue
     */
    private void clientThread(MessagesQueue messagesQueue){
        try {
            Message message = new Message(this.id, this.weightVector);
            messagesQueue.addMessage(message);
            for (Neighbour neighbour : this.neighbours) {
                sendMessage(neighbour.getPort1(), message);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     *
     * @param messagesQueue
     */
    private void serverThread(MessagesQueue messagesQueue){
        try {
            List<Callable<Void>> callables = new ArrayList<>();
            ExecutorService execService = Executors.newFixedThreadPool(100);
            for (Neighbour neighbor : this.neighbours) {
                callables.add(() -> {
                    startNeighborServer(neighbor, messagesQueue);
                    return null; });
            }
            ExManager.ExecutorService(callables, execService);
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     *
     * @return
     */
    private Map<Integer, Double> getWeightVector() {
        Map<Integer, Double> weightVector = new HashMap<>();
        for (Neighbour node : this.neighbours) { weightVector.put(node.getId(), node.getWeight()); }
        return weightVector;
    }

    /**
     *
     * @param port
     * @param message
     */
    static void sendMessage(int port, Message message) {
        try {
            Socket socket = new Socket("127.0.0.1", port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(message);
            out.flush();
            out.close();
            socket.close();
        } catch (Exception e) {

            //sendMessage(port, message);
        }
    }

    /**
     *
     * @param neighbour
     * @param messagesQueue
     */
    public void startNeighborServer(Neighbour neighbour, MessagesQueue messagesQueue) {
        try {
            ServerSocket server = new ServerSocket(neighbour.getPort2());
            server.setSoTimeout(15000);
            while (true) {
                try {
                    Socket socket = server.accept();
                    if (socket != null) {
                        NodeServer nodeServer = new NodeServer(neighbour.getId(),
                                socket, this.neighbours, messagesQueue);
                        nodeServer.start();
                    }
                } catch (SocketTimeoutException e) {
                    if (messagesQueue.size() == this.numOfNodes) {
                        server.close();
                        break;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * updates the weight of a giving neighbour id
     */
    public void updateNeighbour(int id, Double weight) {
        for (Neighbour neighbour : this.neighbours) {
            if (neighbour.getId().equals(id)) {
                neighbour.setWeight(weight);
                break;
            }
        }
        this.weightVector = getWeightVector();
    }

    /**
     * returns the neighbours matrix of this node
     */
    public void getNeighboursMatrix() {
        for (Double[] neighboursMatrix : neighboursMatrix) {
            Arrays.fill(neighboursMatrix, -1.0);
        }
        for (Neighbour neighbour : this.neighbours) {
            Double weight = neighbour.getWeight();
            this.neighboursMatrix[this.id - 1][neighbour.getId() - 1] = weight;
        }
    }

    /**
     *
     * @param messagesQueue
     */
    public synchronized void setNeighboursMatrix(MessagesQueue messagesQueue) {
        for (Map.Entry<Integer, Map<Integer, Double>> message : messagesQueue.getQueue().entrySet()) {
            Map<Integer, Double> weightVector = message.getValue();
            int senderId = message.getKey();
            for (Map.Entry<Integer, Double> nodeWeight : weightVector.entrySet()){
                int neighbourId = nodeWeight.getKey();
                Double weight = nodeWeight.getValue();
                this.neighboursMatrix[senderId - 1][neighbourId - 1] = weight;
            }
        }
    }

    /**
     * add a neighbour to the node neighbour list
     */
    public void addNeighbour(Integer node_id, Double weight, int port1, int port2) {
        this.neighbours.add(new Neighbour(node_id, port1, port2, weight));
    }

    /**
     * prints the neighbours matrix of this node
     */
    public void print_graph() {
        // print the graph only if the algorithm is done
        while (!this.done) {
            try { TimeUnit.SECONDS.sleep(1);
            } catch (Exception e){ e.printStackTrace(); }
        }
        for (Double[] weightVector : neighboursMatrix) {
            for (int col = 0; col < this.neighboursMatrix.length; col++) {
                if (col == 0) {
                    System.out.print(weightVector[col]);
                } else {
                    System.out.print(", " + weightVector[col]);
                }
            } System.out.println();
        }
    }
}

