package LIMBS;

import java.util.*;
import java.awt.Color;

public class Simulation {
  
  private ChangeManager changem = null;
  private SimulationRunner runner = null;
  private boolean simulationRunning = false;
  private boolean finished = false;
  private Interface inter = null;
  
  private class SimulationRunner extends Thread {
    Simulation simulation = null;
    SimulationRunner(Simulation s){
      simulation = s;
    }
    public void run(){
      while( !simulation.getPaused() ) simulation.simulateIteration();
      setSimulationRunning(false);
    }
  }
  
  //inform system of non-saveable components
  static {
    LIMBSSystem sys = LIMBSSystem.SystemCall();
    sys.omitFieldFromSave(Simulation.class, "changem");
    sys.omitFieldFromSave(Simulation.class, "runner");
    sys.omitFieldFromSave(Simulation.class, "inter");
  }
  
  private Set<Agent> agents;
  private Set<Group> groups;
  private Set<Proposition> props;
  private Set<AgentWidget> agentWidgets;
  private Set<UtilityWidget> utilityWidgets;
  private Set<GroupWidget> groupWidgets;
  private Set<EvidenceWidget> evidenceWidgets;
  private Set<ActionWidget> actionWidgets;
  private Set<GoalWidget> goalWidgets;
  
  public Simulation () {
    agents = new LinkedHashSet<Agent>();
    groups = new LinkedHashSet<Group>();
    props = new LinkedHashSet<Proposition>();
    agentWidgets = new LinkedHashSet<AgentWidget>();
    utilityWidgets = new LinkedHashSet<UtilityWidget>();
    groupWidgets =  new LinkedHashSet<GroupWidget>();
    evidenceWidgets = new LinkedHashSet<EvidenceWidget>();
    actionWidgets = new LinkedHashSet<ActionWidget>();
    goalWidgets = new LinkedHashSet<GoalWidget>();
    changem = new ChangeManager();
    runner = new SimulationRunner(this);
    currentIteration = 0;
  }
  
  public void setInterface(Interface i){
    this.inter = i;
  }
  
  public void clearUndoRedo(){
    if( !this.simulationRunning )
      this.changem = new ChangeManager();
  }
  
  public void resetRunner(){
    this.runner = new SimulationRunner(this);
  }
  
  private long componentIdNumber = 0;
  public long getNewComponentNumber() {
    componentIdNumber++;
    return componentIdNumber;
  }
    
  public Set<Agent> getAgents(){
    return new LinkedHashSet<Agent>(agents);
  }
  
  public Set<Group> getGroups(){
    return new LinkedHashSet<Group>(groups);
  }
  
  public Set<Proposition> getPropositions(){
    return new LinkedHashSet<Proposition>(props);
  }
    
  public Set<AgentWidget> getAgentWidgets(){
    return new LinkedHashSet<AgentWidget>(agentWidgets);
  }
      
  public Set<UtilityWidget> getUtilityWidgets(){
    return new LinkedHashSet<UtilityWidget>(utilityWidgets);
  }
  
  public Set<GroupWidget> getGroupWidgets(){
    return new LinkedHashSet<GroupWidget>(groupWidgets);
  }
  
  public Set<EvidenceWidget> getEvidenceWidgets(){
    return new LinkedHashSet<EvidenceWidget>(evidenceWidgets);
  }
  
  public Set<ActionWidget> getActionWidgets(){
    return new LinkedHashSet<ActionWidget>(actionWidgets);
  }
  
  public Set<GoalWidget> getGoalWidgets(){
    return new LinkedHashSet<GoalWidget>(goalWidgets);
  }
  
  public void addGroup(final Group g){
    if( simulationRunning ) return;
    groups.add(g);
    changem.addChange( new Change() {
      public boolean undo(){
        groups.remove(g);
        return true;
      }
      public boolean redo(){
        groups.add(g);
        return true;
      }
    } );
  }
  
