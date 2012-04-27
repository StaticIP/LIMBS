package LIMBS;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.io.Serializable;

abstract class Agent extends Component implements Runnable {
  
  //social structure information
  private Set<Group> groupList = null;
  private Set<Agent> agentList = null;
  private Set<Proposition> propList = null;
  
  //simulation information
  protected PriorityQueue<Message> messageQueue = null;
  
  /** @fn Agent(String n)
   * 
   * @brief Constructor which automatically adds self to the simulation's master lists
   *
   * @param n The name of the new agent (assumed to be non-null), used primarily for human interaction components
   */
  public Agent(String n, Simulation s){
    super(n, s);
    
    //initialize the containers
    messageQueue = new PriorityQueue<Message>(16, Message.getComparator());
    groupList = new LinkedHashSet<Group>();
    agentList = new LinkedHashSet<Agent>();
    propList = new LinkedHashSet<Proposition>();
    messageQueue.clear();
    groupList.clear();
    
    //make the simulation aware of your prresence
    getSimulation().addAgent(this);
  }
  
  
  /** @brief Deconstructor for this item, removes all contacts and groups, then removes self from simulation
   * 
   */
  public final void delete(){
    
    //remove yourself from all groups
    while(!groupList.isEmpty()) groupList.iterator().next().removeAgent(this);
    
    //deconstruct the internals
    destruct();
    
    //remove self from simulation
    getSimulation().removeAgent(this);
  }
  
  
  
  /** @fn Set<Agent> getCommunicatorSet()
   *
   * @brief Returns a read-only set of agents which the current agent can communicate with
   */
  public final Set<Agent> getAgentSet(){
    return new LinkedHashSet<Agent>(agentList);
  }
  
