package App;

import org.apache.commons.cli.*;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

/**
 * Created by gaara on 19.08.16.
 */
public class App {

    public static void main(String[] args) {

        //Parsing options
        Options options = new Options();

        Option addDatabase = new Option("n", "newdatabase", false, "Create database");
        addDatabase.setRequired(false);
        options.addOption(addDatabase);

        Option databasepath = new Option("d", "database", true, "Database to open");
        databasepath.setRequired(true);
        options.addOption(databasepath);

        Option databasePassword = new Option("p", "password", true, "Password to database");
        databasePassword.setRequired(false);
        options.addOption(databasePassword);

        Option addEntry = new Option("a", "addEntry", true, "Add new entry");
        addEntry.setRequired(false);
        options.addOption(addEntry);

        Option getEntry = new Option("g", "getEntry", true, "Read entry");
        getEntry.setRequired(false);
        options.addOption(getEntry);

        Option getAllEntry = new Option("ga", "getAllEntry", false, "Read all entry");
        getAllEntry.setRequired(false);
        options.addOption(getAllEntry);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            exit(1);
            return;
        }

        //cheks login password


        String DatabasePath = cmd.getOptionValue("database");

        //create or open database
        boolean createFile = false;
        if(cmd.hasOption("newdatabase"))
        {
            createFile = true;
        }
        else {
            if(!(new File(DatabasePath).exists())) {
                System.out.println("File does not exists");
                exit(1);
            }
        }

        String password = null;
        Database database = null;

        if(cmd.hasOption("password"))
        {
            password = cmd.getOptionValue("password");
            try {
                database = new Database(DatabasePath, password, createFile);
            } catch (LoginException e) {
                System.out.println(e.getMessage());
                exit(0);

            }
        }
        else
        {
            for (int trys = 0;;trys++) {
                Console cons = System.console();
                char[] pwd = cons.readPassword("Password:");
                password = "";
                for (int i = 0; i < pwd.length; i++) {
                    password += pwd[i];
                }

                //Establish connection
                try {
                    database = new Database(DatabasePath, password, createFile);
                    break;
                } catch (LoginException e) {
                    System.out.println(e.getMessage());
                    if(trys >=5) exit(0);
                }
            }
        }

        if(cmd.hasOption("addEntry")) {


            Pattern pt = Pattern.compile("(.+);(.+);(.+)");
            Matcher mt = pt.matcher(cmd.getOptionValue("addEntry"));
            try {
                mt.find();
                String eName = mt.group(1);
                String eLogin = mt.group(2);
                String ePass = mt.group(3);
                database.addNewEntry(eName, eLogin, ePass);
            }
            catch (IllegalStateException e)
            {
                System.out.println("Invalid addEntry argument, must be val1;val2;val3");
                exit(1);
            }


        }
        if(cmd.hasOption("getEntry"))
        {
            database.readEntry(cmd.getOptionValue("getEntry"));
        }
        if(cmd.hasOption("getAllEntry"))
        {
            database.readAll();
        }


//        System.out.println("Database =" + DatabasePath);
//        System.out.println("Login =" + login);
//        System.out.println("Password =" + password);



    }
}
