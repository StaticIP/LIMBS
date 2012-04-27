package LIMBS;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class HOTCOEmotionPanel extends JPanel {
  private AgentListModel communicationList, empathyList, contagionList, altruismList;
  private HOTCOAgent agentModel;
  
  public HOTCOEmotionPanel(HOTCOAgent model) {
    this.agentModel = model;
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
    setupEmotionPanel();
  }
  
  public void update() {
    if (communicationList != null) {
     communicationList.updateOrderedList();
    }
    if (empathyList != null) {
     empathyList.updateOrderedList();
    }
    if (contagionList != null) {
     contagionList.updateOrderedList();
    }
    if (altruismList != null) {
     altruismList.updateOrderedList();
    }
  }
  
  // define a custom way to render HOTCOAgent (alternative: override HOTCOAgent.toString())
  private void prepareHOTCOListCellRenderer(JList list)
  {
    final ListCellRenderer defaultRenderer = list.getCellRenderer();
    ListCellRenderer agentCellRenderer = new ListCellRenderer(){
      public java.awt.Component getListCellRendererComponent(
        JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
      {
        return defaultRenderer.getListCellRendererComponent(
          list, ((Agent) value).getName(), index, isSelected, cellHasFocus);
      }
    };
    list.setCellRenderer(agentCellRenderer);
  }

  private JScrollPane createEmotionListScrollPane(AgentListModel listModel) {
    JList list = new JList(listModel);
    list.setVisibleRowCount(5);
    list.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

    // only enable drop and export for communications list
    if (listModel == communicationList) {
      list.setDragEnabled(true);
      list.setTransferHandler(new HOTCOExportTransferHandler());
      list.setDropTarget(null);

      // only handling single selections; 
      // drag+drop requres HOTCOAgent to be serialized for multi-selection
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    prepareHOTCOListCellRenderer(list);

    JScrollPane scroll = new JScrollPane(list);    
    Dimension scrollPaneSize = new Dimension(200, 12+(15*5));

    scroll.setMinimumSize(scrollPaneSize);
    scroll.setPreferredSize(scrollPaneSize);
    scroll.setMaximumSize(scrollPaneSize);

    // using scroll instead of list for transfer handler
    if (listModel != communicationList) {
      scroll.setTransferHandler(new HOTCOImportTransferHandler());
    }
    scroll.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    return scroll;
  }

  private JLabel createPanelLabel(String text) {
    JLabel panelLabel = new JLabel(text);
    panelLabel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
    panelLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    return panelLabel;
  }

  private JButton createRemoveButton(final AgentListModel model, final JScrollPane scroll) {    
    JButton remove = new JButton("Remove");
    remove.setMaximumSize(new Dimension(85,20));
    remove.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
    remove.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        
        agentModel.getSimulation().registerEndChange( new ChangeNotification(){
          public boolean notifyChange(){
            update();
            repaint();
            return true;
          }
        });
        
        JList list = (JList)scroll.getViewport().getView();
        if (list.getSelectedIndex() < 0)
          return;

        for (Object o : list.getSelectedValues()) {
          Agent agent = (Agent)o;
          model.remove(agent);
        }
        list.clearSelection();
      }
    });

    // lose focus for other lists when clicking this button
    JList emotionList = (JList)scroll.getViewport().getView();
    final JButton emotionListButton = remove;
    emotionList.addFocusListener(new FocusListener(){
      // note: focus is not gained upon item drag and non-selection
      public void focusGained(FocusEvent e) { };

      public void focusLost(FocusEvent e) {
        if (e.getOppositeComponent() instanceof JButton) {
          JButton button = (JButton)e.getOppositeComponent();
          if (button == emotionListButton) {
            return;
          }
        }
        ((JList)(e.getSource())).clearSelection();
      }
    });

    return remove;
  }

  private void setupEmotionPanel() {
    add(createPanelLabel("Communicators"));

    // create communicators interface
    communicationList = new CommunicationListModel();
    add(createEmotionListScrollPane(communicationList));

    // Empathy - Evidence
    empathyList = new EmpathyListModel();
    JScrollPane empathyScroll = createEmotionListScrollPane(empathyList);
    add(createPanelLabel("Empathy"));
    add(empathyScroll);
    add(createRemoveButton(empathyList, empathyScroll));

    // Contagion - Actions
    contagionList = new ContagionListModel();
    JScrollPane contagionScroll = createEmotionListScrollPane(contagionList);
    add(createPanelLabel("Contagion"));
    add(contagionScroll);
    add(createRemoveButton(contagionList, contagionScroll));

    // Altruism - Goals
    altruismList = new AltruismListModel();
    JScrollPane altruismScroll = createEmotionListScrollPane(altruismList);
    add(createPanelLabel("Altruism"));
    add(altruismScroll);
    add(createRemoveButton(altruismList, altruismScroll));
  }
   
  private abstract class AgentListModel extends AbstractListModel {
    protected Vector<Agent> orderedList;
    
    public AgentListModel() {
      updateOrderedList();
    }
    
    // call update for entire list
    public void updateOrderedList() {
      fireContentsChanged(this, 0, getSize());
    }
    public abstract boolean add(Agent a);
    public abstract boolean remove(Agent a);
    
    public int getSize() {
      return orderedList.size();
    }
    
    public Object getElementAt(int index) {
      return orderedList.elementAt(index);
    }
  }
  
  private class CommunicationListModel extends AgentListModel {
    public void updateOrderedList() {
      orderedList = new Vector<Agent>(agentModel.getAgentSet());
      super.updateOrderedList();
    }
    
    // do not define add or remove for these
    public boolean add(Agent a) { return false; }
    public boolean remove(Agent a) { return false; }
  }
  
  private class EmpathyListModel extends AgentListModel {
    public void updateOrderedList() {
      orderedList = new Vector<Agent>(agentModel.getEmpathyList());
      super.updateOrderedList();
    }
    
    public boolean add(Agent a) {
      if (orderedList.contains(a)) return false;
      agentModel.getSimulation().registerEndChange( new ChangeNotification(){
        public boolean notifyChange(){
          update();
          repaint();
          return true;
        }
      });
      agentModel.addAgentToEmpathyList(a);
      updateOrderedList();
      return true;
    }
    
    public boolean remove(Agent a) {
      if (!orderedList.contains(a)) return false;
      agentModel.getSimulation().registerEndChange( new ChangeNotification(){
        public boolean notifyChange(){
          update();
          repaint();
          return true;
        }
      });
      agentModel.removeAgentFromEmpathyList(a);
      updateOrderedList();
      return true;
    }
  }
  
  private class ContagionListModel extends AgentListModel {
    public void updateOrderedList() {
      orderedList = new Vector<Agent>(agentModel.getContagionList());
      super.updateOrderedList();
    }
    
    public boolean add(Agent a) {
      if (orderedList.contains(a)) return false;
      agentModel.getSimulation().registerEndChange( new ChangeNotification(){
        public boolean notifyChange(){
          update();
          repaint();
          return true;
        }
      });
      agentModel.addAgentToContagionList(a);
      updateOrderedList();
      return true;
    }
    
    public boolean remove(Agent a) {
      if (!orderedList.contains(a)) return false;
      agentModel.getSimulation().registerEndChange( new ChangeNotification(){
        public boolean notifyChange(){
          update();
          repaint();
          return true;
        }
      });
      agentModel.removeAgentFromContagionList(a);
      updateOrderedList();
      return true;
    }
  }
  
  private class AltruismListModel extends AgentListModel {
    public void updateOrderedList() {
      orderedList = new Vector<Agent>(agentModel.getAltruismList());
      super.updateOrderedList();
    }
    
    public boolean add(Agent a) {
      if (orderedList.contains(a)) return false;
      agentModel.getSimulation().registerEndChange( new ChangeNotification(){
        public boolean notifyChange(){
          update();
          repaint();
          return true;
        }
      });
      agentModel.addAgentToAltruismList(a);
      updateOrderedList();
      return true;
    }
    
    public boolean remove(Agent a) {
      if (!orderedList.contains(a)) return false;
      agentModel.getSimulation().registerEndChange( new ChangeNotification(){
        public boolean notifyChange(){
          update();
          repaint();
          return true;
        }
      });
      agentModel.removeAgentFromAltruismList(a);
      updateOrderedList();
      return true;
    }
  }
  
  private class HOTCOExportTransferHandler extends TransferHandler {
    public int getSourceActions(JComponent c) {
      return COPY;
    }
    
    protected Transferable createTransferable(JComponent c) {
      JList dragFrom = (JList)c;
      
      if (dragFrom.getSelectedIndex() < 0)
        return null;
      
      // TODO: handle multiple selections (can only be done if HOTCOAgent is serializable)
      // Object[] selection = dragFrom.getSelectedValues();
      
      return new AgentTransferable((Agent)dragFrom.getSelectedValue());
    }
    
    // strictly copying; no post-drag actions required
    protected void exportDone(JComponent source, Transferable data, int action) { }
  }
  
  private class HOTCOImportTransferHandler extends TransferHandler {
    public boolean canImport(TransferHandler.TransferSupport support) {
      // only support drops for now (not clipboard paste)
      if (!support.isDrop())
        return false;
      
      if (!support.isDataFlavorSupported(AgentTransferable.agentFlavor))
        return false;
      
      support.setDropAction(COPY);
      return true;
    }
    
    public boolean importData(TransferHandler.TransferSupport support) {
      if (!canImport(support))
        return false;
      
      JScrollPane importPane = (JScrollPane)support.getComponent();
      Agent data = null;
      
      // unpack the data
      try {
        data = (Agent)support.getTransferable().getTransferData(AgentTransferable.agentFlavor);
      }
      catch (UnsupportedFlavorException e) {
        return false;
      }
      catch (IOException e) {
        return false;
      }
      
      if (importPane.getViewport().getView() instanceof JList) {
        JList importList = (JList)importPane.getViewport().getView();
        
        ListModel model = importList.getModel();
        if (model instanceof AgentListModel) {
          AgentListModel agentListModel = (AgentListModel)importList.getModel();
          return agentListModel.add(data);
        }
      }
      
      return false;
    }
  }
  
}