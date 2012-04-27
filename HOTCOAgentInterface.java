package LIMBS;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class HOTCOAgentInterface extends AgentInterface {
  
  private HOTCOAgent model;
  private HOTCONetworkWidget network;
  private javax.swing.Timer timer;
  
  private JTabbedPane agentTabPane;
  private HOTCOEmotionPanel emotionPanel;
  private HOTCOCommunicationPanel communicationPanel;
  private JPanel toolkit, agentPanel;
  private JButton addNode, addLink;
  
  private int numNodes;
  
  public HOTCOAgentInterface(HOTCOAgent m){
    super(m);
    model = m;
    numNodes = model.getNodes().size();
    
    setLayout(new BorderLayout());
    setupToolkit();
    add(toolkit, BorderLayout.PAGE_START);
    
    //set up the network
    network = new HOTCONetworkWidget(m);
    network.setSize(256,256);
    network.setVisible(true);
    add(network, BorderLayout.CENTER);
    
    // set up the agent
    setupAgentPanel();
    agentPanel.setVisible(true);
    
    add(agentPanel, BorderLayout.EAST);
        
    //set up the repainting timer
    timer = new javax.swing.Timer(50, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        network.repaint();
        int newNumNodes = model.getNodes().size();
        if (newNumNodes != numNodes) {
          update();
        }
        numNodes = newNumNodes;
      }
    });
    timer.start();
  }
  
  public void update(){
    if (emotionPanel != null) {
      emotionPanel.update();
    }
    this.repaint();
  }
  
  private void setupToolkit() {
    toolkit = new JPanel();
    toolkit.setLayout(new BoxLayout(toolkit, BoxLayout.X_AXIS));
    
    addNode = new JButton("Add Node");
    addNode.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        
        model.getSimulation().registerEndChange(new ChangeNotification(){
          public boolean notifyChange(){
            model.recalculateEmotion();
            return true;
          }
        });
        
        HOTCONode neuron = new HOTCONode(model, true, null);
        model.addNode(neuron);
        model.recalculateEmotion();
      }
    });
    toolkit.add(addNode);
    
    addLink = new JButton("Add Link");
    addLink.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        model.getSimulation().registerEndChange(null);
        LinkedHashSet<HOTCONode> nodeSet = network.getSelectedNodes();
        
        // generic error dialog
        if (nodeSet.size() < 2) {
          JOptionPane.showMessageDialog(null, "Select at least 2 nodes",
            "Node Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        
        // combinations of two (selectedNode.length C 2)
        HOTCONode[] selectedNode = nodeSet.toArray(new HOTCONode[nodeSet.size()]);
        for (int i = 0; i < selectedNode.length; i++) {
          for (int j = i + 1; j < selectedNode.length; j++) {
            HOTCOLink link = new HOTCOLink(model, selectedNode[i], selectedNode[j], 1.0);
            model.addLink(link);
          }
        }
      }
    });
    toolkit.add(addLink);
  }
  
  private void setupAgentPanel() {
    agentPanel = new JPanel();
    agentPanel.setPreferredSize(new Dimension(260, 595));
    
    agentTabPane = new JTabbedPane();
    agentTabPane.setPreferredSize(new Dimension(250, 585));
    
    emotionPanel = new HOTCOEmotionPanel(model);
    communicationPanel = new HOTCOCommunicationPanel(model, network);
    agentTabPane.add("Emotion", emotionPanel);
    agentTabPane.add("Communication", communicationPanel);
    
    agentPanel.add(agentTabPane);
  }
  
@Override
public void refresh() {
    // TODO Auto-generated method stub
    
}
  
};