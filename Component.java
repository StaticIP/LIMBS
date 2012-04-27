package LIMBS;

import java.util.Vector;
import java.util.Set;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

abstract class Component {
  
  private final long id;
  private String name;
  private Simulation simulation = null;
  private Vector<ActionListener> listeners = null;
  
  public Component(String n, Simulation s){
    this.id = s.getNewComponentNumber();
    this.name = n;
    this.simulation = s;
    this.listeners = new Vector<ActionListener>();
  }
  
  //inform system of non-saveable components
  static {
    LIMBSSystem sys = LIMBSSystem.SystemCall();
    sys.omitFieldFromSave(Component.class, "listeners");
  }
  
  public final Simulation getSimulation(){
    return simulation;
  }
  
  /** @fn boolean equals( Component c )
   *
   * @brief Compares a provided component to the current component, regardless of type
   * 
   * @param c The component to compare the current component with
   */
  public boolean equals(Component c){
    return this.getClass().getName().equals( c.getClass().getName() ) && this.id == c.id;
  }
  
  /** @fn long getId()
   *
   * @brief Gets the identifier for this component, which is unique among identifiers for the same component supertype (Group, Agent or Proposition)
   */
  public final long getId(){
    return id;
  }
  
  /** @fn String getName()
   *
   * @brief Gets the agent's name for human interaction use
   */
  public final String getName(){
    return name;
  }
  
  /** @fn void setName(String newName)
   *
   * @brief Sets the agent's name for human interaction use and triggers an undo-able change
   * 
   * @param newName The agent's new name, assumed to be non-null
   */
  public final void setName(String newName){
    final String oldN = name;
    final String newN = newName;
    name = newName;
    simulation.registerChange( new Change(){ 
      public boolean undo() {
        name = oldN;
        return true;
      }
      public boolean redo() {
        name = newN;
        return true;
      }
    }, null );
      
  }
  
  /** @fn void addListener(ActionListener a)
   * 
   * @brief Adds a new listener to this component to listen for changes
   * 
   * @param a The new listener
   */
  public final void addListener(ActionListener a){
    if( listeners == null ) this.listeners = new Vector<ActionListener>();
    if( listeners.contains(a) ) return;
    listeners.add(a);
  }

  /** @fn void removeListener(ActionListener a)
   * 
   * @brief Removes an old listener from this component
   * 
   * @param a The listener to be removed
   */
  public final void removeListener(ActionListener a){
    if( listeners == null ) this.listeners = new Vector<ActionListener>();
    if( !listeners.contains(a) ) return;
    listeners.remove(a);
  }
  
  /** @fn void removeListener(ActionListener a)
   * 
   * @brief Notifies all listeners than a change has occurred
   * 
   */
  public final void notifyListener(){
    if( listeners == null ) this.listeners = new Vector<ActionListener>();
    ActionEvent e = new ActionEvent(this, 0, "Component update notification");
    for( ActionListener a : listeners )
      a.actionPerformed(e);
  }
  
  abstract public void delete();
  
  abstract public void addGroup(Group g);
  abstract public void removeGroup(Group g);
  abstract public Set<Group> getGroupSet();
  
  abstract public void addAgent(Agent a);
  abstract public void removeAgent(Agent a);
  abstract public Set<Agent> getAgentSet();
  
  abstract public void addProposition(Proposition p);
  abstract public void removeProposition(Proposition p);
  abstract public Set<Proposition> getPropositionSet();
  
  abstract public Component copy();
  
  abstract public String getMainComponentType();
  abstract public String getSubComponentType();
};