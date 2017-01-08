package Core;

import java.io.*;

/**
 * Created by gaara on 26.08.16.
 */
public class Entry implements Serializable{
    String name;
    String login;
    String password;
    String description;
    String group;

    public Entry(String name, String login, String password, String description, String group) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.group = group;
        this.description = description;
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
        return String.format("%s,%s,%s,%s", name,login,password,group);
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getGroup() {
        return group;
    }

    public String getDescription() {return description;}
}
