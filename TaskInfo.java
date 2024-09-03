import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskInfo implements Serializavel {
    private byte[] fileBytes;
    private String tarefa;

    public TaskInfo(byte[] fileBytes, String tarefa) {
        this.fileBytes = fileBytes;
        this.tarefa = tarefa;
    }

    public TaskInfo() {
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public String getTarefa() {
        return tarefa;
    }

    public static List<TaskInfo> lerFicheiro(String fileName) throws IOException {
        String filePath = "/Users/martimr/Desktop/merdas da uni/3ano1sem/SD/Projeto_agrVAI/files/" + fileName;
        List<TaskInfo> tarefas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    TaskInfo tarefa = new TaskInfo(Helper.convertStringToBytes(parts[2]), parts[0]);
                    tarefas.add(tarefa);
                } else {
                    System.out.println("Linha inválida: " + line);
                }
            }
        }

        return tarefas;
    }

    @Override
    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(fileBytes.length);
        out.write(fileBytes);

        out.writeUTF(tarefa);
    }

    @Override
    public Serializavel deserialize(DataInputStream in) throws IOException {
        int length = in.readInt();
        byte[] data = new byte[length];
        in.readFully(data);
        String tarefa = in.readUTF();
        return new TaskInfo(data, tarefa);
    }
}
