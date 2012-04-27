package LIMBS;

//necessary Java classes to create component
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.math.*;

public class HOTCONodeWidget extends JFrame implements ActionListener {
 private JPanel panel, menu;
 private JPanel activation, valence;//, intensity, potency;
 private HOTCONode node;
 private final HOTCOAgent model;
 private Dimension maxTextSize = new Dimension(75,25);
 private Dimension menuLabelSize = new Dimension(125, 20);
 private JTextField initialActivationField, initialValenceField;//, initialIntensityField, initialPotencyField;
 private JTextField currentActivationField, currentValenceField;//, currentIntensityField, currentPotencyField;
 private JTextField nameField;
 
 private int size_x = 300;
 private int size_y = 250;
 
 public HOTCONodeWidget(HOTCONode activeNode, final HOTCOAgent model) {
  super(activeNode.getName());
  node = activeNode;
  this.model = model;
  
  panel = new JPanel();
  panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
  panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
  panel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
  
  setupNodeName();
  setupMenu();
  panel.add(menu);
  panel.add(Box.createVerticalGlue());
  
  // add buttons
  JPanel buttonPanel = new JPanel();
  buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
  buttonPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
  buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,5));
  
  JButton update = new JButton("Update!");
  update.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
  update.addActionListener(this);
  
  buttonPanel.add(Box.createHorizontalGlue());
  buttonPanel.add(update);
  
  if (node.isDeletable()) {
    JButton delete = new JButton("Delete");
    delete.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    delete.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        
        model.getSimulation().registerEndChange(new ChangeNotification(){
          public boolean notifyChange(){
            model.recalculateEmotion();
            return true;
          }
        });
   
        model.removeNode(node);
        model.recalculateEmotion();
        
        // close the window
        setVisible(false);
        dispose();
      }
    });
    
    buttonPanel.add(delete);
  }
  
  panel.add(buttonPanel);
  add(panel);
  
  setLocation(150, 150);
  
  pack();
  setSize(size_x, size_y);
  setVisible(true);
 }
 
 public void actionPerformed(ActionEvent e) {
   
   model.getSimulation().registerEndChange(new ChangeNotification(){
     public boolean notifyChange(){
       model.recalculateEmotion();
       return true;
     }
   });
   
   String newName = nameField.getText();
   node.setName(newName);
   
   double newInitActVal, newInitValenceVal, newInitIntensityVal, newInitPotencyVal;
   double newCurrActVal, newCurrValenceVal, newCurrIntensityVal, newCurrPotencyVal;
   
   try {
     // activation
     newInitActVal = Double.valueOf(initialActivationField.getText()).doubleValue();
     newCurrActVal = Double.valueOf(currentActivationField.getText()).doubleValue();
     //double new
  
     // valence
     newInitValenceVal = Double.valueOf(initialValenceField.getText()).doubleValue();
     newCurrValenceVal = Double.valueOf(currentValenceField.getText()).doubleValue();
   
     // intensity
     //newInitIntensityVal = Double.valueOf(initialIntensityField.getText()).doubleValue();
     //newCurrIntensityVal = Double.valueOf(currentIntensityField.getText()).doubleValue();
   
     // potency;
     //newInitPotencyVal = Double.valueOf(initialPotencyField.getText()).doubleValue();
     //newCurrPotencyVal = Double.valueOf(currentPotencyField.getText()).doubleValue();
   }
   catch (NumberFormatException nfe) {
     JOptionPane.showMessageDialog(null, "There is an invalid value.",
        "Input Error", JOptionPane.ERROR_MESSAGE);
      return;
   }

   
   // update initial
   node.setInitialActivation(newInitActVal);
   initialActivationField.setText("" + node.getInitialActivation());
   node.setInitialEmotionalValence(newInitValenceVal);
   initialValenceField.setText("" + node.getInitialEmotionalValence());
   //node.setInitialEmotionalIntensity(newInitIntensityVal);
   //initialIntensityField.setText("" + node.getInitialEmotionalIntensity());
   //node.setInitialEmotionalPotency(newInitPotencyVal);
   //initialPotencyField.setText("" + node.getInitialEmotionalPotency());
   
   // update current
   node.setActivation(newCurrActVal);
   currentActivationField.setText("" + node.getActivation());
   node.setEmotionalValence(newCurrValenceVal);
   currentValenceField.setText("" + node.getEmotionalValence());
   //node.setEmotionalIntensity(newCurrIntensityVal);
   //currentIntensityField.setText("" + node.getEmotionalIntensity());
   //node.setEmotionalPotency(newCurrPotencyVal);
   //currentPotencyField.setText("" + node.getEmotionalPotency());
   
   //update the model's overall emotional values
   model.recalculateEmotion();
   
   // close the window
   setVisible(false);
   dispose();
  }
 
 private JLabel createMenuLabel(String s, Dimension size) {
   JLabel label = new JLabel(s);
   label.setMinimumSize(size);
   label.setPreferredSize(size);
   label.setMaximumSize(size);
   return label;
 }
 
 private void setupMenu() {
   menu = new JPanel();
   menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
   menu.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
   menu.setBackground(Color.green);
   
   JPanel indexPanel = new JPanel();
   indexPanel.setLayout(new BoxLayout(indexPanel, BoxLayout.X_AXIS));
   indexPanel.add(createMenuLabel("", menuLabelSize));
   indexPanel.add(createMenuLabel("Initial", maxTextSize));
   indexPanel.add(createMenuLabel("Current", maxTextSize));
   menu.add(indexPanel);
   
   setupActivationRow();
   setupValenceRow();
   setupIntensityRow();
   setupPotencyRow();
  
   menu.setVisible(true);
 }
 
 private void setupNodeName() {
   panel.add(createMenuLabel("Name", menuLabelSize));
   nameField = new JTextField(node.getName());
   nameField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
   nameField.setMaximumSize(new Dimension(size_x - 10, 25));
   panel.add(nameField);
   panel.add(Box.createVerticalGlue());
 }
 
 private void setupTemplateTextField(JTextField field) {
   field.setMinimumSize(maxTextSize);
   field.setPreferredSize(maxTextSize);
   field.setMaximumSize(maxTextSize);
 }
 
 private void setupActivationRow() {
  activation = new JPanel();
  activation.setLayout(new BoxLayout(activation, BoxLayout.X_AXIS));
  activation.add(createMenuLabel("Activation Value", menuLabelSize));
   
  initialActivationField = new JTextField("" + node.getInitialActivation());
  setupTemplateTextField(initialActivationField);
  currentActivationField = new JTextField("" + node.getActivation());
  setupTemplateTextField(currentActivationField);
  
  activation.add(initialActivationField);
  activation.add(currentActivationField);
  menu.add(activation);
 }
 
 private void setupValenceRow() {
   valence = new JPanel();
   valence.setLayout(new BoxLayout(valence, BoxLayout.X_AXIS));
   valence.add(createMenuLabel("Emotional Valence", menuLabelSize));

  initialValenceField = new JTextField("" + node.getInitialEmotionalValence());
  setupTemplateTextField(initialValenceField);
  currentValenceField = new JTextField("" + node.getEmotionalValence());
  setupTemplateTextField(currentValenceField);
  
  valence.add(initialValenceField);
  valence.add(currentValenceField);
  menu.add(valence);
 }
 
 private void setupIntensityRow() {
   //intensity = new JPanel();
   //intensity.setLayout(new BoxLayout(intensity, BoxLayout.X_AXIS));
   //intensity.add(createMenuLabel("Emotional Intensity",menuLabelSize));

   //initialIntensityField = new JTextField("" + node.getInitialEmotionalIntensity());
   //setupTemplateTextField(initialIntensityField);
   //currentIntensityField = new JTextField("" + node.getEmotionalIntensity());
   //setupTemplateTextField(currentIntensityField);
  
  //intensity.add(initialIntensityField);
  //intensity.add(currentIntensityField);
  //menu.add(intensity);
 }
 
 private void setupPotencyRow() {
   //potency = new JPanel();
   //potency.setLayout(new BoxLayout(potency, BoxLayout.X_AXIS));
   //potency.add(createMenuLabel("Emotional Potency", menuLabelSize));

   //initialPotencyField = new JTextField("" + node.getInitialEmotionalPotency());
   //setupTemplateTextField(initialPotencyField);
   //currentPotencyField = new JTextField("" + node.getEmotionalPotency());
   //setupTemplateTextField(currentPotencyField);
   
   //potency.add(initialPotencyField);
   //potency.add(currentPotencyField);
   //menu.add(potency);
 }
}