import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
public class Warning implements Serializavel {
    private static final long serialVersionUID = 1L;
    private static final String CLASS_TYPE = "Warning";

    private String warning;

    public Warning(String warning) {
        this.warning = warning;
    }

    public Warning() {
    }

    public String getWarning() {
        return this.warning;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(CLASS_TYPE);
        out.writeUTF(warning);
    }

    @Override
    public Serializavel deserialize(DataInputStream in) throws IOException {
        String className = in.readUTF();

        if (!className.equals(CLASS_TYPE)) {
            throw new IOException("Classe incompatível durante a desserialização.");
        }

        String warning = in.readUTF();
        return new Warning(warning);
    }
}
