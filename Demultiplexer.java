import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {
    private final Connection c;
    private final ReentrantLock l;
    private final Map<Integer, TaggedMessages> map;
    private Exception exception = null;

    private static class TaggedMessages {
        Queue<Serializavel> queue = new ArrayDeque<>();
        Condition cond;

        public TaggedMessages(ReentrantLock lock) {
            this.cond = lock.newCondition();
        }
    }


    public Demultiplexer(Connection conn) {
        this.c = conn;
        this.l = new ReentrantLock();
        this.map = new HashMap<>();
        this.start();
    }

    public void start() {
        new Thread(() -> {
            try {
                while (true) {
                    Frame frame = this.c.receive();
                    l.lock();
                    try {
                        TaggedMessages td = map.computeIfAbsent(frame.tag, k -> new TaggedMessages(l));
                        td.queue.add(frame.data);
                        td.cond.signal();
                    } finally {
                        l.unlock();
                    }
                }
            } catch (IOException exc) {
                this.l.lock();
                try {
                    this.exception = exc;
                    this.map.forEach((k, v) -> v.cond.signalAll());
                } finally {
                    this.l.unlock();
                }
            }
        }).start();
    }

    public void send(Frame frame) throws IOException {
        c.send(frame);
    }

    public void send(int tag, byte[] data) throws IOException {
        c.send(tag, new ByteArrayWrapper(data));
    }

    public void send(int tag, Serializavel data) throws IOException {
        c.send(tag, data);
    }

    public Serializavel receive(int tag) throws Exception {
        l.lock();
        try {
            TaggedMessages td = map.computeIfAbsent(tag, k -> new TaggedMessages(l));
            while (true) {
                if (exception != null) {
                    throw this.exception;
                }
                if (!td.queue.isEmpty()) {
                    return td.queue.poll();
                }
                td.cond.await();
            }
        } finally {
            l.unlock();
        }
    }

    public void close() throws IOException {
        c.close();
    }
}