package LIMBS;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.*;

@SuppressWarnings("serial")
public class DetailsPane extends JPanel implements ActionListener {

 Interface i;
 Simulation simulation;
 Component currentComponent = null;

 private JPanel top, bottom;

 private JLabel title, contactsLabel, groupsLabel, agentsLabel,
   actionsLabel, goalsLabel, evidenceLabel, id, emoValLabel, emoIntLabel, emoPotLabel,
   emoNameLabel;
 // All
 private JTextField name;
 // Agents
 private JLabel emotionalValence, emotionalIntensity, emotionalPotency, emotionName;
 private JTextArea contacts = null;
 // Groups
 private JTextArea evidence = null;
 private JTextArea actions = null;
 private JTextArea goals = null;
 // private JTextArea numMembers = null;
 private JTextArea members = null;
 // Agents & Propositions
 private JTextArea groups = null;

 private JScrollPane groupsScrollPane;
 private JScrollPane contactsScrollPane;
 private JScrollPane membersScrollPane;
 private JScrollPane evidenceScrollPane;
 private JScrollPane goalsScrollPane;
 private JScrollPane actionsScrollPane;

 private JButton apply;

 private GridBagConstraints c;

 public DetailsPane(Interface i, Simulation s) {
  this.simulation = s;
  this.i = i;
  this.setLayout(new BorderLayout());

  // Setup the top panel
  top = new JPanel();
  top.setLayout(new GridBagLayout());

  // Initialize the GridBagContstraint object we're
  // using to get a pretty UI
  c = new GridBagConstraints();
  // Add 10px of space on the right and left sides of each
  // component
  c.insets = new Insets(0, 0, 10, 10);

  // Add the Title
  title = new JLabel("Properties");
  title.setFont(new Font("Times New Roman", Font.BOLD, 22));
  c.fill = GridBagConstraints.HORIZONTAL;
  c.ipady = 20;
  c.weightx = 0.0;
  c.gridwidth = 3;
  c.gridx = 0;
  c.gridy = 0;
  top.add(title, c);

  // Add the ID field & the label that will hold the
  // ID of the selected object
  JLabel idLabel = new JLabel("ID:");
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.0;
  c.ipady = 5;
  c.gridx = 0;
  c.gridy = 1;
  c.gridwidth = 1;
  top.add(idLabel, c);

  id = new JLabel("");
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.5;
  c.ipadx = 5;
  c.gridx = 1;
  c.gridy = 1;
  top.add(id, c);

  // Add the name label, the text field that will
  // hold the name, and the button to change the
  // name
  JLabel nameLabel = new JLabel("Name:");
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.0;
  c.gridx = 0;
  c.gridy = 2;
  top.add(nameLabel, c);

  name = new JTextField(15);
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.5;
  c.gridx = 1;
  c.gridy = 2;
  top.add(name, c);

  apply = new JButton("Apply");
  apply.setActionCommand("Apply");
  apply.addActionListener(this);
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.0;
  c.gridx = 1;
  c.gridy = 3;
  c.ipadx = 10;
  top.add(apply, c);

  // reset some of the GridBag constraints
  c.ipadx = 0;
  c.ipady = 0;

  // Add the top panel
  this.add(top, BorderLayout.NORTH);

  // Setup the bottom panel
  bottom = new JPanel();
  bottom.setLayout(new GridBagLayout());

  // Add the bottom panel (currently empty)
  this.add(bottom, BorderLayout.CENTER);

  // Prepare all the components that will be
  // added/removed dynamically depending on what
  // type of component is selected
  contactsLabel = new JLabel("Contacts:");
  groupsLabel = new JLabel("Groups:");
  agentsLabel = new JLabel("Agents:");
  actionsLabel = new JLabel("Actions:");
  goalsLabel = new JLabel("Goals:");
  evidenceLabel = new JLabel("Evidence:");
  emoValLabel = new JLabel("Emotional Valence");
  emoIntLabel = new JLabel("Emotional Intensity:");
  emoPotLabel = new JLabel("Emotional Potency:");
  emoNameLabel = new JLabel("Emotion Name:");
  
  contacts = new JTextArea("");
  evidence = new JTextArea("");
  actions = new JTextArea("");
  goals = new JTextArea("");
  members = new JTextArea("");
  groups = new JTextArea("");
  emotionalValence = new JLabel();
  emotionalIntensity = new JLabel();
  emotionalPotency = new JLabel();
  emotionName = new JLabel();

  groupsScrollPane = new JScrollPane(groups);
  groupsScrollPane.setPreferredSize(new Dimension(50,100));
  contactsScrollPane = new JScrollPane(contacts);
  contactsScrollPane.setPreferredSize(new Dimension(50,100));
  membersScrollPane = new JScrollPane(members);
  evidenceScrollPane = new JScrollPane(evidence);
  goalsScrollPane = new JScrollPane(goals);
  actionsScrollPane = new JScrollPane(actions);

 }

