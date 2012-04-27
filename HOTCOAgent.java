package LIMBS;

import java.util.*;

public class HOTCOAgent extends Agent {
  
  private double emotionalValence;
  private double emotionalIntensity;
  private double emotionalPotency;
  private double cognitiveThreshold = 0.01;
  private double receptiveness = 1.0;
  
  private Set<HOTCONode> neurons;
  private Set<HOTCONode> propNeurons;
  private Set<HOTCOLink> synapses;
  
  private Set<Agent> empathyLinks;
  private Set<Agent> contagionLinks;
  private Set<Agent> altruismLinks;
  
  private Set<HOTCOCommunicationRule> communicationRules;
  
  private long linkID;
  private long nodeID;
  
  public AgentInterface getAgentLevelInterface(){
    return new HOTCOAgentInterface(this);
  }
  
  public HOTCOAgent(String n, Simulation s){
    super(n, s);
    propNeurons = new LinkedHashSet<HOTCONode>();
    neurons = new LinkedHashSet<HOTCONode>();
    synapses = new LinkedHashSet<HOTCOLink>();
    empathyLinks = new LinkedHashSet<Agent>();
    contagionLinks = new LinkedHashSet<Agent>();
    altruismLinks = new LinkedHashSet<Agent>();
    communicationRules = new LinkedHashSet<HOTCOCommunicationRule>();
    linkID = 0;
    nodeID = 0;
  }
  
  protected void destruct(){
    while( !neurons.isEmpty() ) removeNode( neurons.iterator().next() );
    while( !synapses.isEmpty() ) removeLink( synapses.iterator().next() );
  }
  
  //methods that must be overridden (internal)
  
  public boolean isUtility(){
    return false;
  }
  
  public void setup(){
    
    //initialize the proposition neurons
    Iterator<HOTCONode> init = propNeurons.iterator();
    while(init.hasNext()){
      init.next().initialize();
    }
    
    //initialize the rest of the neurons
    init = neurons.iterator();
    while(init.hasNext()){
      init.next().initialize();
    }
  }
  
  public void iterateThroughQueue(){
    
    //clear the inputs for the proposition nodes
    Iterator<HOTCONode> init = propNeurons.iterator();
    while(init.hasNext()){
      HOTCONode currNode = init.next();
      currNode.setInputCognitiveValues(0,0);
      currNode.setInputEmotionalValues(0,0,0,0);
    }
    
    //apply the new inputs for the proposition nodes
    while(!messageQueue.isEmpty()){
      Message m = messageQueue.poll();
      while(m.hasNext()){
        Proposition prop = m.nextItem();
        queryProposition(prop);
        
        //apply the activation change
        currentItem.incrementInputCognitiveValues(m.getPropositionalActivation(), receptiveness);
        
        //apply the emotion rules
        double weight = receptiveness;
        double inputPropositionalValence = 0;
        double inputPropositionalIntensity = 0;
        double inputPropositionalPotency = 0;
        if(empathyLinks.contains(m.getSender()) && prop.getType() == 0 ){
          inputPropositionalValence = m.getPropositionalValence();
          inputPropositionalIntensity = m.getPropositionalIntensity();
          inputPropositionalPotency = m.getPropositionalPotency();
        }else if(contagionLinks.contains(m.getSender()) && prop.getType() == 1 ){
          inputPropositionalValence = m.getPropositionalValence();
          inputPropositionalIntensity = m.getPropositionalIntensity();
          inputPropositionalPotency = m.getPropositionalPotency();
        }else if(altruismLinks.contains(m.getSender())&& prop.getType() == 2 ){
          inputPropositionalValence = m.getPropositionalValence();
          inputPropositionalIntensity = m.getPropositionalIntensity();
          inputPropositionalPotency = m.getPropositionalPotency();
        }
        currentItem.incrementInputEmotionalValues(inputPropositionalValence, inputPropositionalIntensity, inputPropositionalPotency, weight);
        
      }
      m.burnMessage();
    }
    
    //initialize the rest of the neurons
    init = neurons.iterator();
    while(init.hasNext()){
      HOTCONode currNode = init.next();
      currNode.setInputCognitiveValues(0,0);
      currNode.setInputEmotionalValues(0,0,0,0);
    }
    
    //perform the constraint satisfaction process
    double maxError = 4;
    while( maxError > cognitiveThreshold ){
      maxError = 0;
      
      //initialize the single iteration
      for(HOTCONode n : propNeurons )
        n.startSingleIteration();
      
      //perform a single iteration
      for(HOTCOLink link : synapses ){
        (link.getSideOne()).consider( link.getSideTwo(), link.getWeight() );
        (link.getSideTwo()).consider( link.getSideOne(), link.getWeight() );
      }
      
      //recalculate the overall emotion
      recalculateEmotion();
      
      //work through the proposition neurons trying to find largest error
      for(HOTCONode n : propNeurons ){
        double singleError = n.finishSingleIteration(emotionalValence, emotionalIntensity, emotionalPotency, receptiveness);
        if(singleError > maxError) maxError = singleError;
      }
      
      //work through the separate neurons trying to find largest error
      for(HOTCONode n : neurons ){
        double singleError = n.finishSingleIteration(emotionalValence, emotionalIntensity, emotionalPotency, 0.0);
        if(singleError > maxError) maxError = singleError;
      }
    }
    
  }
  