  /** @fn void addGroup( Group g )
   *
   * @brief Links the agent with the provided group
   * 
   * @note This function is a one-touch-all-done method, so it will also force the group to add the agent to its own lists
   */
  public final void addGroup( final Group g ){
    
    //if we already have the group, don't go through the work of adding it again
    if( groupList.contains(g) ) return;
    groupList.add(g);
    
    //register a change with the change manager
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        groupList.remove(g);
        addAgent(null);
        addProposition(null);
        return true;
      }
      public boolean redo(){
        groupList.add(g);
        addAgent(null);
        addProposition(null);
        return true;
      }
    }, null);
    
    //make sure the group knows we're a member
    g.addAgent(this);
    
    //inform the interface layer
    this.notifyListener();
    
  }
  
  /** @fn void removeGroup( Group g )
   *
   * @brief Removes the agent from the provided group
   * 
   * @note This function is a one-touch-all-done method, so it will also force the group to remove the agent from its own lists
   */
  public final void removeGroup( final Group g ){
    
    //if we don't have the group, you can't remove it
    if( !groupList.contains(g) ) return;
    groupList.remove(g);
    
    //register a change with the change manager
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        groupList.add(g);
        addAgent(null);
        addProposition(null);
        return true;
      }
      public boolean redo(){
        groupList.remove(g);
        addAgent(null);
        addProposition(null);
        return true;
      }
    }, null);
    
    //make sure the group knows we've been removed
    g.removeAgent(this);
    
    //inform the interface layer
    this.notifyListener();
    
  }
  
  /** @fn Set<Group> getGroupSet()
   *
   * @brief Returns a read-only set of the groups that this agent currently belongs to
   */
  public final Set<Group> getGroupSet(){
    return new LinkedHashSet<Group>(groupList);
  }
  
  /** @fn void removeProposition( Proposition p )
   *
   * @brief Inform the agent that this proposition may have been removed from their view via some action
   * 
   * @param p The proposition possibly removed from view
   */
  public final void removeProposition( Proposition p ){
    
    //calculate the new proposition visibility
    Set<Proposition> presentGateways = new LinkedHashSet<Proposition>();
    for(Group g : groupList)
      presentGateways.addAll(g.getPropositionSet());
    Set<Proposition> removedGateways = new LinkedHashSet<Proposition>();
    removedGateways.addAll(propList);
    removedGateways.removeAll(presentGateways);
    
    //add any excess propositions to the set and inform the sublayers
    for( Proposition oldProp : removedGateways ){
      propList.remove(oldProp);
      removePropositionInternal(oldProp);
    }
    
    //if we have removed anything, inform the interface layer
    if( !removedGateways.isEmpty() )
      this.notifyListener();
  }
    
  /** @fn void addProposition( Proposition p )
   *
   * @brief Inform the agent that this proposition may have been inserted into their view via some action
   * 
   * @param p The proposition possibly inserted into view
   */
  public final void addProposition( Proposition p ){
    
    //calculate the new proposition visibility
    Set<Proposition> addedGateways = new LinkedHashSet<Proposition>();
    for(Group g : groupList)
      addedGateways.addAll(g.getPropositionSet());
    addedGateways.removeAll(propList);
    
    //add any new propositions to the set and inform the sublayers
    for( Proposition newProp : addedGateways ){
      propList.add(newProp);
      addPropositionInternal(newProp);
    }
    
    //if we have removed anything, inform the interface layer
    if( !addedGateways.isEmpty() )
      this.notifyListener();
  }
    
  /** @fn void removeAgent( Agent a )
   *
   * @brief Inform the agent that another agent may have been removed from their view via some action
   * 
   * @param a The agent possibly removed from view
   */
  public final void removeAgent( Agent a ){
    
    //calculate the new set of agents
    Set<Agent> presentContacts = new LinkedHashSet<Agent>();
    for(Group g : groupList)
      presentContacts.addAll(g.getAgentSet());
    presentContacts.remove(this);
    Set<Agent> removedContacts = new LinkedHashSet<Agent>();
    removedContacts.addAll(agentList);
    removedContacts.removeAll(presentContacts);
    removedContacts.remove(this);
    
    //remove any excess agents and inform the sublayer
    for( Agent oldAgent : removedContacts ){
      agentList.remove(oldAgent);
      removeCommunicationAgentInternal(oldAgent);
    }
    
    //if we have removed anything, inform the interface layer
    if( !removedContacts.isEmpty() )
      this.notifyListener();
    
  }
    
  /** @fn void addAgent( Agent a )
   *
   * @brief Inform the agent that this agent may have been inserted into their view via some action
   * 
   * @param a The agent possibly inserted into view
   */
  public final void addAgent( Agent a ){

    //calculate the new set of agents
    Set<Agent> addedContacts = new LinkedHashSet<Agent>();
    for(Group g : groupList)
      addedContacts.addAll(g.getAgentSet());
    addedContacts.removeAll(agentList);
    addedContacts.remove(this);
    
    //add any new agents and inform the sublayer
    for( Agent newAgent : addedContacts ){
      agentList.add(newAgent);
      addCommunicationAgentInternal(newAgent);
    }
    
    //if we have added anything, inform the interface layer
    if( !addedContacts.isEmpty() )
      this.notifyListener();
  }
  
  /** @fn Set<Proposition> getPropositionIntersection( Agent a )
   *
   * @brief Returns all propositions that the provided agent as well as the current agent both have knowledge of
   * 
   * @param a The agent who shares the returned groups with the current agent
   */
  public final Set<Proposition> getPropositionIntersection( Agent a ){
    
    //find the union of the agents propositions
    Set<Proposition> intersection = new LinkedHashSet<Proposition>();
    intersection.addAll(this.propList);
    intersection.addAll(a.propList);
    
    //find all props in A that are not in B and vice versa
    Set<Proposition> agent1PropsDisjoint = new LinkedHashSet<Proposition>();
    agent1PropsDisjoint.addAll(this.propList);
    agent1PropsDisjoint.removeAll(a.propList);
    Set<Proposition> agent2PropsDisjoint = new LinkedHashSet<Proposition>();
    agent2PropsDisjoint.addAll(a.propList);
    agent2PropsDisjoint.removeAll(this.propList);
    
    //take the union minus the disjoints to get the intersection
    intersection.removeAll(agent1PropsDisjoint);
    intersection.removeAll(agent2PropsDisjoint);
    
    return intersection;
  }
  
  /** @fn Set<Proposition> getPropositionSet
   *
   * @brief Returns all propositions that the provided agent has knowledge of
   */
  public final Set<Proposition> getPropositionSet(){
    return new LinkedHashSet<Proposition>(propList);
  }
  
  /** @fn Set<Proposition> void run()
   *
   * @brief Organizes the various events in a simulation including synchronizing with the other agents between computations
   * 
   * @note This method should only be called by the Simulation class
   */
  public void run() {
    communicate();
    getSimulation().synchronize();
    iterateThroughQueue();
    getSimulation().synchronize();
  }
  
  /** @fn AgentInterface getAgentLevelInterface(String newName)
   *
   * @brief Returns the interface for modifying this agent
   * 
   */
  abstract public AgentInterface getAgentLevelInterface();
  
  //methods that must be overridden (internal)
  public abstract void setup();
  public abstract void iterateThroughQueue();
  public abstract void communicate();
  protected abstract void addPropositionInternal( Proposition p );
  protected abstract void removePropositionInternal( Proposition p );
  protected abstract void addCommunicationAgentInternal( Agent a );
  protected abstract void removeCommunicationAgentInternal( Agent a );
  protected abstract void destruct();
  
  //methods for retrieving information (internal and UI)
  public abstract boolean isUtility();
  public abstract double queryEmotionValence();
  public abstract double queryEmotionIntensity();
  public abstract double queryEmotionPotency();
  
  //other required methods (internal)
  public abstract void queryProposition( Proposition prop );
  public abstract double getPropositionalActivation();
  public abstract double getPropositionalValence();
  public abstract double getPropositionalIntensity();
  public abstract double getPropositionalPotency();
  public abstract void completeQuery();
  
  /** @fn void addMessageToQueue(Message e)
   *
   * @brief Adds the provided message to the end of teh message queue maintained by the current agent and used to receive incoming communication from other agents
   * 
   * @note This method should only be called by other Agent classes during the communication phase of the simulation
   */
  public synchronized final void addMessageToQueue(Message e){
    try{
      messageQueue.add(e);
    }catch(Exception except){
      System.err.println("Queue insertion failure when " + e.getSender().getName() + " inserted into " + getName() + "'s queue.");
      System.err.println(except.toString());
      System.exit(1);
    }
  }
  
  public String getMainComponentType(){
    return "Agent";
  }
 
};