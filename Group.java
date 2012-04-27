package LIMBS;

import java.util.*;

public final class Group extends Component{
  
  //agent and proposition composition
  Set<Agent> agentList;
  Set<Proposition> propList;
  
  /** @fn Group( String n )
    *
    * @brief Constructor for a new group, assigning it a unique identifier and empty lists of agents and propositions
    * 
    * @param n The name of the group, used primarily for human interaction components
    * 
    * @note This method automatically adds the group to the simulation
    */
  public Group( String n, Simulation s ){
    super(n, s);
    
    //initialize containers
    agentList = new LinkedHashSet<Agent>();
    propList = new LinkedHashSet<Proposition>();
    
    //make the simulation aware of your prresence
    getSimulation().addGroup(this);
  }
  
  /** @fn void delete()
    *
    * @brief Destructor which removes the group from the simulation entirely
    * 
    */
  public void delete(){
    //remove all agents from the group
    while( !agentList.isEmpty() ) removeAgent( agentList.iterator().next() );
    
    //remove propositions
    while( !propList.isEmpty() ) removeProposition( propList.iterator().next() );
    
    //remove self from simulation
    getSimulation().removeGroup(this);
  }
  
  /** @fn void addAgent( Agent a )
    *
    * @brief Adds a new agent to the group
    * 
    * @param a The agent to be added to the group, assumed to be non-null
    * 
    * @note This is a one-touch-all-done method, so it is safe to call it multiple times, and it ensures that the agent has added the group to its own lists
    */
  public void addAgent( final Agent a ){
    
    //if we already have the agent, don't bother adding it
    if( agentList.contains(a) ) return;
    
    //add the agent to the list and make sure it knows we've added it
    agentList.add(a);
    a.addGroup(this);
    
    //make an undoable change
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        agentList.remove(a);
        return true;
      }
      public boolean redo(){
        agentList.add(a);
        return true;
      }
    }, null);
    
    //inform the other agents about the potential increased visibility
    for(Agent other : agentList){
      other.addAgent(null);
    }
    
    //inform this agent about the increased visibility
    a.addProposition(null);
    
  }
  
  /** @fn void removeAgent( Agent a )
    *
    * @brief Removes a new agent to the group if they are in it
    * 
    * @param a The agent to be removed from the group, assumed to be non-null
    * 
    * @note This is a one-touch-all-done method, so it is safe to call it multiple times, and it ensures that the agent has removes the group from its own lists
    */
  public void removeAgent( final Agent a ){
    
    //if we don't have the agent, you can't remove it
    if( !agentList.contains(a) ) return;
    
    //remove the agent to the list
    agentList.remove(a);
    
    //make an undoable change
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        agentList.add(a);
        return true;
      }
      public boolean redo(){
        agentList.remove(a);
        return true;
      }
    }, null);
    
    //make sure it knows we've removed it
    a.removeGroup(this);
    
    //inform the other agents about the potential reduced visibility
    for(Agent other : agentList){
      other.removeAgent(null);
    }
    
    //inform this agent about the potential reduced visibility
    a.removeProposition(null);
    a.removeAgent(null);
    
  }
  
  /** @fn Set<Agent> getAgentSet()
    *
    * @brief Returns a read-only set of all of the agents currently in the group
    */
  public Set<Agent> getAgentSet(){
    return new LinkedHashSet<Agent>(agentList);
  }
  
  /** @fn void addProposition( Proposition p )
    *
    * @brief Adds a proposition to the members of the current group
    * 
    * @param p The proposition to be added to the group, assumed to be non-null
    * 
    */
  public void addProposition( final Proposition p ){
    if(propList.contains(p)) return;
    propList.add(p);
    
    //make an undoable change
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        propList.remove(p);
        return true;
      }
      public boolean redo(){
        propList.add(p);
        return true;
      }
    }, null);
    
    for(Agent a : agentList){
      a.addProposition(p);
    }
    p.addGroup(this);
  }
  
  /** @fn void removeProposition( Proposition p )
    *
    * @brief Removes a proposition from the members of the current group
    * 
    * @param p The proposition to be removed from the group, assumed to be non-null
    * 
    */
  public void removeProposition( final Proposition p ){
    if(!propList.contains(p)) return;
    propList.remove(p);
    
    //make an undoable change
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        propList.add(p);
        return true;
      }
      public boolean redo(){
        propList.remove(p);
        return true;
      }
    }, null);
    
    for(Agent a : agentList){
      a.removeProposition(p);
    }
    p.removeGroup(this);
  }
  
  /** @fn et<Proposition> getPropositionSet()
    *
    * @brief Gets a read-only list of propositions that are currently added to the simulation
    * 
    */
  public Set<Proposition> getPropositionSet(){
    return new LinkedHashSet<Proposition>(propList);
  }
  
  
  
  
  
  //the following functions have no meaningful interpretation
  public void addGroup(Group g){}
  public void removeGroup(Group g){}
  public Set<Group> getGroupSet(){
    return new LinkedHashSet<Group>();
  }
  public final void informSubInterface(){}
  
  public Component copy() {
    Group retVal = new Group("Copy of " + this.getName(), this.getSimulation());
    for(Agent a : this.agentList) retVal.addAgent(a);
    for(Proposition p : this.propList) retVal.addProposition(p);
    return retVal;
  }
  
  public String getMainComponentType(){
    return "Group";
  }
  
  public String getSubComponentType(){
    return "Group";
  }
  
};