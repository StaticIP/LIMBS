package LIMBS;

import java.util.Set;
import java.util.LinkedHashSet;

class ComponentWidgetFactory {
  
  private static Set<String> availableGroups = new LinkedHashSet<String>();
  private static Set<String> availablePropositions = new LinkedHashSet<String>();
  private static Set<String> availableAgents = new LinkedHashSet<String>();
  private static Set<String> availableUtilities = new LinkedHashSet<String>();
  
  //static constructor
  static {
    availableGroups.add( "Group" );
    availablePropositions.add( "Evidence" );
    availablePropositions.add( "Action" );
    availablePropositions.add( "Goal" );
    
    availableAgents.add( "HOTCO Agent" );
    
    availableUtilities.add( "Logging Utility" );
    availableUtilities.add( "Event Utility" );
    availableUtilities.add( "Polling Utility" );
    
  }
  
  //methods to access the list of types of components
  public static Set<String> getGroupTypes () {
    return new LinkedHashSet<String>( availableGroups );
  }
  public static Set<String> getPropositionTypes () {
    return new LinkedHashSet<String>( availablePropositions );
  }
  public static Set<String> getAgentTypes () {
    return new LinkedHashSet<String>( availableAgents );
  }
  public static Set<String> getUtilityTypes () {
    return new LinkedHashSet<String>( availableUtilities );
  }
  
  //general factory method for getting a component widget of a particular type
  public static ComponentWidget getNewWidget( Simulation s, String type ){
    if( s == null || s.isSimulationRunning() ) return null;
    
    //if it's a type of group...
    if( type.equals("Group") ){
      String name = "Group #" + (s.getGroupWidgets().size() + 1);
      return new GroupWidget( new Group( name, s ) );
    }
    
    //if it's a type of proposition...
    if( type.equals("Evidence") ){
      String name = "Evidence #" + (s.getEvidenceWidgets().size() + 1);
      return new EvidenceWidget( new Proposition( name, 0, s ) );
    }
    if( type.equals("Action") ){
      String name = "Action #" + (s.getActionWidgets().size() + 1);
      return new ActionWidget( new Proposition( name, 1, s ) );
    }
    if( type.equals("Goal") ){
      String name = "Goal #" + (s.getGoalWidgets().size() + 1);
      return new GoalWidget( new Proposition( name, 2, s ) );
    }
    
    //if it's a type of agent...
    if( type.equals("HOTCO Agent") ){
      String name = "Agent #" + (s.getAgentWidgets().size() + 1);
      return new AgentWidget( new HOTCOAgent( name, s ) );
    }
    
    //if it's a type of utility...
    if( type.equals("Logging Utility") ){
      String name = "Utility #" + (s.getUtilityWidgets().size() + 1);
      return new UtilityWidget( new LoggingUtility( name, s ) );
    }
    if( type.equals("Event Utility") ){
      String name = "Utility #" + (s.getUtilityWidgets().size() + 1);
      return new UtilityWidget( new ExternalEventSchedulingUtility( name, s ) );
    }
    if( type.equals("Polling Utility") ){
      String name = "Utility #" + (s.getUtilityWidgets().size() + 1);
      return new UtilityWidget( new PollingUtility( name, s ) );
    }
    
    return null;
  }
  
  //return true if the type is a member of one of the sets
  public static boolean isValidType( String type ){
    return availableGroups.contains( type ) ||
           availablePropositions.contains( type ) ||
           availableAgents.contains( type ) ||
           availableUtilities.contains( type ); 
  }
  
}