 private ActionListener componentUpdateListener = new ActionListener(){
   public void actionPerformed(ActionEvent e){
     updateComponent();
   }
 };
 
 public void setComponent(Component c) {
   
   if( this.currentComponent != null )
     this.currentComponent.removeListener( componentUpdateListener );
   if( c != null )
     c.addListener( componentUpdateListener );
   
  // Set the current component and update
  // the details that are displayed to match the
  // new current component
  this.currentComponent = c;
  updateComponent();
 
 }
 
 private void updateComponent(){
   if (this.currentComponent != null) {
     String className = this.currentComponent.getMainComponentType();
     if (className.equals("Group")) {
       updateGroup(this.currentComponent.getName(), this.currentComponent.getId(), this.currentComponent.getAgentSet(),
                   this.currentComponent.getPropositionSet());
     } else if (className.equals("Proposition")) {
       updateProp(this.currentComponent.getName(), this.currentComponent.getId(), this.currentComponent.getGroupSet());
     } else {
       Agent a = (Agent) this.currentComponent;
       updateAgent(a.getName(), a.getId(), a.getAgentSet(),
                   a.getGroupSet(), a.queryEmotionValence(), a.queryEmotionIntensity(), a.queryEmotionPotency(), a.isUtility());
     }
   } else {
     // If the current component is null,
     // clear the details pane
     reset();
   }
 }

 private void reset() {

  // remove all components from the bottom panel
  bottom.removeAll();
  bottom.invalidate();

  // reset the title, id and name fields
  this.title.setText("Properties");
  this.id.setText("");
  this.name.setText("");

  // repaint the bottom panel
  bottom.repaint();
 }

