package LIMBS;


//****************************************************************************
//Note to run this you need to add xstream.jar and xpp3.jar to your classpath.
//****************************************************************************




//Java packages
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;

//External Packages
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.*;

/*
  XStream

Copyright (c) 2003-2006, Joe Walnes
Copyright (c) 2006-2007, XStream Committers
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of
conditions and the following disclaimer. Redistributions in binary form must reproduce
the above copyright notice, this list of conditions and the following disclaimer in
the documentation and/or other materials provided with the distribution.

Neither the name of XStream nor the names of its contributors may be used to endorse
or promote products derived from this software without specific prior written
permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
DAMAGE
*/

/*
XPP3:

Indiana University Extreme! Lab Software License
Version 1.1.1
Copyright (c) 2002 Extreme! Lab, Indiana University. All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions 
are met:
1. Redistributions of source code must retain the above copyright notice, 
   this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright 
   notice, this list of conditions and the following disclaimer in 
   the documentation and/or other materials provided with the distribution.

3. The end-user documentation included with the redistribution, if any, 
   must include the following acknowledgment:

  "This product includes software developed by the Indiana University 
  Extreme! Lab (http://www.extreme.indiana.edu/)."
Alternately, this acknowledgment may appear in the software itself, 
if and wherever such third-party acknowledgments normally appear.
4. The names "Indiana Univeristy" and "Indiana Univeristy Extreme! Lab" 
must not be used to endorse or promote products derived from this 
software without prior written permission. For written permission, 
please contact http://www.extreme.indiana.edu/.

5. Products derived from this software may not use "Indiana Univeristy" 
name nor may "Indiana Univeristy" appear in their name, without prior 
written permission of the Indiana University.

THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHORS, COPYRIGHT HOLDERS OR ITS CONTRIBUTORS
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/



public class LIMBSSystem {
  //accessing methods!
  private static LIMBSSystem singleton = null;
  
  public static LIMBSSystem SystemCall(){
    //if we don't have a System yet, make one
    if(singleton == null){
      singleton = new LIMBSSystem();
    }
    //return whatever singleton we have already made
    return singleton;
  }
  
  
  
  //Variables
  private static XStream xstream;
  private static File saveFolder;
  private static File configFile;
  private static File logFile;
  private static Config config;
  private static FileWriter logfstream;
  private static BufferedWriter outLog;
  
  private static Set<Simulation> sims;
  private static Set<Interface> inters;
  

  //returns all subclasses of the provided one
  private static LinkedList<Class> getAllSubclasses( Class c ){
    LinkedList<Class> list = new LinkedList<Class>();
    list.add( c );
    return list;
    
  }
  
  //allows for classes to register things they don't want to save
  public void omitFieldFromSave(Class c, String f){
    xstream.omitField(c,f);
  }
  
  //constructor
  private LIMBSSystem () {
    xstream = new XStream(); //creates a new xml converter stream
    
    sims = new LinkedHashSet<Simulation>();
    inters = new LinkedHashSet<Interface>();
    //setup config file
    configFile = new File("Config.xml");
    loadConfig();
  
    try{
      logFile = new File(config.LogFile);
      logfstream = new FileWriter(logFile.getPath());
      outLog = new BufferedWriter(logfstream);
    }catch(Exception e){
      //TODO incase of log failing
    }
  }

  
  //Public Methods

  public static void newInstance(){//make a new window and system
    Simulation simulation = new Simulation();
    Interface interf = new Interface( simulation );
    
    sims.add(simulation);
    inters.add(interf);
    log("New Instance Created");
    status();
  }
  
  
 public static int load(File file){
    //File projectFolder = new File(saveFolder, fileName);
    Simulation sim = null;
    if(!file.exists()){
      return -1;
    }
    //File projectFile = new File(projectFolder, "Project.xml");    
//    if(!(projectFile.exists())){
//      return -2;
//    }  

    
    try{
      log("Loading " + file.getName() + "...");

      FileReader fstream = new FileReader(file.getPath());
      BufferedReader bin = new BufferedReader(fstream);
      
      ObjectInputStream in = xstream.createObjectInputStream(bin);

      sim = (Simulation)in.readObject();

      in.close();
      bin.close();

      
    }catch (Exception e){
      log("Error: Could not Load "+file.getName() + e.getMessage());
    }
    if(sim == null ){
      return -2;
    }
    //rebuild 
    sim.clearUndoRedo();
    sim.resetRunner();
    Interface interf = new Interface( sim );
    sims.add(sim);
    inters.add(interf);
    log("Finished Loading " + file.getName());
    status();
    return 1;
  }
  
 
 
  public int save(File file, Simulation sim){
    
   // File projectFolder = new File(saveFolder, fileName);
    //projectFolder.mkdir();
    //TODO: setup wrap here
    //System.out.println("save");
                      
    //File projectFile = new File(projectFolder, "Project.xml");
    
    try{
      //System.out.println("save - pre xml convert");
      //writeOut(xstream.toXML(sim), projectFile);
      
      FileWriter fstream = new FileWriter(file.getPath());
      BufferedWriter bout = new BufferedWriter(fstream);
      ObjectOutputStream out = xstream.createObjectOutputStream(bout);
      
      out.writeObject(sim);
      
      out.close();
      bout.close();
      
      
      //System.out.println("save - post xml convert");
    } catch (Exception e){
      log("Error: Save: " + e.getMessage());
      //System.out.println("Error: Save: " + e.getMessage());
      return -1;
    }
    return 1;
  } 
  
  
  
  //The interface calls this method as it closes
  public void shutdown(Interface i, Simulation s){
    inters.remove(i);
    sims.remove(s);
    i.dispose();
    
    Runtime r = Runtime.getRuntime();
    r.gc();//call Java Garbage collector since we just freed lots of memory
    status();
    
    if(inters.isEmpty() || sims.isEmpty() ){//all windows closed
      try{
        outLog.flush();
        outLog.close();
        System.exit(0);
      } catch (Exception e){
        System.exit(0);
      }
    }
  }
  
  public static void status(){
    Runtime r = Runtime.getRuntime();
    System.out.println("MEMORY -> used: " + r.totalMemory() +" max: " +r.maxMemory() + " free: "+r.freeMemory());
    log("MEMORY -> used: " + r.totalMemory() +" max: " +r.maxMemory() + " free: "+r.freeMemory());
  }
  
  
  //saves the current Configuration settings to disk
  public static int saveConfig(){
    try{
      return writeOut(xstream.toXML(config), configFile);
    }catch (Exception e){
      log("Error: Config File: " + e.getMessage());
      return -1;
    }
  }

  
  //writes the comment to the log file (for debug use)
  public static void log(String in){
    Date time = new Date(System.currentTimeMillis() );
    try{
      outLog.write(time.toString() + " : "+ in+"\n");
      outLog.flush();
    }catch (Exception e){//Catch exception if any
      System.err.println("Log Error: " + e.getMessage());
    }  
  }
  
  
  //get the current configueration object
  public static Config config(){
   return config; 
  }
  
  
  //Private Methods
  private static int writeOut(String output, File file){
    try{
      FileWriter fstream = new FileWriter(file.getPath());
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(output);
      out.close();
    }catch (Exception e){//Catch exception if any
      log("Error: File Writer: " + e.getMessage());
      return -1;
    }
    return 1;
  }
  
  private static String readIn(File file){
    String input = "";
    try{
      FileReader fstream = new FileReader(file.getPath());
      BufferedReader in = new BufferedReader(fstream);
      
      while(in.ready()){
        input = input + "\n" + in.readLine();
      }
      
      
      in.close();
    }catch (Exception e){//Catch exception if any
      
      log("Error: File Reader: " + e.getMessage());
      return null;
    }
    if (input == null) {
      return null;
    }
    return input;
  }
  
  
  
  private static void loadConfig(){
    try{
      config = (Config)xstream.fromXML(readIn(configFile));
    }catch (Exception e){
      log("Error: Load Config XML :" + e.getMessage());
    }
    if(config == null){
      log("Error: Failed To Load Config, Restoring Defualts...");
      config = new Config();
      saveConfig();
    }
    
  }
  

  
  
};