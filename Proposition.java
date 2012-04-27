package LIMBS;

import java.util.*;

//Proposition types:
//   0 - Evidence
//   1 - Action
//   2 - Goal

class Proposition extends Component {
  
  private final int type;
  private Set<Group> groupList;
  
  public Proposition(String name, int type, Simulation s){
    super(name, s);
    this.type = type;
    
    groupList = new LinkedHashSet<Group>();
    
    getSimulation().addProp(this);
  }
  
  public void delete(){
    while( !groupList.isEmpty() ) groupList.iterator().next().removeProposition(this);
    getSimulation().removeProp(this);
  }
  
  public long getType() {
   return type;
  }
  
  public String getMainComponentType(){
    return "Proposition";
  }
  
  public String getSubComponentType(){
    switch(type){
      case 0: return "Evidence";
      case 1: return "Action";
      case 2: return "Goal";
      default: return "ERROR - Invalid Type";
    }
  }
  
  public void addGroup(final Group g){
    if(groupList.contains(g)) return;
    groupList.add(g);
    g.addProposition(this);
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        groupList.remove(g);
        return true;
      }
      public boolean redo(){
        groupList.add(g);
        return true;
      }
    }, null);
  }
  
  public void removeGroup(final Group g){
    if(!groupList.contains(g)) return;
    groupList.remove(g);
    g.removeProposition(this);
    
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        groupList.add(g);
        return true;
      }
      public boolean redo(){
        groupList.remove(g);
        return true;
      }
    }, null);
    
  }
  
  public Set<Group> getGroupSet()  {
   return new LinkedHashSet<Group>(groupList);
  }
  
  
  
  public void addAgent(Agent a){}
  public void removeAgent(Agent a){}
  public Set<Agent> getAgentSet(){
    return new LinkedHashSet<Agent>();
  }
  
  public void addProposition(Proposition p){}
  public void removeProposition(Proposition p){}
  public Set<Proposition> getPropositionSet(){
    return new LinkedHashSet<Proposition>();
  }
  
  public void informSubInterface(){}

 public Component copy() {
   Proposition retVal = new Proposition("Copy of " + this.getName(), this.type, this.getSimulation());
   for(Group g : this.groupList) retVal.addGroup(g);
   return (Component) retVal;
 }
  
  
};