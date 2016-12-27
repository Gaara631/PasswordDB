package App;

import java.io.*;

/**
 * Created by gaara on 26.08.16.
 */
public class Entry implements Serializable{
    String name;
    String login;
    String password;

    public Entry(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
    }

    public byte[] serEntry()
    {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(this);
            so.flush();
            return bo.toByteArray();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static Entry deserEntry(byte[] data)
    {
        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(data);
            ObjectInputStream si = new ObjectInputStream(bi);
            Entry obj = (Entry) si.readObject();
            return obj;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s", name,login,password);
    }
}
