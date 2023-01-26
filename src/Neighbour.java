public class Neighbour {
    private final int id;
    private final int port1;
    private final int port2;
    private Double weight;

    public Neighbour(int id, int port1, int port2, Double weight) {
        this.id = id;
        this.port1 = port1;
        this.port2 = port2;
        this.weight = weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getId() {
        return id;
    }

    public int getPort1() {
        return port1;
    }

    public int getPort2() {
        return port2;
    }

    public Double getWeight() {
        return weight;
    }
}