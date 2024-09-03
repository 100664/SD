import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Connection implements AutoCloseable {

    private final Socket socket;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final Lock rl = new ReentrantLock();
    private final Lock wl = new ReentrantLock();

    public Connection(Socket sock) throws IOException {
        this.socket = sock;
        this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void send(Frame frame) throws IOException {
        int tag = frame.tag;
        Serializavel info = frame.data;
        send(tag, info);
    }

    public void send(int tag, Serializavel data) throws IOException{
        wl.lock();
        try{
            this.dos.writeInt(tag);
            this.dos.writeUTF(data.getClass().getName()); // escreve o nome da classe
            data.serialize(this.dos);
            this.dos.flush();
        } finally {
            wl.unlock();
        }
    }

    public Frame receive() throws IOException {
        int tag;
        Serializavel data = null;
        rl.lock();
        try{
            tag = this.dis.readInt();
            String className = this.dis.readUTF();
            Class<?> classe = Class.forName(className);
            data = (Serializavel) classe.getDeclaredConstructor().newInstance();
            data = data.deserialize(this.dis);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            rl.unlock();
        }
        return new Frame(tag, data);
    }


    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
