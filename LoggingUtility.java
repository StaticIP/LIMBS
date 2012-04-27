package LIMBS;

import java.util.*;
import java.math.*;
import java.io.*;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYDataItem;

public class LoggingUtility extends Utility {
  
  public LoggingUtility(String n, Simulation s){
    super(n, s);
  }
  
  //inform system of non-saveable components
  static {
    LIMBSSystem sys = LIMBSSystem.SystemCall();
    sys.omitFieldFromSave(LoggingUtility.class,"seriesMap");
    sys.omitFieldFromSave(LoggingUtility.class,"series");
  }
  
  private String filename = "";
  private int plotSaved = 20;
  
  public String getFilename(){
    return filename;
  }
  
  public void setFilename(final String f){
    
    //if we haven't changed the name, then do nothing
    if( f.equals(filename) ) return;
    
    //else, clear the file we want to open
    final String oldFilename = new String(filename);
    try{
      filename = new String(f);
      File appendFile = new File( filename );
      FileOutputStream outputStream = new FileOutputStream( appendFile, false );
      outputStream.close();
    }catch( Exception e ){
      System.err.println( e.toString() );
      return;
    }
    
    setup();
    
    getSimulation().registerChange( new Change(){
      final String oldName = new String(oldFilename);
      final String newName = new String(f);
      public boolean undo(){
        filename = new String(oldName);
        try{
          File appendFile = new File( filename );
          FileOutputStream outputStream = new FileOutputStream( appendFile, false );
          outputStream.close();
        }catch( Exception e ){
          System.err.println( e.toString() );
        }
        return true;
      }
      public boolean redo(){
        filename = new String(newName);
        try{
          File appendFile = new File( filename );
          FileOutputStream outputStream = new FileOutputStream( appendFile, false );
          outputStream.close();
        }catch( Exception e ){
          System.err.println( e.toString() );
        }
        return true;
      }
    }, null);
  }
  
  public Component copy(){
    LoggingUtility retComp = new LoggingUtility( "Copy of " + this.getName(), this.getSimulation() );
    retComp.filename = filename;
    return retComp;
  }
  
  public AgentInterface getAgentLevelInterface(){
    return new LoggingUtilityInterface(this);
  }
  
  LinkedHashMap< Agent, LinkedHashMap<Proposition, XYSeries> > seriesMap = 
    new LinkedHashMap< Agent, LinkedHashMap<Proposition, XYSeries> >();
  
  XYSeriesCollection series = new XYSeriesCollection();
  
  public void setup(){
    changedProps = true;
    if( series != null ){
      series.removeAllSeries();
    }else{
      series = new XYSeriesCollection();
    }
    if( seriesMap != null ){
      seriesMap.clear();
    }else{
      seriesMap = new LinkedHashMap< Agent, LinkedHashMap<Proposition, XYSeries> >();
    }
    
    try{
      File openFile = new File( filename );
      FileOutputStream outputStream = new FileOutputStream( openFile, false );
      outputStream.close();
    }catch( Exception e ){
      System.err.println( e.toString() );
    }
    
  }
  
  private Map<Proposition, Double> activationMap = new LinkedHashMap<Proposition, Double>();
  private Map<Proposition, Double> valenceMap = new LinkedHashMap<Proposition, Double>();
  private Set<Proposition> props = new LinkedHashSet<Proposition>();
  boolean changedProps = true;
  
