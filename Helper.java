import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Helper {

    public static int fileNumber = 1;
    public static int jobNumber = 0;

    public static boolean register(Demultiplexer demultiplexer, String username, String pass) throws Exception {
        Info acc = new Info(username, pass);
        demultiplexer.send(0, acc);
        Mensagem receive = (Mensagem) demultiplexer.receive(0);
        if (receive.equals(1)) {
            System.out.printf("\nRegistado do Utilizador %s feito com sucesso!%n", username);
            return true;
        } else {
            System.out.printf("\nProblema no registro do Utilizador %s, este nome já se encontra em uso!%n", username);
            return false;
        }
    }

    public static boolean login (Demultiplexer demultiplexer, String username, String pass) throws Exception {
        boolean respost = false;
        Info acc = new Info(username, pass);
        demultiplexer.send(1, acc);
        Mensagem response = (Mensagem) demultiplexer.receive(0);
        if (response.equals(1)) {
            System.out.println("\n");
            System.out.printf("Bem-vindo %s!%n", username);
            System.out.println("\n");
            respost = true;
        } else if (response.equals(0)) {
            System.out.println("\n");
            System.out.println("Password errada. Digite novamente.");
            System.out.println("\n");
        }
        else if (response.equals(3)) {
            System.out.println("\n");
            System.out.println("A conta à qual prentede aceder já se encontra em execução");
            System.out.println("\n");
        }
        else if (response.equals(2)){
            System.out.println("\n");
            System.out.println("A conta que deseja aceder não existe");
            System.out.println("\n");
        }
        else
            System.out.println("BUG no sistema .-.");
        return respost;

    }

    public static byte[] convertStringToBytes(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }

    public static void writeBytesToFile(byte[] bytes,String fileName) {
       String directoryPath = "/Users/martimr/Desktop/merdas da uni/3ano1sem/SD/Projeto_agrVAI/files_output/";
        try (FileOutputStream fos = new FileOutputStream(directoryPath + fileName)) {
            fos.write(bytes);
            System.out.println("Arquivo criado com sucesso: " + directoryPath + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cleanDirectory() {
        String directoryPath = "/Users/martimr/Desktop/merdas da uni/3ano1sem/SD/Projeto_agrVAI/files_output";
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    file.delete();
                }

                System.out.println("Diretório limpo com sucesso: " + directoryPath);
            } else {
                System.out.println("O diretório está vazio: " + directoryPath);
            }
        } else {
            System.out.println("O diretório não existe ou não é um diretório válido: " + directoryPath);
        }
    }

    public static String generateFileName() {
        String fileName = "output_file" + fileNumber;
        fileNumber++;
        return fileName;
    }
    public static int generateJobNumber() {
        jobNumber++;
        return jobNumber;
    }

    public static String tester(byte[] bytes) {
        StringBuilder tester = new StringBuilder();
        for (byte b : bytes) {
            tester.append(String.format("%02X ", b & 0xFF));
        }
        return tester.toString();
    }

    public static int getFileNumber() {
        return fileNumber;
    }
    public static String formatMissingTasks(List<String> tasksList) {
        StringBuilder result = new StringBuilder("Tarefas a faltar:\n");

        for (String task : tasksList) {
            result.append(task).append("\n");
        }

        return result.toString();
    }
}

