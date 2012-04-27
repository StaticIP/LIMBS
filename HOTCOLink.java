package LIMBS;

import java.util.*;

public class HOTCOLink {
  
  private HOTCONode sideA;
  private HOTCONode sideB;
  private double strength;
  private long id;
  private HOTCOAgent currAgent;
  
  
  public HOTCOLink(){
    //Required for rebuilding from XML -John
    id = -1;
  }
  public HOTCOLink( HOTCOAgent owner, HOTCONode a, HOTCONode b, double s){
    strength = s;
    sideA = a;
    sideB = b;
    
    // warning: unused ids in the case of creating duplicate links
    id = owner.getNextLinkID();
    currAgent = owner;
  }
  
  public boolean includes(HOTCONode n){
    return sideA == n || sideB == n;
  }
  
  public HOTCONode getSideOne(){
    return sideA;
  }
  
  public HOTCONode getSideTwo(){
    return sideB;
  }
  
  public double getWeight(){
    return strength;
  }
  
  public void setWeight(final double s) {
    if( s >= -1.0 && s <= 1.0 ){
      
      currAgent.getSimulation().registerChange( new Change(){
        private double currStrength = strength;
        public boolean undo(){
          strength = currStrength;
          return true;
        }
        public boolean redo(){
          currStrength = getWeight();
          strength = s;
          return true;
        }
      }, null);
      
      this.strength = s;
    }
  }
  
  /* Override hashcode() and equals() to redefine equality */
  
  @Override
  public int hashCode() {
    return 42 + sideA.hashCode() + sideB.hashCode();
  }
  
  @Override
  public boolean equals(Object other) {
    if (other == null)
      return false;
    if (other == this)
      return true;
    if (this.getClass() != other.getClass())
      return false;
    
    HOTCOLink otherLink = (HOTCOLink) other;
    if (otherLink.includes(this.sideA) && otherLink.includes(this.sideB))
      return true;
    
    return false;
  }
  
};