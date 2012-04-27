package LIMBS;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.*;

public class LIMBSFileFilter extends FileFilter {

    //Accept all directories and all limbs files
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        //get file extension
        String extension = getFileExtension(f);
        
        //check extension for correctness
        if (extension != null) {
            if ( extension.equals("limbs") ) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //get the extension of the file as a string
    //null if no extension exists
    public String getFileExtension(File f){
        String extension = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            extension = s.substring(i+1).toLowerCase();
        }
        return extension;
    }
    
    //The description of this filter
    public String getDescription() {
        return "LIMBS Framework Models";
    }
}
