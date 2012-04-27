package LIMBS;

//necessary Java classes to create component
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.math.*;

public class HOTCOLinkWidget extends JFrame implements ActionListener {
  private final HOTCOLink link;
  private final HOTCOAgent model;
  private JPanel panel;
  private JTextField weightField;
  private JRadioButton radioInhibit, radioExcitatory;
  
  private Dimension maxTextSize = new Dimension(75,20);
  private Dimension menuLabelSize = new Dimension(125, 20);
  
  public HOTCOLinkWidget(final HOTCOLink link, final HOTCOAgent model) {
    super("Edit Link");
    this.link = link;
    this.model = model;
    
    panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    
    setupMenu();
    panel.add(Box.createVerticalGlue());
    
    // add buttons
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,5));

    JButton update = new JButton("Update!");
    update.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    update.addActionListener(this);

    JButton delete = new JButton("Delete");
    delete.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    delete.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        model.getSimulation().registerEndChange(null);
        model.removeLink(link);

        // close the window
        setVisible(false);
        dispose();
      }
    });

    buttonPanel.add(update);
    buttonPanel.add(Box.createHorizontalGlue());
    buttonPanel.add(delete);
    panel.add(buttonPanel);
    add(panel);
    
    setLocation(150, 150);
    pack();
    setVisible(true);
  }
  
  public void actionPerformed(ActionEvent e) {
    
    model.getSimulation().registerEndChange(null);
    double newWeightVal;
    boolean isInhibit;
    try {
      newWeightVal = Double.valueOf(weightField.getText()).doubleValue();
      if (newWeightVal < 0 || newWeightVal > 1) {
        JOptionPane.showMessageDialog(null, "Value must be between 0 and 1",
           "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      isInhibit = radioInhibit.isSelected();
    }
    catch (NumberFormatException nfe) {
      JOptionPane.showMessageDialog(null, "There is an invalid value.",
         "Input Error", JOptionPane.ERROR_MESSAGE);
       return;
    }
    
    newWeightVal = isInhibit ? (-1d * newWeightVal) : newWeightVal;
    link.setWeight(newWeightVal);
    
    // close the window
    setVisible(false);
    dispose();
  }
  
  private void setupMenu() {
    JLabel weightLabel = new JLabel("Weight:");
    weightLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    panel.add(weightLabel);
    
    weightField = new JTextField("" + Math.abs(link.getWeight()));
    weightField.setMaximumSize(maxTextSize);
    panel.add(weightField);
    
    panel.add(Box.createVerticalGlue());
    
    JLabel inhibitLabel = new JLabel("Type:");
    inhibitLabel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
    inhibitLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    panel.add(inhibitLabel);
    
    boolean isInhibited = link.getWeight() < 0;
    ButtonGroup group = new ButtonGroup();
    radioInhibit = new JRadioButton("Inhibitory", isInhibited);
    radioExcitatory = new JRadioButton("Excitatory", !isInhibited);
    
    group.add(radioInhibit);
    group.add(radioExcitatory);
    panel.add(radioInhibit);
    panel.add(radioExcitatory);
  }
  
  
}