  public void iterateThroughQueue(){
    
    //make sure the plot variables still exist to prevent null pointer exceptions
    if( series == null )
      series = new XYSeriesCollection();
    if( seriesMap == null )
      seriesMap = new LinkedHashMap< Agent, LinkedHashMap<Proposition, XYSeries> >();
    
    try{
      //open the file for appending
      File appendFile = new File( filename );
      FileOutputStream outputStream = new FileOutputStream( appendFile, true );
      PrintStream printStream = new PrintStream( outputStream );
      
      long iteration = getSimulation().getCurrentIteration();
      
      //check to see if we should print another legend line
      if( changedProps ){
        String legendLines = "Iteration Number, Agent Name, Agent ID, ";
        props = this.getPropositionSet();
        for( Proposition p : props ){
          legendLines += p.getName() + ", " + p.getId() + ", ";
        }
        legendLines += "\n , , ,";
        for( Proposition p : props ){
          legendLines += "Activation, Valence, ";
        }
        printStream.println( legendLines );
        changedProps = false;
      }
      
      //for each message, output a line onto the file
       while(!messageQueue.isEmpty()){
         Message m = messageQueue.poll();
         
         //get the sender of the information, and the plotting map for it
         Agent a = m.getSender();
         LinkedHashMap<Proposition, XYSeries> agentMap = null;
         if( seriesMap.containsKey(a) ){
           agentMap = seriesMap.get(a);
         }else{
           agentMap = new LinkedHashMap<Proposition, XYSeries>();
           seriesMap.put(a, agentMap);
         }
         
         //find the message contents with respect to the given proposition
         activationMap.clear();
         valenceMap.clear();
         while(m.hasNext()){
           Proposition prop = m.nextItem();
           activationMap.put( prop, new Double(m.getPropositionalActivation()) );
           valenceMap.put( prop, new Double(m.getPropositionalValence()) );
         }
         
         //print the contents of the message to both the plot and the log
         String outputString = iteration + ", " + a.getName() + ", " + a.getId() + ", ";
         for( Proposition p : props ){
           if( activationMap.containsKey(p) ){
             addPlotData(a, p, agentMap, (double) iteration, activationMap.get(p).doubleValue(), valenceMap.get(p).doubleValue() );
             outputString += activationMap.get(p).doubleValue() + ", " + valenceMap.get(p).doubleValue()  + ", ";
           }else{
             outputString += ", , ";
           }
         }
         activationMap.clear();
         valenceMap.clear();
         printStream.println( outputString );
         
         //deallocate this message and move on
         m.burnMessage();
         
       }
       
       //clear any plot entries older than a predefined number of iterations
       int numberOfSeries = series.getSeriesCount();
       for(int i = 0; i < numberOfSeries; i++){
         XYSeries subSeries = series.getSeries(i);
         int dataCount = subSeries.getItemCount();
         while( subSeries.getItemCount() > 0 ){
           XYDataItem xy = subSeries.getDataItem(0);
           if( xy.getX().doubleValue() >= (double) (iteration - (long) plotSaved) ) break;
           subSeries.remove(0);
           xy = subSeries.getDataItem(0);
         }
       }
       
      //close the file stream
      outputStream.close();
       
      notifyListener();
      
    }catch( Exception e ){
      System.err.println( e.toString() );
    }
  }
  
  private void addPlotData(Agent a, Proposition p, LinkedHashMap<Proposition, XYSeries> map, double it, double act, double val ){
    XYSeries data = null;
    if( map.containsKey(p) ){
      data = map.get(p);
    } else {
      data = new XYSeries( a.getName() + " - " + p.getName() );
      map.put(p, data);
      series.addSeries( data );
    }
    data.add(it, act);
  }
  
  public XYDataset getPlotData(){
    return series;
  }
  
  //this utility has no need to communicate
  public void communicate(){
    return;
  }
  
  protected void addPropositionInternal( Proposition p ){
    changedProps = true;
    return;
  }
  
  protected void removePropositionInternal( Proposition p ){
    changedProps = true;
    return;
  }
  
  protected void addCommunicationAgentInternal( Agent a ){
    return;
  }
  
  protected void removeCommunicationAgentInternal( Agent a ){
    return;
  }
  
  protected void destruct(){
    return;
  }
  
  //other required methods (internal)
  public void queryProposition( Proposition prop ){
    return;
  }
  
  public double getPropositionalActivation(){
    return 0.0;
  }
  
  public void completeQuery(){
    return;
  }
  
  public String getSubComponentType(){
    return "Logging Utility";
  }
  
};