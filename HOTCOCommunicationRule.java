package LIMBS;

import java.util.*;

public class HOTCOCommunicationRule{
  
  private HOTCOAgent currentAgent = null;
  private Agent communicationTarget = null;
  private Vector<HOTCONode> nodeCriteria = new Vector<HOTCONode>();
  private String name;
  private double threshold = 0.5;
  
  public HOTCOCommunicationRule(HOTCOAgent curr, Agent target){
    this.currentAgent = curr;
    this.communicationTarget = target;
    name = "Rule for " + curr.getName();;
  }
  
  public void destruct(){
    nodeCriteria.clear();
    currentAgent = null;
    communicationTarget = null;
  }
  
  public double getThreshold(){
    return threshold;
  }
  
  public void setThreshold(final double t){
    if( t >= -1.0 && t <= 1.0 ){
      
      currentAgent.getSimulation().registerChange( new Change(){
        private double currT = threshold;
        public boolean undo(){
          threshold = currT;
          return true;
        }
        public boolean redo(){
          currT = getThreshold();
          threshold = t;
          return true;
        }
      }, null);
      
      threshold = t;
    }
  }
  
  public Agent getTargetAgent(){
    return communicationTarget;
  }
  
  public void setTargetAgent(final Agent target){
    
      currentAgent.getSimulation().registerChange( new Change(){
        private final Agent oldAgent = communicationTarget;
        public boolean undo(){
          communicationTarget = oldAgent;
          return true;
        }
        public boolean redo(){
          communicationTarget = target;
          return true;
        }
      }, null);
    
    communicationTarget = target;
  }
  
  public Vector<HOTCONode> getCriteria(){
    return nodeCriteria;
  }
  
  public void addCriterion( final HOTCONode n ){
    if( nodeCriteria.contains(n) ) return;
    nodeCriteria.add(n);
    
    currentAgent.getSimulation().registerChange( new Change(){
      public boolean undo(){
        nodeCriteria.remove(n);
        return true;
      }
      public boolean redo(){
        nodeCriteria.add(n);
        return true;
      }
    }, null);
    
  }
  
  public void removeCriterion( final HOTCONode n ){
    if( !nodeCriteria.contains(n) ) return;
    nodeCriteria.remove(n);
    
    currentAgent.getSimulation().registerChange( new Change(){
      public boolean undo(){
        nodeCriteria.add(n);
        return true;
      }
      public boolean redo(){
        nodeCriteria.remove(n);
        return true;
      }
    }, null);
    
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(final String n) {
    
    currentAgent.getSimulation().registerChange( new Change(){
      private final String oldName = name;
      public boolean undo(){
        name = oldName;
        return true;
      }
      public boolean redo(){
        name = n;
        return true;
      }
    }, null);
    
    name = n;
    
  }
  
  public boolean areCriteriaFulfilled(){
    
    //if we have a criterion below a critical level, then we do not fulfill all criteria
    for( HOTCONode c : nodeCriteria ){
      if( c.getActivation() < threshold ){
        return false;
      }
    }
    
    //if we cannot find an unfilled criterion, all criteria must be fulfilled
    return true;
  }
  
};