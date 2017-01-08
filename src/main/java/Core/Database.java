package Core;

import javax.crypto.BadPaddingException;
import java.io.*;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Exceptions.NullPasswordException;
import Exceptions.WrongPasswordException;
import org.apache.commons.codec.binary.Base64;

import static java.lang.System.arraycopy;
import static java.lang.System.exit;

/**
 * Created by gaara on 19.08.16.
 */

public class Database {

    //TODO add updateEntry(String), deleteEntry(String)

    private int dataLength = 0;
    private String key;
    private byte[] vector = new byte[16];
    private String completePassword = "47al50b]3*fd/jld";
    //Pack my box with five dozen liquor jugs
    private final byte[] crc = {0x50,0x61,0x63,0x6b,0x20,0x6d,0x79,0x20,0x62,0x6f,0x78,0x20,0x77,0x69,0x74,0x68,0x20,0x66,0x69,0x76,0x65,0x20,0x64,0x6f,0x7a,0x65,0x6e,0x20,0x6c,0x69,0x71,0x75,0x6f,0x72,0x20,0x6a,0x75,0x67,0x73 };

    private File fileBase;
    private byte[] fileCache;

    public Database(String url,String password, boolean createFile) throws NullPasswordException, WrongPasswordException {
        if (password.length() <= 0) {
            throw new NullPasswordException("Password is null");
        }
        if(password.length()<16)
        {
            password += completePassword.substring(password.length());
        }
        else if(password.length()>16)
        {
            password = password.substring(0,16);
        }

        key = password;




        fileBase = new File(url);

        //Open File
        if (!createFile) {
            readData();



            for(int i=0;fileCache[i]!=0x00;i++)
            {
                if(i < fileCache.length - 4)
                if(fileCache[i] == 0x49 && fileCache[i+1] == 0x44 && fileCache[i+2] == 0x3d && fileCache[i+3] == 0x31)
                {
                    arraycopy(fileCache,i+4,vector,0,16);
                    i = i+19;
                }
            }

            int encryptedCRCLength = Database.encrypt(key,vector,crc).length;
            byte[] encryptedCRC = new byte[encryptedCRCLength];
            for (int j = fileCache.length-encryptedCRCLength,k = 0;j< fileCache.length;j++,k++)
            {
                encryptedCRC[k] = fileCache[j];
            }

            byte[] decryptedSRC = Database.decrypt(key,vector,encryptedCRC);


            if(!Arrays.equals(decryptedSRC,crc))
            {
                throw new WrongPasswordException("Invalid password");
            }
            readDataLength();
        }//Create File
        else {
            try {
                fileBase.createNewFile();
            } catch (IOException e) {
                System.out.println("File creation error");
            }
            //creationg vector
            new Random().nextBytes(vector);

            byte[] dataString = new byte[23];
            dataString[0] = 0x01;
            dataString[1] = 0x0A;
            //id
            dataString[2] = 0x49;
            dataString[3] = 0x44;
            dataString[4] = 0x3d;
            //id number
            dataString[5] = 0x31;
            //vector
            System.arraycopy(vector, 0, dataString, 6, 16);
            //end line
            dataString[22] = 0x0A;

            this.writeData(dataString, 0);




            //creation DataLength
            dataString = new byte[11];
            //id
            dataString[0] = 0x49;
            dataString[1] = 0x44;
            dataString[2] = 0x3d;
            //id number
            dataString[3] = 0x32;
            //DataLength
            dataLength = 0;
            arraycopy(intToByte(dataLength),0,dataString,4,4);
            //end header
            dataString[8] = 0x00;
            //end line
            dataString[9] = 0x0A;
            //text start
            dataString[10] = 0x02;

            this.writeData(dataString, fileCache.length);

            //write crc;
            byte[] encryptedCRC = Database.encrypt(key,vector,crc);

            dataString = new byte[6+encryptedCRC.length];
            dataString[0] = 0x03;
            dataString[1] = 0x0A;
            dataString[2] = 0x63;
            dataString[3] = 0x72;
            dataString[4] = 0x63;
            dataString[5] = 0x3d;

            System.arraycopy(encryptedCRC, 0, dataString, 6, encryptedCRC.length);

            this.writeData(dataString, fileCache.length);

        }
    }

    private byte[] intToByte(int number)
    {
        byte[] ans = {0x2E,0x2E,0x2E,0x2E};
        byte[] stringInt = Integer.toString(number).getBytes();
        arraycopy(stringInt,0,ans,0,stringInt.length);
        return ans;
    }

