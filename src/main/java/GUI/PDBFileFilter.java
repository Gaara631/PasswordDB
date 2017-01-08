package GUI;

import java.io.File;

/**
 * Created by gaara on 1/7/17.
 */
public class PDBFileFilter extends javax.swing.filechooser.FileFilter {
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        } else {
            String filename = f.getName().toLowerCase();
            return filename.endsWith(".pdb");
        }
    }

    @Override
    public String getDescription() {
        return "PDB PasswordDB file (*.pdb)";
    }
}