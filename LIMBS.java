/*
Copyright (c) 2011-2012 Danielle Grenier
Copyright (c) 2011-2012 Kevin Veloso
Copyright (c) 2011-2012 John S. H. Baxter
Copyright (c) 2011-2012 John Stevenson

Licensed under the Educational Community License version 1.0

This Original Work, including software, source code, documents,
or other related items, is being provided by the copyright holder(s)
subject to the terms of the Educational Community License. By
obtaining, using and/or copying this Original Work, you agree that you
have read, understand, and will comply with the following terms and
conditions of the Educational Community License:

Permission to use, copy, modify, merge, publish, distribute, and
sublicense this Original Work and its documentation, with or without
modification, for any purpose, and without fee or royalty to the
copyright holder(s) is hereby granted, provided that you include the
following on ALL copies of the Original Work or portions thereof,
including modifications or derivatives, that you make:

1. The full text of the Educational Community License in a location viewable to
users of the redistributed or derivative work.

2. Any pre-existing intellectual property disclaimers, notices, or terms and
conditions.

3. Notice of any changes or modifications to the Original Work, including the
date the changes were made.

4. Any modifications of the Original Work must be distributed in such a manner as
to avoid any confusion with the Original Work of the copyright holders.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

The name and trademarks of copyright holder(s) may NOT be used
in advertising or publicity pertaining to the Original or Derivative
Works without specific, written prior permission. Title to copyright in
the Original Work and any associated documentation will at all times
remain with the copyright holders.
*/


package LIMBS;

import java.util.*;
import java.io.*;
import java.lang.*;

import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import javax.swing.plaf.metal.OceanTheme;

public class LIMBS {

 /**
  * @param args
  */
  
 // Specify the look and feel to use by defining the LOOKANDFEEL constant
 // Valid values are: null (use the default), "Metal", "System", "Motif",
 // and "GTK"
 final static String LOOKANDFEEL = "NIMBUS";

 // If you choose the Metal L&F, you can also choose a theme.
 // Specify the theme to use by defining the THEME constant
 // Valid values are: "DefaultMetal", and "Ocean"
 final static String THEME = "Ocean";
  
 public static void main(String[] args) {
   //Set the look and feel.
   initLookAndFeel();
   LIMBSSystem.SystemCall().newInstance();   
 }
 
 private static void initLookAndFeel() {
     String lookAndFeel = null;
    
     if (LOOKANDFEEL != null) {
         if (LOOKANDFEEL.equals("Metal")) {
             lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
           //  an alternative way to set the Metal L&F is to replace the 
           // previous line with:
           // lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
             
         }
         
         else if (LOOKANDFEEL.equals("System")) {
             lookAndFeel = UIManager.getSystemLookAndFeelClassName();
         } 
         
         else if (LOOKANDFEEL.equals("Motif")) {
             lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
         } 
         
         else if (LOOKANDFEEL.equals("GTK")) { 
             lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
         } 
         
         else if (LOOKANDFEEL.equals("NIMBUS")) { 
             lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
             //UIManager.put("control", Color.white );
         }
         
         else {
             System.err.println("Unexpected value of LOOKANDFEEL specified: "
                                + LOOKANDFEEL);
             lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
         }

         try {
          
          
             UIManager.setLookAndFeel(lookAndFeel);
             
             // If L&F = "Metal", set the theme
             
             if (LOOKANDFEEL.equals("Metal")) {
               if (THEME.equals("DefaultMetal"))
                  MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
               else if (THEME.equals("Ocean"))
                  MetalLookAndFeel.setCurrentTheme(new OceanTheme());
               else
                  //MetalLookAndFeel.setCurrentTheme(new MetalTheme());
                  
               UIManager.setLookAndFeel(new MetalLookAndFeel()); 
             } 
              
              
               
             
         } 
         
         catch (ClassNotFoundException e) {
             System.err.println("Couldn't find class for specified look and feel:"
                                + lookAndFeel);
             System.err.println("Did you include the L&F library in the class path?");
             System.err.println("Using the default look and feel.");
         } 
         
         catch (UnsupportedLookAndFeelException e) {
             System.err.println("Can't use the specified look and feel ("
                                + lookAndFeel
                                + ") on this platform.");
             System.err.println("Using the default look and feel.");
         } 
         
         catch (Exception e) {
             System.err.println("Couldn't get specified look and feel ("
                                + lookAndFeel
                                + "), for some reason.");
             System.err.println("Using the default look and feel.");
             e.printStackTrace();
         }
     }
 }

 
}
