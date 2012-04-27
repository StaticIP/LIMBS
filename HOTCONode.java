package LIMBS;

import java.util.*;

public class HOTCONode {
  
  private boolean deletable;
  private HOTCOAgent owner;
  private Proposition base;
  private final long id;
  private String name;
  
  //interface display values
  private double xPosition;
  private double yPosition;
  
  //computational values
  private double decay = 0.01;
  
  private double emotionalValence;
  private double emotionalIntensity;
  private double emotionalPotency;
  private double activation;
  
  private double initialEmotionalValence;
  private double initialEmotionalIntensity;
  private double initialEmotionalPotency;
  private double initialActivation;
  
  //intermediate computational values
  private double inputActivation;
  private double inputEmotionalValence;
  private double inputEmotionalIntensity;
  private double inputEmotionalPotency;
  private double numInputCogConnections;
  private double numInputEmoConnections;
  
  private double activationNet;
  private double valenceNet;
  private double intensityNet;
  private double potencyNet;
  private double numCogConnections;
  private double numEmoConnections;
  
  //computational methods
  public double getEmotionalValence(){
    return emotionalValence;
  }
  
  public void setEmotionalValence(final double v){
    if( v >= -1.0 && v <= 1.0 ){
      
      owner.getSimulation().registerChange( new Change(){
        private double currV = emotionalValence;
        public boolean undo(){
          emotionalValence = currV;
          return true;
        }
        public boolean redo(){
          currV = emotionalValence;
          emotionalValence = v;
          return true;
        }
      }, null);
      
      emotionalValence = v;
    }
  }
  
  public double getEmotionalIntensity(){
    return emotionalIntensity;
  }
  
  public void setEmotionalIntensity(final double v){
    if( v >= -1.0 && v <= 1.0 ){
      
      owner.getSimulation().registerChange( new Change(){
        private double currV = emotionalIntensity;
        public boolean undo(){
          emotionalIntensity = currV;
          return true;
        }
        public boolean redo(){
          currV = emotionalIntensity;
          emotionalIntensity = v;
          return true;
        }
      }, null);
      
      emotionalIntensity = v;
    }
  }
  
  public double getEmotionalPotency(){
    return emotionalPotency;
  }
  
  public void setEmotionalPotency(final double v){
    if( v >= -1.0 && v <= 1.0 ){
      
      owner.getSimulation().registerChange( new Change(){
        private double currV = emotionalPotency;
        public boolean undo(){
          emotionalPotency = currV;
          return true;
        }
        public boolean redo(){
          currV = emotionalPotency;
          emotionalPotency = v;
          return true;
        }
      }, null);
      
      emotionalPotency = v;
    }
  }
  
  public double getActivation(){
    return activation;
  }
  
  public void setActivation(final double v){
    if( v >= -1.0 && v <= 1.0 ){
      
      owner.getSimulation().registerChange( new Change(){
        private double currV = activation;
        public boolean undo(){
          activation = currV;
          return true;
        }
        public boolean redo(){
          currV = activation;
          activation = v;
          return true;
        }
      }, null);
      
      activation = v;
    }
  }
  
  public void consider( HOTCONode n, double weight ){
    double currentInputActivation = weight * Math.abs(n.getActivation());
    activationNet += weight * n.getActivation();
    valenceNet += currentInputActivation * n.getEmotionalValence();
    intensityNet += currentInputActivation * n.getEmotionalIntensity();
    potencyNet += currentInputActivation * n.getEmotionalPotency();
    numCogConnections += Math.abs(weight);
    numEmoConnections += Math.abs(weight);
  }

  public void initialize(){
    activation = initialActivation;
    emotionalValence = initialEmotionalValence;
    emotionalIntensity = initialEmotionalIntensity;
    emotionalPotency = initialEmotionalPotency;
    activationNet = 0;
    valenceNet = 0;
    intensityNet = 0;
    potencyNet = 0;
    numCogConnections = 0;
    numEmoConnections = 0;
    inputActivation = 0;
    inputEmotionalValence = 0;
    inputEmotionalIntensity = 0;
    inputEmotionalPotency = 0;
    numInputCogConnections = 0;
    numInputEmoConnections = 0;
  }
  
  public void startSingleIteration(){
    activationNet = inputActivation;
    valenceNet = inputEmotionalValence;
    intensityNet = inputEmotionalIntensity;
    potencyNet = inputEmotionalPotency;
    numCogConnections = numInputCogConnections;
    numEmoConnections = numInputEmoConnections;
  }
  
  public void setInputCognitiveValues(double inputActivation, double weight){
    this.inputActivation = weight*inputActivation;
    numInputCogConnections = Math.abs(weight);
  }
  