  public void removeGroup(final Group g){
    if( simulationRunning ) return;
    groups.remove(g);
    changem.addChange( new Change() {
      public boolean undo(){
        groups.add(g);
        return true;
      }
      public boolean redo(){
        groups.remove(g);
        return true;
      }
    } );
  }
  
  public void addProp(final Proposition p){
    if( simulationRunning ) return;
    props.add(p);
    changem.addChange( new Change() {
      public boolean undo(){
        props.remove(p);
        return true;
      }
      public boolean redo(){
        props.add(p);
        return true;
      }
    } );
  }
  
  public void removeProp(final Proposition p){
    if( simulationRunning ) return;
    props.remove(p);
    changem.addChange( new Change() {
      public boolean undo(){
        props.add(p);
        return true;
      }
      public boolean redo(){
        props.remove(p);
        return true;
      }
    } );
  }
  
  public void addAgent(final Agent a){
    if( simulationRunning ) return;
    agents.add(a);
    changem.addChange( new Change() {
      public boolean undo(){
        agents.remove(a);
        return true;
      }
      public boolean redo(){
        agents.add(a);
        return true;
      }
    } );
  }
  
  public void removeAgent(final Agent a){
    if( simulationRunning ) return;
    agents.remove(a);
    changem.addChange( new Change() {
      public boolean undo(){
        agents.add(a);
        return true;
      }
      public boolean redo(){
        agents.remove(a);
        return true;
      }
    } );
  }
  
  
  public void addAgentWidget(final AgentWidget a){
    if( simulationRunning ) return;
    agentWidgets.add(a);
    changem.addChange( new Change() {
      public boolean undo(){
        agentWidgets.remove(a);
        return true;
      }
      public boolean redo(){
        agentWidgets.add(a);
        return true;
      }
    } );
  }
  
  public void removeAgentWidget(final AgentWidget a){
    if( simulationRunning ) return;
    agentWidgets.remove(a);
    changem.addChange( new Change() {
      public boolean undo(){
        agentWidgets.add(a);
        return true;
      }
      public boolean redo(){
        agentWidgets.remove(a);
        return true;
      }
    } );
  }
  
  public void addUtilityWidget(final UtilityWidget a){
    if( simulationRunning ) return;
    utilityWidgets.add(a);
    changem.addChange( new Change() {
      public boolean undo(){
        utilityWidgets.remove(a);
        return true;
      }
      public boolean redo(){
        utilityWidgets.add(a);
        return true;
      }
    } );
  }
  
  public void removeUtilityWidget(final UtilityWidget a){
    if( simulationRunning ) return;
    utilityWidgets.remove(a);
    changem.addChange( new Change() {
      public boolean undo(){
        utilityWidgets.add(a);
        return true;
      }
      public boolean redo(){
        utilityWidgets.remove(a);
        return true;
      }
    } );
  }
  
  public void addGroupWidget(final GroupWidget g){
    if( simulationRunning ) return;
    groupWidgets.add(g);
    changem.addChange( new Change() {
      public boolean undo(){
        groupWidgets.remove(g);
        return true;
      }
      public boolean redo(){
        groupWidgets.add(g);
        return true;
      }
    } );
  }
  
  public void removeGroupWidget(final GroupWidget g){
    if( simulationRunning ) return;
    groupWidgets.remove(g);
    changem.addChange( new Change() {
      public boolean undo(){
        groupWidgets.add(g);
        return true;
      }
      public boolean redo(){
        groupWidgets.remove(g);
        return true;
      }
    } );
  }
  
  
  public void addEvidenceWidget(final EvidenceWidget e){
    if( simulationRunning ) return;
    evidenceWidgets.add(e);
    changem.addChange( new Change() {
      public boolean undo(){
        evidenceWidgets.remove(e);
        return true;
      }
      public boolean redo(){
        evidenceWidgets.add(e);
        return true;
      }
    } );
  }
  
