import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

// "0" é mensagem de password errada."1" é mensagem de sucesso. "2" é mensagem de conta não existe.

public class Server {

    public static void main(String[] args) throws IOException {

        ServerSocket socket = new ServerSocket(11211);

        final Account contas = new Account();


        while (true) {
            Socket s = socket.accept();
            Connection c = new Connection(s);

            Workers workers = new Workers(c);
            Thread workersThread = new Thread(workers);
            workersThread.start();

            Runnable processing = () -> {
                String userHelper = "";
                try (c) {
                    Runtime.getRuntime().addShutdownHook(new Thread(Helper::cleanDirectory));
                    while (true) {
                        Frame frame = c.receive();

                        if (frame.tag == 0 || frame.tag == 1) {
                            Info acc = (Info) frame.data;
                            String username = acc.username;
                            String password = acc.password;
                            System.out.printf("Utilizador %s está a tentar %s-se.\n", username, (frame.tag == 0) ? "registar" : "logar");


                            switch (frame.tag) {
                                case 0 -> // sign-in
                                        handleSignIn(contas, c, username, password);
                                case 1 -> // log-in
                                        handleLogIn(contas, c, username, password);
                            }
                        }
                        else if (frame.tag == 2){
                                TaskInfo taskInfo = (TaskInfo) frame.data;
                                workers.addTask(taskInfo);
                            }
                        else if (frame.tag == 3){
                            List tarefasNotDone = workers.getAllNTarefas();
                            String resposta = Helper.formatMissingTasks(tarefasNotDone);
                            Warning warning = new Warning(resposta);
                            c.send(3, warning);

                        }
                    }
                } catch (IOException exc) {
                    System.out.printf("Utilizador %s desconectou-se do servidor.\n", userHelper);
                    contas.logOutUser(userHelper);
                }
            };
            new Thread(processing).start();

        }

    }

    private static void handleSignIn(Account contas, Connection c, String username, String password) throws IOException {
        boolean flag = contas.addAccount(username, password);
        c.send(0, new Mensagem(flag ? 1 : 0));
    }

    private static void handleLogIn(Account contas, Connection c, String username, String password) throws IOException {
        String pass = contas.getPassword(username);
        if (pass != null) {
            if (pass.equals(password)) {
                c.send(0, new Mensagem(1));
            } else {
                c.send(0, new Mensagem(0));
            }
        } else {
            c.send(0, new Mensagem(2));
        }
    }
}