 private void updateAgent(String n, long idNum, Set<Agent> contactList,
   Set<Group> groupList, double emoVal, double emoInt, double emoPot, boolean isUtility ) {

  // remove all the component from the bottom panel
  bottom.removeAll();
  bottom.invalidate();

  // update the title, id and name to match the
  // current component
  this.title.setText("Properties - " + n);
  this.id.setText(Long.toString(idNum));
  this.name.setText(n);

  // Generate a textual representation of the
  // current component's contacts, and update
  // the contacts TextArea with that string
  String contactString = "";
  for (Agent a : contactList) {
   contactString += a.getName() + "; ";
  }
  contacts.setText(contactString);

  // Add the contacts label & scrollpane
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.0;
  c.weighty = 0.0;
  c.gridheight = 2;
  c.gridx = 0;
  c.gridy = 0;
  bottom.add(contactsLabel, c);

  c.fill = GridBagConstraints.BOTH;
  c.weightx = 0.5;
  c.weighty = 0.5;
  c.gridheight = 2;
  c.gridx = 1;
  c.gridy = 0;
  bottom.add(contactsScrollPane, c);

  // Generate a textual representation of the
  // current component's groups, and update
  // the groups TextArea with that string
  String groupString = "";
  for (Group g : groupList) {
   groupString += g.getName() + "; ";
  }
  groups.setText(groupString);

  // Add the groups label and scrollpane
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.0;
  c.weighty = 0.0;
  c.gridheight = 2;
  c.gridx = 0;
  c.gridy = 3;
  bottom.add(groupsLabel, c);

  c.fill = GridBagConstraints.BOTH;
  c.weighty = 0.5;
  c.weightx = 0.5;
  c.gridheight = 2;
  c.gridx = 1;
  c.gridy = 3;
  bottom.add(groupsScrollPane, c);

  if( !isUtility ) {
      //emotional valence
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 0.0;
      c.weighty = 0.0;
      c.gridheight = 1;
      c.gridx = 0;
      c.gridy = 5;
      bottom.add(emoValLabel, c);
    
      emotionalValence.setText( Double.toString(emoVal) );
      c.fill = GridBagConstraints.BOTH;
      c.weighty = 0.5;
      c.weightx = 0.5;
      c.gridheight = 1;
      c.gridx = 1;
      c.gridy = 5;
      bottom.add(emotionalValence, c);
      
      //emotional intensity
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 0.0;
      c.weighty = 0.0;
      c.gridheight = 1;
      c.gridx = 0;
      c.gridy = 6;
      //bottom.add(emoIntLabel, c);
    
      emotionalIntensity.setText( Double.toString(emoInt) );
      c.fill = GridBagConstraints.BOTH;
      c.weighty = 0.5;
      c.weightx = 0.5;
      c.gridheight = 1;
      c.gridx = 1;
      c.gridy = 6;
      //bottom.add(emotionalIntensity, c);
    
      //emotional potency
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 0.0;
      c.weighty = 0.0;
      c.gridheight = 1;
      c.gridx = 0;
      c.gridy = 7;
      //bottom.add(emoPotLabel, c);
    
      emotionalPotency.setText( Double.toString(emoPot));
      c.fill = GridBagConstraints.BOTH;
      c.weighty = 0.5;
      c.weightx = 0.5;
      c.gridheight = 1;
      c.gridx = 1;
      c.gridy = 7;
      //bottom.add(emotionalPotency, c);
    
      //emotion name
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 0.0;
      c.weighty = 0.0;
      c.gridheight = 1;
      c.gridx = 0;
      c.gridy = 8;
      bottom.add(emoNameLabel, c);
    
      emotionName.setText( this.i.getModel().getEmotionName(emoVal, emoInt, emoPot) );
      c.fill = GridBagConstraints.BOTH;
      c.weighty = 0.5;
      c.weightx = 0.5;
      c.gridheight = 1;
      c.gridx = 1;
      c.gridy = 8;
      bottom.add(emotionName, c);
  }else{
    
    // TODO: This is a hack put in to make sure that the gridbag layout always is 8x2
    //       else, there is a bug when selecting a utility then a group.
      emotionName.setText( "" );
      c.fill = GridBagConstraints.BOTH;
      c.weighty = 0.5;
      c.weightx = 0.5;
      c.gridheight = 1;
      c.gridx = 1;
      c.gridy = 8;
      bottom.add(emotionName, c);
  }
  
  // repaint the bottom panel
  bottom.repaint();

 }

 private void updateProp(String n, long idNum, Set<Group> groupList) {

  // remove all component from the bottom panel
  bottom.removeAll();
  bottom.invalidate();

  // update the title, id and name fields to
  // match the current component
  this.title.setText("Properties - " + n);
  this.id.setText(Long.toString(idNum));
  this.name.setText(n);

  // Generate a textual representation of the
  // current component's groups, and update
  // the groups TextArea with that string
  String groupString = "";
  for (Group g : groupList) {
   groupString += g.getName() + "; ";
  }
  groups.setText(groupString);

  // Add the groups label and scroll pane
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.0;
  c.weighty = 0.0;
  c.gridx = 0;
  c.gridy = 0;
  bottom.add(groupsLabel, c);

  c.fill = GridBagConstraints.BOTH;
  c.weightx = 0.5;
  c.weighty = 0.5;
  c.gridx = 1;
  c.gridy = 0;
  bottom.add(groupsScrollPane, c);

  // repaint the bottom panel
  bottom.repaint();

 }

