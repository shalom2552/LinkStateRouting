import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable {
    private int senderId;
    private final Map<Integer, Double> weightVector;

    public Message(int fromNodeId, Map<Integer, Double> weightVector) {
        this.senderId = fromNodeId;
        this.weightVector = weightVector;
    }

    @Override
    public String toString() {
        return "Message{fromNodeId=" + senderId + ",weightVector=" + weightVector + "}";
    }

    public int getSenderId() {
        return senderId;
    }

    public Map<Integer, Double> getWeightVector() {
        return weightVector;
    }

}