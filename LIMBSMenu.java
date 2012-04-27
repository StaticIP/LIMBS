package LIMBS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Graphics;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.JFileChooser;
import java.io.File;

@SuppressWarnings("serial")
public class LIMBSMenu extends JMenuBar implements ActionListener {

 private JMenu file, edit, help;
 private JMenuItem newSim, open, save, saveAs, exit, undo, redo, clone,
   delete, helpTopics, preferences;

 private final Interface i;
 private final Simulation simulation;

 private final JFileChooser fc;
 private File savedFile = null;

 ComponentWidget currentComponent = null;

 ComponentWidget copiedComponent = null;
 
 private boolean enableControls = true;

 public LIMBSMenu(Interface i, Simulation s) {
  this.i = i;
  this.simulation = s;

  // set up the file filter
  this.fc = new JFileChooser();
  this.fc.addChoosableFileFilter(new LIMBSFileFilter());
  this.fc.setAcceptAllFileFilterUsed(false);

  file = new JMenu("File");
  file.setMnemonic(KeyEvent.VK_F);

  newSim = new JMenuItem("New");
  newSim.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
    ActionEvent.CTRL_MASK));
  newSim.setActionCommand("newSim");
  newSim.addActionListener(this);

  open = new JMenuItem("Open");
  open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
    ActionEvent.CTRL_MASK));
  open.setActionCommand("openSim");
  open.addActionListener(this);

  save = new JMenuItem("Save");
  save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
    ActionEvent.CTRL_MASK));
  save.setActionCommand("save");
  save.addActionListener(this);

  saveAs = new JMenuItem("Save As");
  saveAs.setAccelerator(KeyStroke.getKeyStroke('S',
    (KeyEvent.SHIFT_MASK | ActionEvent.CTRL_MASK)));
  saveAs.setActionCommand("saveAs");
  saveAs.addActionListener(this);

  preferences = new JMenuItem("Preferences");
  preferences.setActionCommand("preferences");
  preferences.addActionListener(this);

  exit = new JMenuItem("Exit");
  exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
    ActionEvent.CTRL_MASK));
  exit.setActionCommand("exit");
  exit.addActionListener(this);

  file.add(newSim);
  file.add(open);
  file.add(save);
  file.add(saveAs);
  file.addSeparator();
  file.add(preferences);
  file.addSeparator();
  file.add(exit);

  this.add(file);

  edit = new JMenu("Edit");
  edit.setMnemonic(KeyEvent.VK_E);

  undo = new JMenuItem("Undo");
  undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
    ActionEvent.CTRL_MASK));
  undo.setActionCommand("undo");
  undo.addActionListener(this);
  undo.setEnabled(false);

  redo = new JMenuItem("Redo");
  redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
    ActionEvent.CTRL_MASK));
  redo.setActionCommand("redo");
  redo.addActionListener(this);
  redo.setEnabled(false);

  clone = new JMenuItem("Clone");
  clone.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
    ActionEvent.CTRL_MASK));
  clone.setActionCommand("clone");
  clone.addActionListener(this);

  delete = new JMenuItem("Delete");
  delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
    ActionEvent.CTRL_MASK));
  delete.setActionCommand("delete");
  delete.addActionListener(this);

  edit.add(undo);
  edit.add(redo);
  edit.addSeparator();
  edit.add(clone);
  edit.addSeparator();
  edit.add(delete);

  this.add(edit);

  help = new JMenu("Help");
  help.setMnemonic(KeyEvent.VK_H);

  helpTopics = new JMenuItem("Help Topics");
  helpTopics.setAccelerator(KeyStroke.getKeyStroke('H',
    (KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK)));
  helpTopics.setActionCommand("help");
  helpTopics.addActionListener(this);

  help.add(helpTopics);

  this.add(help);

 }

 public void enableControls() {
  save.setEnabled(true);
  saveAs.setEnabled(true);
  exit.setEnabled(true);
  undo.setEnabled(true);
  redo.setEnabled(true);
  clone.setEnabled(true);
  delete.setEnabled(true);
  enableControls = true;
 }

 public void disableControls() {
  save.setEnabled(false);
  saveAs.setEnabled(false);
  exit.setEnabled(false);
  undo.setEnabled(false);
  redo.setEnabled(false);
  clone.setEnabled(false);
  delete.setEnabled(false);
  enableControls = false;
 }

 public JFileChooser getFileChooser() {
  return this.fc;
 }

 public void setComponentWidget(ComponentWidget c) {
  this.currentComponent = c;
 }

 public void paint(Graphics g) {
  undo.setEnabled(simulation.canUndo() && enableControls);
  redo.setEnabled(simulation.canRedo() && enableControls);
  super.paint(g);
 }

 public void actionPerformed(ActionEvent e) {
  String command = e.getActionCommand();

  if (command.equals("newSim")) {
   LIMBSSystem.SystemCall().newInstance();
  } else if (command.equals("openSim")) {

   int openValue = fc.showOpenDialog(this);
   if (openValue == JFileChooser.APPROVE_OPTION) {
    savedFile = fc.getSelectedFile();
    LIMBSSystem.SystemCall().load(savedFile);
   }

  } else if (command.equals("redo")) {
      simulation.redo();
  } else if (command.equals("undo")) {
   simulation.undo();
  } else if (command.equals("save")) {

   if (this.savedFile == null) {
    int saveValue = fc.showSaveDialog(this);
    if (saveValue == JFileChooser.APPROVE_OPTION) {
     savedFile = fc.getSelectedFile();

     // find enxtrension if any and isolate file name
     String fileName = null;
     String s = savedFile.getName();
     int i = s.lastIndexOf('.');
     if (i > 0 && i < s.length() - 1) {
      fileName = s.substring(0, i).toLowerCase();
     } else {
      fileName = s;
     }

     // forcing name conventions
     File tempFile = new File(savedFile.getParent(), fileName
       + ".limbs");
     savedFile = tempFile;
     LIMBSSystem.SystemCall().save(savedFile, simulation);
    }
   } else {
    LIMBSSystem.SystemCall().save(this.savedFile, simulation);
   }

  } else if (command.equals("saveAs")) {

   int saveValue = fc.showSaveDialog(this);
   if (saveValue == JFileChooser.APPROVE_OPTION) {
    savedFile = fc.getSelectedFile();

    // find enxtrension if any and isolate file name
    String fileName = null;
    String s = savedFile.getName();
    int i = s.lastIndexOf('.');
    if (i > 0 && i < s.length() - 1) {
     fileName = s.substring(0, i).toLowerCase();
    } else {
     fileName = s;
    }

    // forcing name conventions
    File tempFile = new File(savedFile.getParent(), fileName
      + ".limbs");
    savedFile = tempFile;
    LIMBSSystem.SystemCall().save(savedFile, simulation);
   }

  } else if (command.equals("preferences")) {
   this.i.openPreferences();
  } else if (command.equals("exit")) {
   // check for save stuff
    i.stopSimulation();
   LIMBSSystem.SystemCall().shutdown(this.i, this.simulation);
   // System.exit(0);
  } else if (command.equals("help")) {
   this.i.openHelpContents();
  } else if (command.equals("clone")) {
   this.i.getCanvas().copyComponent(this.currentComponent);
  } else if (command.equals("delete")) {
   this.i.getCanvas().removeComponent(this.currentComponent);
  }
 }
}
