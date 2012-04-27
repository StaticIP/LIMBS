package LIMBS;

import java.util.*;

public class ExternalEventSchedulingUtilityEvent {
  
  private ExternalEventSchedulingUtility parent;
  private String eventName = "";
  private long triggerTime = 0;
  private boolean allAgents = false;
  private Set<Agent> agentSet = new HashSet<Agent>();
  private Vector<Proposition> propSet = new Vector<Proposition>();
  private Vector<Double> propVal = new Vector<Double>();
  
//----------------------------------------------------------------------------------
  public ExternalEventSchedulingUtilityEvent(ExternalEventSchedulingUtility p){
    parent = p;
  }
  
  
//----------------------------------------------------------------------------------
   public String getEventName(){
    return eventName;
  }
  
  public void setEventName(final String name){
    
    parent.getSimulation().registerChange( new Change(){
      final String oldName = new String(eventName);
      final String newName = new String(name);
      public boolean undo(){
        eventName = new String(oldName);
        return true;
      }
      public boolean redo(){
        eventName = new String(newName);
        return true;
      }
    }, null);
    
    eventName = new String(name);
  } 
  
  
//----------------------------------------------------------------------------------
  public long getTriggerTime(){
    return triggerTime;
  }
  
  public void setTriggerTime(final long time){
    if( time < 1 ) return;
    
    parent.getSimulation().registerChange( new Change(){
      final long oldTime = triggerTime;
      public boolean undo(){
        triggerTime = oldTime;
        return true;
      }
      public boolean redo(){
        triggerTime = time;
        return true;
      }
    }, null);
    
    this.triggerTime = time;
  }
  
  
//----------------------------------------------------------------------------------
  public boolean sendingToAllCommuncitors(){
    return allAgents;
  }
  
  public void sendToAllCommunicators(){
    
    parent.getSimulation().registerChange( new Change(){
      final boolean oldVal = allAgents;
      public boolean undo(){
        allAgents = oldVal;
        return true;
      }
      public boolean redo(){
        allAgents = true;
        return true;
      }
    }, null);
    
    allAgents = true;
  }
  
  public void dontSendToAllCommunicators(){
    
    parent.getSimulation().registerChange( new Change(){
      final boolean oldVal = allAgents;
      public boolean undo(){
        allAgents = oldVal;
        return true;
      }
      public boolean redo(){
        allAgents = false;
        return true;
      }
    }, null);
    
    allAgents = false;
  }
  
//----------------------------------------------------------------------------------
  public ExternalEventSchedulingUtilityEvent copy(){
    
    //create a new event at the same time
    ExternalEventSchedulingUtilityEvent clone = new ExternalEventSchedulingUtilityEvent(this.parent);
    clone.setTriggerTime( this.triggerTime );
    
    //shallow copy over the proposition information
    
    //shallow copy over the communicator information
    
    return clone;
    
  }
  
//----------------------------------------------------------------------------------
  public void addAgent( final Agent a ){
    if( agentSet.contains(a) ) return;
    agentSet.add(a);
    
    //handle undoing
    parent.getSimulation().registerChange( new Change(){
      public boolean undo(){
        agentSet.remove(a);
        return true;
      }
      public boolean redo(){
        agentSet.add(a);
        return true;
      }
    }, null);
    
  }
  
  public void removeAgent( final Agent a ){
    if( !agentSet.contains(a) ) return;
    agentSet.remove(a);
    
    //handle undoing
    parent.getSimulation().registerChange( new Change(){
      public boolean undo(){
        agentSet.add(a);
        return true;
      }
      public boolean redo(){
        agentSet.remove(a);
        return true;
      }
    }, null);
  }
  
  public Set<Agent> getAgents(){
    return agentSet;
  }
  
//----------------------------------------------------------------------------------
  
  public void changePropositionValue( final Proposition p, double newValue ){
    if( !propSet.contains(p) || newValue < -1.0 || newValue > 1.0 ) return;
    final Double oldValueHolder = propVal.get( propSet.indexOf(p) );
    final Double newValueHolder = new Double( newValue );
    final int index = propSet.indexOf(p);
                                     
    //handle undoing
    parent.getSimulation().registerChange( new Change(){
      public boolean undo(){
        propVal.remove(index);
        propVal.add( index, oldValueHolder );
        return true;
      }
      public boolean redo(){
        propVal.remove(index);
        propVal.add( index, newValueHolder );
        return true;
      }
    }, null);
    
    propVal.remove(index);
    propVal.add( index, newValueHolder );
  }
  
  public void addProposition( final Proposition p, double value ){
    if( propSet.contains(p) || value < -1.0 || value > 1.0 ) return;
    final Double val = new Double(value);
    propSet.add(p);
    propVal.add(val);
    
    //handle undoing
    parent.getSimulation().registerChange( new Change(){
      public boolean undo(){
        propSet.remove(p);
        propVal.remove(val);
        return true;
      }
      public boolean redo(){
        propSet.add(p);
        propVal.add(val);
        return true;
      }
    }, null);
    
  }
  
  public void removeProposition( final Proposition p ){
    
    if( !propSet.contains(p) ) return;
    final int index = propSet.indexOf( p );
    
    //handle undoing
    parent.getSimulation().registerChange( new Change(){
      final Double oldVal = propVal.get(index);
      public boolean undo(){
        propSet.add(p);
        propVal.add(oldVal);
        return true;
      }
      public boolean redo(){
        propSet.remove(p);
        propVal.remove(oldVal);
        return true;
      }
    }, null);
    
    propSet.remove(index);
    propVal.remove(index);
  }
  
  public Vector<Proposition> getPropositionVector(){
    return new Vector<Proposition>(propSet);
  }

  public Vector<Double> getPropositionValues(){
    return new Vector<Double>(propVal);
  }
  
//----------------------------------------------------------------------------------
  
  public void processEvent(){
    //prepare the event scheduler for the event
    
    //send off the messages to the communicators
    
  }
  
};