package LIMBS;

import java.util.*;

public final class Message {
 
  private static Comparator<Message> comparator = new Comparator<Message>(){
    public int compare(Message m1, Message m2){
      return (int) (m1.sender.getId() - m2.sender.getId());
    }
  };
  public static Comparator<Message> getComparator(){
    return comparator;
  }
  
  private Agent sender;
  
  private class TransferInformation {
    public double activation;
    public double valence;
    public double intensity;
    public double potency;
  };
  
  private Iterator<Proposition> pit;
  private Iterator<TransferInformation> vit;
  private Vector<Proposition> props;
  private Vector<TransferInformation> vals;
  private double currentPropActivation;
  private double currentPropValence;
  private double currentPropIntensity;
  private double currentPropPotency;
  
  public Message(Agent sender, Agent receiver){
    this.sender = sender;
    
    //populate a proposition/value vector
    Set<Proposition> inputProps = sender.getPropositionIntersection(receiver);
    props = new Vector<Proposition>();
    vals = new Vector<TransferInformation>();
    for(Proposition p : inputProps){
      props.add(p);
      TransferInformation temp = new TransferInformation();
      sender.queryProposition(p);
      temp.activation = sender.getPropositionalActivation();
      temp.valence = sender.getPropositionalValence();
      temp.intensity = sender.getPropositionalIntensity();
      temp.potency = sender.getPropositionalPotency();
      vals.add( temp );
    }
    
    //initialize the iterators
    currentPropActivation = 0;
    currentPropValence = 0;
    currentPropIntensity = 0;
    currentPropPotency = 0;
    pit = props.iterator();
    vit = vals.iterator();
    
    //deliver to recipient
    receiver.addMessageToQueue(this);
  }
  
  public Message(Agent sender, Group receiver){
    this.sender = sender;
    
    //populate a proposition/value vector
    Set<Proposition> inputProps = receiver.getPropositionSet();
    props = new Vector<Proposition>();
    vals = new Vector<TransferInformation>();
    Iterator<Proposition> inputPropIt = inputProps.iterator();
    while(inputPropIt.hasNext()){
      Proposition p = inputPropIt.next();
      props.add(p);
      sender.queryProposition(p);
      TransferInformation temp = new TransferInformation();
      temp.activation = sender.getPropositionalActivation();
      temp.valence = sender.getPropositionalValence();
      temp.intensity = sender.getPropositionalIntensity();
      temp.potency = sender.getPropositionalPotency();
      vals.add( temp );
    }
    
    //initialize the iterators
    currentPropActivation = 0;
    currentPropValence = 0;
    currentPropIntensity = 0;
    currentPropPotency = 0;
    pit = props.iterator();
    vit = vals.iterator();
    
    //clone the message and send to all recipients
    Iterator<Agent> recipients = receiver.getAgentSet().iterator();
    while(recipients.hasNext()){
      recipients.next().addMessageToQueue(new Message(this) );
    }
    
  }
  
  private Message(Message m){
    //set the parameters
    sender = m.sender;
    currentPropActivation = 0;
    currentPropValence = 0;
    currentPropIntensity = 0;
    currentPropPotency = 0;
    
    //populate the message (cheaply)
    props = new Vector<Proposition>();
    vals = new Vector<TransferInformation>();
    for(int i = m.props.size() - 1; i >= 0; i++){
      props.add( m.props.elementAt(i) );
      TransferInformation temp = new TransferInformation();
      temp.activation = m.vals.elementAt(i).activation;
      temp.valence = m.vals.elementAt(i).valence;
      temp.intensity = m.vals.elementAt(i).intensity;
      temp.potency = m.vals.elementAt(i).potency;
      vals.add(temp);
    }
    pit = props.iterator();
    vit = vals.iterator();
    
  }
  
  public Agent getSender(){
    return sender;
  }
  
  public boolean hasNext(){
    return pit.hasNext();
  }
  
  public Proposition nextItem(){
    if(pit.hasNext()){
      TransferInformation temp = vit.next();
      currentPropActivation = temp.activation;
      currentPropValence = temp.valence;
      currentPropIntensity = temp.intensity;
      currentPropPotency = temp.potency;
      return pit.next();
    }else{
      return null;
    }
  }
  
  public double getPropositionalActivation(){
    return currentPropActivation;
  }
  
  public double getPropositionalValence(){
    return currentPropValence;
  }
  
  public double getPropositionalIntensity(){
    return currentPropIntensity;
  }
  
  public double getPropositionalPotency(){
    return currentPropPotency;
  }
  
  public void burnMessage(){
    props.clear();
    vals.clear();
  }
  
};