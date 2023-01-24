import org.xml.sax.helpers.AttributesImpl;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Node implements Runnable {
    protected final int id;
    private int num_of_nodes;
    private Double[][] neighbours_matrix;
    protected ArrayList<Pair<Integer, Pair<Double, ArrayList<Integer>>>> neighbours;
    private ArrayList<MyServer> active_servers;

    // todo graph nodes = array(0,..,n)
    // remove this.id from above list todo ?


    public Node(int id, int num_of_nodes) {
        this.id = id;
        this.neighbours = new ArrayList<>();
        // initiate neighbours matrix
        this.neighbours_matrix = new Double[num_of_nodes][num_of_nodes];
        this.active_servers = new ArrayList<>();
    }

    public static class MyServer implements Runnable {
        private final int port;
//        public boolean is_active;

        public MyServer(int port) {
            this.port = port;
//            this.is_active = true;
        }

        @Override
        public void run() {
            try (ServerSocket ss = new ServerSocket(this.port)) {
                Socket socket = ss.accept();

                InputStream inputStream = socket.getInputStream();
                ObjectInputStream in = new ObjectInputStream(inputStream);

                Pair<Integer, ArrayList<Pair<Integer, Double>>> line;

                line = (Pair<Integer, ArrayList<Pair<Integer, Double>>>) in.readObject();
                while (line != null) {
                    System.out.println("New mssg: " + line);
                    line = (Pair<Integer, ArrayList<Pair<Integer, Double>>>) in.readObject();
                }

            } catch (Exception e) {
                assert true;
            }
        }

        public int getPort() {
            return this.port;
        }
    }

    public static class Sender implements Runnable {
        private final int port;
        private final Pair<Integer, ArrayList<Pair<Integer, Double>>> message;

        //        public Sender(int port, Pair message) {  // todo
        public Sender(int port, Pair<Integer, ArrayList<Pair<Integer, Double>>> message) {
            this.port = port;
            this.message = message;
        }

        @Override
        public void run() {
//            try (Socket socket = new Socket("127.0.0.1", this.port)) {
//                OutputStream out = socket.getOutputStream();
//                DataOutputStream data_out = new DataOutputStream(out);
//                data_out.writeBytes(this.message); // TODO
//                data_out.flush();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
            // TODO exp
            try (Socket socket = new Socket("127.0.0.1", this.port)) {
                OutputStream out_stream = socket.getOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(out_stream);
                out.writeObject(this.message);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {  // TODO implement LinkStateRouting algorithm
        // make a thread to listen to all neighbours
        for (Pair<Integer, Pair<Double, ArrayList<Integer>>> node : this.neighbours) {
            int port = node.getValue().getValue().get(0);
            MyServer server = new MyServer(port);
            Thread t = new Thread(server);
//            System.out.println(this.id + " Started a Server on port: " + port); // todo
            t.start();
            this.active_servers.add(server);
        }


        // TODO send weightVector to all neighbours for the first time
        sendToAllNeighbours();
    }

    public void sendToAllNeighbours() {
        ArrayList<Pair<Integer, Double>> message_data = new ArrayList<>();
        for (Pair<Integer, Pair<Double, ArrayList<Integer>>> node : this.neighbours) {
            // check if the server is on before sending message
            int port = node.getValue().getValue().get(1);

            // TODO check if the server to send on is up!
            //  this cause trying to send a message to an off line server

            message_data.add(new Pair<>(node.getKey(), node.getValue().getKey()));
            Sender sender = new Sender(port, new Pair<>(this.id, message_data));
            Thread t = new Thread(sender);
            t.start();
        }
    }

    public boolean notActive(int port) {  // TODO not a good check- check for all servers in the other nodes not this one
        for (MyServer server : this.active_servers) {
            if (port == server.getPort()) {
                return true;
            }
        }
        return false;
    }

    /**
     * updates the weight of a giving neighbour id
     */
    public void updateNeighbour(int id, Double weight) {
        for (Pair<Integer, Pair<Double, ArrayList<Integer>>> node : this.neighbours) {
            if (node.getKey().equals(id)) {
                // update weight
                ArrayList<Integer> ports = node.getValue().getValue();
                Pair<Double, ArrayList<Integer>> data = new Pair<>(weight, ports);
                node.setValue(data);
                break;
            }
        }
    }

    /**
     * returns the neighbours matrix of this node
     */
    public void getNeighbours_matrix() {
        for (Double[] neighboursMatrix : neighbours_matrix) {
            Arrays.fill(neighboursMatrix, -1.0);
        }
        for (Pair<Integer, Pair<Double, ArrayList<Integer>>> neighbour : this.neighbours) {
            Double weight = neighbour.getValue().getKey();
            this.neighbours_matrix[this.id - 1][neighbour.getKey() - 1] = weight;
        }
    }

    /**
     * add a neighbour to the node neighbour list
     */
    public void add_neighbour(Integer node_id, Double weight, int port1, int port2) {
        ArrayList<Integer> ports = new ArrayList<>(2);
        ports.add(port1);
        ports.add(port2);
        Pair<Double, ArrayList<Integer>> data = new Pair<>(weight, ports);
        // add to adjacency list
        this.neighbours.add(new Pair<>(node_id, data));
    }

    /**
     * prints the neighbours matrix of this node
     */
    public void print_graph() {  // todo print the graph only if the algorithm is done
        System.out.println("Neighbours Matrix for node id: " + this.id);
        for (Double[] neighboursMatrix : neighbours_matrix) {
            for (int col = 0; col < neighbours_matrix.length; col++) {
                if (col == 0) {
                    System.out.print(neighboursMatrix[col]);
                } else {
                    System.out.print(", " + neighboursMatrix[col]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public int getId() {
        return this.id;
    }

    public ArrayList get_neighbours() {
        return this.neighbours;
    }
}
