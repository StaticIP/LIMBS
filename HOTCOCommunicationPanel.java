package LIMBS;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.Border;

public class HOTCOCommunicationPanel extends JPanel {
  
  private final RuleListModel communicationList;
  private final NodeListModel nodeList;
  private HOTCOAgent agentModel;
  private HOTCONetworkWidget network;
  private final JLabel targetLabel;
  
  public HOTCOCommunicationPanel(HOTCOAgent model, HOTCONetworkWidget network) {
    this.agentModel = model;
    this.network = network;
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
    
    targetLabel = new JLabel("\t");
    communicationList = new RuleListModel();
    nodeList = new NodeListModel();
    
    setupCommunicationPanel();
  }
  
  private void setupCommunicationPanel() {
    add(createPanelLabel("Communication Rules", 5));
    
    // add rule button
    JButton addRuleButton = new JButton("New Rule");
    addRuleButton.setMaximumSize(new Dimension(100,20));
    addRuleButton.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    addRuleButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        agentModel.getSimulation().registerEndChange( new ChangeNotification(){
          public boolean notifyChange(){
            repaint();
            return true;
          }
        });
        HOTCOCommunicationRule rule = new HOTCOCommunicationRule(agentModel, null);
        communicationList.add(rule);
      }
    });
    add(addRuleButton);

    // create communication list
    JList communicationDisplayList = new JList(communicationList);
    communicationDisplayList.setVisibleRowCount(5);
    communicationDisplayList.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    communicationDisplayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    prepareListCellRenderer(communicationDisplayList);
    addRuleMouseListener(communicationDisplayList);
    communicationDisplayList.addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
          Object selected = ((JList)e.getSource()).getSelectedValue();
          if (selected != null) {
            nodeList.updateOrderedList((HOTCOCommunicationRule)selected);
            updateTargetText((HOTCOCommunicationRule)selected);
          } else {
            nodeList.updateOrderedList(null);
            targetLabel.setText("\t");
          }
        }
      }
    });

    JScrollPane communicationScrollList = new JScrollPane(communicationDisplayList);
    Dimension scrollPaneSize = new Dimension(200, 12+(15*5));

    communicationScrollList.setMinimumSize(scrollPaneSize);
    communicationScrollList.setPreferredSize(scrollPaneSize);
    communicationScrollList.setMaximumSize(scrollPaneSize);

    communicationScrollList.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    add(communicationScrollList);
    add(createRemoveButton("Remove Rule", communicationList, communicationScrollList));
    
    // display current target
    targetLabel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
    targetLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    add(targetLabel);
    
    // node add button
    add(createPanelLabel("Nodes", 10));    
    JButton addNodeToRule = new JButton("Add");
    addNodeToRule.setMaximumSize(new Dimension(80,20));
    addNodeToRule.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    addNodeToRule.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        
        agentModel.getSimulation().registerEndChange( new ChangeNotification(){
          public boolean notifyChange(){
            repaint();
            return true;
          }
        });
        
        for (HOTCONode node : network.getSelectedNodes()) {
          nodeList.add(node);
        }
      }
    });
    add(addNodeToRule);
    
    // create node list
    JList nodeDisplayList = new JList(nodeList);
    nodeDisplayList.setVisibleRowCount(5);
    nodeDisplayList.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    prepareListCellRenderer(nodeDisplayList);
    JScrollPane nodeScrollList = new JScrollPane(nodeDisplayList);

    nodeScrollList.setMinimumSize(scrollPaneSize);
    nodeScrollList.setPreferredSize(scrollPaneSize);
    nodeScrollList.setMaximumSize(scrollPaneSize);

    nodeScrollList.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    add(nodeScrollList);
    add(createRemoveButton("Remove", nodeList, nodeScrollList));
    
    // add receptiveness label
    add(createPanelLabel("Receptiveness", 10));
    
    final JTextField receptivenessField = new JTextField("" + agentModel.getReceptiveness());
    receptivenessField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    receptivenessField.setMaximumSize(new Dimension(200,25));
    add(receptivenessField);
    
    JButton receptivenessButton = new JButton("Update");
    receptivenessButton.setMaximumSize(new Dimension(115,20));
    receptivenessButton.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    receptivenessButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        double newVal;
        try {
          newVal = Double.valueOf(receptivenessField.getText()).doubleValue();
          if( newVal < 0.0 || newVal > 1.0 ) {
            JOptionPane.showMessageDialog(null, "Value must be between 0.0 and 1.0.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
          }
          agentModel.setReceptiveness(newVal);
        }
        catch (Exception nfe) {
          JOptionPane.showMessageDialog(null, "There is an invalid value.",
              "Input Error", JOptionPane.ERROR_MESSAGE);
        }
        
        receptivenessField.setText("" + agentModel.getReceptiveness());
        JOptionPane.showMessageDialog(null, "Receptiveness Updated!",
          "Success", JOptionPane.INFORMATION_MESSAGE);
      }
    });
    add(receptivenessButton);
  }
  
  private void updateTargetText(HOTCOCommunicationRule rule) {
    Agent targetAgent = rule.getTargetAgent();
    String targetName = targetAgent == null ? "None" : targetAgent.getName();
    targetLabel.setText("Target: " + targetName);
  }
  
  private JLabel createPanelLabel(String text, int borderPadding) {
    JLabel panelLabel = new JLabel(text);
    panelLabel.setBorder(BorderFactory.createEmptyBorder(borderPadding,2,borderPadding,0));
    panelLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    return panelLabel;
  }
  
  private JButton createRemoveButton(String text, final ListModel model, final JScrollPane scroll) {
    JButton remove = new JButton(text);
    remove.setMaximumSize(new Dimension(115,20));
    remove.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    remove.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        
        agentModel.getSimulation().registerEndChange( new ChangeNotification(){
          public boolean notifyChange(){
            repaint();
            return true;
          }
        });
        
        JList list = (JList)scroll.getViewport().getView();
        if (list.getSelectedIndex() < 0)
          return;
        
        if (model instanceof RuleListModel) {
          for (Object o : list.getSelectedValues()) {
            HOTCOCommunicationRule rule = (HOTCOCommunicationRule)o;
            ((RuleListModel)model).remove(rule);
          }
        }
        else if (model instanceof NodeListModel) {
          for (Object o : list.getSelectedValues()) {
            HOTCONode node = (HOTCONode)o;
            ((NodeListModel)model).remove(node);
          }
        }
        list.clearSelection();
      }
    });
    return remove;
  }
  
  private void prepareListCellRenderer(JList list) {
    final ListCellRenderer defaultRenderer = list.getCellRenderer();
    ListCellRenderer agentCellRenderer = new ListCellRenderer(){
      public java.awt.Component getListCellRendererComponent(
        JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
      {
        ListModel model = list.getModel();
        if (model instanceof RuleListModel) {
          return defaultRenderer.getListCellRendererComponent(
            list, ((HOTCOCommunicationRule)value).getName(), index, isSelected, cellHasFocus);
        }
        else if (model instanceof NodeListModel) {
          return defaultRenderer.getListCellRendererComponent(
            list, ((HOTCONode)value).getName(), index, isSelected, cellHasFocus);
        }
        else if (model instanceof CommunicationListModel) {
          return defaultRenderer.getListCellRendererComponent(
            list, ((Agent)value).getName(), index, isSelected, cellHasFocus);
        }
        else {
          return defaultRenderer.getListCellRendererComponent(
            list, value, index, isSelected, cellHasFocus);
        }
      }
    };
    list.setCellRenderer(agentCellRenderer);
  }
  
  private void addRuleMouseListener(final JList list) {
    MouseListener ruleMouseListener = new MouseAdapter(){
      public void mouseClicked(MouseEvent e) {
        
        agentModel.getSimulation().registerEndChange( new ChangeNotification(){
          public boolean notifyChange(){
            repaint();
            return true;
          }
        });
        
        if (e.getClickCount() == 2) {
          int index = list.locationToIndex(e.getPoint());
          if (index < 0)
            return;
          
          NodeRuleFrame ruleFrame = new NodeRuleFrame(
            (HOTCOCommunicationRule)communicationList.getElementAt(index));
        }
      }
    };
    list.addMouseListener(ruleMouseListener);
  }
  
  private class RuleListModel extends AbstractListModel {
    private Vector<HOTCOCommunicationRule> orderedList;
    
    public RuleListModel() {
      updateOrderedList();
    }
    
    // call update for entire list
    public void updateOrderedList() {
      orderedList = new Vector<HOTCOCommunicationRule>(agentModel.getCommunicationRules());
      fireContentsChanged(this, 0, getSize());
    }
    
    public boolean add(HOTCOCommunicationRule r) {
      if (orderedList.contains(r)) return false;
      agentModel.addCommunicationRule(r);
      updateOrderedList();
      return true;
    }
    public boolean remove(HOTCOCommunicationRule r) {
      if (!orderedList.contains(r)) return false;
      agentModel.removeCommunicationRule(r);
      updateOrderedList();
      return true;
    }
    
    public int getSize() {
      return orderedList.size();
    }
    
    public Object getElementAt(int index) {
      return (HOTCOCommunicationRule)orderedList.elementAt(index);
    }
  }
  
  private class NodeListModel extends AbstractListModel {
    private Vector<HOTCONode> orderedList;
    private HOTCOCommunicationRule rule;
    private Vector<HOTCONode> emptyList;
    
    public NodeListModel() {
      emptyList = new Vector<HOTCONode>();
      updateOrderedList(null);
    }
    
    // call update for entire list
    public void updateOrderedList(HOTCOCommunicationRule rule) {
      this.rule = rule;
      orderedList = (rule != null) ? rule.getCriteria() : emptyList;      
      fireContentsChanged(this, 0, getSize());
    }
    
    public void updateOrderedList() {
      updateOrderedList(this.rule);
    }
    
    public boolean add (HOTCONode n) {
      if (orderedList == emptyList) return false;
      if (orderedList.contains(n)) return false;
      rule.addCriterion(n);
      updateOrderedList();
      return true;
    }
    
    public boolean remove (HOTCONode n) {
      if (orderedList == emptyList) return false;
      if (!orderedList.contains(n)) return false;
      rule.removeCriterion(n);
      updateOrderedList();
      return true;
    }
    
    public int getSize() {
      return orderedList.size();
    }
    
    public Object getElementAt(int index) {
      return (HOTCONode)orderedList.elementAt(index);
    }
  }
  
  private class CommunicationListModel extends AbstractListModel {
    private Vector<Agent> orderedList;
    
    public CommunicationListModel() {
      orderedList = new Vector<Agent>(agentModel.getAgentSet());
    }
    
    public int getSize() {
      return orderedList.size();
    }
    
    public Object getElementAt(int index) {
      return orderedList.elementAt(index);
    }
  }
  
  private class NodeRuleFrame extends JFrame implements ActionListener {
    private Dimension menuLabelSize = new Dimension(200, 25);
    private HOTCOCommunicationRule rule;
    private JTextField nameField, thresholdField;
    private JPanel panel;
    private JList list;
    
    public NodeRuleFrame(HOTCOCommunicationRule rule) {
      super(rule.getName());
      
      this.rule = rule;
      panel = new JPanel();
      panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
      
      setupRuleName();
      setupThreshold();
      setupTargetPicker();
      
      // update fields
      JPanel updatePanel = new JPanel();
      updatePanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
      updatePanel.setLayout(new BoxLayout(updatePanel, BoxLayout.X_AXIS));
      updatePanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
      
      JButton update = new JButton("Update!");
      update.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
      update.addActionListener(this);
      updatePanel.add(Box.createHorizontalGlue());
      updatePanel.add(update);
      panel.add(updatePanel);
      add(panel);
      
      setLocation(150,150);
      setSize(250,250);
      pack();
      update.requestFocusInWindow();
      setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
      
      agentModel.getSimulation().registerEndChange( new ChangeNotification(){
        public boolean notifyChange(){
          repaint();
          return true;
        }
      });
        
      // update name
      String newName = nameField.getText();
      rule.setName(newName);
      communicationList.updateOrderedList();
      
      // update threshold
      double newThreshold;
      try {
        newThreshold = Double.valueOf(thresholdField.getText()).doubleValue();
        if( newThreshold < -1.0 || newThreshold > 1.0 ) {
          JOptionPane.showMessageDialog(null, "Threshold must be between -1 and 1.",
             "Input Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        rule.setThreshold(newThreshold);
      }
      catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog(null, "There is an invalid value for Threshold.",
           "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      
      // update target
      Agent oldAgent = rule.getTargetAgent();
      Agent newAgent = null;
      Object selected = list.getSelectedValue();
      if (selected != null) {
        newAgent = (Agent)selected;
      }
      else {
        newAgent = oldAgent;
      }
      rule.setTargetAgent(newAgent);
      updateTargetText(rule);
      
      // close the window
      setVisible(false);
      dispose();
    }
    
    private void setupRuleName() {
      panel.add(createMenuLabel("Name", menuLabelSize, BorderFactory.createEmptyBorder(5,0,5,5)));
      nameField = new JTextField(rule.getName());
      setupTemplateTextField(nameField);
      panel.add(nameField);
    }
    
    private void setupTemplateTextField(JTextField field) {
      field.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
      field.setMinimumSize(menuLabelSize);
      field.setPreferredSize(menuLabelSize);
      field.setMaximumSize(menuLabelSize);
    }
    
    private JLabel createMenuLabel(String s, Dimension size, Border border) {
     JLabel label = new JLabel(s);
     label.setBorder(border);
     label.setMinimumSize(size);
     label.setPreferredSize(size);
     label.setMaximumSize(size);
     label.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
     return label;
   }
   
   private void setupThreshold() {
     panel.add(createMenuLabel("Threshold", menuLabelSize, BorderFactory.createEmptyBorder(5,0,5,5)));
     thresholdField = new JTextField("" + rule.getThreshold());
     setupTemplateTextField(thresholdField);
     panel.add(thresholdField);
   }
   
   private void setupTargetPicker() {
     panel.add(createMenuLabel("Target", menuLabelSize, BorderFactory.createEmptyBorder(15,0,15,5)));
     
     list = new JList(new CommunicationListModel());
     list.setVisibleRowCount(5);
     list.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
     list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
     list.setSelectedValue(rule.getTargetAgent(), true);
     prepareListCellRenderer(list);
     
     JScrollPane scroll = new JScrollPane(list);
     Dimension scrollPaneSize = new Dimension(200, 12+(15*5));
     
     scroll.setMinimumSize(scrollPaneSize);
     scroll.setPreferredSize(scrollPaneSize);
     scroll.setMaximumSize(scrollPaneSize);
     scroll.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
     panel.add(scroll);
   }
  }
}