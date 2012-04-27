package LIMBS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;

@SuppressWarnings("serial")
public class PollingUtilityInterface extends AgentInterface implements ActionListener {

    PollingUtility model = null;

    private JPanel constit, issues, elections, digests, issueList, constitList, digestList;
    private GridBagConstraints c;
    private Map<JCheckBox, Agent> constitMap = new HashMap<JCheckBox, Agent>();
    private Map<JCheckBox, Agent> digestMap = new HashMap<JCheckBox, Agent>();
    private Map<JCheckBox, Proposition> issueMap = new HashMap<JCheckBox, Proposition>();
    private JScrollPane digestScroller, constitScroller, issueScroller;
    private JButton save, refresh;
    private JButton selectAllConstits, deselectAllConstits, selectAllDigests, deselectAllDigests, selectAllIssues, deselectAllIssues;
    private JTextField timeBefore, pollingTime , timeBetween;
    
    PollingUtilityInterface( PollingUtility m ){
        super(m);
        model = m;
        this.setLayout(new BorderLayout(10,10));
        //initialize the gridbaglayout stuff
        c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 10, 10);
        
        setupConstituencyPanel();
        setupIssuesPanel();
        setupElectionsPanel();
        setupDigestsPanel();
        
        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        right.add(elections, BorderLayout.NORTH);
        right.add(digests, BorderLayout.CENTER);
         
        save = new JButton("Save");
        save.setMargin(new Insets(0,0,0,0));
        save.setPreferredSize(new Dimension(100,25));
        save.setActionCommand("save");
        save.addActionListener(this);

        refresh = new JButton("Refresh");
        refresh.setMargin(new Insets(0,0,0,0));
        refresh.setPreferredSize(new Dimension(100,25));
        refresh.setActionCommand("refresh");
        refresh.addActionListener(this);
        
        JPanel bottom = new JPanel();
        bottom.add(save);
        bottom.add(refresh);
        
        JPanel left = new JPanel(new BorderLayout());
        left.add(constit, BorderLayout.CENTER);
        
        JPanel main = new JPanel(new FlowLayout(FlowLayout.CENTER) );
        main.add(left);
        main.add(issues);
        main.add(right);
        
