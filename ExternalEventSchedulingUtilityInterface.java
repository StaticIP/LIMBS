package LIMBS;

//TODO: Add functionality for updating existing events

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class ExternalEventSchedulingUtilityInterface extends AgentInterface implements ActionListener, ListSelectionListener {


    private ExternalEventSchedulingUtility model = null;
    private JPanel listPanel, creator, creatorExternal, agentList, propList;
    private JList eventList;
    private JScrollPane eventScroller, agentScroller, propScroller;
    private JTextField nameField, iterField; 
    private GridBagConstraints c;
    private JButton selectAllAgents, deselectAllAgents, selectAllProps, deselectAllProps;

    private DefaultListModel events = new DefaultListModel();
    private Map<JCheckBox, Agent> agentMap = new HashMap<JCheckBox, Agent>();
    private Map<PropositionListItem, Proposition> propMap = new HashMap<PropositionListItem, Proposition>();
    private Map<String, ExternalEventSchedulingUtilityEvent> eventMap = new HashMap<String, ExternalEventSchedulingUtilityEvent>();

    private ExternalEventSchedulingUtilityEvent newEvent;
    private ExternalEventSchedulingUtilityEvent currentEvent = null;

    public ExternalEventSchedulingUtilityInterface( ExternalEventSchedulingUtility m ) {
        super(m);
        this.model = m;        
        this.setLayout(new GridBagLayout());     

        //initialize the gridbaglayout stuff
        c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 10, 10);

        //setup each of the two main panels in the interface
        setupListPanel();
        setupCreatorPanel();        

        //add the two main panels to the pane
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        this.add(listPanel, c);
        c.gridx = 1;
        c.gridwidth = 3;
        this.add(creatorExternal, c);

        // Display the window.
        this.repaint();
    }

    private void setupListPanel() {
        listPanel = new JPanel(new BorderLayout());

        //Create and add the New Event and Remove Event buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(130,60));
        JButton newEvent = new JButton("New Event");
        newEvent.setMargin(new Insets(0,0,0,0));
        newEvent.setPreferredSize(new Dimension(100,25));
        newEvent.setActionCommand("newEvent");
        newEvent.addActionListener(this);

        JButton removeEvent = new JButton("Remove Event");
        removeEvent.setMargin(new Insets(0,0,0,0));
        removeEvent.setPreferredSize(new Dimension(120,25));
        removeEvent.setActionCommand("removeEvent");
        removeEvent.addActionListener(this);

        buttonPanel.add(newEvent);
        buttonPanel.add(removeEvent);
        listPanel.add(buttonPanel, BorderLayout.NORTH);

        //Create, Initialize, and add the event list panel
        eventList = new JList(events); 
        eventList.setFixedCellWidth(150);
        getEvents();    
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        eventList.setVisibleRowCount(-1);
        eventList.addListSelectionListener(this);
        eventScroller = new JScrollPane(eventList);
        eventScroller.setPreferredSize(new Dimension(100, 350));

        listPanel.add(eventScroller, BorderLayout.CENTER);
        listPanel.setBorder(new EmptyBorder(10,10,10,10));
    }

    private void setupCreatorPanel() {
        creatorExternal = new JPanel();
        creatorExternal.setPreferredSize(new Dimension(555,455));
        creator = new JPanel(new BorderLayout());
        creator.setBorder(BorderFactory.createLineBorder(Color.black));
        creator.setPreferredSize(new Dimension(550,450));

        //Create info panel, which holds the labels and fields
        //for the event name and iteration 
        JPanel info = new JPanel(new GridBagLayout());
        JLabel name = new JLabel("Event Name");
        nameField = new JTextField(25);
        JLabel iter = new JLabel("Iteration");
        iterField = new JTextField(25);
        iterField.setToolTipText("Enter an integer value from 1 to infinity");

        //add the components to the info panel
        c.gridx = 0;
        c.gridy = 0;
        info.add(name, c);
        c.gridx = 1;
        info.add(nameField, c);
        c.gridy = 1;
        c.gridx = 0;
        info.add(iter, c);
        c.gridx = 1;
        info.add(iterField, c);
        info.setBorder(new EmptyBorder(10, 10, 10, 10) );
        creator.add(info, BorderLayout.NORTH);    

        //Setup the agent list panel
        JPanel agentPanel = new JPanel(new GridBagLayout());
        JLabel title = new JLabel("Agents to inform"); 

        //add the select/deselect all checkboxes
        selectAllAgents = new JButton("Select All");
        selectAllAgents.setMargin(new Insets(0,0,0,0));
        selectAllAgents.setPreferredSize(new Dimension(100,25));
        selectAllAgents.setActionCommand("selectAllAgents");
        selectAllAgents.addActionListener(this);

        deselectAllAgents = new JButton("Deselect All");
        deselectAllAgents.setMargin(new Insets(0,0,0,0));
        deselectAllAgents.setPreferredSize(new Dimension(100,25));
        deselectAllAgents.setActionCommand("deselectAllAgents");
        deselectAllAgents.addActionListener(this);        

        agentList = new JPanel(new GridLayout(0,1,5,5));
        setupAgents();
        agentScroller = new JScrollPane(agentList);
        agentScroller.setPreferredSize(new Dimension(150, 200));

        //Add the agent list to the panel
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        agentPanel.add(title, c);
        c.gridy = 1;
        c.gridwidth = 1;
        agentPanel.add(selectAllAgents, c);
        c.gridx = 1;
        agentPanel.add(deselectAllAgents, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        agentPanel.add(agentScroller, c);
        agentPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
        creator.add(agentPanel, BorderLayout.WEST);

        //Setup the proposition list panel
        JPanel propsPanel = new JPanel(new GridBagLayout());
        JLabel propTitle = new JLabel("Proposition - Value");

        //add the select/deselect all checkboxes
        selectAllProps = new JButton("Select All");
        selectAllProps.setMargin(new Insets(0,0,0,0));
        selectAllProps.setPreferredSize(new Dimension(100,25));
        selectAllProps.setActionCommand("selectAllProps");
        selectAllProps.addActionListener(this);

        deselectAllProps = new JButton("Deselect All");
        deselectAllProps.setMargin(new Insets(0,0,0,0));
        deselectAllProps.setPreferredSize(new Dimension(100,25));
        deselectAllProps.setActionCommand("deselectAllProps");
        deselectAllProps.addActionListener(this);

        propList = new JPanel(new GridLayout(0,1,5,5));
        setupProps();
        propScroller = new JScrollPane(propList);
        propScroller.setPreferredSize(new Dimension(250, 200));

        //Add the list to the panel
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        propsPanel.add(propTitle, c);
        c.gridy = 1;
        c.gridwidth = 1;
        propsPanel.add(selectAllProps, c);
        c.gridx = 1;
        propsPanel.add(deselectAllProps, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        propsPanel.add(propScroller, c);
        propsPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
        creator.add(propsPanel, BorderLayout.EAST);

        creator.setVisible(false);

        //Add the control panel at the bottom
        JPanel controls = new JPanel();
        JButton save = new JButton("Save");
        save.setMargin(new Insets(0,0,0,0));
        save.setPreferredSize(new Dimension(100,25));
        save.setActionCommand("save");
        save.addActionListener(this);

        controls.add(save);

        creator.add(controls, BorderLayout.SOUTH);
        creatorExternal.add(creator);
    }

    private void getEvents() {
        //Get the updated list of events and sort 
        Set<ExternalEventSchedulingUtilityEvent> set = this.model.getEvents(); 
        set = sortEvents(set);
        //Add the updated events to the data map and the display panel
        events.clear();
        eventMap.clear();
        if( set.size() > 0 ) {
            Iterator<ExternalEventSchedulingUtilityEvent> it = set.iterator();
            while(it.hasNext()){
                ExternalEventSchedulingUtilityEvent e = it.next();
                events.addElement(e.getEventName());
                eventMap.put(e.getEventName(), e);
            }
        } 
        //Repaint the display panel
        validate();
        eventList.repaint();
    }

    private void setupAgents() {

        //Get the updated set of agents, and sort
        Set<Agent> set = model.getAgentSet();
        set = sortAgents(set);

        //Add the updated events to the data map and the display panel
        Iterator<Agent> it = set.iterator();
        agentList.removeAll();
        agentMap.clear();
        while(it.hasNext()){
            Agent a = it.next();
            JCheckBox b = new JCheckBox(a.getName());
            if(currentEvent != null) {
                if(currentEvent.getAgents().contains(a)) {
                    b.setSelected(true);
                }
            }
            agentMap.put(b, a);
            agentList.add(b);
        }

        //Repaint
        validate();
        agentList.repaint();
    }

    private void setupProps() {
        //Get an updated list and sort
        Set<Proposition> set = model.getPropositionSet();
        set = sortPropositions(set);
        //Add the updated propositions to the data map & display panel
        Iterator<Proposition> it = set.iterator();
        propMap.clear();
        propList.removeAll();
        while(it.hasNext()){
            Proposition p = it.next();
            PropositionListItem l = new PropositionListItem(p.getName());
            if(currentEvent != null) {
                int index = currentEvent.getPropositionVector().indexOf(p);
                if( index != -1 ) {
                    l.name.setSelected(true);
                    l.valence.setText(String.valueOf(currentEvent.getPropositionValues().elementAt(index)));
                }
            }
            propMap.put(l, p);
            propList.add(l);
        }
        //Repaint
        validate();
        propList.repaint();
    }

    private void setupNewEvent() {
        if( currentEvent == null || !currentEvent.getEventName().equals("New Event")) {
            //Initialize a new event
            newEvent = new ExternalEventSchedulingUtilityEvent(model);
            currentEvent = newEvent;
            nameField.setText("New Event");
            newEvent.setEventName("New Event");
            model.addEvent(newEvent);
            iterField.setText("");
            getEvents();
            eventList.setSelectedIndex(0);
            setupAgents();
            setupProps();
            creator.setVisible(true);
            creator.repaint();
        } 
    }

    private void populateEventInfo() {
        //Retrieve the information from the selected event and populate
        this.nameField.setText(currentEvent.getEventName());
        this.iterField.setText(String.valueOf(currentEvent.getTriggerTime()));
        setupAgents();
        setupProps();
        creator.repaint();
    }

    private void undoRedoLogic(){
        //undo/redo logic
        model.getSimulation().registerEndChange( new ChangeNotification(){
            public boolean notifyChange() {
                refresh();
                return true;
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if(command.equals("newEvent")) {
            setupNewEvent();
        } else if ( command.equals("removeEvent") ) {
            //Determine which event to remove and remove it
            Object[] selected = eventList.getSelectedValues();
            for(int i = 0; i < selected.length ; i++) {
                if( eventMap.get(selected[i]) != null ) {
                    undoRedoLogic();
                    model.removeEvent(eventMap.get(selected[i]));
                    break;
                }
            }
            //Update the list of events and initialize for a new event
            getEvents();
            if(model.getEvents().size() > 0) {
                eventList.setSelectedIndex(0);
            } else {
                creator.setVisible(false);
            }
        } else if ( command.equals("save")) {
            undoRedoLogic();
            if( currentEvent != null ) {
                //Remove the event, the recreate it (easier to handle)
                Object[] selected = eventList.getSelectedValues();
                for(int i = 0; i < selected.length ; i++) {
                    if( eventMap.get(selected[i]) != null ) {
                        model.removeEvent(eventMap.get(selected[i]));
                    }
                }
                currentEvent = null; 
            }
            //Initialize the new event and populate it with the given data
            newEvent = new ExternalEventSchedulingUtilityEvent(model);
            currentEvent = newEvent;
            //Add the agents
            Iterator it = agentMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox b = (JCheckBox) pairs.getKey();
                if( b.isSelected() ) {
                    newEvent.addAgent((Agent) pairs.getValue());
                }
            }
            //Add the propositions
            it = propMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                PropositionListItem l = (PropositionListItem) pairs.getKey();
                if( l.name.isSelected() ) {
                    Double v = Double.valueOf(l.valence.getText());
                    newEvent.addProposition((Proposition)pairs.getValue(), v);
                }
            }
            //Setup the remaining attributes and add the event
            try{
                newEvent.setTriggerTime(Long.valueOf(iterField.getText()));
            }catch(Exception exc){
                newEvent.setTriggerTime(1);
                iterField.setText("1");
            }
            newEvent.setEventName(nameField.getText());
            model.addEvent(newEvent);
            //Reset everything for a new event
            newEvent = null;
            getEvents();
            this.repaint();
        } else if (command.equals("selectAllAgents")) {
            Iterator it = agentMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox b = (JCheckBox) pairs.getKey();
                b.setSelected(true);
            }
        } else if (command.equals("deselectAllAgents")) {
            Iterator it = agentMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox b = (JCheckBox) pairs.getKey();
                b.setSelected(false);
            }
        } else if (command.equals("selectAllProps")) {
            Iterator it = propMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                PropositionListItem l = (PropositionListItem) pairs.getKey();
                l.name.setSelected(true);
            }
        } else if (command.equals("deselectAllProps")) {
            Iterator it = propMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                PropositionListItem l = (PropositionListItem) pairs.getKey();
                l.name.setSelected(false);
            }
        } 

    }

    public void valueChanged(ListSelectionEvent e) {
        //Handle the selection/deselection of events in the event list
        if (e.getValueIsAdjusting() == false) {
            currentEvent = eventMap.get(eventList.getSelectedValue());
            if(currentEvent != null) {
                //if an event is selected populate its info
                populateEventInfo();
                creator.setVisible(true);
            }
        }
    }

    //Sort a list of agents alphabetically by name
    private Set<Agent> sortAgents(Set<Agent> agents) {
        Set<Agent> temp = new LinkedHashSet<Agent>();
        Iterator<Agent> it = agents.iterator();
        while( agents.size() > 0 ) {
            Agent front = it.next();
            while(it.hasNext()) {
                Agent curr = it.next();
                if( curr.getName().compareTo(front.getName()) < 0 ) {
                    front = curr;
                }
            }
            temp.add(front);
            agents.remove(front);
            it = agents.iterator();
        }
        return temp;
    }

    //Sort a list of propositions alphabetically by name
    private Set<Proposition> sortPropositions(Set<Proposition> props) {
        Set<Proposition> temp = new LinkedHashSet<Proposition>();
        Iterator<Proposition> it = props.iterator();
        while( props.size() > 0 ) {
            Proposition front = it.next();
            while(it.hasNext()) {
                Proposition curr = it.next();
                if( curr.getName().compareTo(front.getName()) < 0 ) {
                    front = curr;
                }
            }
            temp.add(front);
            props.remove(front);
            it = props.iterator();
        }
        return temp;
    }

    //Sort a set of events alphabetically by name
    private Set<ExternalEventSchedulingUtilityEvent> sortEvents(Set<ExternalEventSchedulingUtilityEvent> e) {
        Set<ExternalEventSchedulingUtilityEvent> temp = new LinkedHashSet<ExternalEventSchedulingUtilityEvent>();
        Iterator<ExternalEventSchedulingUtilityEvent> it = e.iterator();
        while( e.size() > 0 ) {
            ExternalEventSchedulingUtilityEvent front = it.next();
            while(it.hasNext()) {
                ExternalEventSchedulingUtilityEvent curr = it.next();
                if( curr.getEventName().compareTo(front.getEventName()) < 0 ) {
                    front = curr;
                }
            }
            temp.add(front);
            e.remove(front);
            it = e.iterator();
        }
        return temp;
    }

    public void refresh() {

    }
};