package LIMBS;

import java.awt.Shape;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class ActionWidget extends ComponentWidget {
  
  private Rectangle2D shape;
  private Proposition prop = null;
  private int x = (LIMBSSystem.config().propSize / 2 ) + 10;
  private int y = (LIMBSSystem.config().propSize / 2 ) + 10;
  private int width = LIMBSSystem.config().propSize;
  
  public ActionWidget(Proposition action) {
    this.prop = action;
    this.shape = new Rectangle2D.Double();
    this.shape.setFrame(x-width/2, y-width/2, width, width);
    this.prop.getSimulation().addActionWidget(this);
  }
  
  public void delete(){
    this.prop.delete();
    this.prop.getSimulation().removeActionWidget(this);
  }
  
  public Component getComponent() {
    return (Component) this.prop;
  }
  
  public Shape getShape() {
    return this.shape;
  }
  
  public void setCentre(final double x, final double y) {
    this.x = (int) x; this.y = (int) y;
    this.shape.setFrame(x-width/2, y-width/2, width, width);
  }
  
  public void updateSize() {
    this.width = LIMBSSystem.config().propSize;
    this.setCentre(this.x, this.y);
  }
  
  public void connect(ComponentWidget w) {
    String className = w.getClass().getName();
    if (className.equals("LIMBS.GroupWidget")) {
      this.prop.addGroup((Group) w.getComponent());
    }
  }
  
  public void disconnect(ComponentWidget w) {
    String className = w.getClass().getName();
    if (className.equals("LIMBS.GroupWidget")) {
      this.prop.removeGroup((Group) w.getComponent());
    }
  }
  
  protected void setComponent(Component comp ) {
    this.prop = (Proposition) comp;
  }
  
  //doesn't have a particular functionality
  public void mouseMove(int nx, int ny){}
  
  //sends the first half of the movement event
  public void mousePress(int nx, int ny){
    setCentre( nx, ny );
    prop.getSimulation().registerChange( new Change(){
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
    prop.getSimulation().registerChange( new Change(){
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
  
  public ComponentWidget copy() {
    Proposition p = (Proposition) this.prop.copy();
    ComponentWidget copy = new ActionWidget(p);
    copy.setCentre(this.getCentreX()+5, this.getCentreY()+5);
    return copy;
  }
  
  private static Color backgroundColour = new Color(0.0f,0.5f,1.0f,1.0f);
  public Color getBackgroundColour(){
    return backgroundColour;
  }
  
};
