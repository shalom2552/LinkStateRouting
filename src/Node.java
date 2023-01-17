import java.util.*;

public class Node implements Runnable {
    private final int id;
    //    private int num_of_nodes; // todo check
    private ArrayList<Pair<Integer, Pair<Double, ArrayList<Integer>>>> neighbours;
    private Double[][] neighbours_matrix;


    public Node(int id, int num_of_nodes) {
        this.id = id;
        this.neighbours = new ArrayList<>();
        // initiate neighbours matrix
        this.neighbours_matrix = new Double[num_of_nodes][num_of_nodes];
        for (Double[] neighboursMatrix : neighbours_matrix) {
            Arrays.fill(neighboursMatrix, -1.0);
        }
        this.neighbours_matrix = getNeighbours_matrix();
    }

    @Override
    public void run() {  // TODO implement LinkStateRouting algorithm

        // TODO 1 define the sockets for each neighbour of this.node

        // TODO 2 send this.node.neighbour_matrix to all neighbours

        // TODO 3 get from all neighbours theirs neighbours_matrix
        //      join only after receiving from all of them

        // TODO 4 calculate new neighbour_matrix based on all other neighbours_matrix's

        // TODO 5 stop when there is no change in the matrix

//
//        for (Pair<Integer, Pair<Double, ArrayList<Integer>>> neighbour : this.neighbours) {
//            // update w(u,v)
////            Double weight = neighbour.getValue().getKey();
//
//        }
//        // ℓv ← {⟨(u,v),w(u,v)⟩ | u ∈ N(v)}
//        getNeighbours_matrix();
//        // send neighbours_weight to neighbours
//        for (Pair<Integer, Pair<Double, ArrayList<Integer>>> neighbour : this.neighbours) {
//
//        }
    }

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

    public Double[][] getNeighbours_matrix() {
        for (Pair<Integer, Pair<Double, ArrayList<Integer>>> neighbour : this.neighbours) {
            Double weight = neighbour.getValue().getKey();
            this.neighbours_matrix[this.id - 1][neighbour.getKey() - 1] = weight;
        }
        return neighbours_matrix;
    }

    public void add_neighbour(Integer node_id, Double weight, int port1, int port2) {
        // neighbour = Pair<Integer, Pair<Double, ArrayList<Integer>>>;
        ArrayList<Integer> ports = new ArrayList<>(2);
        ports.add(port1); ports.add(port2);
        Pair<Double, ArrayList<Integer>> data = new Pair<>(weight, ports);
        // add to adjacency list
        this.neighbours.add(new Pair<>(node_id, data));
    }

    public void print_graph() {
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
