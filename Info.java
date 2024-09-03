import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Info implements Serializavel {
    public final String username;
    public final String password;

    public Info() {
        this.username = null;
        this.password = null;
    }

    public Info(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        try {
            out.writeUTF(username);
            out.writeUTF(password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Serializavel deserialize(DataInputStream in) throws IOException {
        try {
            String username = in.readUTF();
            String password = in.readUTF();
            return new Info(username, password);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
