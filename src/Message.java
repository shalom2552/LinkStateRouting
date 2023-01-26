import java.io.Serializable;
import java.util.Map;

public class Message implements Serializable {
    private final int senderId;
    private final Map<Integer, Double> weightVector;

    public Message(int fromNodeId, Map<Integer, Double> weightVector) {
        this.senderId = fromNodeId;
        this.weightVector = weightVector;
    }

    /**
     * returns a string representation of the object's state.
     */
    @Override
    public String toString() {
        return "Message{fromNodeId=" + senderId + ",weightVector=" + weightVector + "}";
    }
    /**
     * return the sender ID.
     */
    public int getSenderId() {
        return senderId;
    }
    /**
     * return the weight vector.
     */
    public Map<Integer, Double> getWeightVector() {
        return weightVector;
    }
}