 private void updateGroup(String n, long idNum, Set<Agent> agentList,
   Set<Proposition> propList) {

  // remove all components from the bottom panel
  bottom.removeAll();
  bottom.invalidate();

  // update the title, id and name fields to
  // match the current component
  this.title.setText("Properties - " + n);
  this.id.setText(Long.toString(idNum));
  this.name.setText(n);

  // Generate a textual representation of the
  // current component's agent members, and update
  // the members TextArea with that string
  String memberString = "";
  for (Agent a : agentList) {
   memberString += a.getName() + "; ";
  }
  members.setText(memberString);

  // Add the agent label and scroll pane
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.0;
  c.weighty = 0.0;
  c.gridx = 0;
  c.gridy = 0;
  bottom.add(agentsLabel, c);

  c.fill = GridBagConstraints.BOTH;
  c.weightx = 0.5;
  c.weighty = 0.5;
  c.gridx = 1;
  c.gridy = 0;
  bottom.add(membersScrollPane, c);

  // Generate a textual representation of the
  // current component's actions, goals, and
  // evidence and update the corresponding
  // TextAreas with those strings
  String actionString = "";
  String goalString = "";
  String evidenceString = "";
  for (Proposition p : propList) {
   long type = p.getType();
   String name = p.getName();
   if (type == 0) {
    // evidence
    evidenceString += name + "; ";
   } else if (type == 1) {
    // action
    actionString += name + "; ";
   } else if (type == 2) {
    // goal
    goalString += name + "; ";
   }
  }
  evidence.setText(evidenceString);
  actions.setText(actionString);
  goals.setText(goalString);

  // Add the evidence label and scrollpane
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.0;
  c.weighty = 0.0;
  c.gridx = 0;
  c.gridy = 1;
  bottom.add(evidenceLabel, c);

  c.fill = GridBagConstraints.BOTH;
  c.weightx = 0.5;
  c.weighty = 0.5;
  c.gridx = 1;
  c.gridy = 1;
  bottom.add(evidenceScrollPane, c);

  // Add the action label and scroll pane
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.0;
  c.weighty = 0.0;
  c.gridx = 0;
  c.gridy = 2;
  bottom.add(actionsLabel, c);

  c.fill = GridBagConstraints.BOTH;
  c.weightx = 0.5;
  c.weighty = 0.5;
  c.gridx = 1;
  c.gridy = 2;
  bottom.add(actionsScrollPane, c);

  // Add the goal label and scroll pane
  c.fill = GridBagConstraints.HORIZONTAL;
  c.weightx = 0.0;
  c.weighty = 0.0;
  c.gridx = 0;
  c.gridy = 3;
  bottom.add(goalsLabel, c);

  c.fill = GridBagConstraints.BOTH;
  c.weightx = 0.5;
  c.weighty = 0.5;
  c.gridx = 1;
  c.gridy = 3;
  bottom.add(goalsScrollPane, c);

  // Repaint the bottom panel
  bottom.repaint();

 }

 public void actionPerformed(ActionEvent e) {
  String command = e.getActionCommand();
  if (command.equals("Apply") && this.currentComponent != null) {

   // register the undo action with the simulation
   simulation.registerEndChange(new ChangeNotification() {
    public boolean notifyChange() {
     setComponent(currentComponent);
     repaint();
     return true;
    }
   });
   //check if this name is already used, if so, issue a warning
   String newName = name.getText();
   int duplicates = this.i.getCanvas().nameExists(newName);
   if( duplicates > 0 ) {
       //issue warning
       JOptionPane.showMessageDialog(this.i.getFrame(),
               "The name " + newName + " has already been used in this simulation." +
               "\nThis component will be renamed " + newName + duplicates + " instead." ,
               "Duplicate Name Warning",
               JOptionPane.WARNING_MESSAGE);
       newName = newName + duplicates;
       name.setText(newName);
   }
   
   // perform the name change
   String oldName = this.currentComponent.getName();
   this.currentComponent.setName(newName);
   this.title.setText("Properties - " + newName);
   String className = this.currentComponent.getClass().getName();
   if (!className.equals("LIMBS.Group")
     && !className.equals("LIMBS.Proposition")) {
    this.i.changeAgentInterfaceTabName(oldName, this.currentComponent.getName());
   }
  }
 }

}
