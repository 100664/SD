import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Client {

    public static void main(String[] args) throws Exception {
        Socket socket = null;

        try {
            socket = new Socket("localhost", 11211);
        } catch (ConnectException exc) {
            System.out.println("Reinicie o processo com o Servidor ligado!");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try (Demultiplexer demultiplexer = new Demultiplexer(new Connection(socket))) {
            ReentrantLock Lok = new ReentrantLock();
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

            String username = null;

            while (username == null) {
                System.out.print("""
                 -----------------Menu-----------------
                 | 1-> Registar nova conta.           |
                 | 2-> Iniciar sessão.                |
                 --------------------------------------

                 Opção:\s""");

                try {
                    String input1 = stdin.readLine();

                    if (input1.equals("1")) {
                        System.out.print("""
                 -------------Menu Registro--------------
                 | Pense num username.                  |
                 | Pense numa password.                 |
                 ----------------------------------------
                 Introduza o seu username:\s""");

                        String inputUser = stdin.readLine();
                        System.out.print("Introduza a sua palavra-passe: ");
                        String inputPass = stdin.readLine();
                        boolean helper = Helper.register(demultiplexer,inputUser,inputPass);
                        if (helper) username = inputUser;

                    }
                    else if (input1.equals("2")) {
                        System.out.print("""
                  --------------Menu Log-IN---------------
                  | Digite o seu username.               |
                  | Digite a sua password.               |
                  ----------------------------------------
                  Introduza o seu username:\s""");

                        String uName = stdin.readLine();
                        System.out.print("Introduza a sua palavra-passe: ");
                        String password = stdin.readLine();
                        boolean helper = Helper.login(demultiplexer, uName, password);
                        if (helper) username = uName;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Thread receivingNotifications = new Thread(()->{
                while(true) {
                    Notification notification = null;
                    try {
                        notification = (Notification) demultiplexer.receive(69);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Lok.lock();
                    try{
                        Mensagem mensagem = notification.getMensagem();
                        String tarefa = notification.getTarefa();
                    if (mensagem.equals(3)) {
                        System.out.println(tarefa + " teve sucesso na execução.");
                    } else if (mensagem.equals(4)) {
                        System.out.println(tarefa + " não teve sucesso.");
                    }
                    } finally {
                        Lok.unlock();
                    }
                }
                });
            receivingNotifications.start();

            boolean menu = false;
            while (!menu) {
                System.out.print("""
                        ------------------Menu------------------
                        | 1 -> Dar ficheiro                    |
                        | 2-> Tarefas Pendentes                |
                        | 3 -> Sair.                           |
                        ----------------------------------------
                        Opção:\s""");
                String option = stdin.readLine();
                switch (option) {
                    case "3" -> menu = true;
                    case "2" -> {
                        demultiplexer.send(3, new Mensagem());
                        Warning warning = (Warning) demultiplexer.receive(3);
                        String tarefasPendentes = warning.getWarning();
                        System.out.println(tarefasPendentes);
                    }
                    case "1" -> {
                        int nTarefa = Helper.generateJobNumber();
                        System.out.printf("---------------Processo %d-------------\n", nTarefa);
                        System.out.print("| Nome do ficheiro a se trabalhar.     |\n");
                        System.out.print("----------------------------------------\n");
                        System.out.println("File Name: ");
                        String fileName = stdin.readLine();
                        List<TaskInfo> Alltasks = TaskInfo.lerFicheiro(fileName);
                        for (TaskInfo taskInfo : Alltasks) {
                            byte[] fileBytes = taskInfo.getFileBytes();
                            String tarefa = taskInfo.getTarefa();
                            TaskInfo file_t = new TaskInfo(fileBytes, tarefa);
                            demultiplexer.send(2, file_t);
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
