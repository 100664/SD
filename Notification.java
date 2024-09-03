import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Notification implements Serializavel {
    private String tarefa;
    private Mensagem mensagem;

    public Notification(String tarefa, Mensagem mensagem) {
        this.tarefa = tarefa;
        this.mensagem = mensagem;
    }
    public Notification() {
    }

    public String getTarefa() {
        return tarefa;
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(tarefa);
        mensagem.serialize(out);
    }

    @Override
    public Serializavel deserialize(DataInputStream in) throws IOException {
        String tarefa = in.readUTF();
        Mensagem mensagem = (Mensagem) new Mensagem().deserialize(in);
        return new Notification(tarefa, mensagem);
    }
}
