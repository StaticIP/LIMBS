package LIMBS;

import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class AgentWidget extends ComponentWidget {
  
  private Agent agent = null;
  private Ellipse2D shape = new Ellipse2D.Double();
  private int x = (LIMBSSystem.config().agentSize / 2) + 10;
  private int y = (LIMBSSystem.config().agentSize / 2) + 10;
  private int width = LIMBSSystem.config().agentSize;
  
  public AgentWidget(Agent agent) {
    this.agent = agent;
    this.shape.setFrame(x - width/2, y - width/2, width, width);
    this.agent.getSimulation().addAgentWidget(this);
  }
  
  public void delete(){
    this.agent.delete();
    this.agent.getSimulation().removeAgentWidget(this);
  }
  
  public void setCentre(final double x, final double y) {
    this.x = (int) x; this.y = (int) y;
    this.shape.setFrame(x - width/2, y - width/2, width, width);
  }
  
  public void updateSize() {
    this.width = LIMBSSystem.config().agentSize;
    this.setCentre(this.x, this.y);
  }
  
  public Shape getShape() {
    return shape;
  }
  
  public Component getComponent() {
    return this.agent;
  }
  
  public void connect(ComponentWidget w) {
    String className = w.getClass().getName();
    if (className.equals("LIMBS.GroupWidget")) {
      this.agent.addGroup((Group) w.getComponent());
    }
  }
  
  public void disconnect(ComponentWidget w) {
    String className = w.getClass().getName();
    if (className.equals("LIMBS.GroupWidget")) {
      this.agent.removeGroup((Group) w.getComponent());
    }
  }
  
  protected void setComponent(Component comp ) {
    this.agent = (Agent) comp;
  }
  
  public ComponentWidget copy() {
    Agent a = (Agent) this.agent.copy();
    ComponentWidget copy = new AgentWidget(a);
    copy.setCentre(this.getCentreX()+5, this.getCentreY()+5);
    return copy;
  }
  
  //doesn't have a particular functionality
  public void mouseMove(int nx, int ny){}
  
  //sends the first half of the movement event
  public void mousePress(int nx, int ny){
    setCentre( nx, ny );
    agent.getSimulation().registerChange( new Change(){
      private final int ux = x;
      private final int uy = y;
      public boolean undo(){
        setCentre( this.ux, this.uy );
        return true; 
      }
      public boolean redo(){
        return true;
      }
    }, null);
  }
  
  //drags the widget
  public void mouseDrag(int nx, int ny){
    setCentre( nx, ny );
  }
  
  //send the second half of the movement event
  public void mouseRelease(int nx, int ny){
    setCentre( nx, ny );
    agent.getSimulation().registerChange( new Change(){
      private final int ux = x;
      private final int uy = y;
      public boolean undo(){
        return true; 
      }
      public boolean redo(){
        setCentre( this.ux, this.uy );
        return true;
      }
    }, null);
  }
  
  public void drawWidget(Graphics2D g){
    
    //save the original colour
    Color original = g.getColor();
    
    //fill the component with the preferred background colour
    g.setColor(getBackgroundColour());
    g.fill(getShape());
    
    //draw the eyes
    g.setColor(Color.black);
    g.drawOval( x-width/4-width/16, y-width/4, width/8, width/8 );
    g.drawOval( x+width/4-width/16, y-width/4, width/8, width/8 );
    
    //draw the mouth
    double valence = agent.queryEmotionValence();
    if( valence > 0.01 ){
      valence = Math.sqrt( valence );
      double smileWidth = ((double) width / 4.0) / Math.sin( Math.PI * 0.5 * valence );
      g.drawArc(x - (int) smileWidth, y + width/4 - (int) smileWidth, (int)(2.0*smileWidth),(int) smileWidth,
                (int)(270.0-90.0*valence), (int)(180.0*valence));
    }else if( valence > -0.01 ){
      g.drawLine(x-width/4,y+width/4, x+width/4,y+width/4 );
    }else{
      valence = Math.sqrt( -1 * valence );
      double smileWidth = ((double) width / 4.0) / Math.sin( Math.PI * 0.5 * valence );
      g.drawArc(x - (int) smileWidth, y + width/4 - (int)(Math.sin( Math.PI * 0.5 * valence ) * (double) width / 8.0),
                (int)(2.0*smileWidth), (int) smileWidth, (int)(90.0-90.0*valence), (int)(180.0*valence));
    }
    
    //draw the outline around the component
    g.draw(getShape());
    
    //restore the original colour
    g.setColor(original);
    
  }
    
  public Color getBackgroundColour(){
    return Simulation.getEmotionColor(this.agent.queryEmotionValence(),
                                      this.agent.queryEmotionIntensity(),
                                      this.agent.queryEmotionPotency());
  }
};
