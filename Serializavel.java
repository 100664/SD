import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Serializavel {
    void serialize (DataOutputStream out) throws IOException;
    Serializavel deserialize(DataInputStream in) throws IOException;
}