  public void removeEvidenceWidget(final EvidenceWidget e){
    if( simulationRunning ) return;
    evidenceWidgets.remove(e);
    changem.addChange( new Change() {
      public boolean undo(){
        evidenceWidgets.add(e);
        return true;
      }
      public boolean redo(){
        evidenceWidgets.remove(e);
        return true;
      }
    } );
  }
  
  public void addActionWidget(final ActionWidget e){
    if( simulationRunning ) return;
    actionWidgets.add(e);
    changem.addChange( new Change() {
      public boolean undo(){
        actionWidgets.remove(e);
        return true;
      }
      public boolean redo(){
        actionWidgets.add(e);
        return true;
      }
    } );
  }
  
  public void removeActionWidget(final ActionWidget e){
    if( simulationRunning ) return;
    actionWidgets.remove(e);
    changem.addChange( new Change() {
      public boolean undo(){
        actionWidgets.add(e);
        return true;
      }
      public boolean redo(){
        actionWidgets.remove(e);
        return true;
      }
    } );
  }
  
  public void addGoalWidget(final GoalWidget e){
    if( simulationRunning ) return;
    goalWidgets.add(e);
    changem.addChange( new Change() {
      public boolean undo(){
        goalWidgets.remove(e);
        return true;
      }
      public boolean redo(){
        goalWidgets.add(e);
        return true;
      }
    } );
  }
  
  public void removeGoalWidget(final GoalWidget e){
    if( simulationRunning ) return;
    goalWidgets.remove(e);
    changem.addChange( new Change() {
      public boolean undo(){
        goalWidgets.add(e);
        return true;
      }
      public boolean redo(){
        goalWidgets.remove(e);
        return true;
      }
    } );
  }
  
  public static Color getEmotionColor(double valence, double intensity, double potency){
    float hue = (float) (valence + 1.0) / 6.0f;
    float brightness = 1.0f; //0.25f + 0.375f * (float) (intensity + 1.0);
    float saturation = 1.0f; //0.25f + 0.375f * (float) (potency + 1.0);
    return Color.getHSBColor(hue,saturation,brightness);
  }
  
  public static Color getAuraColor(double activation) {
    float hue = 0.625f; // for now, it's this colour
    float saturation = activation > 0 ? 1f : (float)(1f + activation);
    float brightness = activation < 0 ? 1f : (float)(1f - activation);
    return Color.getHSBColor(hue,saturation,brightness);
  }
  
  public static String getEmotionName(double valence, double intensity, double potency){
    
    //temporary valence-only model
    boolean valenceOnlyUsed = true;
    if( valenceOnlyUsed ){
      if( valence < -0.75 ) return "Horrible";
      if( valence < -0.50 ) return "Very Bad";
      if( valence < -0.25 ) return "Bad";
      if( valence < -0.125 ) return "Somewhat Bad";
      if( valence > 0.75 ) return "Euphoric";
      if( valence > 0.5 ) return "Very Good";
      if( valence > 0.25 ) return "Good";
      if( valence > 0.125 ) return "Somewhat Good";
      return "Nondescript";
    }
    
    //create a single integer representing grouped emotions
    int i = 0;
    if(valence < -0.25) i = i + 32;
    if(intensity < -0.25) i = i + 16;
    if(potency < -0.25) i = i + 8;
    
    if(valence > 0.25) i = i + 4;
    if(intensity > 0.25) i = i + 2;
    if(potency > 0.25) i = i + 1;
    
    switch(i){
      case 0: return "Nondescript";
      case (32+16+8): return "Sad";
      case (32+16+1): return "Disappointed";
      case (32+2+8): return "Afraid";
      case (32+2+1): return "Angry";
      case (4+16+8): return "Glad";
      case (4+16+1): return "Contented";
      case (4+2+8): return "Euphoric";
      case (4+2+1): return "Joyous";
      
      case (16): return "Apathetic";
      case (16+8): return "Anxious";
      
      default: return "Nondescript";
    }
  }
  
