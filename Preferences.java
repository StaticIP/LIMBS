package LIMBS;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class Preferences extends JFrame implements ActionListener { 

    private JPanel simulation, agent, general, saveClose;
    private JRadioButton small, medium, large, smallP, mediumP, largeP, smallN, mediumN, largeN;
    private ButtonGroup simGroup, propGroup, nodeGroup;
    private JCheckBox alwaysShowNames;
    private JTextField frameRate; 
    private Interface interf;
    private JRadioButton min, max;
    private ButtonGroup screenSize;

    private GridBagConstraints c;

    public Preferences( Interface i ) {
        super();
        this.interf = i;
        this.setPreferredSize(new Dimension(400, 550));
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setTitle("Preferences");
        //this.setResizable(false);

        JTabbedPane main = new JTabbedPane();

        // Initialize the GridBagContstraint object we're
        // using to get a pretty UI
        c = new GridBagConstraints();
        // Add 10px of space on the right and left sides of each
        // component
        c.insets = new Insets(0, 0, 10, 10);

        setupSimulationPanel();
        setupAgentPanel();  
        setupGeneralPanel();
        setupSavePanel();

        main.add("Simulation", simulation);
        main.add("Agent", agent);
        main.add("General", general);
        this.getContentPane().add(main, BorderLayout.CENTER);
        this.getContentPane().add(saveClose, BorderLayout.PAGE_END);

        // Display the window.
        this.repaint();
        this.pack();
        this.setVisible(true);
    }

    private void setupSavePanel() {

        saveClose = new JPanel();

        JButton save = new JButton("Save");
        save.setActionCommand("save");
        save.setPreferredSize(new Dimension(80, 20));
        save.addActionListener(this);

        JButton saveAndClose = new JButton("Save & Close");
        saveAndClose.setPreferredSize(new Dimension(130, 20));
        saveAndClose.setActionCommand("saveAndClose");
        saveAndClose.addActionListener(this);

        JButton cancel = new JButton("Cancel");
        cancel.setPreferredSize(new Dimension(70, 20));
        cancel.setActionCommand("cancel");
        cancel.addActionListener(this);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.gridx = 0;
        c.gridy = 6;
        saveClose.add(save,c);
        c.gridx = 1;
        saveClose.add(saveAndClose, c);
        c.gridx = 2;
        saveClose.add(cancel, c);
    }

    private void setupGeneralPanel() {
        general = new JPanel();

        JPanel window = new JPanel();
        //window.setSize(new Dimension(300, 300));

        Toolkit toolkit =  Toolkit.getDefaultToolkit ();
        Dimension dim = toolkit.getScreenSize();     

        min = new JRadioButton("900x700");
        max = new JRadioButton(dim.width + "x" + dim.height);

        //query preferences to determine which is selected
        if(LIMBSSystem.config().screenSize == 1) {
            min.setSelected(true);
        } else {
            max.setSelected(true);
        }

        screenSize = new ButtonGroup();
        screenSize.add(min);
        screenSize.add(max);

        window.setLayout(new GridLayout(3, 1, 10, 10));

        window.add(new JLabel("Window Size"));
        window.add(min);
        window.add(max);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        general.add(window, c);
        
    }

    private void setupAgentPanel() {
        //Set up the Agent Preferences
        agent = new JPanel();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        agent.add(new JLabel("Node Size"), c);

        JPanel nodeSize = new JPanel();
        nodeSize.setLayout(new GridLayout(0,3));
        nodeSize.setPreferredSize(new Dimension(350, 140));

        //Create the radio buttons.
        smallN = new JRadioButton("Small");
        mediumN = new JRadioButton("Medium");
        largeN = new JRadioButton("Large");

        if( LIMBSSystem.config().nodeSize == 20 )
            smallN.setSelected(true);
        else if( LIMBSSystem.config().nodeSize == 40 )
            mediumN.setSelected(true);
        else if( LIMBSSystem.config().nodeSize == 60 )
            largeN.setSelected(true);


        //Group the radio buttons.
        nodeGroup = new ButtonGroup();
        nodeGroup.add(smallN);
        nodeGroup.add(mediumN);
        nodeGroup.add(largeN);

        nodeSize.add(new CustomPanel(1,30,30,20,20));
        nodeSize.add(new CustomPanel(1,20,20,40,40));
        nodeSize.add(new CustomPanel(1,10,10,60,60));
        nodeSize.add(smallN);
        nodeSize.add(mediumN);
        nodeSize.add(largeN);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 1;
        agent.add(nodeSize, c);

    }

    private void setupSimulationPanel() {
        //Set up the Simulation Preferences Tab
        simulation = new JPanel(new GridBagLayout());

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        simulation.add(new JLabel("Agent Size"), c);

        JPanel agentSize = new JPanel();
        agentSize.setLayout(new GridLayout(0,3));
        agentSize.setPreferredSize(new Dimension(350, 140));

        //Create the radio buttons.
        small = new JRadioButton("Small"); 
        medium = new JRadioButton("Medium");
        large = new JRadioButton("Large");

        if( LIMBSSystem.config().agentSize == 20 )
            small.setSelected(true);
        else if ( LIMBSSystem.config().agentSize == 40)
            medium.setSelected(true);
        else if ( LIMBSSystem.config().agentSize == 60 ) 
            large.setSelected(true);

        //Group the radio buttons.
        simGroup = new ButtonGroup();
        simGroup.add(small);
        simGroup.add(medium);
        simGroup.add(large);

        agentSize.add(new CustomPanel(1,30,30,20,20));
        agentSize.add(new CustomPanel(1,20,20,40,40));
        agentSize.add(new CustomPanel(1,10,10,60,60));
        agentSize.add(small);
        agentSize.add(medium);
        agentSize.add(large);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        simulation.add(agentSize, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 2;
        simulation.add(new JLabel("Proposition Size"), c);

        JPanel propSize = new JPanel();
        propSize.setLayout(new GridLayout(0,3));
        propSize.setPreferredSize(new Dimension(350, 150));

        //Create the radio buttons.
        smallP = new JRadioButton("Small");
        mediumP = new JRadioButton("Medium");
        largeP = new JRadioButton("Large");

        if( LIMBSSystem.config().propSize == 20 )
            smallP.setSelected(true);
        else if ( LIMBSSystem.config().propSize == 40)
            mediumP.setSelected(true);
        else if ( LIMBSSystem.config().propSize == 60 ) 
            largeP.setSelected(true);

        //Group the radio buttons.
        propGroup = new ButtonGroup();
        propGroup.add(smallP);
        propGroup.add(mediumP);
        propGroup.add(largeP);

        propSize.add(new CustomPanel(2,30,30,20,20));
        propSize.add(new CustomPanel(2,20,20,40,40));
        propSize.add(new CustomPanel(2,10,10,60,60));
        propSize.add(smallP);
        propSize.add(mediumP);
        propSize.add(largeP);


        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 3;
        simulation.add(propSize, c);


        alwaysShowNames = new JCheckBox("Always show object names");
        alwaysShowNames.setSelected(LIMBSSystem.config().showNames);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridheight = 2;
        c.gridx = 0;
        c.gridy = 4;
        simulation.add(alwaysShowNames, c);  

        
        JPanel frameRatePanel = new JPanel();
        frameRate = new JTextField();
        frameRate.setColumns(5);
        frameRate.setText(Integer.toString(LIMBSSystem.config().framerate));
        frameRate.setToolTipText("Set how frequently you want to redraw the canvas during simulation.");
        JLabel frameRateLabel = new JLabel("Canvas Framerate");
        
        frameRatePanel.add(frameRateLabel);
        frameRatePanel.add(frameRate);
        frameRatePanel.setPreferredSize(new Dimension(150, 27));
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridheight = 2;
        c.gridx = 0;
        c.gridy = 6;
        simulation.add(frameRatePanel, c);
        
    }


    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if( command.equals("save")) {

            saveCanvasPreferences();
            saveNodePreferences();
            saveGeneralPreferences();

        } else if ( command.equals("cancel")) {

            this.setVisible(false);

        } else if ( command.equals("saveAndClose")) {

            saveCanvasPreferences();
            saveNodePreferences();
            saveGeneralPreferences();
            this.setVisible(false);
        }

    }

    private void saveCanvasPreferences() {
        int agentSize = 40;
        if( small.isSelected()) {
            agentSize = 20;
        } else if ( medium.isSelected() ) {
            agentSize = 40;
        } else if ( large.isSelected()) {
            agentSize = 60;
        }
        int propSize = 40;
        if( smallP.isSelected()) {
            propSize = 20;
        } else if ( mediumP.isSelected() ) {
            propSize = 40;
        } else if ( largeP.isSelected()) {
            propSize = 60;
        }
        LIMBSSystem.config().agentSize = agentSize;
        LIMBSSystem.config().propSize = propSize;
        LIMBSSystem.config().showNames = alwaysShowNames.isSelected();
        LIMBSSystem.config().framerate = Integer.parseInt(frameRate.getText());
        LIMBSSystem.saveConfig();

        this.interf.getCanvas().updateWidgetSizes();
    }

    private void saveNodePreferences() {
        int nodeSize = 20;
        if( smallN.isSelected()) {
            nodeSize = 20;
        } else if ( mediumN.isSelected() ) {
            nodeSize = 40;
        } else if ( largeN.isSelected()) {
            nodeSize = 60;
        }
        LIMBSSystem.config().nodeSize = nodeSize;
        LIMBSSystem.saveConfig();

    }

    private void saveGeneralPreferences() {
        int windowSize = 1;
        if( max.isSelected() ) {
            windowSize = 2;
        }
        LIMBSSystem.config().screenSize = windowSize;
        LIMBSSystem.saveConfig();
    }

    private class CustomPanel extends JPanel {

        private int x, y, w, h, type;

        public CustomPanel(int type, int x, int y, int w, int h) {
            super();
            this.type = type;
            this.x = x;
            this.y = y;
            this.w = w; 
            this.h = h;
        }

        public void paint (Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            if( this.type == 1 ) 
                g2.drawOval(this.x,this.y,this.w,this.h);
            else if ( this.type == 2 )
                g2.drawRect(this.x, this.y, this.w, this.h);
        }

    }
}
