import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Mensagem implements Serializavel {
    public byte mensagem;

    public Mensagem() {
        this.mensagem = (byte) 0;
    }

    public Mensagem(byte message) {
        this.mensagem = message;
    }

    public Mensagem(int message) {
        this.mensagem = (byte) message;
    }

    public boolean equals(int m) {
        return this.mensagem == (byte) m;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        try {
            out.writeByte(mensagem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Serializavel deserialize(DataInputStream in) throws IOException {
        try {
            byte mess = in.readByte();
            return new Mensagem(mess);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}