  public void setInputEmotionalValues(double inputValence, double inputIntensity, double inputPotency, double weight){
    inputEmotionalValence = weight*inputValence;
    inputEmotionalIntensity = weight*inputIntensity;
    inputEmotionalPotency = weight*inputPotency;
    numInputEmoConnections = Math.abs(weight);
  }
  
  public void incrementInputCognitiveValues(double inputActivation, double weight){
    this.inputActivation += weight*inputActivation;
    numInputCogConnections += Math.abs(weight);
  }
  
  public void incrementInputEmotionalValues(double inputValence, double inputIntensity, double inputPotency, double weight){
    inputEmotionalValence += weight*inputValence;
    inputEmotionalIntensity += weight*inputIntensity;
    inputEmotionalPotency += weight*inputPotency;
    numInputEmoConnections += Math.abs(weight);
  }
  
  public double finishSingleIteration(double agentEmotionalValence, double agentEmotionalIntensity, double agentEmotionalPotency, double receptiveness){
    
    //save the old values if necessary
    double oldActivation = activation;
    double oldValence = emotionalValence;
    double oldIntensity = emotionalIntensity;
    double oldPotency = emotionalPotency;
    
    //take a modified average of the input (doesn't consider input value, allowing for offset)
    if(numEmoConnections != 0.0) valenceNet /= (double) numEmoConnections;
    if(numEmoConnections != 0.0) intensityNet /= (double) numEmoConnections;
    if(numEmoConnections != 0.0) potencyNet /= (double) numEmoConnections;

    
    //apply the valence updating equations
    if( numEmoConnections != 0.0 ){
      if( valenceNet > 0.0 )
        emotionalValence = ( 1.0 - decay ) * emotionalValence + valenceNet * ( 1.0 - emotionalValence );
      else
        emotionalValence = emotionalValence + valenceNet * ( 1.0 + emotionalValence );
    }
  
    //apply the intensity updating equations
    if( numEmoConnections != 0.0 ){
      if( intensityNet > 0.0 )
        emotionalIntensity = ( 1.0 - decay ) * emotionalIntensity + intensityNet * ( 1.0 - emotionalIntensity );
      else
        emotionalIntensity = emotionalIntensity + intensityNet * ( 1.0 + emotionalIntensity );
    }
    
    //apply the potency updating equations
    if( numEmoConnections != 0.0 ){
      if( potencyNet > 0.0 )
        emotionalPotency = ( 1.0 - decay ) * emotionalPotency + potencyNet * ( 1.0 - emotionalPotency );
      else
        emotionalPotency = emotionalPotency + potencyNet * ( 1.0 + emotionalPotency );
    }
        
    //calculate a validation factor (how invalid a state is with the entire agent's emotion)
    double validation = (owner.queryEmotionValence() / 3.0) * ( 4.0 / (1.0 + Math.exp( -2.0 * emotionalValence + owner.queryEmotionValence())) - 2.0) +
      (owner.queryEmotionIntensity() / 3.0) * ( 4.0 / (1.0 + Math.exp( -2.0 * emotionalIntensity + owner.queryEmotionIntensity())) - 2.0) +
      (owner.queryEmotionPotency() / 3.0) * ( 4.0 / (1.0 + Math.exp( -2.0 * emotionalPotency + owner.queryEmotionPotency())) - 2.0);
    activationNet = (activationNet + validation) / (double) (numCogConnections+Math.abs(validation));
         
    //apply the activation updating equations
    if( activationNet > 0.0 )
      activation = ( 1.0 - decay ) * activation + activationNet * ( 1.0 - activation );
    else
      activation = activation + activationNet * ( 1.0 + activation );
    
    //reset the accumulators
    activationNet = 0;
    valenceNet = 0;
    intensityNet = 0;
    potencyNet = 0;
    numCogConnections = 0;
    numEmoConnections = 0;
    
    //find the differences in each component
    oldActivation -= activation;
    oldValence -= emotionalValence;
    oldIntensity -= emotionalIntensity;
    oldPotency -= emotionalPotency;
    
    //return a Pythagorean measure of the difference between the consecutive states
    double retErr = Math.sqrt(oldActivation*oldActivation + oldValence*oldValence + oldIntensity*oldIntensity + oldPotency*oldPotency);
    return retErr;
  }
  
  public HOTCONode(){
    //Required for rebuilding from XML -John
    id = -1;
  }
  
