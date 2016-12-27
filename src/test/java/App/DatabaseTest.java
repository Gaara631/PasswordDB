package App;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.Timeout;

import javax.security.auth.login.LoginException;
import javax.xml.crypto.Data;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.lang.System.exit;
import static org.junit.Assert.*;

/**
 * Created by gaara on 22.08.16.
 */
public class DatabaseTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    Database testDB;
    @Before
    public void init()
    {
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setErr(null);
    }

    @Test(timeout=3000)
    public void crcTestWithCreate() throws Exception{
        testDB = new Database("ForTest","Password",true);
    }

    @Test(timeout=3000)
    public void crcTestWithoutCreate() throws Exception{
        testDB = new Database("ForTest","Password",false);
    }



    @Test
    public void addReadEntryWithCreate() throws Exception {

       System.setOut(new PrintStream(outContent));
        try {
            testDB = new Database("ForTest","Password",true);
        } catch (LoginException e) {
            e.printStackTrace();
        }

        testDB.addNewEntry("NewEntry1","MyLogin","MyPassword");
        testDB.addNewEntry("NewEntry2","MyLogin","MyPassword");
        testDB.readEntry("NewEntry1");

        assertNotEquals("Entry not found\n",outContent.toString());
        System.setOut(null);
    }

    @Test
    public void addReadEntryWithoutCreate() throws Exception {

        System.setOut(new PrintStream(outContent));
        try {
            testDB = new Database("ForTest","Password",false);
        } catch (LoginException e) {
            e.printStackTrace();
            exit(1);
        }

        testDB.addNewEntry("SuperEntry","MyLogin","MyPassword");
        testDB.readEntry("SuperEntry");


        assertNotEquals("Entry not found\n",outContent.toString());
        System.setOut(null);
    }

    @Test
    public void addReadAllEntry() throws Exception {

        try {
            testDB = new Database("ForTest","Password",false);
        } catch (LoginException e) {
            e.printStackTrace();
        }

        testDB.addNewEntry("NewEntry1","MyLogin","MyPassword");
        testDB.addNewEntry("NewEntry2","MyLogin","MyPassword");
        testDB.addNewEntry("NewEntry3","MyLogin","MyPassword");

        testDB.readAll();
        assertNotEquals("Total entries:0",outContent.toString());
    }






}