    private void writeData(byte[] data, int from) {
        if (from < 0) {
            System.out.println("Error \"From\" parametr");
            exit(1);
        }
        readData();
        byte[] newfileCache = new byte[fileCache.length+data.length];
        arraycopy(fileCache,0,newfileCache,0,from);//before data write
        arraycopy(data,0,newfileCache,from,data.length);//data write
        arraycopy(fileCache,from,newfileCache,from+data.length,fileCache.length-from);//after data write


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileBase);
            fos.write(newfileCache, 0, newfileCache.length);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can't write data:File not found");
            exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        readData();
    }

    private void readData() {
        fileCache = null;
        try {
            FileInputStream fis = new FileInputStream(fileBase);
            fileCache = new byte[fis.available()];
            fis.read(fileCache);
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addNewEntry(String EntryName, String login, String password,String description,String group) {


        byte[] data = new Entry(EntryName,login,password,description,group).serEntry();

        byte[] encryptedData = Database.encrypt(key,vector,data);

        byte[] readyToWrite = new byte[encryptedData.length+1];
        arraycopy(encryptedData,0,readyToWrite,0,encryptedData.length);
        readyToWrite[encryptedData.length] = 0x0A;

        dataLength +=readyToWrite.length;
        writeDataLength();

        writeData(readyToWrite,entryStart());

        System.out.println("New entry added\nName: " + EntryName + "\nLogin: " + login + "\nPassword: " + "********");

    }
    private void writeDataLength()
    {
        String fileCacheString = "";
        for (byte aFileCache : fileCache) {
            fileCacheString += (char) aFileCache;
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(fileBase,"rw");
            raf.seek(fileCacheString.indexOf("ID=2")+4);
            raf.write(intToByte(dataLength));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void readDataLength()
    {
        String fileCacheString = "";
        for (byte aFileCache : fileCache) {
            fileCacheString += (char) aFileCache;
        }
        byte[] readedLength = new byte[4];
        try {
            RandomAccessFile raf = new RandomAccessFile(fileBase,"r");
            raf.seek(fileCacheString.indexOf("ID=2")+4);
            raf.read(readedLength,0,4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String readedLengthS = "";
        for (int i=0;i<readedLength.length;i++)
        {
            if(readedLength[i]!=0x2E)
            readedLengthS += String.format("%c", readedLength[i]);
        }
        dataLength = Integer.parseInt(readedLengthS,10);



    }

    private int entryStart()
    {
        readData();
        for (int i=0;i<fileCache.length;i++)
        {
            if(fileCache[i] == 0x02)
            {
                return i+1;
            }
        }
        System.out.println("Invalid file structure can't find entry start");
        exit(1);
        return 0;
    }


    public Entry readEntry(String entryName) {
        String allEncryptedEntries = "";
        boolean read = false;
        int j = 0;
        for (byte ch : fileCache) {
            if (ch == 0x03) read = false;
            if (read) {
                allEncryptedEntries+= (char)ch;
                j++;
            }
            if (ch == 0x02) read = true;
        }

        StringTokenizer st = new StringTokenizer(allEncryptedEntries,"\n");
        while(st.hasMoreTokens())
        {
            String thisStr = st.nextToken();
            //System.out.println(thisStr);
            Entry thisEntr = null;
            try {
                thisEntr = Entry.deserEntry(Database.decrypt(key, vector, thisStr.getBytes()));
            } catch (WrongPasswordException e) {
                e.printStackTrace();
            }
            assert thisEntr != null;
            if(thisEntr.name.equals(entryName))
            {
                return thisEntr;
            }


        }


        return null;
    }
    public ArrayList<Entry> readAll()
    {

        String allEncryptedEntries = "";
        boolean read = false;
        int j = 0;
        for (byte ch : fileCache) {
            if (ch == 0x03) read = false;
            if (read) {
                allEncryptedEntries+= (char)ch;
                j++;
            }
            if (ch == 0x02) read = true;
        }

        StringTokenizer st = new StringTokenizer(allEncryptedEntries,"\n");
        int total = 0;
        //System.out.println("Name,Login,Password");
        ArrayList<Entry> Entries = new ArrayList<>();
        while(st.hasMoreTokens()) {
            String thisStr = st.nextToken();
            //System.out.println(thisStr);
            Entry thisEntr = null;
            try {
                thisEntr = Entry.deserEntry(Database.decrypt(key, vector, thisStr.getBytes()));
            } catch (WrongPasswordException e) {
                e.printStackTrace();
            }
            assert thisEntr != null;
            total++;
            //System.out.println(thisEntr);
            Entries.add(thisEntr);

        }

        //System.out.println("Total entries:" + total);
        return Entries;
    }

    private static String encrypt(String key, byte[] initVector, String value) {
        try {
            if(value == null)
                throw new NullPointerException("Data can not be null");
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            //System.out.println("encrypted string: " + Base64.encodeBase64String(encrypted));

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    private static byte[] encrypt(String key, byte[] initVector, byte[] value) {
        try {
            if(value == null)
                throw new NullPointerException("Data can not be null");
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value);
            //System.out.println("encrypted string: " + Base64.encodeBase64String(encrypted));

            return Base64.encodeBase64(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new byte[0];
    }

    private static String decrypt(String key, byte[] initVector, String encrypted) {
        try {
            if(encrypted == null)
                throw new NullPointerException("Data can not be null");
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    private static byte[] decrypt(String key, byte[] initVector, byte[] encrypted) throws WrongPasswordException {
        try {
            if(encrypted == null)
                throw new NullPointerException("Data can not be null");
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            return cipher.doFinal(Base64.decodeBase64(encrypted));

        }catch (BadPaddingException e) {
            WrongPasswordException le = new WrongPasswordException("Invalid password");
            le.initCause(e);
            throw le;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return new byte[0];

    }


}
