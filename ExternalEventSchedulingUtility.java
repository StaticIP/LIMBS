package LIMBS;

import java.util.*;

public class ExternalEventSchedulingUtility extends Utility {
  
  private Set<ExternalEventSchedulingUtilityEvent> events = null;
  private Vector<Proposition> currentProps = null;
  private Vector<Double> currentPropVals = null;
  private int propIndex = -1;
  
//----------------------------------------------------------------------------------
  
  public ExternalEventSchedulingUtility(String n, Simulation s){
    super(n, s);
    this.events = (Set<ExternalEventSchedulingUtilityEvent>) new HashSet<ExternalEventSchedulingUtilityEvent>();
  }
  
  protected void destruct(){
    this.events.clear();
    return;
  }
  
//----------------------------------------------------------------------------------
  //Events for adding, removing and seeing events
  public void addEvent( final ExternalEventSchedulingUtilityEvent event ){
    
    //if we have the event already, return, else, add it in
    if( events.contains( event ) ) return;
    events.add( event );
    
    //handle undoing
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        events.remove( event );
        return true;
      }
      public boolean redo(){
        events.add( event );
        return true;
      }
    }, null);
    
  }
  
  public void removeEvent( final ExternalEventSchedulingUtilityEvent event ){
    
    //if we have the event, remove it , else just return
    if( !events.contains( event ) ) return;
    events.remove( event );
    
    //handle undoing
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        events.add( event );
        return true;
      }
      public boolean redo(){
        events.remove( event );
        return true;
      }
    }, null);
    
  }
  
  public Set<ExternalEventSchedulingUtilityEvent> getEvents(){
    return new LinkedHashSet<ExternalEventSchedulingUtilityEvent>(events);
  }
  
//----------------------------------------------------------------------------------
  public Component copy(){
    ExternalEventSchedulingUtility retUtil = new ExternalEventSchedulingUtility( "Copy of " + this.getName(), this.getSimulation() );
    
    //copy over the internal members
    for(ExternalEventSchedulingUtilityEvent e : events)
      retUtil.events.add( e.copy() );
    
    return retUtil;
  }
  
  public AgentInterface getAgentLevelInterface(){
    return new ExternalEventSchedulingUtilityInterface(this);
  }
  
  
//----------------------------------------------------------------------------------
  
  public void setup(){
  }
  
  //this utility has no need to read messages, so just immediately burn them
  public void iterateThroughQueue(){
    while(!messageQueue.isEmpty())
      messageQueue.poll().burnMessage();
  }
  
  public void communicate(){
    
    //make sure the current props are empty so we don't provide erroneous info
    currentProps = null;
    currentPropVals = null;
    propIndex = -1;
    
    //process any relevant events
    for(ExternalEventSchedulingUtilityEvent e : events)
      if( e.getTriggerTime() == getSimulation().getCurrentIteration() )
        this.processEvent(e);
    
    return;
  }
  
  
//----------------------------------------------------------------------------------
  private void processEvent(ExternalEventSchedulingUtilityEvent e){
    
    //determine who to send the event to
    Set<Agent> toCommunicateWith = null;
    if( e.sendingToAllCommuncitors() ){
      toCommunicateWith = e.getAgents();
    }else{
      toCommunicateWith = this.getAgentSet();
    }
    
    //set up the vector to get information from
    currentProps = e.getPropositionVector();
    currentPropVals = e.getPropositionValues();
    
    //send off the messages
    for( Agent a : toCommunicateWith ){
      Message m = new Message(this, a);
    }
      
    //reset the vectors just in case
    currentProps = null;
    currentPropVals = null;
    propIndex = -1;
  }
  
//----------------------------------------------------------------------------------
  
  protected void addPropositionInternal( Proposition p ){
    return;
  }
  
  protected void removePropositionInternal( Proposition p ){
    for( ExternalEventSchedulingUtilityEvent e : events ){
      e.removeProposition(p);
    }
  }
  
  protected void addCommunicationAgentInternal( Agent a ){
    return;
  }
  
  protected void removeCommunicationAgentInternal( Agent a ){
    for( ExternalEventSchedulingUtilityEvent e : events ){
      e.removeAgent(a);
    }
  }
  
//----------------------------------------------------------------------------------
  //other required stubmethods (internal)
  
  public void queryProposition( Proposition prop ){
    if( currentProps == null ) return;
    
    propIndex = -1;
    if( currentProps.contains( prop ) ){
      propIndex = currentProps.indexOf( prop );
    }
        
  }
  
  public double getPropositionalActivation(){
    if( propIndex == -1 ) return 0.0;
    return this.currentPropVals.get(propIndex).doubleValue();
  }
  
  public void completeQuery(){
    return;
  }
  
  
  public String getSubComponentType(){
    return "Event Utility";
  }
//----------------------------------------------------------------------------------
  
};