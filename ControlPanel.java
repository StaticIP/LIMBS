package LIMBS;

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel implements ActionListener {
  
  private JPanel groups, propositions, agents, utilities;
  private JTabbedPane objectTabs;
  private Vector<JButton> buttons = new Vector<JButton>();
  private JButton newSimulation, openSimulation, play, pause, stop, reset,
    step;
  private JButton zoomIn, zoomOut, panNorth, panEast, panSouth, panWest;
  private Interface i;
  private Simulation simulation;
  private File savedFile = null;
  
  public void disableControls() {
    java.awt.Component[] comps = this.getComponents();
    for( int i = 0 ; i < comps.length ; i++ )
      comps[i].setEnabled(false);
    for( JButton button : buttons )
      button.setEnabled(false);
  }
  
  public void enableControls() {
    java.awt.Component[] comps = this.getComponents();
    for( int i = 0 ; i < comps.length ; i++ )
      comps[i].setEnabled(true);
    for( JButton button : buttons )
      button.setEnabled(true);
  }
    
  public JButton getButton ( String text ){
    JButton button; 
    if( text.length() > 8 ) {
        button = new JButton();
        button.setLayout(new FlowLayout());
        int count = 0;
        while( true ){
         int nextSpace = text.indexOf(' ', count);
         JLabel textLabel = null;
         if( nextSpace == -1 )
           textLabel = new JLabel( text.substring(count) );
         else
           textLabel = new JLabel( text.substring(count, nextSpace) );
         button.add( textLabel );
         count = text.indexOf(' ', count) + 1;
         if( count == 0 ) break;
        }
    } else {
        button = new JButton(text);
    }
    buttons.add(button);
    return button;
  }
    
  public ControlPanel(Interface i, Simulation s) {
    this.i = i;
    this.simulation = s;
    this.setLayout(new BorderLayout());
    
    objectTabs = new JTabbedPane();
    objectTabs.setPreferredSize(new Dimension(300, 100));
    
    //get an action listener for component creation
    ActionListener newComponentsListener = i.getComponentCreationActionListener();
    
    // Create the groups tab
    groups = new JPanel();
    groups.setLayout(new FlowLayout());
    for( String type : ComponentWidgetFactory.getGroupTypes() ){
      JButton newGroupButton = getButton( type );
      newGroupButton.setMargin(new Insets(0, 0, 0, 0));
      newGroupButton.setPreferredSize(new Dimension(80, 60));
      newGroupButton.setActionCommand( type );
      newGroupButton.addActionListener( newComponentsListener );
      groups.add( newGroupButton );
    }
    objectTabs.add("Groups", groups);
    
    // Create the agents tab
    agents = new JPanel();
    agents.setLayout(new FlowLayout());
    for( String type : ComponentWidgetFactory.getAgentTypes() ){
      JButton newAgentButton = getButton( type );
      newAgentButton.setMargin(new Insets(0, 0, 0, 0));
      newAgentButton.setPreferredSize(new Dimension(80, 60));
      newAgentButton.setActionCommand( type );
      newAgentButton.addActionListener( newComponentsListener );
      agents.add( newAgentButton );
    }
    objectTabs.add("Agents", agents);
    
    // create the propositions tab
    propositions = new JPanel();
    propositions.setLayout(new FlowLayout());
    for( String type : ComponentWidgetFactory.getPropositionTypes() ){
      JButton newPropButton = getButton( type );
      newPropButton.setMargin(new Insets(0, 0, 0, 0));
      newPropButton.setPreferredSize(new Dimension(80, 60));
      newPropButton.setActionCommand( type );
      newPropButton.addActionListener( newComponentsListener );
      propositions.add( newPropButton );
    }
    objectTabs.add("Propositions", propositions);
    
    // create the utilities tab
    utilities = new JPanel();
    utilities.setLayout(new FlowLayout());
    for( String type : ComponentWidgetFactory.getUtilityTypes() ){
      JButton newUtilButton = getButton( type );
      newUtilButton.setMargin(new Insets(0, 0, 0, 0));
      newUtilButton.setPreferredSize(new Dimension(80, 60));
      newUtilButton.setActionCommand( type );
      newUtilButton.addActionListener( newComponentsListener );
      utilities.add( newUtilButton );
    }
    objectTabs.add("Utilities", utilities);
    
    // create the control panel
    JPanel leftControls = new JPanel(new FlowLayout());
    leftControls.setPreferredSize(new Dimension(150, 75));
    
    newSimulation = new JButton("New Simulation");
    newSimulation.setPreferredSize(new Dimension(150, 30));
    newSimulation.setActionCommand("newSim");
    newSimulation.addActionListener(this);
    
    openSimulation = new JButton("Open Simulation");
    openSimulation.setPreferredSize(new Dimension(150, 30));
    openSimulation.setActionCommand("openSim");
    openSimulation.addActionListener(this);
    
    leftControls.add(newSimulation);// , BorderLayout.NORTH);
    leftControls.add(openSimulation);// , BorderLayout.SOUTH);
    
    JPanel rightControls = new JPanel(new FlowLayout()); // GridLayout(2, 3,
    // 10, 10));
    rightControls.setPreferredSize(new Dimension(250, 75));
    
    play = new JButton("Play");
    play.setPreferredSize(new Dimension(70, 30));
    play.setMargin(new Insets(0, 0, 0, 0));
    play.setActionCommand("play");
    play.addActionListener(this);
    
    pause = new JButton("Pause");
    pause.setPreferredSize(new Dimension(70, 30));
    pause.setMargin(new Insets(0, 0, 0, 0));
    pause.setActionCommand("pause");
    pause.addActionListener(this);
    
    stop = new JButton("Stop");
    stop.setPreferredSize(new Dimension(70, 30));
    stop.setMargin(new Insets(0, 0, 0, 0));
    stop.setActionCommand("stop");
    stop.addActionListener(this);
    
    reset = new JButton("Reset");
    reset.setMargin(new Insets(0, 0, 0, 0));
    reset.setPreferredSize(new Dimension(70, 30));
    reset.setActionCommand("reset");
    reset.addActionListener(this);
    
    step = new JButton("Single Iteration");
    step.setMargin(new Insets(0, 0, 0, 0));
    step.setPreferredSize(new Dimension(145, 30));
    step.setActionCommand("step");
    step.addActionListener(this);
    
    rightControls.add(play);
    rightControls.add(pause);
    rightControls.add(stop);
    rightControls.add(reset);
    rightControls.add(step);
    
    this.add(objectTabs, BorderLayout.WEST);
    JPanel right = new JPanel(new BorderLayout());
    right.add(leftControls, BorderLayout.WEST);
    right.add(rightControls, BorderLayout.EAST);
    this.add(right, BorderLayout.EAST);
    
    //add the pan & zoom controls
    Dimension size = new Dimension(35,35);
    zoomIn = new JButton("+");
    zoomIn.setPreferredSize(size);
    zoomIn.setActionCommand("zoomIn");
    zoomIn.addActionListener(this);
    
    zoomOut = new JButton("-");
    zoomOut.setPreferredSize(size);
    zoomOut.setActionCommand("zoomOut");
    zoomOut.addActionListener(this);
    
    panNorth = new JButton("N");
    panNorth.setActionCommand("panNorth");
    panNorth.addActionListener(this);
    
    panEast = new JButton("E");
    panEast.setActionCommand("panEast");
    panEast.addActionListener(this);
    
    panSouth = new JButton("S");
    panSouth.setActionCommand("panSouth");
    panSouth.addActionListener(this);
    
    panWest = new JButton("W");
    panWest.setActionCommand("panWest");
    panWest.addActionListener(this);
    
    
    JPanel panAndZoom = new JPanel(new GridBagLayout());
    
    // Initialize the GridBagContstraint object we're
    // using to get a pretty UI
    GridBagConstraints c = new GridBagConstraints();
    
    c.gridx = 1;
    c.gridy = 0;
    panAndZoom.add(panNorth, c);
    
    c.gridx = 5;
    c.gridy = 0;
    panAndZoom.add(zoomIn, c);
    
    c.gridx = 2;
    c.gridy = 1;
    panAndZoom.add(panEast, c);
    
    c.gridx = 5;
    c.gridy = 1;
    panAndZoom.add(zoomOut, c);
    
    c.gridx = 1;
    c.gridy = 2;
    panAndZoom.add(panSouth, c);
    
    c.gridx = 0;
    c.gridy = 1;
    panAndZoom.add(panWest, c);
    
    this.add(panAndZoom, BorderLayout.CENTER);
    
  }
  
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    
    if (command.equals("newSim")) {
      LIMBSSystem.SystemCall().newInstance();
    } else if (command.equals("openSim")) {
      int openValue = this.i.getMenu().getFileChooser()
        .showOpenDialog(this);
      if (openValue == JFileChooser.APPROVE_OPTION) {
        savedFile = this.i.getMenu().getFileChooser().getSelectedFile();
        LIMBSSystem.SystemCall().load(savedFile);
      }
    } else if (command.equals("play")) {
      i.startSimulation();
      simulation.runSimulation();
    } else if (command.equals("pause")) {
      simulation.pauseSimulation();
      i.stopSimulation();
    } else if (command.equals("stop")) {
      simulation.stopSimulation();
      i.stopSimulation();
    } else if (command.equals("reset")) {
      simulation.resetSimulation();
    } else if (command.equals("step")) {
      simulation.simulateIteration();
    } else if (command.equals("zoomIn")) {
      this.i.getCanvas().zoom(-1);
    } else if (command.equals("zoomOut")) {
      this.i.getCanvas().zoom(1);
    } else if (command.equals("panNorth")) {
      this.i.getCanvas().pan(0,-10);
    } else if (command.equals("panEast")) {
      this.i.getCanvas().pan(10,0);
    } else if (command.equals("panSouth")) {
      this.i.getCanvas().pan(0,10);
    } else if (command.equals("panWest")) {
      this.i.getCanvas().pan(-10,0);
    }
  }
  
}