import sd23.JobFunction;
import sd23.JobFunctionException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Workers implements Runnable {
    private final Queue<TaskInfo> tasks;
    private final Connection connection;
    private final Lock queueLock;

    public Workers(Connection connection) {
        this.tasks = new LinkedList<>();
        this.connection = connection;
        this.queueLock = new ReentrantLock();
    }

    public void addTask(TaskInfo task) {
        queueLock.lock();
        try {
            tasks.offer(task);
        } finally {
            queueLock.unlock();
        }
    }

    public List<String> getAllNTarefas() {
        List<String> TarefasList = new ArrayList<>();

        queueLock.lock();
        try {
            for (TaskInfo taskInfo : tasks) {
                TarefasList.add(taskInfo.getTarefa());
            }
        } finally {
            queueLock.unlock();
        }

        return TarefasList;
    }

    @Override
    public void run() {
        while (true) {
            TaskInfo taskInfo = null;
            queueLock.lock();
            try {
                if (!tasks.isEmpty()) {
                    taskInfo = tasks.poll();
                }
            } finally {
                queueLock.unlock();
            }

            if (taskInfo != null) {
                byte[] file = taskInfo.getFileBytes();
                String tarefa = taskInfo.getTarefa();

                try {
                    byte[] output = JobFunction.execute(file);
                    Notification notification = new Notification(tarefa, new Mensagem(3));
                    connection.send(69, notification);

                    String fileName = Helper.generateFileName();
                    Helper.writeBytesToFile(output, fileName);
                } catch (JobFunctionException e) {
                    try {
                        Notification notification = new Notification(tarefa, new Mensagem(4));
                        connection.send(69, notification);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
