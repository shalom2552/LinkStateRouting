import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Node implements Runnable {
    protected final int id;
    protected ArrayList<Neighbour> neighbours;
    protected Double[][] neighboursMatrix;
    protected ArrayList<Integer> visitedNodes;   // todo remove
    private int numOfNodes;
    protected ArrayList<Integer> doneNodes;  // todo remove
    protected boolean done;
    private Map<Integer, Double> weightVector;


    public Node(int id, int num_of_nodes, ArrayList<Integer> doneNodes) {
        this.id = id;
        this.done = false;
        this.numOfNodes = num_of_nodes;
        this.neighbours = new ArrayList<>();
        this.neighboursMatrix = new Double[num_of_nodes][num_of_nodes];
        // ids that this.node yet to be received a message from.
        this.visitedNodes = new ArrayList<>(num_of_nodes - 1);   // todo remove
        for (int i = 1; i <= num_of_nodes; i++) if (this.id != i) this.visitedNodes.add(i);    // todo remove
        this.doneNodes = doneNodes;   // todo remove
    }

    @Override
    public void run() {
        // a Queue to hold the messages for the Node to run from
        MessagesQueue messagesQueue = new MessagesQueue();

        this.weightVector = getWeightVector();
        Thread server = new Thread(() -> {
            try {
                List<Callable<Void>> callables = new ArrayList<>();
                ExecutorService execService = Executors.newFixedThreadPool(100);
                for (Neighbour neighbor : this.neighbours) {
                    callables.add(() -> {
                        startNeighborServer(neighbor, messagesQueue);
                        return null;
                    });
                }
                ExManager.ExecutorService(callables, execService);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        Thread client = new Thread(() -> {
            try {
                Message message = new Message(this.id, this.weightVector);
                messagesQueue.addMessage(message);
                for (Neighbour neighbour : this.neighbours) {
//                    System.out.println(message);  // todo
                    sendMessage(neighbour.getPort1(), message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            server.start();
            TimeUnit.SECONDS.sleep(2);
            client.start();
            client.join();
            server.join();
            setNeighboursMatrix(messagesQueue);
            this.done = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, Double> getWeightVector() {
        // initialize the weight vector
        Map<Integer, Double> weightVector = new HashMap<>();
        for (Neighbour node : this.neighbours) {
            weightVector.put(node.getId(), node.getWeight());
        }
        return weightVector;
    }

    private void sendMessage(int port, Message message) {
//        if (!this.doneNodes.contains(neighbour.getId())) {
        try {
            Socket socket = new Socket("127.0.0.1", port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(message);
            out.flush();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("ERROR! connection refuse on port: " + port + "; " + message);  // todo print debug
//                e.printStackTrace();
        }
//    }
    }


    public void startNeighborServer(Neighbour neighbour, MessagesQueue messagesQueue) {
        try {
            ServerSocket server = new ServerSocket(neighbour.getPort2());  // todo port 1?
            server.setSoTimeout(15000);
            ArrayList<NodeServer> threadsList = new ArrayList<>();
            while (true) {
                try {
                    Socket socket = server.accept();
                    if (socket != null) {
                        NodeServer nodeServer = new NodeServer(this.id, neighbour.getId(),
                                socket, this.neighbours, messagesQueue);
                        threadsList.add(nodeServer);
                        nodeServer.start();
                    }
                } catch (SocketTimeoutException e) {
                    if (messagesQueue.size() == this.numOfNodes) {
                        server.close();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public synchronized void setNeighboursMatrix(MessagesQueue messagesQueue) {
        for (Map.Entry<Integer, Map<Integer, Double>> message : messagesQueue.getQueue().entrySet()) {
            int senderId = message.getKey();
            Map<Integer, Double> weightVector = message.getValue();
            for (Map.Entry<Integer, Double> nodeWeight : weightVector.entrySet()){
//                print_graph();  // todo
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
    public void print_graph() throws InterruptedException {
        // print the graph only if the algorithm is done
        while (!this.done) {
            Thread.sleep(1000);
        }

//        System.out.println("Neighbours Matrix for node id: " + this.id);  // todo
        for (Double[] weightVector : neighboursMatrix) {
            for (int col = 0; col < this.neighboursMatrix.length; col++) {
                if (col == 0) {
                    System.out.print(weightVector[col]);
                } else {
                    System.out.print(", " + weightVector[col]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public int getId() {
        return this.id;
    }

}