        this.add(main, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);
        this.repaint();
    }
    
    private void setupConstituencyPanel() {
        constit = new JPanel(new BorderLayout());
        constit.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel constitLabel = new JLabel("Constituency");
        
        //add the select/deselect all checkboxes
        selectAllConstits = new JButton("Select All");
        selectAllConstits.setMargin(new Insets(0,0,0,0));
        selectAllConstits.setPreferredSize(new Dimension(100,25));
        selectAllConstits.setActionCommand("selectAllConstits");
        selectAllConstits.addActionListener(this);
        
        deselectAllConstits = new JButton("Deselect All");
        deselectAllConstits.setMargin(new Insets(0,0,0,0));
        deselectAllConstits.setPreferredSize(new Dimension(100,25));
        deselectAllConstits.setActionCommand("deselectAllConstits");
        deselectAllConstits.addActionListener(this); 
        
        JPanel bottom = new JPanel();
        bottom.add(selectAllConstits);
        bottom.add(deselectAllConstits);
        
        constitList = new JPanel(new GridLayout(0,1,5,5));
        setupConstits();
        constitScroller = new JScrollPane(constitList);
        constitScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        constitScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        constitScroller.setPreferredSize(new Dimension(200, 500));
        
        constit.add(constitLabel, BorderLayout.NORTH);
        constit.add(constitScroller, BorderLayout.CENTER);
        constit.add(bottom, BorderLayout.SOUTH);
        
    }
    
    private void setupIssuesPanel() {
        issues = new JPanel(new BorderLayout());
        issues.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
       
        JLabel issueLabel = new JLabel("Issues");
        
        //add the select/deselect all checkboxes
        selectAllIssues = new JButton("Select All");
        selectAllIssues.setMargin(new Insets(0,0,0,0));
        selectAllIssues.setPreferredSize(new Dimension(100,25));
        selectAllIssues.setActionCommand("selectAllIssues");
        selectAllIssues.addActionListener(this);
        
        deselectAllIssues = new JButton("Deselect All");
        deselectAllIssues.setMargin(new Insets(0,0,0,0));
        deselectAllIssues.setPreferredSize(new Dimension(100,25));
        deselectAllIssues.setActionCommand("deselectAllIssues");
        deselectAllIssues.addActionListener(this); 
        
        JPanel bottom = new JPanel();
        bottom.add(selectAllIssues);
        bottom.add(deselectAllIssues);
        
        issueList = new JPanel(new GridLayout(0,1,5,5));
        setupIssues();
        issueScroller = new JScrollPane(issueList);
        issueScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        issueScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        issueScroller.setPreferredSize(new Dimension(200, 500));
        
        issues.add(issueLabel, BorderLayout.NORTH);
        issues.add(issueScroller, BorderLayout.CENTER);
        issues.add(bottom, BorderLayout.SOUTH);
    }
    
    private void setupElectionsPanel(){
        elections = new JPanel(new GridBagLayout());
        
        JLabel electionsLabel = new JLabel("Elections");
        
        JLabel timeBeforeElection = new JLabel("Time before first Election: ");
        timeBefore = new JTextField(10);
        timeBefore.setText(String.valueOf(model.getTimeToFirstElection()));
        
        JLabel pollingTimeLabel = new JLabel("Polling Time: ");
        pollingTime = new JTextField(10); 
        pollingTime.setText(String.valueOf(model.getTimeForVoting()));
        
        JLabel timeBetweenElections = new JLabel("Time between Elections: ");
        timeBetween = new JTextField(10);
        timeBetween.setText(String.valueOf(model.getTotalElectionTime()));
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        elections.add(electionsLabel, c);
        c.gridwidth = 1;
        c.gridy = 1;
        elections.add(timeBeforeElection, c);
        c.gridx = 1;
        elections.add(timeBefore, c);
        c.gridy = 2;
        c.gridx = 0;
        elections.add(pollingTimeLabel, c);
        c.gridx = 1;
        elections.add(pollingTime, c);
        c.gridx = 0;
        c.gridy = 3;
        elections.add(timeBetweenElections, c);
        c.gridx = 1;
        elections.add(timeBetween, c);
        
    }
    
    private void setupDigestsPanel() {
        digests = new JPanel(new BorderLayout());
        
        JLabel digestLabel = new JLabel("Digests");
        
        //add the select/deselect all checkboxes
        selectAllDigests = new JButton("Select All");
        selectAllDigests.setMargin(new Insets(0,0,0,0));
        selectAllDigests.setPreferredSize(new Dimension(100,25));
        selectAllDigests.setActionCommand("selectAllDigests");
        selectAllDigests.addActionListener(this);
        
        deselectAllDigests = new JButton("Deselect All");
        deselectAllDigests.setMargin(new Insets(0,0,0,0));
        deselectAllDigests.setPreferredSize(new Dimension(100,25));
        deselectAllDigests.setActionCommand("deselectAllDigests");
        deselectAllDigests.addActionListener(this); 
        
        JPanel bottom = new JPanel();
        bottom.add(selectAllDigests);
        bottom.add(deselectAllDigests);
        
        digestList = new JPanel(new GridLayout(0,1,5,5));
        setupDigests();
        digestScroller = new JScrollPane(digestList);
        digestScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        digestScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        digestScroller.setPreferredSize(new Dimension(200, 257));
        
        digests.add(digestLabel, BorderLayout.NORTH);
        digests.add(digestScroller, BorderLayout.CENTER);
        digests.add(bottom, BorderLayout.SOUTH);
        
        digests.repaint();
    }
    
    private void setupConstits() {
        //Get the updated set of agents, and sort
        Set<Agent> set = model.getAgentSet();
        set = sortAgents(set);
        //Add the updated events to the data map and the display panel
        Iterator<Agent> it = set.iterator();
        constitMap.clear();
        constitList.removeAll();
        while(it.hasNext()){
            Agent a = it.next();
            JCheckBox b = new JCheckBox(a.getName());
            if( model.getConstituency().contains(a)) {
              b.setSelected(true);
            }
            constitMap.put(b, a);
            constitList.add(b);
        }
        //Repaint
        validate();
        constitList.repaint();
    }
    
    private void setupDigests() {
        //Get the updated set of agents, and sort
        Set<Agent> set = model.getAgentSet();
        set = sortAgents(set);
        //Add the updated events to the data map and the display panel
        Iterator<Agent> it = set.iterator();
        digestMap.clear();
        digestList.removeAll();
        while(it.hasNext()){
            Agent a = it.next();
            JCheckBox b = new JCheckBox(a.getName());
            if( model.getDigests().contains(a)) {
              b.setSelected(true);
            }
            digestMap.put(b, a);
            digestList.add(b);
        }
        //Repaint
        validate();
        digestList.repaint();
    }
    
    private void setupIssues() {
        //Get an updated list and sort
        Set<Proposition> set = model.getPropositionSet();
        set = sortPropositions(set);
        //Add the updated propositions to the data map & display panel
        Iterator<Proposition> it = set.iterator();
        issueMap.clear();
        issueList.removeAll();
        while(it.hasNext()){
            Proposition p = it.next();
            JCheckBox l = new JCheckBox(p.getName());
            if( model.getIssues().contains(p)) {
                l.setSelected(true);
            }
            issueMap.put(l, p);
            issueList.add(l);
        }
        //Repaint
        validate();
        issueList.repaint();
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

    
    //Refresh the interface when components are added to the simulation
    public void refresh() {
        setupDigests();
        setupConstits();
        setupIssues();
        validate();
        constitList.repaint();
        digestList.repaint();
        issueList.repaint();
        this.repaint();        
    }
    
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        if ( command.equals("save")) {
            
          //undo/redo logic
          model.getSimulation().registerEndChange( new ChangeNotification(){
            public boolean notifyChange() {
              refresh();
              return true;
            }
          });
          
            //Add/remove the constituents
            Set<Agent> constituents = model.getConstituency();
            Iterator it = constitMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox b = (JCheckBox) pairs.getKey();
                Agent a = (Agent) pairs.getValue();
                if( b.isSelected() && !constituents.contains(a) ) {
                    model.addAgentToConstituency(a);
                } else if ( !b.isSelected() && constituents.contains(a)  ) {
                    model.removeAgentFromConstituency(a);
                }
            }
            //Add the propositions
            Set<Proposition> issues = model.getIssues();
            it = issueMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox l = (JCheckBox) pairs.getKey();
                Proposition p = (Proposition) pairs.getValue();
                if( l.isSelected() && !issues.contains(p) ) {
                    model.addPropositionToIssues(p);
                } else if( !l.isSelected() && issues.contains(p) ) {
                    model.removePropositionFromIssues(p);
                }
            }
            
            //Add/remove the digests
            Set<Agent> digest = model.getDigests();
            it = digestMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox b = (JCheckBox) pairs.getKey();
                Agent a = (Agent) pairs.getValue();
                if( b.isSelected() && !digest.contains(a) ) {
                    model.addAgentToDigests(a);
                } else if ( !b.isSelected() && constituents.contains(a)  ) {
                    model.removeAgentFromDigests(a);
                }
            }
            
            //Save the other stuff
            Long timeToFirstElection = Long.valueOf(timeBefore.getText());
            Long timeForVoting = Long.valueOf(pollingTime.getText());
            Long totalElectionTime = Long.valueOf(timeBetween.getText());
            if( timeToFirstElection < 0 || timeForVoting < 0 || totalElectionTime < 0 ) {
                JOptionPane.showMessageDialog(this,
                        "All provided values must be above 0.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                model.setTimeToFirstElection(timeToFirstElection);
                model.setTimeForVoting(timeForVoting);
                model.setTotalElectionTime(totalElectionTime);
            }
            
        } else if (command.equals("refresh")) {
            this.refresh();
        } else if (command.equals("selectAllConstits")) {
            Iterator it = constitMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox b = (JCheckBox) pairs.getKey();
                b.setSelected(true);
            }
        } else if (command.equals("deselectAllConstits")) {
            Iterator it = constitMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox b = (JCheckBox) pairs.getKey();
                b.setSelected(false);
            }
        } else if (command.equals("selectAllDigests")) {
            Iterator it = digestMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox b = (JCheckBox) pairs.getKey();
                b.setSelected(true);
            }
        } else if (command.equals("deselectAllDigests")) {
            Iterator it = digestMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox b = (JCheckBox) pairs.getKey();
                b.setSelected(false);
            }
        } else if (command.equals("selectAllIssues")) {
            Iterator it = issueMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox l = (JCheckBox) pairs.getKey();
                l.setSelected(true);
            }
        } else if (command.equals("deselectAllIssues")) {
            Iterator it = issueMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                JCheckBox l = (JCheckBox) pairs.getKey();
                l.setSelected(false);
            }
        } 
        
    }

};