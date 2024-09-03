import java.io.*;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class that stores information about accounts.
 */
public class Account {

    class PassAndIslogged{
        String password;
        boolean isLogged;

        public PassAndIslogged(String password) {
            this.password = password;
            this.isLogged = true; // porque sempre que se cria uma conta, o utilizador entra automaticamente na aplicação.
        }

        public PassAndIslogged(String password, boolean isLogged) {
            this.password = password;
            this.isLogged = isLogged;
        }
    }
    private ReentrantReadWriteLock l = new ReentrantReadWriteLock();
    private final HashMap<String, PassAndIslogged> contas;

    public Account() {
        this.contas = new HashMap<>();
    }


    public boolean isLogged(String username){
        l.readLock().lock();
        try{
            return contas.get(username).isLogged;
        } finally {
            l.readLock().unlock();
        }
    }


    public void logOutUser(String username) {
        l.writeLock().lock();
        try {
            if (accountExists(username)) {
                contas.get(username).isLogged = false;
            } else {

            }
        } finally {
            l.writeLock().unlock();
        }
    }


    public String getPassword(String username) {
        l.readLock().lock();
        try{
            if (accountExists(username)){
                return contas.get(username).password;
            }
            return null;
        } finally {
            l.readLock().unlock();
        }
    }


    public boolean addAccount(String username, String password) {
        l.writeLock().lock();
        try {
            if (accountExists(username)) return false;
            contas.put(username, new PassAndIslogged(password));
            return true;
        } finally {
            l.writeLock().unlock();
        }
    }


    private boolean accountExists(String username) {
        return contas.containsKey(username);
    }




    public void serialize(String filepath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filepath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
        fos.close();
    }

    public static Account deserialize(String filepath) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filepath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Account accounts = (Account) ois.readObject();
        ois.close();
        fis.close();
        return accounts;
    }
}