package LIMBS;


//Config Object for system Variables
//if you modify this file you must delete Config.xml
public class Config {  //defaults go here
  public String LogFile;
  public int agentSize;
  public int propSize;
  public int nodeSize;
  public boolean showNames;
  public int framerate;
  public int screenSize;
  Config(){
    LogFile = "LIMBS_Log.txt";
    agentSize = 40;
    propSize = 40;
    nodeSize = 20;
    showNames = false;
    framerate = 0;
    screenSize = 1;
  }
};