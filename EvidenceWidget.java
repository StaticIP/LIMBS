package LIMBS;

import java.awt.Shape;
import java.awt.Color;
import java.awt.Polygon;

@SuppressWarnings("serial")
public class EvidenceWidget extends ComponentWidget {
  
  private Proposition prop = null;
  private Polygon shape = null;
  private int x = (LIMBSSystem.config().propSize / 2) + 10;
  private int y = (LIMBSSystem.config().propSize / 2) + 10;
  private int width = LIMBSSystem.config().propSize;
  
  public EvidenceWidget(Proposition evidence) {
    this.prop = evidence;
    this.shape = new Polygon();
    this.shape.addPoint(x - width / 2, y + width / 2);
    this.shape.addPoint(x, y - width / 2);
    this.shape.addPoint(x + width / 2, y + width / 2);
    this.prop.getSimulation().addEvidenceWidget(this);
  }
  
  public void delete() {
    this.prop.delete();
    this.prop.getSimulation().removeEvidenceWidget(this);
  }
  
  public void updateSize() {
    this.width = LIMBSSystem.config().propSize;
    this.setCentre(this.x, this.y);
  }
  
  public Component getComponent() {
    return this.prop;
  }
  
  public Shape getShape() {
    return this.shape;
  }
  
  public void setCentre(final double x, final double y) {
    this.x = (int) x; this.y = (int) y;
    this.shape = new Polygon();
    this.shape.addPoint(this.x - width / 2, this.y + width / 2);
    this.shape.addPoint(this.x, this.y - width / 2);
    this.shape.addPoint(this.x + width / 2, this.y + width / 2);
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
  
  protected void setComponent(Component comp) {
    this.prop = (Proposition) comp;
  }
  
  public ComponentWidget copy() {
    Proposition copiedEvidence = (Proposition) this.prop.copy();
    ComponentWidget copy = new EvidenceWidget(copiedEvidence);
    copy.setCentre(this.getCentreX() + 5, this.getCentreY() + 5);
    return copy;
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
  
  public Color getBackgroundColour(){
    return Color.cyan;
  }
  
};
