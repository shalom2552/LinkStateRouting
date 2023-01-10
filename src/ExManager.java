public class ExManager {
    private String path;
    private int num_of_nodes;
    private Hashtable<Integer, Node> nodes;

    public ExManager(String path){
        this.path = path;
        this.nodes = new Hashtable<>();
    }

    public Node get_node(int id){
        // your code here
        return new Node(0, 0);  // TODO temp
    }

    public int get_num_of_nodes() {
        return this.num_of_nodes;
    }

    public void update_edge(int id1, int id2, double weight){
        //your code here
    }

    public void read_txt() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();

            // end of graph representation
            if(line.contains("stop")){
                break;
            }

            //
            String[] data = line.split(" ");

            // first line holds num of nodes
            if (data.length == 1){
                this.num_of_nodes = Integer.parseInt(data[0]);
                System.out.println("num of nodes: " + this.num_of_nodes);
                continue;
            }

            // for each node build its adjacency matrix
            int node_id = Integer.parseInt(data[0]);
            Node node = new Node(node_id, this.num_of_nodes);
            System.out.println(node_id);  // TODO debug
            for (int i = 1; i < data.length; i += 4){
                int neighbour_id = Integer.parseInt(data[i]);
                Double weight = Double.parseDouble(data[i + 1]);
                Integer port1 = Integer.parseInt(data[i + 2]);
                Integer port2 = Integer.parseInt(data[i + 3]);
                node.add_neighbour(neighbour_id, weight, port1, port2);
            }
            System.out.println(node.get_neighbours());  // TODO debug


                // TODO yossi
//                // for each node build its adjacency matrix
//                List<Double> myList = new List<>();
//                Pair<Integer, List<Double>> pair = new Pair<>;
//                Node node = new Node(Integer.parseInt(data[0]), this.num_of_nodes);
//                for (int i = 1; i < data.length; i += 4){
//                    //node.add_neighbour();
//                    for (int j= i; i < j + 3; j++) {
//                        myList.add(Double.parseDouble(data[j]));
//                    }
//                    Pair<Integer, List<E>> pair = new Pair<>(Integer.parseInt(data[0], myList);
//

            // add the node to the nodes list
            System.out.println(Arrays.deepToString(node.getNeighbours_matrix()));
            this.nodes.put(node_id, node);
        }


        }

    public void start(){
        // your code here
    }

    public void terminate(){
        // your code here
    }
}
