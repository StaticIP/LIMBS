package LIMBS;

import java.util.*;

abstract class Utility extends Agent {
  
  public Utility(String n, Simulation s){
    super(n, s);
  }
  
  //methods for retrieving information (internal and UI)
  public final boolean isUtility(){
    return true;
  }
  
  public final double queryEmotionValence(){
    return 0.0;
  }
  
  public final double queryEmotionIntensity(){
    return 0.0;
  }
  
  public final double queryEmotionPotency(){
    return 0.0;
  }
  //other required methods (internal)  
  final public double getPropositionalValence(){
    return 0.0;
  }
  
  final public double getPropositionalIntensity(){
    return 0.0;
  }
  
  final public double getPropositionalPotency(){
    return 0.0;
  }
  
  final public String getMainComponentType(){
    return "Utility";
  }
}