  public Set<HOTCOCommunicationRule> getCommunicationRules(){
    return communicationRules;
  }
  
  public void addCommunicationRule(HOTCOCommunicationRule r){
    communicationRules.add(r);
  }
  
  public void removeCommunicationRule(HOTCOCommunicationRule r){
    communicationRules.remove(r);
  }
  
  public Set<Agent> getAltruismList(){
    return altruismLinks;
  }
  
  public void addAgentToAltruismList(final Agent a){
    if( !getAgentSet().contains(a) || altruismLinks.contains(a) ) return;
    altruismLinks.add(a);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        altruismLinks.remove(a);
        return true;
      }
      public boolean redo(){
        altruismLinks.add(a);
        return true;
      }
    }, null);
    
  }
  
  public void removeAgentFromAltruismList(final Agent a){
    if( !altruismLinks.contains(a) ) return;
    altruismLinks.remove(a);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        altruismLinks.add(a);
        return true;
      }
      public boolean redo(){
        altruismLinks.remove(a);
        return true;
      }
    }, null);
  }
  
  public Set<Agent> getContagionList(){
    return contagionLinks;
  }
  
  public void addAgentToContagionList(final Agent a){
    if( !getAgentSet().contains(a) || contagionLinks.contains(a) ) return;
    contagionLinks.add(a);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        contagionLinks.remove(a);
        return true;
      }
      public boolean redo(){
        contagionLinks.add(a);
        return true;
      }
    }, null);
    
  }
  
  public void removeAgentFromContagionList(final Agent a){
    if( !contagionLinks.contains(a) ) return;
    contagionLinks.remove(a);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        contagionLinks.add(a);
        return true;
      }
      public boolean redo(){
        contagionLinks.remove(a);
        return true;
      }
    }, null);
  }
  
  public Set<Agent> getEmpathyList(){
    return empathyLinks;
  }
  
  public void addAgentToEmpathyList(final Agent a){
    if( !getAgentSet().contains(a) || empathyLinks.contains(a) ) return;
    empathyLinks.add(a);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        empathyLinks.remove(a);
        return true;
      }
      public boolean redo(){
        empathyLinks.add(a);
        return true;
      }
    }, null);
  }
  
  public void removeAgentFromEmpathyList(final Agent a){
    if( !empathyLinks.contains(a) ) return;
    empathyLinks.remove(a);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        empathyLinks.add(a);
        return true;
      }
      public boolean redo(){
        empathyLinks.remove(a);
        return true;
      }
    }, null);
    
  }
  
  public void communicate(){
    
    //if we have no people to communicate with, return
    Set<Agent> communicatorsList = getAgentSet();
    if( communicatorsList.size() == 0) return;
    
    //we need to chose a receiver for the message
    Vector<Agent> receiver = new Vector<Agent>();
    receiver.clear();
    
    //if we have rules, iterate through them
    for( HOTCOCommunicationRule r : communicationRules ){
      if( r.areCriteriaFulfilled() ){
        receiver.add( r.getTargetAgent() );
      }
    }
    
    //if no receiver is chosen, choose a random one
    //if( receiver.isEmpty() ){
    //  int numToMessage = (int) (( (double) communicatorsList.size() ) * Math.random());
    //  Iterator<Agent> it = communicatorsList.iterator();
    //  while(numToMessage > 0){
    //    numToMessage--;
    //    it.next();
    //  }
    //  receiver.add( it.next() );
    //}
    
    //iterate through the list, giving messages to the receivers
    for(Agent r : receiver){
      Message m = new Message(this, r);
    }
    
  }
  
  protected void addCommunicationAgentInternal( Agent a ){ }
  
  protected void removeCommunicationAgentInternal( Agent a ){
    
    //remove agent from emotional transmission infrastructure
    removeAgentFromEmpathyList(a);
    removeAgentFromContagionList(a);
    removeAgentFromAltruismList(a);
    
    //remove agent from communication structure
    final Set<HOTCOCommunicationRule> removal = new LinkedHashSet<HOTCOCommunicationRule>();
    for( HOTCOCommunicationRule r : communicationRules ){
      if( a.equals(r.getTargetAgent()) ){
        removal.add(r);
      }
    }
    communicationRules.removeAll(removal);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        communicationRules.addAll(removal);
        return true;
      }
      public boolean redo(){
        communicationRules.removeAll(removal);
        return true;
      }
    }, null);
    
  }
  
  protected void addPropositionInternal( Proposition p ){
    
    //find out if we already have this proposition in node form, (end method if we do)
    boolean flag = false;
    for( HOTCONode n : propNeurons ){
      if( n.getBase() != null && n.getBase().equals(p) ){
        return;
      }
    }
    
    //else, we need to create a new neuron for this proposition!
    final HOTCONode newNeuron = new HOTCONode(this, false, p);
    propNeurons.add(newNeuron);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        propNeurons.remove(newNeuron);
        return true;
      }
      public boolean redo(){
        propNeurons.add(newNeuron);
        return true;
      }
    }, null);
    
  }
  
  protected void removePropositionInternal( Proposition p ){
    
    //find the node associate with the proposition
    HOTCONode nodeToRemove = null;
    Iterator<HOTCONode> it = propNeurons.iterator();
    while(it.hasNext()){
      HOTCONode n = it.next();
      if( n.getBase() == p ){
        nodeToRemove = n;
      }
    }
    
    //if we find the node associated with the proposition, remove it
    if(nodeToRemove != null){
      propNeurons.remove(nodeToRemove);
      final HOTCONode propNode = nodeToRemove;
      
      //register a change with the change manager
      this.getSimulation().registerChange( new Change(){
        public boolean undo(){
          propNeurons.add(propNode);
          return true;
        }
        public boolean redo(){
          propNeurons.remove(propNode);
          return true;
        }
      }, null);
      
    }
    
    //remove any links including the node
    final Set<HOTCOLink> linksToRemove = new LinkedHashSet<HOTCOLink>();
    for(HOTCOLink l : synapses){
      if(l.includes(nodeToRemove)){
        linksToRemove.add(l);
      }
    }
    
    //remove the links from the set of links
    synapses.removeAll(linksToRemove);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        synapses.addAll(linksToRemove);
        return true;
      }
      public boolean redo(){
        synapses.removeAll(linksToRemove);
        return true;
      }
    }, null);
  }
  
  //methods for retrieving information (internal and UI)
  HOTCONode currentItem = null;
  public void queryProposition( Proposition prop ){
    
    //find the queried proposition
    currentItem = mapPropositionToNode(prop);
    
    //should not reach here
    if(currentItem == null) System.err.println("REACHED INVALID POINT. MARKER 1.");
  }
  
  private HOTCONode mapPropositionToNode( Proposition prop ){
    
    //set the flag/return value
    HOTCONode retItem = null;
    
    //find the node associate with the proposition
    for(HOTCONode n : propNeurons)
      if( n.getBase() == prop ) retItem = n;
    
    //return whatever we find
    return retItem;
  }
  
  public double getPropositionalActivation(){
    return currentItem.getActivation();
  }
  
  public double getPropositionalValence(){
    return currentItem.getEmotionalValence();
  }
  
  public double getPropositionalIntensity(){
    return currentItem.getEmotionalIntensity();
  }
  
  public double getPropositionalPotency(){
    return currentItem.getEmotionalPotency();
  }
  
  public void completeQuery(){
    currentItem = null;
  }
  
  public double queryEmotionValence(){
    return emotionalValence;
  }
  
  public double queryEmotionIntensity(){
    return emotionalIntensity;
  }
  
  public double queryEmotionPotency(){
    return emotionalPotency;
  }
  
  public void recalculateEmotion(){
    
    //provide accumulators 
    double sumEmotionalValence = 0;
    double sumEmotionalIntensity = 0;
    double sumEmotionalPotency = 0;
    double sumDivisor = 0;
    
    //work through the proposition neurons
    for(HOTCONode n : propNeurons ){
      sumEmotionalValence += n.getActivation() * n.getEmotionalValence();
      sumEmotionalIntensity += n.getActivation() * n.getEmotionalIntensity();
      sumEmotionalPotency += n.getActivation() * n.getEmotionalPotency();
      sumDivisor += Math.abs( n.getActivation() );
    }
    
    //work through the separate neurons
    for(HOTCONode n : neurons ){
      sumEmotionalValence += n.getActivation() * n.getEmotionalValence();
      sumEmotionalIntensity += n.getActivation() * n.getEmotionalIntensity();
      sumEmotionalPotency += n.getActivation() * n.getEmotionalPotency();
      sumDivisor += Math.abs( n.getActivation() );
    }
    
    //temper the emotional values
    if( sumDivisor > 0.0 ){
      emotionalValence = sumEmotionalValence / sumDivisor;
      emotionalIntensity = sumEmotionalIntensity / sumDivisor;
      emotionalPotency = sumEmotionalPotency / sumDivisor;
    }else{
      emotionalValence = 0.0;
      emotionalIntensity = 0.0;
      emotionalPotency = 0.0;
    }
    
    //notify any listeners of the new emotional status
    this.notifyListener();
  }
  
  public void addNode(final HOTCONode n){
    if( neurons.contains(n) ) return;
    neurons.add(n);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        neurons.remove(n);
        return true;
      }
      public boolean redo(){
        neurons.add(n);
        return true;
      }
    }, null);
  }
  
  public void removeNode(final HOTCONode n){
    if( !neurons.contains(n) ) return;
    
    if(n.isDeletable()){
      //remove from the set of nodes
      neurons.remove(n);
      
      //register a change with the change manager
      this.getSimulation().registerChange( new Change(){
        public boolean undo(){
          neurons.add(n);
          return true;
        }
        public boolean redo(){
          neurons.remove(n);
          return true;
        }
      }, null);
      
      //remove any links including that node
      Iterator<HOTCOLink> linkIt = synapses.iterator();
      while(linkIt.hasNext()){
        HOTCOLink link = linkIt.next();
        if(link.includes(n)){
          removeLink(link);
          linkIt = synapses.iterator();
        }
      }
      
      // remove from communication rules
      Iterator<HOTCOCommunicationRule> ruleIt = communicationRules.iterator();
      while (ruleIt.hasNext()) {
        HOTCOCommunicationRule rule = ruleIt.next();
        if (rule.getCriteria().contains(n)) {
          rule.removeCriterion(n);
          ruleIt = communicationRules.iterator();
        }
      }
      
    }
  }
  
  public void addLink(final HOTCOLink s){
    if( synapses.contains(s) ) return;
    synapses.add(s);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        synapses.remove(s);
        return true;
      }
      public boolean redo(){
        synapses.add(s);
        return true;
      }
    }, null);
  }
  
  public void removeLink(final HOTCOLink s){
    if( !synapses.contains(s) ) return;
    synapses.remove(s);
    
    //register a change with the change manager
    this.getSimulation().registerChange( new Change(){
      public boolean undo(){
        synapses.add(s);
        return true;
      }
      public boolean redo(){
        synapses.remove(s);
        return true;
      }
    }, null);
  }
  
  public Set<HOTCONode> getNodes(){
    Set<HOTCONode> retval = new LinkedHashSet<HOTCONode>();
    retval.addAll(neurons);
    retval.addAll(propNeurons);
    return retval;
  }
  
  public Set<HOTCOLink> getLinks(){
    return synapses;
  }
  
  public long getNextLinkID(){
    return linkID++;
  }
  
  public long getNextNodeID(){
    return nodeID++;
  }
  
  public String toString(){
    String retVal = " " + this.getId() + " " + this.getName() + "\n";
    Iterator<HOTCONode> it = propNeurons.iterator();
    while(it.hasNext()){
      HOTCONode n = it.next();
      retVal += "\t" + n.getId() + ":\t" + n.getActivation() + "\t\t" + n.getEmotionalValence() + "\t\t" + n.getEmotionalIntensity()+ "\t\t" + n.getEmotionalPotency();
      if(n.getBase() != null){
        retVal += "\t" + n.getBase().getName() + " " + n.getBase().getType();
      }else{
        retVal += "\t" + n.getName();
      }
      retVal += "\n";
    }
    it = neurons.iterator();
    while(it.hasNext()){
      HOTCONode n = it.next();
      retVal += "\t" + n.getId() + ":\t" + n.getActivation() + "\t\t" + n.getEmotionalValence() + "\t\t" + n.getEmotionalIntensity()+ "\t\t" + n.getEmotionalPotency();
      if(n.getBase() != null){
        retVal += "\t" + n.getBase().getName() + " " + n.getBase().getType();
      }
      retVal += "\n";
    }
    return retVal;
  }
  
  @Override
  public Component copy() {
    
    //create a replica of the basic agent (should automatically generate the prop nodes)
    HOTCOAgent retVal = new HOTCOAgent( "Copy of " + this.getName(), this.getSimulation() );
    for(Group g : this.getGroupSet()){
      retVal.addGroup(g);
    }
    retVal.addAgent(null);
    retVal.addProposition(null);
    
    //copy over internal variables
    retVal.receptiveness = receptiveness;
    retVal.cognitiveThreshold = 0.01;
    retVal.empathyLinks = new LinkedHashSet<Agent>(empathyLinks);
    retVal.contagionLinks = new LinkedHashSet<Agent>(contagionLinks);
    retVal.altruismLinks = new LinkedHashSet<Agent>(altruismLinks);
    retVal.linkID = linkID;
    retVal.nodeID = nodeID;
    retVal.emotionalValence = emotionalValence;
    retVal.emotionalIntensity = emotionalIntensity;
    retVal.emotionalPotency = emotionalPotency;
      
    //put all of the auto-generated nodes into the mapping and update their locations
    Map<Long, HOTCONode> mapping = new HashMap<Long, HOTCONode>();
    for(HOTCONode n : this.propNeurons){
      HOTCONode newPropNeuron = retVal.mapPropositionToNode(n.getBase());
      mapping.put( new Long(n.getId()), newPropNeuron );
      
      //update position and activations
      newPropNeuron.cloneValues(n);
    }
    
    //create copies of all of the neurons adding the pair to the map
    for(HOTCONode n : this.neurons){
      HOTCONode newNeuron = n.copy(retVal);
      retVal.neurons.add(newNeuron);
      mapping.put( new Long(n.getId()), newNeuron );
    }
    
    //create nodes via mapping to the new nodes
    for(HOTCOLink l : this.synapses){
      HOTCONode side1 = mapping.get(new Long(l.getSideOne().getId()) );
      HOTCONode side2 = mapping.get(new Long(l.getSideTwo().getId()) );
      HOTCOLink newLink = new HOTCOLink(retVal, side1, side2, l.getWeight());
      retVal.synapses.add(newLink);
    }
    
    //copy over communication rules
    for( HOTCOCommunicationRule rule : communicationRules ){
      HOTCOCommunicationRule tempRule = new HOTCOCommunicationRule( retVal, rule.getTargetAgent() );
      tempRule.setName( rule.getName() );
      tempRule.setThreshold( rule.getThreshold() );
      for( HOTCONode n : rule.getCriteria() )
        tempRule.addCriterion( mapping.get( new Long( n.getId() ) ) );
      retVal.communicationRules.add( tempRule );
    }
    
    return retVal;
  }
  
  public double getReceptiveness(){
    return receptiveness;
  }
  
  public void setReceptiveness(double r){
    if( r >= 0.0 && r <= 1.0 ) receptiveness = r;
  }
  
  public String getSubComponentType(){
    return "HOTCO Agent";
  }
};