  private long currentIteration;
  private boolean paused = false;
  
  synchronized public void undo(){
    if( !this.simulationRunning )
      changem.undo();
  }
  
  synchronized public void redo(){
    if( !this.simulationRunning )
      changem.redo();
  }
  
  synchronized public void registerChange(Change c, ChangeNotification n){
    if( this.simulationRunning ) return;
    if(c == null) return;
    changem.addChange(c);
    if(n != null) changem.addChangeNotifier(n);
  }
  
  synchronized public void registerEndChange(ChangeNotification n){
    if( this.simulationRunning ) return;
    changem.addEndChangeMarker();
    if(n != null) changem.addChangeNotifier(n);
  }
  
  synchronized public boolean canUndo(){
    return changem.canUndo();
  }
  
  synchronized public boolean canRedo(){
    return changem.canRedo();
  }
  
  synchronized public boolean isSimulationRunning(){
    return simulationRunning;
  }
  
  synchronized private void setSimulationRunning(boolean b){
    simulationRunning = b;
  }
  
  public void pauseSimulation(){
    setPaused(true);
  }
  
  public void stopSimulation(){
    setPaused(true);
    resetCurrentIteration();
    prepareSystemForIteration();
  }
  
  public void resetSimulation(){
    resetCurrentIteration();
    prepareSystemForIteration();
  }
  
  public void runSimulation(){
    
    //send out a new running if the current one is dead
    if( !runner.isAlive() ){
        
      //unpause us if we are paused
      setPaused(false);
      setSimulationRunning(true);
      
      runner = new SimulationRunner(this);
      runner.start();
    }
    
  }
  
  public synchronized long getCurrentIteration(){
    return currentIteration;
  }
  
  private synchronized void resetCurrentIteration(){
    currentIteration = 0;
  }
  
  private synchronized void incrementCurrentIteration(){
    currentIteration++;
  }
  
  private synchronized boolean getPaused(){
    return this.paused;
  }
  
  private synchronized void setPaused(boolean value){
    this.paused = value;
  }
  
  private void prepareSystemForIteration( ){
    resetCurrentIteration();
    Iterator<Agent> it = agents.iterator();
    while(it.hasNext()){
      Agent a = it.next();
      a.setup();
    }
  }
  
  synchronized public void simulateIteration( ){
    
    //get the start time for the simulation and the minimum endtime
    long startTime = System.currentTimeMillis();
    long endTime = (LIMBSSystem.config().framerate <= 0) ? 0 : startTime + (long) ( 1000.0 / (double) LIMBSSystem.config().framerate );
    
    if( getCurrentIteration() == 0 )
      prepareSystemForIteration();
    
    numWaiting = 0;
    numTasks = agents.size()+1;
    finished = false;
    
    //increment the iteration counter
    incrementCurrentIteration();
    
    //simulate an iteration at the cognitive levels
    for(Agent a : agents){
      Thread t = new Thread(a);
      t.start();
    }
    
    //block until all threads are done
    synchronize();
    synchronize();
    
    //redraw the interface components
    inter.paintInterface();
    
    //wait for the time to expire to start the next iteration
    long currentTime = System.currentTimeMillis();
    long timeToWait = endTime - currentTime;
    while( timeToWait > 0 ){
      try{ Thread.sleep( timeToWait ); } catch( Exception e ) {}
      timeToWait = endTime - System.currentTimeMillis();
    }
    
  }
  
  private long numWaiting;
  private long numTasks;
  public synchronized void synchronize(){
    numWaiting++;
    try{
      if(numWaiting < numTasks){
        wait();
      }else if(numWaiting == numTasks){
        notifyAll();
      }else if(numWaiting > numTasks && numWaiting < 2 * numTasks){
        wait();
      }else{
        notifyAll();
        finished = true;
      }
    }catch(InterruptedException exception){
      System.out.println("Concurrency major fail. numWaiting = " + numWaiting);
      System.out.println(exception.toString());
    }
  }
  
};