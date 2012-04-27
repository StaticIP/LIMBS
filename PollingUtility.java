package LIMBS;

import java.util.*;
import java.math.*;
import java.io.*;

public class PollingUtility extends Utility {
  
  public PollingUtility(String n, Simulation s){
    super(n, s);
  }
  
  private long timeToFirstElection = 0;
  private long timeForVoting = 0;
  private long totalElectionTime = 0;
  
  private Set<Agent> constituency = new LinkedHashSet<Agent>();
  private Set<Agent> digests = new LinkedHashSet<Agent>();
  private Set<Proposition> issues = new LinkedHashSet<Proposition>();
  
  private Set<Agent> voted = new LinkedHashSet<Agent>();
  private Map<Proposition, Double> tally = new LinkedHashMap<Proposition, Double>();
  private Map<Proposition, Integer> castVotes = new LinkedHashMap<Proposition, Integer>();
  
  public void addAgentToConstituency( final Agent a ){
    if( constituency.contains(a) ) return;
    
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        constituency.remove(a);
        return true;
      }
      public boolean redo(){
        constituency.add(a);
        return true;
      }
    }, null);
    
    constituency.add(a);
  }
  
  public void removeAgentFromConstituency( final Agent a ){
    if( !constituency.contains(a) ) return;
    
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        constituency.add(a);
        return true;
      }
      public boolean redo(){
        constituency.remove(a);
        return true;
      }
    }, null);
    
    constituency.remove(a);
  }
  
  public Set<Agent> getConstituency( ){
    return new LinkedHashSet<Agent>( constituency );
  }
  
  public void addAgentToDigests( final Agent a ){
    if( digests.contains(a) ) return;
    
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        digests.remove(a);
        return true;
      }
      public boolean redo(){
        digests.add(a);
        return true;
      }
    }, null);
    
    digests.add(a);
  }
  
  public void removeAgentFromDigests( final Agent a ){
    if( !digests.contains(a) ) return;
    
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        digests.add(a);
        return true;
      }
      public boolean redo(){
        digests.remove(a);
        return true;
      }
    }, null);
    
    digests.remove(a);
  }
  
  public Set<Agent> getDigests( ){
    return new LinkedHashSet<Agent>( digests );
  }
  
  public void addPropositionToIssues( final Proposition p ){
    if( issues.contains(p) ) return;
    
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        issues.remove(p);
        return true;
      }
      public boolean redo(){
        issues.add(p);
        return true;
      }
    }, null);
    
    issues.add(p);
  }
  
  public void removePropositionFromIssues( final Proposition p ){
    if( !issues.contains(p) ) return;
    
    getSimulation().registerChange( new Change(){
      public boolean undo(){
        issues.add(p);
        return true;
      }
      public boolean redo(){
        issues.remove(p);
        return true;
      }
    }, null);
    
    issues.remove(p);
  }
  
  public Set<Proposition> getIssues( ){
    return new LinkedHashSet<Proposition>( issues );
  }
  
  public void setTimeToFirstElection( final long t ){
    if( t < 0 ) return;
    
    getSimulation().registerChange( new Change(){
      final long oldTime = timeToFirstElection;
      final long newTime = t;
      public boolean undo(){
        timeToFirstElection = oldTime;
        return true;
      }
      public boolean redo(){
        timeToFirstElection = newTime;
        return true;
      }
    }, null);
    
    timeToFirstElection = t;
    
  }
  
  public long getTimeToFirstElection() {
      return timeToFirstElection;
  }
  
  public void setTimeForVoting( final long t ){
    if( t < 0 ) return;
    
    getSimulation().registerChange( new Change(){
      final long oldTime = timeForVoting;
      final long newTime = t;
      public boolean undo(){
        timeForVoting = oldTime;
        return true;
      }
      public boolean redo(){
        timeForVoting = newTime;
        return true;
      }
    }, null);
    
    timeForVoting = t;
    
  }
  
  public long getTimeForVoting() {
      return timeForVoting;
  }
  
  public void setTotalElectionTime( final long t ){
    if( t < 0 ) return;
    
    getSimulation().registerChange( new Change(){
      final long oldTime = totalElectionTime;
      final long newTime = t;
      public boolean undo(){
        totalElectionTime = oldTime;
        return true;
      }
      public boolean redo(){
        totalElectionTime = newTime;
        return true;
      }
    }, null);
    
    totalElectionTime = t;
    
  }
  
  public long getTotalElectionTime() {
      return totalElectionTime;
  }
  
  public Component copy(){
    PollingUtility retComp = new PollingUtility( "Copy of " + this.getName(), this.getSimulation() );
    retComp.timeToFirstElection = timeToFirstElection;
    retComp.timeForVoting = timeForVoting;
    retComp.totalElectionTime = totalElectionTime;
    retComp.constituency = new LinkedHashSet<Agent>( constituency );
    retComp.voted = new LinkedHashSet<Agent>( voted );
    retComp.digests = new LinkedHashSet<Agent>( digests );
    retComp.issues = new LinkedHashSet<Proposition>( issues );
    for( Proposition p : issues ) retComp.tally.put( p, new Double( tally.get(p) ) );
    for( Proposition p : issues ) retComp.castVotes.put( p, new Integer( castVotes.get(p) ) );
    return retComp;
  }
  
  public AgentInterface getAgentLevelInterface(){
    return new PollingUtilityInterface(this);
  }
  
  public void setup(){
    voted.clear();
    tally.clear();
    castVotes.clear();
    for( Proposition p : issues ) tally.put( p, new Double(0.0) );
    for( Proposition p : issues ) castVotes.put( p, new Integer(0) );
  }
  
  public void iterateThroughQueue(){
    long iterationNumber = getSimulation().getCurrentIteration();
    
    //check if it's voting time
    boolean votingTime = (iterationNumber > timeToFirstElection) &&
      (totalElectionTime > 0) &&
      ( (iterationNumber - timeToFirstElection - 1) % totalElectionTime < timeForVoting );
    
    //for each message, tally the vote if neccessary
    while(!messageQueue.isEmpty()){
      Message m = messageQueue.poll();
      
      if( votingTime && constituency.contains(m.getSender()) && !voted.contains(m.getSender()) ){
        while(m.hasNext()){
          Proposition prop = m.nextItem();
          if( !issues.contains( prop ) ) continue;
          double value = 0.0;
          if(tally.containsKey(prop)) value = tally.remove( prop ).doubleValue();
          value += m.getPropositionalActivation();
          int vote = 0;
          if(castVotes.containsKey(prop)) vote = castVotes.remove( prop ).intValue();
          vote++;
          tally.put( prop, new Double( value ) );
          castVotes.put( prop, new Integer( vote ) );
        }
        voted.add( m.getSender() );
      }
      
      m.burnMessage();
    }
  }
  
  public void communicate(){
    long iterationNumber = getSimulation().getCurrentIteration();
    
    //check if it is time to send digests and we have valid votes, return if not
    boolean digestTime = (iterationNumber > timeToFirstElection) &&
      (totalElectionTime > 0) &&
      ( (iterationNumber - timeToFirstElection - 1) % totalElectionTime == timeForVoting ||
       (totalElectionTime == timeForVoting && (iterationNumber - timeToFirstElection - 1) % totalElectionTime == totalElectionTime-1));
    if( !digestTime || voted.isEmpty() ) return;
    
    //communicate to those who ordered digests
    for( Agent a : digests ){
      Message m = new Message(this, a);
    }
    
    //clear voting information
    voted.clear();
    tally.clear();
    castVotes.clear();
    for( Proposition p : issues ) tally.put( p, new Double(0.0) );
    for( Proposition p : issues ) castVotes.put( p, new Integer(0) );
    
  }
  
  protected void addPropositionInternal( Proposition p ){
    return;
  }
  
  protected void removePropositionInternal( Proposition p ){
    removePropositionFromIssues( p );
    return;
  }
  
  protected void addCommunicationAgentInternal( Agent a ){
    return;
  }
  
  protected void removeCommunicationAgentInternal( Agent a ){
    removeAgentFromConstituency( a );
    removeAgentFromDigests( a );
    return;
  }
  
  protected void destruct(){
    return;
  }
  
  //methods for retrieving information (internal and UI)
  private double queriedActivation = 0.0;
  public void queryProposition( Proposition prop ){
    if( issues.contains(prop) && castVotes.get(prop).intValue() > 0 ){
      queriedActivation = tally.get(prop).doubleValue() / castVotes.get(prop).doubleValue();
    }else{
      queriedActivation = 0.0;
    }
    return;
  }
  
  public double getPropositionalActivation(){
    return queriedActivation;
  }
  
  public void completeQuery(){
    return;
  }
  
  public String getSubComponentType(){
    return "Polling Utility";
  }
};