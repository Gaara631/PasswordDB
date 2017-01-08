package GUI;

import Core.Database;
import Core.Entry;
import Exceptions.NullPasswordException;
import Exceptions.WrongPasswordException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by gaara on 12/27/16.
 */
public class GUI extends JFrame {

    private Container contentPane;
    private File DBFile;
    private Database databaseObject;
    private JFrame frame;
    private Vector<Vector> groupData;
    private Vector<Vector> elementData;
    private JTable elementTable;
    private JTable groupTable;
    private JMenuItem editItemAdd;
    private JMenuItem fileItemClose;
    public GUI(){
        super("PasswordDB");
        this.setBounds(100,100,600,500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame = this;
        //Initializing components
        setupMenu();
        setupContent();


    }
    private void load()
    {
        editItemAdd.setEnabled(true);
        fileItemClose.setEnabled(true);
        updateTables();
    }
    private void unload()
    {
        databaseObject = null;
        DBFile = null;
        editItemAdd.setEnabled(false);
        fileItemClose.setEnabled(false);
        elementData.clear();
        groupData.clear();
        elementTable.revalidate();
        groupTable.revalidate();
    }
    private void updateTables()
    {
        editItemAdd.setEnabled(true);
        elementData.clear();
        groupData.clear();
        ArrayList<Entry> entries = databaseObject.readAll();
        for (Entry entr: entries) {
            Vector<String> elementRow = new Vector<>();
            elementRow.add(entr.getName());
            elementRow.add(entr.getLogin());
            elementRow.add(entr.getPassword());
            elementRow.add(entr.getDescription());
            elementData.add(elementRow);
            Vector<String> groupRow = new Vector<>();
            groupRow.add(entr.getGroup());
            groupData.add(groupRow);
        }
        groupTable.revalidate();
        elementTable.revalidate();
    }

    private void setupContent()
    {
        JPanel content;
        JScrollPane groupScrollPane;
        JScrollPane elementsScrollPane;
        JSplitPane splitPane;

        content = new JPanel(new BorderLayout());
        content.setBounds(frame.getBounds());

        //secondLevelElement - tables
        //groupTable
        groupData = new Vector<Vector>();

        Vector<String> groupHeaders = new Vector<String>();
        groupHeaders.addElement("Group");
        //elementTable
        elementData = new Vector<Vector>();

        Vector<String> elementHeaders = new Vector<String>();
        elementHeaders.add("Name");
        elementHeaders.add("Login");
        elementHeaders.add("Password");
        elementHeaders.add("Description");

        //setupTabes
        groupTable = new JTable(groupData,groupHeaders){
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };
        elementTable = new JTable(elementData,elementHeaders) {
            public boolean isCellEditable(int rowIndex, int vColIndex) {
                return false;
            }
        };

        //topLevelElemnent - scrollPane
        groupScrollPane = new JScrollPane(groupTable);
        elementsScrollPane = new JScrollPane(elementTable);



        splitPane = new JSplitPane();
        splitPane.setLeftComponent(groupScrollPane);
        splitPane.setRightComponent(elementsScrollPane);
        splitPane.setDividerSize(5);
        splitPane.setDividerLocation(150);

        //adding elemnts to content
        content.add(splitPane);

        //adding content to root content pane
        frame.getContentPane().add(content);



    }

    private void setupMenu(){
        JMenuBar menuBar = new JMenuBar();
        //Create FILE menu
        JMenu menuFile = new JMenu("File");

        //Creating items
        JMenuItem fileItemOpen = new JMenuItem("Open");
        menuFile.add(fileItemOpen);

        JMenuItem fileItemCreate = new JMenuItem("Create");
        menuFile.add(fileItemCreate);

        fileItemClose = new JMenuItem("Close");
        fileItemClose.setEnabled(false);
        menuFile.add(fileItemClose);

        JMenuItem fileItemExit = new JMenuItem("Exit");
        menuFile.add(fileItemExit);

        //Adding listeners
        fileItemOpen.addActionListener(new FileItemOpenActionListener());

        fileItemCreate.addActionListener(new FileItemCreateActionListener());

        fileItemClose.addActionListener(new FileItemCloseActionListener());

        fileItemExit.addActionListener(new FileItemExitActionListener());

        //Create Edit menu
        JMenu menuEdit = new JMenu("Edit");

        //Creating items
        editItemAdd = new JMenuItem("Add Entry");
        editItemAdd.setEnabled(false);
        menuEdit.add(editItemAdd);

        //Adding listeners
        editItemAdd.addActionListener(new EditItemAddActionListener());





        menuBar.add(menuFile);
        menuBar.add(menuEdit);

        this.setJMenuBar(menuBar);

    }

    private class ElementTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        public boolean isCellEditable(int row, int column){
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
        }

    }


