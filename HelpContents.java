package LIMBS;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class HelpContents extends JFrame implements ListSelectionListener {

 private JSplitPane splitPane;
 private JList list;
 private JScrollPane indexScrollPane, contentScrollPane;
 private String[] indexNames = {
   "Intro",
   "What is the LIMBS Framework?",
   "Interface Overview",
   "Simulation Basics",
   "    Propositions, Agents, and Groups",
   "    Grouping Agents and Propositions",
   "    Cloning (Copying) Components",
   "    Messsages and Communication",
   "    Running a Simulation",
   "    Saving and Opening Simulations",
   "HOTCO Agents",
   "    The Basic HOTCO Agent Interface",
   "    Communication Rules",
   "    Emotional Transfer Mechanisms",
   "Utilities",
   "    Logging Utility",
   "    Event Utility",
   "    Polling Utility",
   "Useful Model Structures",
   "    The Free-For-All Model",
   "    The Diplomacy Model",
   "    The Corporation Model",
   "Extending The LIMBS Framework",
   "    New Agents and Utilities",
   "    The Agent Interface",
   "Preferences",
   "Glossary",
   "References",
   "License" };

 private JEditorPane helpIntro, limbsIntro, interfaceOverview;
 private JEditorPane startingNewModel, propsAgentsGroups,
   groupingAgentsAndProps, copyPaste, messages,
   runningSims, saveAndOpenSims;
 private JEditorPane hotcoAgents, basicHOTCO, communicationRules, emotionalTransfer;
 private JEditorPane utilities, loggingUtil, eventUtil, pollingUtil;
 private JEditorPane usefulModelStructures, freeForAllModel, diplomacyModel,
   corporationModel;
 private JEditorPane extending, newAgents, agentInterface;
 private JEditorPane preferences, glossary, references, license;

 public HelpContents() {
  super();
  this.setPreferredSize(new Dimension(1100, 700));
  this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
  this.setTitle("Help Contents");
  
  list = new JList(indexNames);
  list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  list.setSelectedIndex(0);
  list.addListSelectionListener(this);

  indexScrollPane = new JScrollPane(list);

  preparePanels();

  contentScrollPane = new JScrollPane(helpIntro);

  // Create a split pane with the two scroll panes in it.
  splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
    indexScrollPane, contentScrollPane);
  splitPane.setOneTouchExpandable(true);
  splitPane.setDividerLocation(250);

  indexScrollPane.setPreferredSize(new Dimension(250, 600));
  contentScrollPane.setPreferredSize(new Dimension(650, 600));

  this.add(splitPane);

  this.pack();
  this.setVisible(true);

 }
 
 private void preparePanels() {
  helpIntro = new JEditorPane();
  helpIntro.setEditable(false);
  addHTML( helpIntro, "HTML/HelpIntro.html");
  
  limbsIntro = new JEditorPane();
  limbsIntro.setEditable(false);
  addHTML( limbsIntro, "HTML/LIMBSIntro.html");
  
  interfaceOverview = new JEditorPane();
  interfaceOverview.setEditable(false);
  addHTML(interfaceOverview, "HTML/InterfaceOverview.html");
  
  startingNewModel = new JEditorPane();
  startingNewModel.setEditable(false);
  addHTML( startingNewModel, "HTML/StartingANewModel.html");
  
  propsAgentsGroups = new JEditorPane();
  propsAgentsGroups.setEditable(false);
  addHTML( propsAgentsGroups , "HTML/PropositionsAgentsAndGroups.html" );
  
  groupingAgentsAndProps = new JEditorPane();
  groupingAgentsAndProps.setEditable(false);
  addHTML( groupingAgentsAndProps, "HTML/GroupingAgentsAndProps.html");
  
  copyPaste = new JEditorPane();
  copyPaste.setEditable(false);
  addHTML( copyPaste, "HTML/CloneComponents.html");
  
  messages = new JEditorPane();
  messages.setEditable(false);
  addHTML( messages, "HTML/MessagesAndCommunication.html");
  
  runningSims = new JEditorPane();
  runningSims.setEditable(false);
  addHTML( runningSims, "HTML/RunningSimulations.html");
  
  saveAndOpenSims = new JEditorPane();
  saveAndOpenSims.setEditable(false);
  addHTML( saveAndOpenSims, "HTML/SaveAndOpenSimulations.html");
  
  hotcoAgents = new JEditorPane();
  hotcoAgents.setEditable(false);
  addHTML( hotcoAgents, "HTML/HOTCOAgents.html");
  
  basicHOTCO = new JEditorPane(); 
  basicHOTCO.setEditable(false);
  addHTML( basicHOTCO, "HTML/HOTCOBasicInterface.html");
  
  communicationRules = new JEditorPane();
  communicationRules.setEditable(false);
  addHTML( communicationRules, "HTML/HOTCOCommunicationRules.html" );
  
  emotionalTransfer = new JEditorPane();
  emotionalTransfer.setEditable(false);
  addHTML( emotionalTransfer, "HTML/HOTCOEmotionalTransfer.html");
  
  utilities = new JEditorPane();
  utilities.setEditable(false);
  addHTML( utilities, "HTML/Utilities.html");
  
  loggingUtil = new JEditorPane();
  loggingUtil.setEditable(false);
  addHTML( loggingUtil, "HTML/LoggingUtility.html");
  
  eventUtil = new JEditorPane();
  eventUtil.setEditable(false);
  addHTML( eventUtil, "HTML/EventUtility.html");
  
  pollingUtil = new JEditorPane();
  pollingUtil.setEditable(false);
  addHTML( pollingUtil, "HTML/PollingUtility.html");
  
  usefulModelStructures = new JEditorPane();
  usefulModelStructures.setEditable(false);
  addHTML( usefulModelStructures, "HTML/UsefulModelStructures.html");
  
  freeForAllModel = new JEditorPane(); 
  freeForAllModel.setEditable(false);
  addHTML( freeForAllModel, "HTML/FreeForAll.html");
  
  diplomacyModel = new JEditorPane();
  diplomacyModel.setEditable(false);
  addHTML( diplomacyModel, "HTML/DiplomacyModel.html");
  
  corporationModel = new JEditorPane();
  corporationModel.setEditable(false);
  addHTML( corporationModel, "HTML/CorporationModel.html");
  
  extending = new JEditorPane();
  extending.setEditable(false);
  addHTML( extending, "HTML/ExtendingLIMBS.html");
  
  newAgents = new JEditorPane();
  newAgents.setEditable(false);
  addHTML( newAgents, "HTML/NewAgentsAndUtilities.html");
  
  agentInterface = new JEditorPane();
  agentInterface.setEditable(false);
  addHTML( agentInterface, "HTML/TheAgentInterface.html");
 
  preferences = new JEditorPane();
  preferences.setEditable(false);
  addHTML(preferences, "HTML/Preferences.html");
  
  glossary = new JEditorPane(); 
  glossary.setEditable(false);
  addHTML( glossary, "HTML/Glossary.html");
  
  references = new JEditorPane();
  references.setEditable(false);
  addHTML( references, "HTML/References.html");
  
  license = new JEditorPane();
  license.setEditable(false);
  addHTML( license, "HTML/License.html");
  
 }

 private void addHTML( JEditorPane p, String file) {
  java.net.URL url = LIMBS.class.getResource(file);
  if (url != null) {
   try {
    p.setPage(url);
   } catch (IOException e) {
    System.err.println("Attempted to read a bad URL: " + url);
   }
  } else {
   System.err.println("Couldn't find file: " + file);
  }
 }
 
 private void showPanel(int i) {
   
  switch (i) {
  case 0:
   contentScrollPane.setViewportView(helpIntro);
   break;
  case 1:
   contentScrollPane.setViewportView(limbsIntro);
   break;
  case 2:
   contentScrollPane.setViewportView(interfaceOverview) ;  
   break;
   
  case 3:
   contentScrollPane.setViewportView(startingNewModel);
   break;
  case 4:
   contentScrollPane.setViewportView(propsAgentsGroups);
   break;
  case 5:
   contentScrollPane.setViewportView(groupingAgentsAndProps);
   break;
  case 6:
   contentScrollPane.setViewportView(copyPaste);
   break;  
  case 7:
   contentScrollPane.setViewportView(messages);
   break;  
  case 8:
   contentScrollPane.setViewportView(runningSims);
   break;
  case 9:
   contentScrollPane.setViewportView(saveAndOpenSims);
   break;
   
  case 10:
   contentScrollPane.setViewportView(hotcoAgents);
   break;
  case 11:
   contentScrollPane.setViewportView(basicHOTCO);
   break;
  case 12:
   contentScrollPane.setViewportView(communicationRules);
   break;
  case 13:
   contentScrollPane.setViewportView(emotionalTransfer);
   break; 
   
  case 14:
   contentScrollPane.setViewportView(utilities);
   break;
  case 15:
   contentScrollPane.setViewportView(loggingUtil);
   break;
  case 16:
   contentScrollPane.setViewportView(eventUtil);
   break;
  case 17:
   contentScrollPane.setViewportView(pollingUtil);
   break; 
   
  case 18:
   contentScrollPane.setViewportView(usefulModelStructures);
   break;  
  case 19:
   contentScrollPane.setViewportView(freeForAllModel);
   break;  
  case 20:
   contentScrollPane.setViewportView(diplomacyModel);
   break;  
  case 21:
   contentScrollPane.setViewportView(corporationModel);
   break;
     
  case 22:
   contentScrollPane.setViewportView(extending);
   break;  
  case 23:
   contentScrollPane.setViewportView(newAgents);
   break;  
  case 24:
   contentScrollPane.setViewportView(agentInterface);
   break;
   
  case 25:
   contentScrollPane.setViewportView(preferences);
   break;
  case 26:
   contentScrollPane.setViewportView(glossary);
   break;  
  case 27:
   contentScrollPane.setViewportView(references);
   break;
  case 28:
   contentScrollPane.setViewportView(license);
   break;
   
  default:
   break;
  }
  
 }

 public void valueChanged(ListSelectionEvent e) {
  if (!list.isSelectionEmpty()) {
   // Find out which indexes are selected.
   int minIndex = list.getMinSelectionIndex();
   int maxIndex = list.getMaxSelectionIndex();
   for (int i = minIndex; i <= maxIndex; i++) {
    if (list.isSelectedIndex(i)) {
     showPanel(i);
    }
   }
  }

 }

}