  public HOTCONode( HOTCOAgent owner, boolean del, Proposition p){
    deletable = del;
    if( !deletable ){
      base = p;
    }else{
      base = null;
    }
    activation = 0;
    initialActivation = 0;
    emotionalValence = 0;
    initialEmotionalValence = 0;
    emotionalIntensity = 0;
    initialEmotionalIntensity = 0;
    emotionalPotency = 0;
    initialEmotionalPotency = 0;
    
    //initialize to a random position for now
    xPosition = Math.random()*100;
    yPosition = Math.random()*100;
    
    this.owner = owner;
    id = owner.getNextNodeID();
    
    //give the node a default name
    if( base == null )
      name = "Node #" + id;
    
  }
  
  public String getName(){
    if( base == null ) return name;
    else return base.getName();
  }
  
  public void setName(String n){
    if( n != null && base == null ) name = n;
  }
  
  public Proposition getBase() {
    return base;
  }
  
  public double getInitialActivation(){
    return initialActivation;
  }
  
  public void setInitialActivation(final double v){
    final double oldActivation = initialActivation;
    if( v >= -1.0 && v <= 1.0 ){
      initialActivation = v;
      owner.getSimulation().registerChange( new Change(){
        public boolean undo(){
          initialActivation = oldActivation;
          return true;
        }
        public boolean redo(){
          initialActivation = v;
          return true;
        }
      }, null);
    }
  }
  
  public double getInitialEmotionalValence(){
    return initialEmotionalValence;
  }
  
  public void setInitialEmotionalValence(final double v){
    final double oldValence = initialEmotionalValence;
    if( v >= -1.0 && v <= 1.0 ){
      initialEmotionalValence = v;
      owner.getSimulation().registerChange( new Change(){
        public boolean undo(){
          initialEmotionalValence = oldValence;
          return true;
        }
        public boolean redo(){
          initialEmotionalValence = v;
          return true;
        }
      }, null);
    }
  }
  
  public double getInitialEmotionalIntensity(){
    return initialEmotionalIntensity;
  }
  
  public void setInitialEmotionalIntensity(final double v){
    final double oldIntensity = initialEmotionalIntensity;
    if( v >= -1.0 && v <= 1.0 ){
      initialEmotionalIntensity = v;
      owner.getSimulation().registerChange( new Change(){
        public boolean undo(){
          initialEmotionalIntensity = oldIntensity;
          return true;
        }
        public boolean redo(){
          initialEmotionalIntensity = v;
          return true;
        }
      }, null);
    }
  }
  
  public double getInitialEmotionalPotency(){
    return initialEmotionalPotency;
  }
  
  public void setInitialEmotionalPotency(final double v){
    final double oldPotency = initialEmotionalPotency;
    if( v >= -1.0 && v <= 1.0 ){
      initialEmotionalPotency = v;
      owner.getSimulation().registerChange( new Change(){
        public boolean undo(){
          initialEmotionalPotency = oldPotency;
          return true;
        }
        public boolean redo(){
          initialEmotionalPotency = v;
          return true;
        }
      }, null);
    }
  }
  
  public long getId(){
    return id;
  }
  
  public boolean isDeletable(){
    return deletable;
  }
  
  public void setPosition(final double xPos, final double yPos){
    
    owner.getSimulation().registerChange( new Change(){
      private final double xP = xPosition;
      private final double yP = yPosition;
      public boolean undo(){
        xPosition = xP;
        yPosition = yP;
        return true;
      }
      public boolean redo(){
        xPosition = xPos;
        yPosition = yPos;
        return true;
      }
    }, null);
    
    xPosition = xPos;
    yPosition = yPos;
  }
  
  public double getXPosition(){
    return xPosition;
  }
  
  public double getYPosition(){
    return yPosition;
  }
  
  public HOTCONode copy(HOTCOAgent owner){
    
    //create an initial blank node with equivalent position
    HOTCONode retNode = new HOTCONode( owner, this.deletable, this.base);
    retNode.name = new String( this.name );
    retNode.cloneValues(this);
    
    return retNode;
  }
  
  public void cloneValues(HOTCONode n){
    
    //copy over the position
    this.xPosition = n.xPosition;
    this.yPosition = n.yPosition;
    
    //copy over the current activation value and emotional transfer
    this.emotionalValence = n.emotionalValence;
    this.emotionalIntensity = n.emotionalIntensity;
    this.emotionalPotency = n.emotionalPotency;
    this.activation = n.activation;
  
    //copy over the inital activation value and emotional vector
    this.initialEmotionalValence = n.initialEmotionalValence;
    this.initialEmotionalIntensity = n.initialEmotionalIntensity;
    this.initialEmotionalPotency = n.initialEmotionalPotency;
    this.initialActivation = n.initialActivation;
    
  }
  
};