    // ACTION LISTENERS SECTION=====================================================


    private class FileItemCreateActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setSelectedFile(new File(""));
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            //Add filter
            chooser.setFileFilter(new PDBFileFilter());

            if (chooser.showOpenDialog(frame) == JFileChooser.OPEN_DIALOG) {

                DBFile = chooser.getSelectedFile();





                //Initialize Dialog
                final JDialog passwordSetDialog = new JDialog();
                passwordSetDialog.setName("Set password");
                passwordSetDialog.setBounds(200,200,300,250);
                passwordSetDialog.setLayout(null);
                passwordSetDialog.setResizable(false);
                //Add label and buttons
                JLabel textForDialog = new JLabel("Set password for " + DBFile.getName()+ ":");
                textForDialog.setBounds(20,20,260,20);
                passwordSetDialog.getContentPane().add(textForDialog);

                JButton buttonOkforDialog = new JButton("Ok");
                buttonOkforDialog.setBounds(20,190,100,20);
                passwordSetDialog.getContentPane().add(buttonOkforDialog);

                JButton buttonCancelforDialog = new JButton("Cancel");
                buttonCancelforDialog.setBounds(180,190,100,20);
                passwordSetDialog.getContentPane().add(buttonCancelforDialog);

                final JPasswordField passwordField = new JPasswordField();
                passwordField.setBounds(20,50,260,25);
                passwordSetDialog.getContentPane().add(passwordField);

                JLabel confirmLabel = new JLabel("Confirm password:");
                confirmLabel.setBounds(20,80,200,20);
                passwordSetDialog.getContentPane().add(confirmLabel);

                final JPasswordField passwordConfirmField = new JPasswordField();
                passwordConfirmField.setBounds(20,110,260,25);
                passwordSetDialog.getContentPane().add(passwordConfirmField);

                final JLabel errorText = new JLabel("");
                errorText.setBounds(20,150,200,20);
                errorText.setForeground(Color.red);
                errorText.setVisible(false);
                passwordSetDialog.getContentPane().add(errorText);

                buttonOkforDialog.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        char[] ch = {};
                        if (Arrays.equals(passwordField.getPassword(),ch)){
                            errorText.setVisible(true);
                            errorText.setText("Password field is empty");
                            System.out.println("Password field is empty");
                        }
                        else {
                            if (Arrays.equals(passwordField.getPassword(), (passwordConfirmField.getPassword()))) {
                                DBFile = chooser.getSelectedFile();
                                try {
                                    if(DBFile.getName().endsWith(".pdb"))
                                        databaseObject = new Database(DBFile.getAbsolutePath(),String.copyValueOf(passwordField.getPassword()),true);
                                    else
                                        databaseObject = new Database(DBFile.getAbsolutePath() + ".pdb",String.copyValueOf(passwordField.getPassword()),true);
                                    load();

                                } catch (NullPasswordException | WrongPasswordException e1) {
                                    e1.printStackTrace();
                                }
                                passwordSetDialog.setVisible(false);
                                frame.setEnabled(true);



                            } else {
                                errorText.setVisible(true);
                                errorText.setText("Passwords do not match");
                                System.out.println("Passwords do not match");
                            }
                        }
                    }

                });

                buttonCancelforDialog.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DBFile = null;
                        passwordSetDialog.setVisible(false);
                        frame.setEnabled(true);
                    }
                });

                passwordSetDialog.setVisible(true);
                frame.setEnabled(false);

            }
            else {

            }

        }
    }
    private class FileItemOpenActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setSelectedFile(new File(""));
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            //Add filter
            chooser.setFileFilter(new PDBFileFilter());


            if (chooser.showOpenDialog(frame) == JFileChooser.OPEN_DIALOG) {
                DBFile = chooser.getSelectedFile();
                //Initialize Dialog
                final JDialog passwordDialog = new JDialog();
                passwordDialog.setName("Password");
                passwordDialog.setBounds(200,200,250,200);
                passwordDialog.setLayout(null);
                passwordDialog.setResizable(false);
                //Add label and buttons
                JLabel textForDialog = new JLabel("Enter password for " + DBFile.getName()+ ":");
                textForDialog.setBounds(20,20,260,20);
                passwordDialog.getContentPane().add(textForDialog);

                JButton buttonOkforDialog = new JButton("Ok");
                buttonOkforDialog.setBounds(10,140,100,20);
                passwordDialog.getContentPane().add(buttonOkforDialog);

                JButton buttonCancelforDialog = new JButton("Cancel");
                buttonCancelforDialog.setBounds(140,140,100,20);
                passwordDialog.getContentPane().add(buttonCancelforDialog);

                final JTextField textField = new JTextField();
                textField.setBounds(20,60,210,25);
                passwordDialog.getContentPane().add(textField);

                final JLabel errorText = new JLabel("");
                errorText.setBounds(20,100,200,20);
                errorText.setForeground(Color.red);
                errorText.setVisible(false);
                passwordDialog.getContentPane().add(errorText);
                //Add Action listener for buttons
                buttonOkforDialog.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            databaseObject = new Database(DBFile.getCanonicalPath(), textField.getText(), false);
                            passwordDialog.setVisible(false);
                            frame.setEnabled(true);
                            load();
                        } catch (WrongPasswordException | NullPasswordException ex) {
                            errorText.setVisible(true);
                            errorText.setText(ex.getMessage());
                            System.out.println(ex.getMessage());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

                buttonCancelforDialog.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DBFile = null;
                        passwordDialog.setVisible(false);
                        frame.setEnabled(true);
                    }
                });

                passwordDialog.setVisible(true);
                frame.setEnabled(false);

            } else {
                // do when cancel
            }
        }
    }
    private class FileItemExitActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }
    private class EditItemAddActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //CreateDialog
            final JDialog addDialog = new JDialog();
            addDialog.setName("Add entry");
            addDialog.setBounds(200,200,350,200);
            addDialog.setLayout(null);
            addDialog.setResizable(false);
            //Adding elements
                //Group
                JLabel groupLabel = new JLabel("Group:");
                groupLabel.setBounds(10,5,100,20);
                addDialog.add(groupLabel);

                final JTextField groupText = new JTextField();
                groupText.setBounds(115,5,200,20);
                addDialog.add(groupText);

                //Name
                JLabel nameLabel = new JLabel("Name:");
                nameLabel.setBounds(10,25,100,20);
                addDialog.add(nameLabel);

                final JTextField nameText = new JTextField();
                nameText.setBounds(115,25,200,20);
                addDialog.add(nameText);

                //Login
                JLabel loginLabel = new JLabel("Login:");
                loginLabel.setBounds(10,45,100,20);
                addDialog.add(loginLabel);

                final JTextField loginText = new JTextField();
                loginText.setBounds(115,45,200,20);
                addDialog.add(loginText);

                //Password
                JLabel passwordLabel = new JLabel("Password:");
                passwordLabel.setBounds(10,65,100,20);
                addDialog.add(passwordLabel);

                final JTextField passwordText = new JTextField();
                passwordText.setBounds(115,65,200,20);
                addDialog.add(passwordText);

                //Description
                JLabel descriptionLabel = new JLabel("Description:");
                descriptionLabel.setBounds(10,85,100,20);
                addDialog.add(descriptionLabel);

                final JTextField descriptionText = new JTextField();
                descriptionText.setBounds(115,85,200,20);
                addDialog.add(descriptionText);

                //Button add
                JButton buttonAdd = new JButton("Add");
                buttonAdd.setBounds(125,130,100,20);
                addDialog.add(buttonAdd);

                //Button cancel
                JButton buttonCancel = new JButton("Cancel");
                buttonCancel.setBounds(230,130,100,20);
                addDialog.add(buttonCancel);

                buttonAdd.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        databaseObject.addNewEntry(nameText.getText(),loginText.getText(),passwordText.getText(),descriptionText.getText(),groupText.getText());
                        addDialog.setVisible(false);
                        frame.setEnabled(true);
                        updateTables();
                    }
                });

                buttonCancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addDialog.setVisible(false);
                        frame.setEnabled(true);
                    }
                });

            //Showing up
            addDialog.setVisible(true);
            frame.setEnabled(false);

        }
    }
    class FileItemCloseActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            unload();
        }
    }
}
